package fwk.outbound.tcpip;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import nexcore.framework.integration.tcp.TCPConnectionException;
import nexcore.framework.integration.tcp.TCPConnectionReadTimeoutException;
import nexcore.framework.integration.tcp.internal.TCPConnection;

/**
 * TCP/IP 통신을 통해 전문송수신을 하는 클래스
 */
public class OutboundTCPConnection extends TCPConnection {

	private final static int CH_TOTAL_SIZE = 10; //채널헤더 전체 사이즈

	private final static int CH_TC_LENGTH_SIZE = 8; //전문길이
	private final static int CH_TC_KIND_SIZE = 2; //메시지종류 ("PP" : 세션 체크 데이터, "DD" : 단말 데이터 송신)
//	private final static int CH_TC_FILLER_SIZE = (CH_TOTAL_SIZE-CH_TC_LENGTH_SIZE-CH_TC_KIND_SIZE);
	
	private Reader reader;
	private BlockingQueue<Object> queue;
	private boolean calling;
	private boolean pleaseClose;

	private byte[] headerBuff = new byte[CH_TOTAL_SIZE];

	public void connect() {
		//		close();
		pleaseClose = false;
		calling = false;

		super.connect();
		getBufferedOutputStream();
		queue = new ArrayBlockingQueue<Object>(1);

		reader = new Reader();
		reader.start();
	}

	//	public void reconnect(){
	//		super.reconnect();
	//	}

	public void close() {
		pleaseClose = true;
		calling = false;
		if (reader != null) {
			try {
				reader.interrupt();
			} catch (Exception e) {
			    throw new RuntimeException(e);
			}
		}
		super.close();
	}

	@Override
	public void send(byte[] data) {
		send(data, 0, data.length);
	}

	@Override
	public void send(byte[] data, int offset, int length) {
		try {
			writeSendChannelHeader(bos, length);
//			writeChannelHeader(bos, length, "3", "0", "D");
			bos.write(data, offset, length);
			bos.flush();
			// input은 받지 않음
		} catch (TCPConnectionException e) {
			throw e;
		} catch (Exception e) {
			throw new TCPConnectionException(e, getName() + " : Can not send byte[].");
		}
	}

	@Override
	public byte[] call(byte[] data) {
		return call(data, 0, data.length);
	}

	@Override
	public byte[] call(byte[] data, int timeout) {
		return call(data, 0, data.length, timeout);
	}

	@Override
	public byte[] call(byte[] data, int offset, int length) {
		return call(data, 0, data.length, -1);
	}

	@Override
	public byte[] call(byte[] data, int offset, int length, int timeout) {
		try {
			calling = true;
			queue.clear();
			setReadTimeout(timeout);
			writeCallChannelHeader(bos, length);
//			writeChannelHeader(bos, length, "3", "1", "D");
			bos.write(data, offset, length);
			bos.flush();
			Object responseData = queue.take();
			//			System.out.println(getName() + " : responseData = " + responseData);
			if (responseData instanceof TCPConnectionException) {
				throw (TCPConnectionException) responseData;
			} else if (responseData instanceof Exception) {
				throw new TCPConnectionException((Exception) responseData, "");
			}
			return (byte[]) responseData;
		} catch (TCPConnectionException e) {
			throw e;
		} catch (Exception e) {
			throw new TCPConnectionException(e, getName() + " : Can not send/receive byte[].");
		} finally {
			calling = false;
		}
	}

	@Override
	public void send(Object data) {
		throw new TCPConnectionException("Can not use object send.");
	}

	@Override
	public Object call(Object data) {
		return call(data, -1);
	}

	@Override
	public Object call(Object data, int timeout) {
		throw new TCPConnectionException("Can not use object call.");
	}

	@Override
	protected void occuredReadTimeout() {
		TCPConnectionException e = new TCPConnectionReadTimeoutException(getName() + " : Timeout(" + getReadTimeout() + "ms) occurred.");
		try {
			queue.put(e);
		} catch (InterruptedException e1) {
		    throw new RuntimeException(e1);
		}
	}

	private void read() {
		try {
			int readLen = bis.read(headerBuff);
			if (readLen == -1) {
				throw new TCPConnectionException(getName() + " : The server has ended the connection.");
			}
			if (readLen != headerBuff.length) {
				throw new TCPConnectionException(getName() + " : Invalid data size. read length=" + readLen);
			}

			if (logger != null && logger.isTraceEnabled()) {
				logger.trace(getName() + " : RECEIVED CHANNEL HEADER : [" + new String(headerBuff) + "]");
			}

			int totalLength = Integer.parseInt(new String(headerBuff, 0, CH_TC_LENGTH_SIZE).trim()); //전문길이
			String kind = new String(headerBuff, CH_TC_LENGTH_SIZE, CH_TC_KIND_SIZE); //메시지종류 ("DD":데이터, "PP":시스템)
			
			// 시스템 메시지인 경우
			if ("PP".equals(kind)) {
				bos.write(headerBuff, 0, totalLength);
				bos.flush();
				if (logger != null && logger.isTraceEnabled()) {
					logger.trace(getName() + " : RECEIVED POLL DATA.");
				}
			} else {
				int length = totalLength - CH_TOTAL_SIZE;
				byte[] bodyBuff = new byte[length];
				int offset = 0;
				while ((readLen = bis.read(bodyBuff, offset, length - offset)) != -1 && offset < length) {
					offset += readLen;
				}
				if (logger != null && logger.isTraceEnabled()) {
					logger.trace(getName() + " : RECEIVED SERVICE DATA.");
				}

				clearReadTimeout();
				if (calling) {
					queue.put(bodyBuff);
				}
			}
		} catch (Exception e) {
			TCPConnectionException ne = null;
			if (e instanceof TCPConnectionException) {
				ne = (TCPConnectionException) e;
			} else if (e instanceof IOException) {
				ne = new TCPConnectionException(e, getName() + " : An error occurred communicating with the server.");
			} else {
				ne = new TCPConnectionException(e, getName() + " : Unknown error occurred.");
			}

			if (!pleaseClose) {
				try {
					if (calling) {
						queue.put(ne);
					}
				} catch (InterruptedException e1) {
				    throw new RuntimeException(e1);
				}
			}

			throw ne;
		}
	}

	private void writeSendChannelHeader(OutputStream os, int length) throws IOException{
		writeChannelHeader(bos, length, "DD");
	}
	
	private void writeCallChannelHeader(OutputStream os, int length) throws IOException{
		writeChannelHeader(bos, length, "DD");
	}

	private void writeChannelHeader(OutputStream os, int length, String kind) throws IOException {
		lpad(os, String.valueOf(length + CH_TOTAL_SIZE), CH_TC_LENGTH_SIZE, (byte) 0x30); //전문길이
		rpad(os, kind, CH_TC_KIND_SIZE, (byte) 0x20); //메시지종류 ("DD":데이터, "PP":시스템)
	}

	private void lpad(OutputStream os, String value, int length, byte padding) throws IOException {
		byte[] data = (value == null) ? new byte[0] : value.getBytes();
		int diff = length - data.length;
		for (int i = 1; i <= diff; i++) {
			os.write(padding);
		}
		os.write(data);
	}

	private void rpad(OutputStream os, String value, int length, byte padding) throws IOException {
		byte[] data = (value == null) ? new byte[0] : value.getBytes();
		int diff = length - data.length;
		os.write(data);
		for (int i = 1; i <= diff; i++) {
			os.write(padding);
		}
	}

	class Reader extends Thread {
		public void run() {
			try {
				getBufferedInputStream();
				while (true) {
					if (!isConnected()) {
						break;
					}
					read();
				}
			} catch (Exception e) {
				if (logger != null && logger.isErrorEnabled()) {
					logger.error(OutboundTCPConnection.this.getName(), e);
				}
			} finally {
				try {
					close();
				} catch (Exception ee) {
				    throw new RuntimeException(ee);
				}
			}
		}
	}

}

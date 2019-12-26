/*
 * Copyright (c) 2007 SK C&C. All rights reserved.
 *
 * This software is the confidential and proprietary information of SK C&C.
 * You shall not disclose such Confidential Information and shall use it
 * only in accordance with the terms of the license agreement you entered into
 * with SK C&C.
 */

package fwk.flat;


import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import nexcore.framework.core.data.IOnlineContext;
import nexcore.framework.core.data.IResultMessage;
import nexcore.framework.core.log.LogManager;
import nexcore.framework.core.service.front.IByteArrayMemoryPool;
import nexcore.framework.core.transform.FlatTransformerUtil;
import nexcore.framework.core.transform.FlatUtil;
import nexcore.framework.core.transform.IFlatHeaderParser;
import nexcore.framework.core.util.ByteArrayWrap;
import nexcore.framework.core.util.PaddableDataOutputStream;

import org.apache.commons.logging.Log;

import fwk.common.internal.CommonAreaHelper;
import fwk.common.internal.ImplCommonArea;
import fwk.common.internal.ImplFlatHeader;

/**
 * 전문헤더 변환기
 */
public class FlatHeaderParser implements IFlatHeaderParser {
    private static FlatHeaderParser	instance;
    
    private IByteArrayMemoryPool    byteArrayMemoryPool;
    private boolean                 flatCache;

    private String                  encoding;
    private String                  trailer;
    private byte[]                  trailerBytes;
    private int                     trailerLength;
    
    private Log                     logger;
    private ThreadLocal<ByteArrayWrap> FLAT_CACHES = new ThreadLocal<ByteArrayWrap>();

    public FlatHeaderParser() {
    	instance = this;
    }

    public static FlatHeaderParser getInstance() {
    	return instance;
    }

    public void init() {
    	if(logger == null){
    		logger = LogManager.getFwkLog();
    	}
    	if(logger.isInfoEnabled()){
    		logger.info("init(). encoding=" + encoding +", trailer=" + trailer +", flatCache="+flatCache);
    	}
    }

    public void destroy() {
    }
    
    /**
     * 전문 인코딩
     * @param encoding 전문 인코딩
     */
    public void setEncoding(String encoding){
    	this.encoding = encoding;
    }

    /**
     * 전문 트레일러
     * @param trailer 전문 트레일러
     */
    public void setTrailer(String trailer) {
        if (trailer != null) {
            this.trailer = trailer.trim();
            this.trailerBytes = FlatTransformerUtil.parseTrailerBytes(trailer);
            if(trailerBytes != null){
    			this.trailerLength = trailerBytes.length;
    		}
        }
    }
    
    public void setByteArrayMemoryPool(IByteArrayMemoryPool byteArrayMemoryPool) {
        this.byteArrayMemoryPool = byteArrayMemoryPool;
    }
    
    /**
     * 전문 캐시 여부
     */
    public void setFlatCache(boolean flatCache){
    	this.flatCache = flatCache;
    }
    
    public byte[] getTrailerBytes(){
      //2015.10.13 jihooyim code inspector 점검 수정 (04. Public 메소드로부터 반환된 Private 배열)
        //return trailerBytes;
        byte[] localTrailerBytes = null;
        if(this.trailerBytes != null) {
            localTrailerBytes = new byte[trailerBytes.length];
            for (int i=0; i < trailerBytes.length; i++){
                localTrailerBytes[i] = this.trailerBytes[i];
            }
        }
        return localTrailerBytes;
    }
    
    public String getEncoding(){
    	return encoding;
    }
    
    /**
     * 프로젝트 마다 전문 헤더의 total_length 가 타입도 다르고 위치도 다르므로 이렇게 customize 영역에서 처리함.
     * 전문 전체를 읽어 byte[] 로 리턴함.
     * byte[] 버퍼 풀을 이용하여 효율적으로 구현할 수 도 있음.
     *
     * @param in DataInputStream
     * @return 읽은 전문을 리턴함.
     * @throws IOException
     */
    public ByteArrayWrap readTotalData(DataInputStream in) throws IOException {
        byte[] data     = null;
        int whlMesgLen = -1;
        int definedLength = -1;
//        int stnHdrLng = -1;
        int offset     = 0;
        try {
        	// 전체전문길이 READ
        	byte[] b1 = new byte[FlatHeaderSpec.WHL_MESG_LEN.length()];
        	in.readFully(b1, 0, b1.length);
        	whlMesgLen = Integer.parseInt(new String(b1).trim());
        	offset += b1.length;
        	
            // 입력부 트레일러도 함께 READ
        	definedLength = whlMesgLen + trailerLength;
        	
            // 여기서 확보한 메모리는 앞으로 계속 사용해야하므로 return 하면안된다.
            data = byteArrayMemoryPool.getByteArray(definedLength);

            // 전문읽기.
            while (offset < definedLength) {
                int size = in.read(data, offset, definedLength - offset);
                if (size < 0) break; // 다 읽었다.
                offset += size;
            }
            
            System.arraycopy(b1, 0, data, 0, b1.length);
            if (definedLength != offset) {
                throw new EOFException("WHL_MESG_LEN="+whlMesgLen+", RECEIVED_LENGTH="+offset + ", DEFINED_LENGTH=" + definedLength);
            }
            
            ByteArrayWrap byteArrayWrap = new ByteArrayWrap(data, 0, definedLength - trailerLength);

            // 전문 전체 캐시
            if(flatCache){
            	FLAT_CACHES.set(new ByteArrayWrap(data, 0, definedLength));
            }

        	return byteArrayWrap;
        }catch(IOException e) {
            // 전문 read 하다가 에러나면 read한 만큼만이라도 로그 찍기 위해.
        	if(logger == null){
        		logger = LogManager.getFwkLog();
        	}
            if (whlMesgLen < -1) {
            	// 처음 8바이트조차 못읽은 경우.
                if (logger.isErrorEnabled()) {
                    logger.error("Can not read the first " + FlatHeaderSpec.WHL_MESG_LEN.length() + " bytes of ALL_TLMSG_LNG field.", e);
                }
                //throw e;
            }else {
                // 에러가 나더라도 읽은거 만큼은 로그 찍는다.
                if (logger.isErrorEnabled()) {
                    logger.error(FlatUtil.printBytesDataToHex(new ByteArrayWrap(data, 0, offset), "Request Data"));
                }
                //throw e;
            }
          //2015.10.13 jihooyim code inspector 점검 수정 (if/else문 body에 같은 것 반복 금지)
            throw e;
        }  
   }

    /**
     * 기존 readTotalData() 메소드와 동일하나 EAI에서는 전문을 송신시 헤더길이만큼을 뺀 전체길이를 보내주기 때문에 
     * FWK에서 EAI에서 보낸 전문의 길이를 그대로 사용하면 넘어온 전문을 제대로 읽지 못하는 현상이 발생한다.
     * 이에 별도의 메소드를 두어 EAI전문을 제대로 split할 수 있도록 하기 위해 해당 메소드를 만든다. by PSI (2014.07.09)
     * @param in
     * @return
     * @throws IOException
     */
    public ByteArrayWrap readEaiTotalData(DataInputStream in) throws IOException {
    	 byte[] data     = null;
         int whlMesgLen = -1;
         int definedLength = -1;
//         int stnHdrLng = -1;
         int offset     = 0;
         try {
         	// 전체전문길이 READ
         	byte[] b1 = new byte[EaiFlatHeaderSpec.STD_TGRM_LEN.length()];
         	in.readFully(b1, 0, b1.length);
         	/**
         	 * EAI는 전문길이 전송시 EAI헤더전문길이부분은 뺀 나머지 길이를 보내준다. 따라서
         	 *  FWK에서 전체 길이를 읽기 위해서는 전문길이부분만큼 다시 더해서 계산한다.
         	 */
         	whlMesgLen = Integer.parseInt(new String(b1).trim())+EaiFlatHeaderSpec.STD_TGRM_LEN.length();
         	offset += b1.length;
         	
             // 입력부 트레일러도 함께 READ
//         	definedLength = whlMesgLen + trailerLength;
         	definedLength = whlMesgLen; //EAI는 trailer Length부분을 더해서 보내줌.
         	
             // 여기서 확보한 메모리는 앞으로 계속 사용해야하므로 return 하면안된다.
             data = byteArrayMemoryPool.getByteArray(definedLength);

             // 전문읽기.
             while (offset < definedLength) {
                 int size = in.read(data, offset, definedLength - offset);
                 if (size < 0) break; // 다 읽었다.
                 offset += size;
             }
             
             System.arraycopy(b1, 0, data, 0, b1.length);
             if (definedLength != offset) {
                 throw new EOFException("WHL_MESG_LEN="+whlMesgLen+", RECEIVED_LENGTH="+offset + ", DEFINED_LENGTH=" + definedLength);
             }
             
             ByteArrayWrap byteArrayWrap = new ByteArrayWrap(data, 0, definedLength - trailerLength);

             // 전문 전체 캐시
             if(flatCache){
             	FLAT_CACHES.set(new ByteArrayWrap(data, 0, definedLength));
             }

         	return byteArrayWrap;
         }catch(IOException e) {
             // 전문 read 하다가 에러나면 read한 만큼만이라도 로그 찍기 위해.
         	if(logger == null){
         		logger = LogManager.getFwkLog();
         	}
             if (whlMesgLen < -1) {
             	// 처음 8바이트조차 못읽은 경우.
                 if (logger.isErrorEnabled()) {
                     logger.error("Can not read the first " + FlatHeaderSpec.WHL_MESG_LEN.length() + " bytes of ALL_TLMSG_LNG field.", e);
                 }
                 //throw e;
             }else {
                 // 에러가 나더라도 읽은거 만큼은 로그 찍는다.
                 if (logger.isErrorEnabled()) {
                     logger.error(FlatUtil.printBytesDataToHex(new ByteArrayWrap(data, 0, offset), "Request Data"));
                 }
                 //throw e;
             }
           //2015.10.13 jihooyim code inspector 점검 수정 (if/else문 body에 같은 것 반복 금지)
             throw e;
         }  
    	
    }
    /**
     * <p>
     * 전체 전문(헤더부와 바디부)을 선행 스켄(분석)한다.
     * </p>
     * FrontService EJB 같이 전문을 byte[]로 직접 받는 경우 선행 체크하기 위해 사용한다. 
     * 
     * @param totalData 전체 전문(헤더부와 바디부)
     * @throws IOException
     */ 
	public void preScanTotalData(ByteArrayWrap totalData) throws IOException {
        // 전문 전체 캐시
		if(flatCache){
			FLAT_CACHES.set(new ByteArrayWrap(totalData.getByteArray(), totalData.getOffset(), totalData.getLength()));
		}
	}

    /**
     * 전문을 파싱하여 헤더를 추출해냄.
     *
     * 주의) 여기서 읽은 다음부터 본 method 에서 전문의 body를 읽어야하므로 여기서는 꼭 헤더 만큼만 읽도록함.
     *       헤더보다 더읽어버리면, body 읽을때 전문 포멧과 안맞게됨.
     *
     * @param readProtocol : javax.servlet.http.HttpServletRequest 객체.
     * @param in : Request 데이타의 InputStream
     * @return : 전문을 파싱하여 IOnlineContext, IResultMessage 객체를 만든 후 Map에 담아 리턴함.
     *  Key 로는 [IOnlineContext.class, IResultMessage.class, "RESULT_MESSAGE_TXT"] 를 사용함.
     *  예)
     *  <code>
     *      map.put(IOnlineContext.class,   onlineCtx);
     *      map.put(IResultMessage.class,   resultMsg);
     *      map.put("RESULT_MESSAGE_TXT",   headers);   // EAI로 부터 응답받은 응답 전문의 경우 메세지부가 포함되어있다.
     *  </code>
     *
     * @throws IOException
     */
    public Map<Object, Object> parseHeader(DataInputStream in) throws IOException {
        // 캐시된 전문 조회
    	ByteArrayWrap flatByteArrayWrap = FLAT_CACHES.get();
    	FLAT_CACHES.remove();

    	byte[] buff = null;
    	try {
	    	buff = byteArrayMemoryPool.getByteArray(1024);

	    	// 전문 헤더 ==> onlineCtx Attributes
	    	Map<String, String> headers = FlatHeaderHelper.toHeaderMap(in, buff, encoding);
	    	
	    	// make OnlineCtx
	    	IOnlineContext onlineCtx = FlatHeaderHelper.makeOnlineContext(headers, false);
	    	onlineCtx.setAttribute("__FLAT__", flatByteArrayWrap);
	    	
	    	Map<Object, Object> retval = new HashMap<Object, Object>();
	        retval.put(IOnlineContext.class,	onlineCtx);
	        return retval;    
    	} finally {
    		if(buff != null){
    			byteArrayMemoryPool.returnByteArray(buff);
    		}
    	}
    }
 
    public Map<Object, Object> parseEaiHeader(DataInputStream in) throws IOException {
    	byte[] buff = null;
    	ByteArrayWrap flatByteArrayWrap = FLAT_CACHES.get();
    	FLAT_CACHES.remove();
    	try {
	    	buff = byteArrayMemoryPool.getByteArray(1024);

	    	// EAI용 전문 헤더 ==> onlineCtx Attributes
	    	Map<String, String> headers = FlatHeaderHelper.toEaiHeaderMap(in, buff, encoding);
	    	
	    	// make OnlineCtx
	    	IOnlineContext onlineCtx = FlatHeaderHelper.makeOnlineContextForEai(headers, false);
	    	onlineCtx.setAttribute("__FLAT__", flatByteArrayWrap);
	    	
	    	Map<Object, Object> retval = new HashMap<Object, Object>();
	        retval.put(IOnlineContext.class,	onlineCtx);
	        return retval;    
    	} finally {
    		if(buff != null){
    			byteArrayMemoryPool.returnByteArray(buff);
    		}
    	}
    }
    /**
     * 응답전송시 전문의 Header를 write하는 메소드.
     * 프로젝트마다 전문 형식이 다르므로 프로젝트마다 customize 해야함.
     */
    public void writeHeader(Map onlineCtxAttributes, IResultMessage resultMessage, ByteArrayWrap userData, PaddableDataOutputStream out, int trType) throws IOException {
    	// 인바운드 응답
    	if(trType == IFlatHeaderParser.TR_TYPE_RESPONSE){
	    	// onlineCtxAttributes ==> make Flat Header
			ImplFlatHeader flatHeader = null;
    		ImplCommonArea ca = CommonAreaHelper.getImpl(onlineCtxAttributes);
    		
    		// 업무가 호출되기 이전에 에러가 발생한 경우
	    	if(ca == null){
	    		flatHeader = new ImplFlatHeader();
	    		try {
	    			FlatHeaderHelper.toHeader(new HashMap(onlineCtxAttributes), flatHeader);
	    		}
	    		catch(Exception e){
	    			if(logger == null){
	            		logger = LogManager.getFwkLog();
	            	}
	    			if (logger.isErrorEnabled()) {
	                    logger.error("Can not attributes to FlatHeader", e);
	                }
	    		}
	    	}
	    	// 정상적으로 업무가 기동된 경우
	    	else {
	    		// Flat Header ==> Map
	    		flatHeader = ca.getFlatHeader();

		    	// 메시지 강제 초기화
		    	ca.clearMsgList();
	    	}
    	
	    	// 인바운드 응답 항목 초기화
	    	FlatHeaderHelper.initInboundResponseHeaders(flatHeader, resultMessage, onlineCtxAttributes);

	    	// Flat Header Map ==> 전문
	    	FlatHeaderHelper.toStream(flatHeader, resultMessage, userData.getLength(), out, true);
    	}
    	else {
    		throw new RuntimeException("Can not support TR_TYPE("+trType+")");
    	}
    }

    public void writeEaiHeader(Map onlineCtxAttributes, IResultMessage resultMessage, ByteArrayWrap userData, PaddableDataOutputStream out, int trType) throws IOException {
    	// 인바운드 응답
    	if(trType == IFlatHeaderParser.TR_TYPE_RESPONSE){
	    	// onlineCtxAttributes ==> make Flat Header
			ImplFlatHeader flatHeader = null;
    		ImplCommonArea ca = CommonAreaHelper.getImpl(onlineCtxAttributes);
    		
//    		// 업무가 호출되기 이전에 에러가 발생한 경우
//	    	if(ca == null){
//	    		flatHeader = new ImplFlatHeader();
//	    		try {
//	    			FlatHeaderHelper.toHeader(new HashMap(onlineCtxAttributes), flatHeader);
//	    		}
//	    		catch(Exception e){
//	    			if(logger == null){
//	            		logger = LogManager.getFwkLog();
//	            	}
//	    			if (logger.isErrorEnabled()) {
//	                    logger.error("Can not attributes to FlatHeader", e);
//	                }
//	    		}
//	    	}
//	    	// 정상적으로 업무가 기동된 경우
//	    	else {
//	    		// Flat Header ==> Map
//	    		flatHeader = ca.getFlatHeader();
//
//		    	// 메시지 강제 초기화
//		    	ca.clearMsgList();
//	    	}
    		Map<String, String> eaiHeader = FlatHeaderHelper.toEaiHeaderMap(ca, resultMessage, true);
	    	// 인바운드 응답 항목 초기화
//	    	FlatHeaderHelper.initInboundResponseHeaders(flatHeader, resultMessage, onlineCtxAttributes);

	    	// Flat Header Map ==> 전문
	    	FlatHeaderHelper.toEaiStream(eaiHeader, userData.getLength(), out);
	    	
    	}
    	else {
    		throw new RuntimeException("Can not support TR_TYPE("+trType+")");
    	}
    }
    /**
     * 헤더 분석시 에러가 발생한 경우에 이 메소드가 호출됨.
     */
	public Map<Object, Object> makeBlankHeader() {
		Map<Object, Object> headers = new HashMap<Object, Object>();
//		headers.put("rt_cd", "F");
		return headers;
	}
	
}

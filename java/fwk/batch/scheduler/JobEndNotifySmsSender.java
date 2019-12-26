package fwk.batch.scheduler;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import nexcore.framework.bat.JobNotifySendInfo;
import nexcore.framework.bat.monitor.IJobEndNotifySender;
import nexcore.framework.bat.util.Util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * <ul>
 * <li>업무 그룹명 : 금융 프레임워크 </li>
 * <li>서브 업무명 : 배치 코어 </li>
 * <li>설  명 : Job End 통지 대상 중 Email 인 건들을 send 함. </li>
 * <li>작성일 : 2011. 11. 11.</li>
 * <li>작성자 : 정호철</li>
 * </ul>
 */
public class JobEndNotifySmsSender implements IJobEndNotifySender {
	private boolean         enable;
	private DataSource      dataSource;
	private String          senderTel1;
	private String          senderTel2;
	private String          senderTel3;
	
    private Log             log;

    private String          campaignId;
    
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
	public void init() {
		log = LogFactory.getLog("scheduler");
	}
	
	public void destroy() {
	}
	
	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public String getSenderTel1() {
		return senderTel1;
	}

	public void setSenderTel1(String senderTel1) {
		this.senderTel1 = senderTel1;
	}

	public String getSenderTel2() {
		return senderTel2;
	}

	public void setSenderTel2(String senderTel2) {
		this.senderTel2 = senderTel2;
	}

	public String getSenderTel3() {
		return senderTel3;
	}

	public void setSenderTel3(String senderTel3) {
		this.senderTel3 = senderTel3;
	}
	
	// =========================================================================================
	

	private PreparedStatement prepare(Connection conn) throws SQLException {
		return conn.prepareStatement(
			"INSERT INTO SMS_IF "+
			"		(CLIENT,CAMPAIGN_ID,MEMBER_SEQ,MEMBER_NAME,MSG,DESTEL1,DESTEL2,DESTEL3,SRCTEL1,SRCTEL2,SRCTEL3,RESERVETIME) "+
			"VALUES (?,?," +
			"	(SELECT nvl(max(member_seq), '1000000000')+1  " +
			"	   FROM SMS_IF " +
			"	  WHERE CLIENT=? AND CAMPAIGN_ID=?)," +
			"?,?,?,?,?,?,?,?,?)");
	}
	
	public void insertIntoSmsTable(PreparedStatement pstmt, JobNotifySendInfo sendInfo) throws SQLException {
		// 메세지 만들기
		
		String serverName = System.getProperty("system.id").startsWith("r") ? "운영" : "개발";
		
		String msg; // 보낸 내용
		if (sendInfo.getReturnCode() != 0) {
			msg = "["+serverName+"배치에러]"+sendInfo.getJobId()+"/"+Util.fitLength(sendInfo.getJobDesc(), 40)+"/"+sendInfo.getErrorMsg();
		}else {
			msg = "["+serverName+"배치정상]"+sendInfo.getJobId()+"/"+sendInfo.getJobDesc();
		}
		msg = Util.fitLength(msg, 79);
		
		// 수신번호 만들기
		String telNo = sendInfo.getRecvPoint();
		if (telNo==null) {
			throw new RuntimeException(sendInfo.getReceiverName()+"의 전화번호 포멧 오류 ["+sendInfo.getRecvPoint()+"]");
			
		}
		if (telNo.indexOf('-') > -1 || telNo.indexOf(' ') > -1) { // 번호 사이에 - 가 들어있으면
			telNo = telNo.replaceAll("-", "");
			telNo = telNo.replaceAll(" ", "");
		}
		
		String tel1=null, tel2=null, tel3=null;
		if (telNo.length() == 11) {  // 010-1234-5678 형식
			tel1 = telNo.substring(0,3);
			tel2 = telNo.substring(3,7);
			tel3 = telNo.substring(7);
		}else if (telNo.length() == 10) {  // 019-222-3333
			tel1 = telNo.substring(0,3);
			tel2 = telNo.substring(3,6);
			tel3 = telNo.substring(6);
		}else {
			throw new RuntimeException("전화번호 포멧 오류 ["+sendInfo.getRecvPoint()+"]");
		}
		
		pstmt.setString(1,   "001");							// 001:batch, 002:ems
		pstmt.setString(2,   campaignId);						// CAMPAIGN_ID. YYYYMMDD+d/r
		pstmt.setString(3,   "001");							// CLIENT, TODO 정리해야함.
		pstmt.setString(4,   campaignId);						// CAMPAIGN_ID. YYYYMMDD+d/r
		pstmt.setString(5,   sendInfo.getReceiverName());		// MEMBER_NAME,
		pstmt.setString(6,   msg);								// MSG,
		pstmt.setString(7,   tel1);								// DESTEL1,
		pstmt.setString(8,   tel2);								// DESTEL2,
		pstmt.setString(9,   tel3);								// DESTEL3,
		pstmt.setString(10,  senderTel1);						// SRCTEL1,
		pstmt.setString(11,  senderTel2);						// SRCTEL2,
		pstmt.setString(12,  senderTel3);						// SRCTEL3,
		pstmt.setString(13,  sdf.format(new Date(System.currentTimeMillis())));		// RESERVETIME
		pstmt.executeUpdate();
	}
	
	public int doSend(List<JobNotifySendInfo> sendList) {
		if (sendList != null && sendList.size() > 0) {
			Connection        conn  = null;
			PreparedStatement pstmt = null;
			try {
				campaignId = Util.getCurrentYYYYMMDD()+System.getProperty("system.id").charAt(0); // campaign + 노드 구분 1바이트
				conn  = dataSource.getConnection();
				pstmt = prepare(conn);
				for (JobNotifySendInfo sendInfo : sendList) {
					try {
						insertIntoSmsTable(pstmt, sendInfo);
						sendInfo.setSendState("S");
					}catch(Exception e) {
						sendInfo.setSendState("F");
						log.error("배치 실행 결과를 SMS 서버로 전달하는 중 에러가 발생했습니다/"+sendInfo, e);
					}finally {
						sendInfo.setSendTime(new Timestamp(System.currentTimeMillis()));
						sendInfo.setTryCount(sendInfo.getTryCount()+1);
					}
				}
			}catch(Exception e) {
				log.error("배치 실행 결과를 SMS 서버로 전달하는 중 에러가 발생했습니다", e);
			}finally {
                try {
                    pstmt.close();
                } catch (Exception ignore) {
                    //2015.10.13 jihooyim code inspector 점검 수정 (02. 오류 상황 대응 부재)
                    if (log.isErrorEnabled()) log.error("pstmt.close error");                 
                }
                try {
                    conn.close();
                } catch (Exception ignore) {
                  //2015.10.13 jihooyim code inspector 점검 수정 (02. 오류 상황 대응 부재)
                    if (log.isErrorEnabled()) log.error("conn.close error");
                }
			}
		}
		return 0;
	}
	
	public String toString() {
		String dbUrl = null;
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			DatabaseMetaData dbmd = conn.getMetaData();
			dbUrl = dbmd.getURL();
		}catch(Exception e) {
		    return "error";
		}finally {
			try  { conn.close(); }catch(Exception e) {}
		}
		
		return "JobEndNotifySmsSender["+dbUrl +"]";
	}
}

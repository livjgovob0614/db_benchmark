package fwk.utils;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Properties;

import nexcore.framework.core.ServiceConstants;
import nexcore.framework.core.exception.BizRuntimeException;
import nexcore.framework.core.ioc.ComponentRegistry;
import nexcore.framework.core.log.LogManager;
import nexcore.framework.core.message.IMessageManager;
import nexcore.framework.core.parameter.IWasInstanceManager;
import nexcore.framework.core.parameter.WasInstance;
import nexcore.framework.core.util.Base64;
import nexcore.framework.core.util.BaseUtils;
import nexcore.framework.core.util.CryptoUtils;
import nexcore.framework.core.util.ExceptionUtil;
import nexcore.framework.core.util.StringUtils;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.time.FastDateFormat;
import org.apache.commons.logging.Log;

import com.ksign.securedb.api.SDBCrypto;
import com.ksign.securedb.api.util.SDBException;

import fwk.code.HpcCodeManager;
import fwk.code.internal.HpcCode;
import fwk.constants.DmsConstants;
import fwk.file.internal.HpcUploadedFileManager;
import fwk.message.internal.HpcMessage;
import fwk.outbound.jmx.JMXConnection;
import fwk.resolver.IdentityResolver;
import fwk.resource.bundle.HpcResourceBundle;

/**
 * <ul>
 * <li>업무 그룹명 : dev-hpc-customizing</li>
 * <li>서브 업무명 : fwk.utils</li>
 * <li>설  명 : HpcUtils</li>
 * <li>작성일 : 2015. 2. 2.</li>
 * <li>작성자 : Administrator</li>
 * </ul>
 */
public class HpcUtils {

    private static final String MASKING_CHAR = "*"; // 마스킹문자
    
	public static String makeGlobId(String chnCd) {
		IdentityResolver resolver = IdentityResolver.getInstance();
		if(resolver==null) {
			resolver = new IdentityResolver(); 
		}
		return resolver.newGlobalId(chnCd);
	}

	/**
	 * WAS 노드번호를 return함. ex) APDEV01 일 경우는 1을 return함.
	 * @return int
	 */
	public static int getWasNodeNo() {
		String wasId = BaseUtils.getCurrentWasInstanceId();
		int wasNodeNo = 0;
		if("R".equals(BaseUtils.getRuntimeMode())||"D".equals(BaseUtils.getRuntimeMode())) {
			if(StringUtils.isNotEmpty(wasId)) {
				try {
					wasNodeNo = Integer.parseInt(wasId.substring(wasId.length()-1));
				} catch (Exception e) {
					wasNodeNo = 0;
				}
			}
		} else {
			wasNodeNo = 0;
		}
		return wasNodeNo;
	}

	/**
	 * WAS의 system.id를 read하여 was분류를 return한다. ex) hpc_ndap1_AP1 일 경우는 'AP'를 return함.
	 * @return String
	 */
	public static String getWasNodeKind() {
		String wasId = BaseUtils.getCurrentWasInstanceId();
		String[]wasIdArr = null;
		if("L".equals(BaseUtils.getRuntimeMode())) {
			return "LC";
		} else {
			wasIdArr = StringUtils.tokenizeToStringArray(wasId, "_");
			return wasIdArr[2].substring(0, 2);
		}
	}

	/**
	 * SHA-265암호화 실시(Base64처리)
	 * 단방향암호화로서 암호화된 byte를 Base64로 Encoding하여 return함.
	 * @param plainText
	 * @return String
	 */
	public static String encryptTextBySHA256(String plainText) {
	    String encode64 = "";
		try {
			if(StringUtils.isNotEmpty(plainText)){
				byte byteData[] =  encryptTextBySHA256ExcpBse64(plainText);
				encode64 = new String(Base64.encodeToByte(byteData, false));
//				sb = new StringBuffer();
//				for (int i = 0; i < byteData.length; i++) {
//					sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
//				}
			}
		} catch (Exception e) {
			throw new BizRuntimeException("SKFE5010", new String[]{"SHA Encrypt"}, e);
		}
		return  encode64;
	}

	/**
	 * SHA-265암호화 실시(Base64미처리)
	 *  단방향암호화로서 암호화된 byte[]를 return함.
	 *  Input pararmeter가 null 또는 Empty경우에는 return value가 null임을 인지하기 바람.   
	 * @param plainText
	 * @return byte[] 
	 */
	public static byte[] encryptTextBySHA256ExcpBse64(String plainText) {
	    byte[] byteData = null;
        try {
            if(StringUtils.isNotEmpty(plainText)){
                MessageDigest md = MessageDigest.getInstance("SHA-256");
                md.update(plainText.getBytes());
                byteData = md.digest();
            }
        } catch (NoSuchAlgorithmException e) {
            throw new BizRuntimeException("SKFE5010", new String[]{"SHA Encrypt"}, e);
        }
        return  byteData;
	}
	
	/**
     * SHA-265암호화 실시(Base64미처리&String return)
     *  단방향암호화로서 암호화된 String를 return함.
     *  Input pararmeter가 null 또는 Empty경우에는 return value가 null임을 인지하기 바람.   
	 * @param plainText
	 * @return String
	 */
	public static String encryptTextBySHA256ToStr(String plainText) {
	    byte[] byteData = encryptTextBySHA256ExcpBse64(plainText);
	    if(byteData == null) {
	        return "";
	    }
	    StringBuffer sb = new StringBuffer();
	    for (int i = 0; i < byteData.length; i++) {
          sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
       }
	    return sb.toString();
	}
	
	
	/**
	 * Byte[]를 Hex String로 변환하여 리턴한다. 
	 *  
	 * @param byteArr
	 * @return String
	 */
	public static String convertByteArrToHexStr(byte[] byteArr) {
//	    return new java.math.BigInteger(byteArr).toString(16);
	    StringBuffer sb = new StringBuffer();
	    if(byteArr ==null) {
	        return "";
	    }
	    for (int i = 0; i < byteArr.length; i++) {
            sb.append(Integer.toHexString(0x0100 + (byteArr[i] & 0x00FF)).substring(1));
        }
        return sb.toString();
	}
	
	/**
	 * Hex String 을 Byte[]로 변환한다.  
	 * @param hexString
	 * @return byte[]
	 */
	public static byte[] convertHexStrToByteArray(String hexString) {
//	    return  new java.math.BigInteger(hexString, 16).toByteArray();
	    if (StringUtils.isEmpty(hexString)) {
	        return null;
	    }

	    byte[] ba = new byte[hexString.length() / 2];
	    for (int i = 0; i < ba.length; i++) {
	        ba[i] = (byte) Integer.parseInt(hexString.substring(2 * i, 2 * i + 2), 16);
	    }
	    return ba;

	}
	/**
	 * AES로 암호화한 텍스트를 리턴한다.
	 * @param plainText
	 * @return String
	 */
	public static String encodeByAES(String plainText) {
	    String encodedStr = "";
	    try {
	        // SecureDB API 사용을 위한 instance 얻어오기
	        // Param: domain_name, primary server ip, primary server port, timeout(초) 
	        String domainName = BaseUtils.getConfiguration("ksign.securedb.domain."+BaseUtils.getRuntimeMode());
	        String serverIp = BaseUtils.getConfiguration("ksign.securedb.ip."+BaseUtils.getRuntimeMode());
	        int serverPort = Integer.parseInt(BaseUtils.getConfiguration("ksign.securedb.port."+BaseUtils.getRuntimeMode()));
	        String objName = BaseUtils.getConfiguration("ksign.securedb.obj."+BaseUtils.getRuntimeMode());
	        
            SDBCrypto crypto = SDBCrypto.getInstanceDomain(domainName,  serverIp , serverPort);
	        encodedStr  = crypto.encryptEx(objName, plainText, "MS949", 1);
	        
	    } catch (SDBException ex) {
	        throw new BizRuntimeException("SKFE5010", new String[]{"AES Encrypt"}, ex);
	    }
		return encodedStr;
	}

	/**
	 * plainText가 지정된 길이보다 짧은 경우에는 그 길이만큼을 공백으로 채운 뒤,
	 * AES로 암호화하여 반환한다. 
	 * encodeRpadStrByAES
	 *  
	 * @param length
	 * @param plainText
	 * @return String
	 */
	public static String encodeRpadStrByAES(int length, String plainText) {
	    String tempText = StringUtils.rpad(plainText, length, null);
	    String encodedStr = "";
	    try {
            encodedStr  = encodeByAES(tempText);
        } catch (Exception e) {
            throw new BizRuntimeException("SKFE5010", new String[]{"AES Encrypt"}, e);
        }
	    return encodedStr;
	}
	
	/**
	 *  plainText가 지정된 바이트 길이보다 짧은 경우에는 그 길이만큼을 공백으로 채운 뒤,
     * AES로 암호화하여 반환한다. 
	 * encodeRpadByteByAES
	 *  
	 * @param length
	 * @param plainText
	 * @return String
	 */
	public static String encodeRpadByteByAES(int length, String plainText) {
	    String tempText = StringUtils.rpadByte(plainText,  (char)0x20, length);
	    String encodedStr = "";
	    try {
	        encodedStr = encodeByAES(tempText);
	    } catch(Exception e) {
	        throw new BizRuntimeException("SKFE5010", new String[]{"AES Encrypt"}, e);
	    }
        return encodedStr;
	}
	
	/**
	 * AES로 암호화된 텍스트를 복호화한다.
	 * @param encryptedText
	 * @return String
	 */
	public static String decodeByAES(String encryptedText) {
	    String encodedStr = "";
	    try {
            // SecureDB API 사용을 위한 instance 얻어오기
            // Param: domain_name, primary server ip, primary server port, timeout(초) 
            String domainName = BaseUtils.getConfiguration("ksign.securedb.domain."+BaseUtils.getRuntimeMode());
            String serverIp = BaseUtils.getConfiguration("ksign.securedb.ip."+BaseUtils.getRuntimeMode());
            int serverPort = Integer.parseInt(BaseUtils.getConfiguration("ksign.securedb.port."+BaseUtils.getRuntimeMode()));
            String objName = BaseUtils.getConfiguration("ksign.securedb.obj."+BaseUtils.getRuntimeMode());
            
            SDBCrypto crypto = SDBCrypto.getInstanceDomain(domainName,  serverIp , serverPort);
            encodedStr  = crypto.decryptEx(objName, encryptedText, "MS949", 1);
            
        } catch (SDBException ex) {
            throw new BizRuntimeException("SKFE5010", new String[]{"AES Decrypt"}, ex);
	    }
		return encodedStr;
	}


	/**
	 * 코드그룹 리스트를 반환한다.
	 * @return List<String>
	 */
	public static List<String> getCodeGroupIds() {
		HpcCodeManager cm = (HpcCodeManager) ComponentRegistry.lookup(DmsConstants.CODE_MANAGER);
		return cm.getCodeGroupIds();
	}
	/**
	 * 코드그룹에 속해있는 공통코드 리스트를 가지고 온다.
	 * @param groupId
	 * @return List<HpcCode>
	 */
	public static List<HpcCode>getCodes(String groupId) {
		HpcCodeManager cm = (HpcCodeManager) ComponentRegistry.lookup(DmsConstants.CODE_MANAGER);
		List<HpcCode>codes = cm.getCodes(groupId);
		if(codes == null){
			return new ArrayList<HpcCode>(0);
		}
		return codes;
	}

	/**
	 * 선택된 코드그룹안에 있는 공통코드를 반환한다.
	 * @param groupId
	 * @param codeId
	 * @return HpcCode
	 */
	public static HpcCode getCode(String groupId, String codeId) {
		HpcCodeManager cm = (HpcCodeManager) ComponentRegistry.lookup(DmsConstants.CODE_MANAGER);
		return cm.getCode(groupId, codeId);
	}

	/**
	 * 모든 WAS의 캐쉬된 공통코드를 refresh한다. 
	 */
	public static void codeRefresh() {
		refreshCacheToAllWas(DmsConstants.CODE_CACHE_NAME);
	}


	/**
	 * 메시지ID에 따른 메시지를 취득한다. 
	 * @param msgId
	 * @return String
	 */
	public static String getMessage(String msgId) {
		return BaseUtils.getMessage(msgId)==null?"":BaseUtils.getMessage(msgId);
	}

	   /**
     * 메시지ID와 parameter에 따른 메시지를 취득한다. 
     * @param msgId
     * @return String
     */
    public static String getMessage(String msgId, String[] params) {
        return BaseUtils.getMessage(msgId, params)==null?"":BaseUtils.getMessage(msgId, params);
    }

    /**
     * 추가컬럼 POS 응답코드 취득
     *  
     * @param msgId
     * @return String
     */
    public static String getMessagePosRespCd(String msgId) {
        IMessageManager manager = (IMessageManager) ComponentRegistry.lookup(ServiceConstants.MESSAGE);
        HpcMessage message =(HpcMessage)manager.getMessage(msgId, BaseUtils.getDefaultLocale(), null);
        if(message == null) {
            return "";
        } else {
            return message.getPosRespCd();
        }
    }
	
    /**
     * 제휴사응답코드 취득
     *  
     * @param msgId
     * @return String
     */
    public static String getMessageCoRespCd(String msgId) {
        IMessageManager manager = (IMessageManager) ComponentRegistry.lookup(ServiceConstants.MESSAGE);
        HpcMessage message =(HpcMessage)manager.getMessage(msgId, BaseUtils.getDefaultLocale(), null);
        if(message == null) {
            return "";
        } else {
            return message.getCoRespCd();
        }
    }
    
    public static String getMessageCoRespDtlCd(String msgId) {
        IMessageManager manager = (IMessageManager) ComponentRegistry.lookup(ServiceConstants.MESSAGE);
        HpcMessage message =(HpcMessage)manager.getMessage(msgId, BaseUtils.getDefaultLocale(), null);
        if(message == null) {
            return "";
        } else {
            return message.getCoRespDtlCd();
        }
    }
    
    /**
     * encoding을 통한 Right Padding실시 (한글이 포함되어 있는 경우를 대비한 패딩처리메소드)
     *  
     * @param src
     * @param padChar
     * @param len
     * @param encoding
     * @return String
     */
    public static String rpadByte(String src, char padChar, int len, String encoding) {
    	
    	StringBuffer sb = new StringBuffer();
    	byte[] bb = null;   
        try {
        	if(StringUtils.isEmpty(encoding)) encoding = DmsConstants.MS949;
            bb = src==null ? new byte[0] : src.getBytes(encoding);
            
            if (bb.length >= len) { // n 보다 s가 길면 잘라낸다.
            	sb.append(new String(bb, 0, len, encoding)); 
            } else {
	            byte[] pad = new byte[len];
	            
	            pad = Arrays.copyOf(bb, pad.length);
	            for (int i=bb.length; i<pad.length; i++) {
	                pad[i] = (byte)padChar;
	            }
	            sb.append(new String(pad, encoding));
            }
            return sb.toString();
        } catch(UnsupportedEncodingException e){
        	throw new BizRuntimeException("SKFE5010", new String[]{"Right Padding"}, e);
        }
        
    }
    
    /**
     * encoding을 통한 Right Padding실시 (한글이 포함되어 있는 경우를 대비한 패딩처리메소드)
     *  
     * @param src
     * @param padChar
     * @param len
     * @return String
     */
    public static String rpadByte(String src, char padChar, int len) {
    	return rpadByte(src, padChar, len, DmsConstants.MS949);
    }
    
	/**
	 * 모든 WAS의 캐쉬된 메시지를 refresh한다.
	 */
	public static void msgRefresh() {
		refreshCacheToAllWas(DmsConstants.MSG_CACHE_NAME);
		
	}
	/**
	 * 등록된 WAS에 대한 Cache Refresh를 실시한다.
	 * @param cacheName
	 */
	private static void refreshCacheToAllWas(String cacheName) {
		Log log = LogManager.getFwkLog();
		List<WasInstance> wasList = getAllWasInstance();
		String runtimeMode = BaseUtils.getRuntimeMode();
		JMXConnection conn = null;
		Object returnValue = null;
		String wasInstanceId = "";
		try {
			if("L".equals(runtimeMode)) {
				wasInstanceId = "localhost";
				HpcCodeManager cm = (HpcCodeManager) ComponentRegistry.lookup(DmsConstants.CODE_MANAGER);
				if(log.isInfoEnabled()) log.info("[Request "+wasInstanceId+" cache refresh by JMX ]");
				cm.refresh();
				if(log.isInfoEnabled())log.info("[End "+wasInstanceId+" cache refresh by JMX  ]");
			} else {
				for(WasInstance wasInstance : wasList) {
					wasInstanceId = wasInstance.getWasInstanceId();
					if(log.isInfoEnabled()) log.info("[Request "+wasInstanceId+" cache refresh by JMX ]");
					try {
					    conn = JMXUtils.createJMXConnection(wasInstance);
					    returnValue = conn.invoke("Nexcore:Service=CacheManager", "refresh", new Object[] { cacheName }, new String[] { String.class.getName() });
					} catch(Exception e) {
					  //JMX통신이 실패할 경우에는 에러로그만 찍고 다시 for문 실행하도록 함.
					    if(log.isErrorEnabled()) log.error("["+wasInstanceId+"] : refresh fail >>>> " + ExceptionUtil.getExceptionMessage(e));
					}
					if(log.isInfoEnabled())log.info("Return Value for JMX : "+returnValue);
					if(log.isInfoEnabled())log.info("[End "+wasInstanceId+" cache refresh by JMX  ]");
				}
			}
//		} catch (Exception e) {
//			throw new FwkRuntimeException("SKFE5010", new String[]{"JMX Cache Refresh ("+wasInstanceId+")"}, e);
		} finally {
			JMXConnection.close(conn);
		}
	}
	
	/**
     * 카드번호 마스킹처리
     * 카드BIN 뒤 8자리(ex:1234********5678)
     *  
     * @param cardNo
     * @return String
     */
    public static String maskingCardNo(String cardNo) {
        if (cardNo == null || cardNo.length() < 15 || cardNo.length() >16) {
            return "";
        }
        StringBuilder sb = new StringBuilder(cardNo.substring(0, 4));
        for (int i=0; i<8; i++) {
            sb.append(MASKING_CHAR);
        }
        sb.append(cardNo.substring(12));
        return sb.toString();
    }
    
    /**
     * 전화번호 마스킹처리
     * 국번 (ex:010****1234)
     *  
     * @param telNo
     * @return String
     */
    public static String maskingTelNo(String telNo) {
        // 서울(02), 부산(051), 대구(053), 인천(032), 광주(062), 대전(042), 울산(052), 세종(044), 
        // 경기(031), 강원(033), 충북(043), 충남(041), 전북(063), 전남(061), 경북(054), 경남(055), 제주(064)
        // 휴대전화(010,011,016,017,019)
        // 인터넷전화(070)
        if (StringUtils.isEmpty(telNo)) {
            return "";
        } else if (telNo.length()<5){
            return telNo;
        }        
        
        StringBuilder sb = new StringBuilder();
        int exNoLen = 0;
        if (telNo.length() < 9){
            exNoLen = telNo.length() - 4;
        } else if (telNo.startsWith("02")) {
            sb.append(telNo.substring(0,2));
            exNoLen = telNo.length() - 4 - 2;
        } else {
            sb.append(telNo.substring(0,3));
            exNoLen = telNo.length() - 4 - 3;
        }
        
        for (int i=0; i<exNoLen; i++) {
            sb.append(MASKING_CHAR);
        }
        sb.append(telNo.substring(telNo.length()-4));
        return sb.toString();
    }
    
    /**
     * 이메일 마스킹처리
     * 아이디 뒤 두자리 (ex:***in@gmail.com)
     *  
     * @param email
     * @return String
     */
    public static String maskingEmail(String email) {
        if (StringUtils.isEmpty(email)) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        int idx = email.indexOf('@')-2;
        if (idx<1) {
            return email;
        }
        for (int i=0; i<idx; i++) {
            sb.append(MASKING_CHAR);
        }
        sb.append(email.substring(idx));
        return sb.toString();
    }
    
    /**
     * 주소 마스킹처리 
     * 문자 10자리 이휴 주소에 대해 고정자리(5자리) 마스킹 (ex:서울시 강남구 역삼****)
     *  
     * @param addr
     * @return String
     */
    public static String maskingAddress(String addr) {
        if (StringUtils.isEmpty(addr)) {
            return "";
        }
        
        StringBuilder sb = new StringBuilder();
        if (addr.length() < 10){
            sb.append(addr);
        } else {
            sb.append(addr.substring(0,10));
        }
        
        for (int i=0; i<5; i++) {
            sb.append(MASKING_CHAR);
        }
        return sb.toString();
    }

    /**
     * 주소 마스킹처리 
     * 상세 주소는 전체 마스킹 (ex:********)
     *  
     * @param addr
     * @return String
     */
    public static String maskingAddrDtl(String addr) {
        if (StringUtils.isEmpty(addr)) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i=0; i<addr.length(); i++) {
            sb.append(MASKING_CHAR);
        }
        return sb.toString();
    }
    
    /**
     * 성명 마스킹처리
     * 성명 뒤에서 두번째 자리(ex:홍*동)
     *  
     * @param name
     * @return String
     */
    public static String maskingName(String name) {
        if (StringUtils.isEmpty(name)) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        int iNameCnt = name.length();
        for (int i=0; i<iNameCnt; i++) {
           //2015.10.26 임지후 *이름의 첫번째 자리가 마스킹되도록 변경 
            if((iNameCnt > 2 && name.length()-2 != i) || (iNameCnt == 2 && i == 0)){
                sb.append(name.charAt(i));
            } else {
                sb.append(MASKING_CHAR);
            }
           
            /*if (name.length()-2 != i) {
                sb.append(name.charAt(i));
            } else {
                sb.append(MASKING_CHAR);
            }*/
        }
        return sb.toString();
    }
    
    /**
     * 계좌번호마스킹처리
     * 전체(ex:************)
     *  
     * @param acntNo
     * @return String
     */
    public static String maskingAccountNo(String acntNo) {
        if (StringUtils.isEmpty(acntNo)) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i=0; i<acntNo.length(); i++) {
            sb.append(MASKING_CHAR);
        }
        return sb.toString();
    }

    /**
     * 주빈번호마스킹처리
     * 전체(ex:7811301******)
     *  
     * @param acntNo
     * @return String
     */
    public static String maskingJuminNo(String juminNo) {
        if (StringUtils.isEmpty(juminNo)) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(juminNo.substring(0,7));
        for (int i=7; i<juminNo.length(); i++) {
            sb.append(MASKING_CHAR);
        }
        return sb.toString();
    }
    
    /**
     * 생년월일마스킹처리
     * 전체(ex:********)
     *  
     * @param birthYmd
     * @return String
     */
    public static String maskingBirthYmd(String birthYmd) {
        if (StringUtils.isEmpty(birthYmd)) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i=0; i<birthYmd.length(); i++) {
            sb.append(MASKING_CHAR);
        }
        return sb.toString();
    }
    
    /**
     * 크로스사이트스크립팅 방지 메소드
     * cleanXSS
     *  
     * @param value
     * @return String
     */
    public static String cleanXSS(String value) {
        //XSS스크립팅 활성화여부에 따라 체크여부 판단
        if(!"true".equals(BaseUtils.getConfiguration("xss.scripting.check.enabled"))) {
            return value;
        }
        
        if(StringUtils.isEmpty(value)) {
            return value;
        }
        value = value.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
//        value = value.replaceAll("\\(", "& #40;").replaceAll("\\)", "& #41;");
//        value = value.replaceAll("&", "&amp;");
        value = value.replaceAll("'", "&#39;");
        value = value.replaceAll("eval\\((.*)\\)", "");
        value = value.replaceAll("[\\\"\\\'][\\s]*javascript:(.*)[\\\"\\\']", "\"\"");
        value = value.replaceAll("script", " _script_ ");
        value = value.replaceAll("SCRIPT", " _SCRIPT_ ");
        return value;
    }

    /**
     * 크로스사이트스크립팅 방지를 위해 변환된 value 중 일부만 다시 원복하는 메소드
     *  
     * @param value
     * @return String
     */
    public static String restoreSpecialChar(String value) {
        if(StringUtils.isEmpty(value)) {
            return value;
        }
        value = value.replaceAll("&lt;", "<").replaceAll("&gt;", ">");
//        value = value.replaceAll("&#40;", "\\(" ).replaceAll( "&#41;", "\\)");
        value = value.replaceAll("&#39;", "'");
        return value;
    }
    
    /**
     * 대문자와 '_'조합으로 된 문자열을 카멜 명명규칙으로 변환한다.
     * ex)   AB5C1_D6EF2_G7HI3_JKL4_M8NO =>ab5c1D6ef2G7hi3Jkl4M8no
     * @param uppStr
     * @return String
     */
    public static String chngUppStrToCamelStr(String uppStr) {
        StringBuffer sb = new StringBuffer();
        if(StringUtils.isEmpty(uppStr)) {
            return "";
        }
        String[] tokenArr = StringUtils.tokenizeToStringArray(uppStr, "_");
        if(tokenArr!=null && tokenArr.length>0) {
            for(int i=0; i<tokenArr.length;i++) {
                if(i==0) {
                    sb.append(tokenArr[i].toLowerCase());
                } else {
                    sb.append(StringUtils.firstLetterUpper(tokenArr[i].toLowerCase()));
                }
            }
        }
        return sb.toString();
    }
    
    /**
     * 카멜명명규칙으로 정의된 문자열을 대문자+'_'의 구조로 변경한다.
     *  ex) ab5c1D6ef2G7hi3Jkl4M8no => AB5C1_D6EF2_G7HI3_JKL4_M8NO
     * @param camelStr
     * @return String
     */
    public static String chngCamelStrToUppStr(String camelStr) {
        StringBuffer sb = new StringBuffer();
        if(StringUtils.isEmpty(camelStr)) {
            return "";
        }
        char[] charArr = camelStr.toCharArray();
        if(charArr !=null && charArr.length>0) {
            for(int i=0; i<charArr.length;i++) {
                if(charArr[i] >=65 && charArr[i] <=90) {//대문자라면
                    sb.append("_");
                    //sb.append(charArr[i]);
                }                 
                /*else {
                   // sb.append(charArr[i]);
                }*/
              //2015.10.13 jihooyim code inspector 점검 수정 (if/else문 body에 같은 것 반복 금지)
                sb.append(charArr[i]);
            }
        }
        return sb.toString().toUpperCase();
    }
    
    /**
     * 시간의 포맷이 맞는지 체크하는 메소드
     * 00:00:00~24:00:00  일경우는 true return, 그 외에는 false
     * 
     * ex) 시분초 사이에 ':'이 들어간 경우나 아예 ':'이 없는 6자리문자를 제외하고는 모두 에러 포맷임. 
     *  
     * @param hhmmss
     * @return boolean
     */
    public static boolean isTimeFormat(String hhmmss) {
        int hour = 0;
        int min = 0;
        int sec = 0;
        if(StringUtils.isEmpty(hhmmss)) {
            return false;
        }
        try {
            if(hhmmss.length() == 6) {//시간이 ':'없이 숫자만 들어온 경우
                hour = Integer.parseInt(hhmmss.substring(0, 2));
                min = Integer.parseInt(hhmmss.substring(2, 4));
                sec = Integer.parseInt(hhmmss.substring(4, 6));
            } else {//6자리가 아니면서 ':'이 들어가 있는 경우임. 
                if(hhmmss.indexOf(":")!= -1 ) {//':'이 들어간 경우
                    String tempTime = hhmmss.replaceAll(":", "");
                    if(tempTime.length() !=6) {
                        return false;
                    } else {
                        hour = Integer.parseInt(tempTime.substring(0, 2));
                        min = Integer.parseInt(tempTime.substring(2, 4));
                        sec = Integer.parseInt(tempTime.substring(4, 6));
                    }
                } else {
                   return false;
                }
            }
        } catch(NumberFormatException e) {
            return false;//문자열이 들어가 있는 경우에는 Exception을 catch하여 false로 return함. 
        }
        
        if(hour >= 0 && hour <= 23) {
            if(min >= 0 && min <= 59) {
                if(sec >=0 && min <=59) {
                    return true;
                }
            }
        } else if(hour == 24 && min == 0 && sec == 0) {
            return true;
        }
        return false;
    }
    
    /**
     *   업로드된 파일의 root경로가 필요한 경우(업로드파일이 존재하는 root경로)
     *   개발&운영 : /app_src/upload/permanent
     *   로컬 : C:\\projects\\hpc\\workspace\\runtime\\upload\\permanent
     * @return String
     */
    public static String getUploadFilePath() {
        return BaseUtils.getConfiguration("file.upload.root.folder."+BaseUtils.getRuntimeMode());
    }
    
    /**
     * 본프로젝트에서는 Temp폴더는 사용하지 않음. 2015.03.13 By PSI
     * 임시로 저장할 파일의 root경로가 필요한 경우  
     * 개발&운영 : /app_src/upload/temporary
     * 로컬 : C:\\projects\\hpc\\workspace\\runtime\\upload\\temporary
     * @return String
     */
    @Deprecated 
    public static String getTempFilePath() {
        return BaseUtils.getConfiguration("file.upload.temp.folder."+BaseUtils.getRuntimeMode());
  
    }
    
    /**
     * 파일 업로드를 통해서 파일을 저장하는 경우가 아니라 서버에서 생성된 파일을 저장하는 경우에도
     * 업로드프로세스와 같은 프로세스를 탈 수 있도록 하기 위해 다음과 같은 메소드를 생성함. 
     *  
     * @param file
     * @return Map<String,String>
     */
    public static Map<String, String> createFileForDownload(File file) {
        HpcUploadedFileManager manager = (HpcUploadedFileManager)ComponentRegistry.lookup(ServiceConstants.UPLOADED_FILE_MANAGER);
        return manager.storeFilesForDownload(file);
    }
    /**
     *  WAS별 Instance ID가 필요한데 그 자릿수가 10byte 이내의 ID가 필요한 경우에 사용하도록 하기 위한 메소드임.
     *  BaseUtils.getFwkId()가 localhost인 경우에는 localhost가 return됨.
     *  그외의 경우에는 축약된 FWK ID가 return됨
     * @return 축약된 FWK ID
     */
    public static String getHpcWasInstanceId() {
        String fwkId = BaseUtils.getFwkId();
        String runtimeMode = BaseUtils.getRuntimeMode();
        
        if("localhost".equals(fwkId)) {
            return fwkId;
        } else {
            StringBuffer sb = new StringBuffer();
            sb.append(runtimeMode);
            try {
                int index = fwkId.lastIndexOf("_");
                sb.append(fwkId.substring(index+1));
            }catch(StringIndexOutOfBoundsException e) {
                sb.append("Unknown");
            }
            return sb.toString();
        }
    }
    
    /**
     * 파일 이동을 실시하기 위한 API 
     *  
     * @param originFile 파일명까지 포함되어 있는 경로 (ex. /app/attach/temporary/HPC_SAMPLE_SAM_20150202.txt)
     * @param destDir 파일을 이동시키고자 하는 경로명 ( ex. /app/attach/permanent)
     * @param createDestDir 이동시키고자 하는 경로가 생성되어 있지 않을 경우 경로 생성여부
     */
    public static void moveFile(String originFile, String destDir, boolean createDestDir) {
        File oriFile = null;
        File moveFile = null;
        try {
            oriFile = new File(originFile);
            moveFile = new File(destDir);
            if(!oriFile.exists()) {
                throw new BizRuntimeException("SKFE5004", new String[]{"File move", "No File "});
            }
            FileUtils.moveFileToDirectory(oriFile, moveFile, createDestDir);
        } catch (IOException e) {
            throw new BizRuntimeException("SKFE5010", new String[]{"File move"}, e);
        }
        
    }
    
    /**
     * 특정 Dir있는 파일을 다른 Dir로 옮기기 위한 API
     *  
     * @param originDir 옮기고자 하는 파일들이 들어 있는 경로명  ( ex. /app/attach/temporary)
     * @param destDir 파일을 이동시키고자 하는 경로명 ( ex. /app/attach/permanent)
     * @param createDestDir  이동시키고자 하는 경로가 생성되어 있지 않을 경우 경로 생성여부
     */
    public static void moveDirectoryToDirectory(String originDir, String destDir, boolean createDestDir) {
        File oriDir = null;
        File moveDir = null;
        try {
            oriDir = new File(originDir);
            moveDir = new File(destDir);
            FileUtils.moveDirectoryToDirectory(oriDir, moveDir, createDestDir);
        } catch (IOException e) {
            throw new BizRuntimeException("SKFE5010", new String[]{"File move"}, e);
        }
    }
    
    /**
     * 오라클의 에러코드를 입력하면 오라클 Exception ID포맷으로 구성해서 return함. 
     *  
     * @param errorCd
     * @return String
     */
    public static String convertOraId(int errorCd) {
        StringBuffer sb = new StringBuffer();
        sb.append("ORA-");
        sb.append(StringUtils.lpad(errorCd+"", 5, "0"));
        return sb.toString();
    }
    
    /**
     * Exception의 에러메시지 중 오라클의  에러코드부터 메시지라인만 추출하여 return함.
     *  
     * @param errMsg
     * @param errCd
     * @return String
     */
    public static String getOraErrorMsg(int errCd, String errMsg) {
        String oraErrId = convertOraId(errCd);
        int beginIndex = 0;
        beginIndex = errMsg.indexOf(oraErrId);
        if(beginIndex != -1) {
            return errMsg.substring(beginIndex);
        } else {
            return "";
        }
    }
	/**
	 * DB에 등록된 모든 WAS Instance의 정보를 취득한다.
	 * @return
	 */
	private static List<WasInstance> getAllWasInstance() {
		IWasInstanceManager manager = (IWasInstanceManager) ComponentRegistry.lookup(ServiceConstants.WAS_INSTANCE);
		return manager.getAllWasInstances();
	}

	 public static final int UTF_8 = 0;
	 public static final int MS949 = 1;
	 public static final int EUC_KR = 2;
	 /**
     * 주어진 (한글을 포함한) 문자열을 정해진 크기에 맞춰 나누어 문자열 배열을 얻는다.
     * 지정된 Encoding으로 자른 바이트의 한글이 깨진 경우에는 그 전 글자까지만 return함. 
     * @param str 원본 문자열<br>source string
     * @param length 분할사이즈<br>length to divide by
     * @param encodingNo 인코딩 하고자 하는 번호(UTF-8:0, MS949:1) ex)HpcUtils.UTF_8, HpcUtils.MS949, HpcUtils.EUC_KR
     * @return String 
     */
    public static String splitKorString(String str, int length, int encodingNo) {
        StringBuffer sb = new StringBuffer();
        if(StringUtils.isEmpty(str)) {//입력값 체크
            return str;
        }
        try {
            byte[] textArr = str.getBytes(0==encodingNo?DmsConstants.UTF_8:DmsConstants.MS949); 
            int textLen = textArr.length;
            if(length >=  textLen) {//잘라야 하는 길이보다 실제길이가 짧은 경우는 return
                return str;
            } else {
                byte[] textArrNew = new byte[length];
                System.arraycopy(textArr,0,  textArrNew, 0, length);
                switch(encodingNo) {
                    case 0 ://UTF-8인경우
                        for(int i=0; i<length;) {
                            byte b = textArrNew[i];
                            if((b &0x80) == 0x80) {//한글을 바이트로 취득할 경우 0x7F 이후 범위를 가지게 되므로 비트연산자로 처리하면 한글의 경우는 늘 0x80이 나오게 됨   
                                if(length-3 >= i) {
                                    sb.append(new String(textArrNew, i, 3, DmsConstants.UTF_8));    
                                    i+=2;
                                }
                            } else {
                                sb.append((char)b);
                            }
                            ++i;
                        }
                        break;
                    default ://MS949, EUC-KR인경우
                        for(int i=0; i<length;) {
                            byte b = textArrNew[i];
                            if((b &0x80) == 0x80) {
                                if(length-2 >= i) {
                                    sb.append(new String(textArrNew, i, 2, DmsConstants.MS949));    
                                    i+=1;
                                }
                            } else {
                                sb.append((char)b);
                            }
                            ++i;
                        }
                        break;
                }
                return sb.toString();
            }
        } catch (Exception e) {
            throw new BizRuntimeException("SKFE5010", new String[]{"Split string"}, e);
        } 
    }
    
    /**
    * 주어진 (한글을 포함한) 문자열을 정해진 크기에 맞춰 나누어 문자열 배열을 얻는다.
    * 지정된 Encoding으로 자른 바이트의 한글이 깨진 경우에는 그 전 글자까지만 return함. 
    * @param str 원본 문자열<br>source string
    * @param start 시작인덱스<br>start index
    * @param length 분할사이즈<br>length to divide by
    * @param encodingNo 인코딩 하고자 하는 번호(UTF-8:0, MS949:1) ex)HpcUtils.UTF_8, HpcUtils.MS949, HpcUtils.EUC_KR
    * @return String 
    */
   public static String splitKorString(String str, int start, int length, int encodingNo) {
       StringBuffer sb = new StringBuffer();
       if(StringUtils.isEmpty(str)) {//입력값 체크
           return str;
       }
       try {
           byte[] textArr = str.getBytes(0==encodingNo?DmsConstants.UTF_8:DmsConstants.MS949); 
           int textLen = textArr.length;
           
           if(start+1 >  textLen) {//잘라야 하는 길이보다 실제길이가 짧은 경우는 return
               return "";
           } else {
               
               switch(encodingNo) {
                   case 0 ://UTF-8인경우
                       int idx = 0;
                       while(idx<start) {
                           byte b = textArr[idx];
                           if((b &0x80) == 0x80) idx+=2; //한글의 경우는 늘 0x80이 나오게 됨   
                           ++idx;
                       }
                       if ( idx+length-1 >= textLen) length =  textLen-idx; 
                       byte[] textArrNew = new byte[length];
                       System.arraycopy(textArr,  idx,  textArrNew, 0, length);
                       
                       for(int i=0; i<length;) {
                           byte b = textArrNew[i];
                           if((b &0x80) == 0x80) { //한글의 경우는 늘 0x80이 나오게 됨    
                               if(length-3 >= i) {
                                   sb.append(new String(textArrNew, i, 3, DmsConstants.UTF_8));    
                                   i+=2;
                               }
                           } else {
                               sb.append((char)b);
                           }
                           ++i;
                       }
                       break;
                       
                   default ://MS949, EUC-KR인경우
                       idx = 0;
                       while(idx<start) {
                           byte b = textArr[idx];
                           if((b &0x80) == 0x80) idx++; //한글의 경우는 늘 0x80이 나오게 됨   
                           ++idx;
                       }
                       if ( idx+length-1 >= textLen) length =  textLen-idx; 
                       textArrNew = new byte[length];
                       System.arraycopy(textArr,  idx,  textArrNew, 0, length);
                       
                       for(int i=0; i<length;) {
                           byte b = textArrNew[i];
                           if((b &0x80) == 0x80) {
                               if(length-2 >= i) {
                                   sb.append(new String(textArrNew, i, 2, DmsConstants.MS949));    
                                   i+=1;
                               }
                           } else {
                               sb.append((char)b);
                           }
                           ++i;
                       }
                       break;
               }
               return sb.toString();
           }
       } catch (Exception e) {
           throw new BizRuntimeException("SKFE5010", new String[]{"Split string"}, e);
       } 
   }
    
    /**
     * Bundle Language Properties 취득 API
     *  
     * @param key
     * @return String
     */
    public static String getLangMsg(String key) {
        HpcResourceBundle hrb = (HpcResourceBundle)ComponentRegistry.lookup("nc.hpc.msgResourceBundle");
        String value = "";
        try {
            value = hrb.getBundleMsg(key);
        } catch (MissingResourceException e) {//정의된 key가 없는 경우에는 Exception을 발생시키지 않고 key 자체를return한다. 
            //2015.10.13 jihooyim code inspector 점검 수정 (01-2.오류 메시지 통한 정보 노출(printStackTrace))
            value = key;
        }
        return value;
    }
    
    
    public static final int LOCALE_KO=0;
    public static final int LOCALE_EN=1;
    public static final int LOCALE_JP=2;
    public static final int LOCALE_CN=3;
    /**
     * Bundle Language Properties 취득 API
     *  
     * @param locale ex)HpcUtils.LOCALE_KO:0, HpcUtils.LOCALE_EN:1, HpcUtils.LOCALE_JP:2, HpcUtils.LOCALE_CN:3
     * @param key
     * @return String
     */
    public static String getLangMsg(int locale, String key) {
        HpcResourceBundle hrb = (HpcResourceBundle)ComponentRegistry.lookup("nc.hpc.msgResourceBundle");
        String value = "";
        try {
            switch (locale) {
                case 1:    
                    value = hrb.getBundleMsg(DmsConstants.EN, key);
                    break;
                case 2:    
                    value = hrb.getBundleMsg(DmsConstants.JP, key);
                    break;
                case 3:    
                    value = hrb.getBundleMsg(DmsConstants.CN, key);
                    break;
                default:
                    value = hrb.getBundleMsg(DmsConstants.KO, key);
                    break;
            }
        } catch (MissingResourceException e) {//정의된 key가 없는 경우에는 Exception을 발생시키지 않고 key 자체를return한다.            
            value = key;
        }
        return value;
    }
    
    /**
     * 로케일에 맞는 현재 일자를 패턴에 맞게 return한다.
     * 
     * <type>yyyyMMdd</type><br>
     * <type>yyyyMMddHH </type><br>
     * <type>yyyyMMddHHmm</type><br>
     * <type>yyyyMMddHHmmSS</type><br>
     * <type>yyyyMMddHHmmssSSS</type><br>
     * <type>
     *  yyyy.MMMMM.dd EEEEEE hh:mm aaa
     *  ex) Locale.KOREA :2015.2월.04 수요일 10:48 오전, 
     *        Locale.ENGLISH : 2015.February.04 Wednesday 10:48 AM
     *        Locale.JAPAN : 2015.2月.04 水曜日 10:48 午前]
     *        Locale.CHINA :2015.二月.04 星期三 10:48 上午
     *  </type><br>
     * @param localeNo, ex)HpcUtils.LOCALE_KO:0, HpcUtils.LOCALE_EN:1, HpcUtils.LOCALE_JP:2, HpcUtils.LOCALE_CN:3
     * @return String
     */
    
    public static String getCurrentDate(int localeNo, String pattern) {
        Locale locale = null;
        switch (localeNo) {
            case 1:    
                locale = Locale.ENGLISH;
                break;
            case 2:    
                locale = Locale.JAPAN;
                break;
            case 3:    
                locale = Locale.CHINA;
                break;
            default:
                locale = Locale.KOREA;
                break;
        }
        
        FastDateFormat fdf = FastDateFormat.getInstance (pattern, locale);
        
        return fdf.format(new Date());
    }
    
    /**
     * 로케일에 맞는 현재 일자를 패턴에 맞게 return한다.
     * 
     * <type>yyyyMMdd</type><br>
     * <type>yyyyMMddHH </type><br>
     * <type>yyyyMMddHHmm</type><br>
     * <type>yyyyMMddHHmmSS</type><br>
     * <type>yyyyMMddHHmmssSSS</type><br>
     * <type>
     *  yyyy.MMMMM.dd EEEEEE hh:mm aaa
     *  ex) Locale.KOREA :2015.2월.04 수요일 10:48 오전, 
     *        Locale.ENGLISH : 2015.February.04 Wednesday 10:48 AM
     *        Locale.JAPAN : 2015.2月.04 水曜日 10:48 午前]
     *        Locale.CHINA :2015.二月.04 星期三 10:48 上午
     *  </type><br>
     * @param locale
     * @return String
     */
    public static String getCurrentDate(Locale locale, String pattern) {
        FastDateFormat fdf = FastDateFormat.getInstance (pattern, locale);
        return fdf.format(new Date());
    }
    
	public static void main (String[]args) {
//	    System.out.println("미국 : ["+HpcUtils.getCurrentDate(HpcUtils.LOCALE_EN, "yyyyMMddHHmmssSSS")+"]");
//	    System.out.println("한국 : ["+HpcUtils.getCurrentDate(HpcUtils.LOCALE_KO, "yyyyMMddHHmmssSSS")+"]");
//	    System.out.println("일본 : ["+HpcUtils.getCurrentDate(HpcUtils.LOCALE_JP, "yyyyMMddHHmmssSSS")+"]");
//	    System.out.println("중국 : ["+HpcUtils.getCurrentDate(HpcUtils.LOCALE_CN, "yyyyMMddHHmmssSSS")+"]");
	    
	    
//	       System.out.println("한국 : ["+HpcUtils.getCurrentDate(HpcUtils.LOCALE_KO, "yyyy.MMMMM.dd EEEEEE hh:mm aaa")+"]");
//	       System.out.println("미국 : ["+HpcUtils.getCurrentDate(HpcUtils.LOCALE_EN, "yyyy.MMMMM.dd EEEEEE hh:mm aaa")+"]");
//	       System.out.println("일본 : ["+HpcUtils.getCurrentDate(HpcUtils.LOCALE_JP, "yyyy.MMMMM.dd EEEEEE hh:mm aaa")+"]");
//	       System.out.println("중국 : ["+HpcUtils.getCurrentDate(HpcUtils.LOCALE_CN, "yyyy.MMMMM.dd EEEEEE hh:mm aaa")+"]");
	    
	    //System.out.println("["+HpcUtils.rpadByte("가나 다", '_', 9)+"]");
//	    String plainText = "심상준";
//	    
//	    String encText = encodeByAES(plainText);
//	    System.out.println("encText["+encText+"]");
//	    
//	    String decText = decodeByAES(encText);
//        System.out.println("decText["+decText+"]");
        
	  //2015.10.13 jihooyim code inspector 점검 수정 (02-2.제거되지 않고 남은 디버그 코드(print))
       // System.out.println("maskingJuminNo["+maskingJuminNo("7811301675719")+"]");
        //System.out.println("maskingBirthYmd["+maskingBirthYmd("19781130")+"]");
	    //System.out.println("이름변경 : ["+HpcUtils.maskingName("임지후")+"]");
	    //System.out.println("이름변경 : ["+HpcUtils.maskingName("임치")+"]");
	    //System.out.println("이름변경 : ["+HpcUtils.maskingName("황우슬혜")+"]");
	    //System.out.println("이름변경 : ["+HpcUtils.maskingName("황우슬혜요")+"]");
	    
	}
}


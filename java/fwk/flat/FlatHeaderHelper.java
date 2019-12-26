package fwk.flat;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import nexcore.framework.core.component.IMethodMetaData;
import nexcore.framework.core.data.Channel;
import nexcore.framework.core.data.IChannel;
import nexcore.framework.core.data.IOnlineContext;
import nexcore.framework.core.data.IResultMessage;
import nexcore.framework.core.data.IRuntimeContext;
import nexcore.framework.core.data.ITerminal;
import nexcore.framework.core.data.ITransaction;
import nexcore.framework.core.data.OnlineContext;
import nexcore.framework.core.data.RuntimeContext;
import nexcore.framework.core.data.Terminal;
import nexcore.framework.core.data.Transaction;
import nexcore.framework.core.data.user.UserInfo;
import nexcore.framework.core.log.LogManager;
import nexcore.framework.core.prototype.IMessageCoded;
import nexcore.framework.core.transform.FlatConstants;
import nexcore.framework.core.transform.FlatUtil;
import nexcore.framework.core.util.BaseUtils;
import nexcore.framework.core.util.ByteArrayWrap;
import nexcore.framework.core.util.DateUtils;
import nexcore.framework.core.util.NexCoreServiceUtil;
import nexcore.framework.core.util.PaddableDataOutputStream;
import nexcore.framework.core.util.StringUtils;
import nexcore.framework.coreext.pojo.biz.base.BizServiceHelper;
import fwk.common.CommonUtils;
import fwk.common.TrtmRsltMsg;
import fwk.common.internal.ImplCommonArea;
import fwk.common.internal.ImplFlatHeader;
import fwk.constants.DmsConstants;
import fwk.utils.HpcUtils;
import org.apache.commons.logging.Log;

/**
 * 전문 헤더 관련 헬퍼 클래스
 */
public class FlatHeaderHelper {

	private final static String RAW_LEVEL_ERROR_ID = "00000000";
	private final static char SLASH = 0x2F;
	/**
	 * 입력전문(InputStream)을 표준전문헤더 Map으로 변환
	 */
	public static Map<String, String> toHeaderMap(DataInputStream in, byte[] buff, String encoding) throws IOException {
		Map<String, String> attributes = new HashMap<String, String>();
		
		// 전문길이
		readString(in, buff, encoding, attributes, FlatHeaderSpec.WHL_MESG_LEN       );  // 전체전문길이
		readString(in, buff, encoding, attributes, FlatHeaderSpec.STND_HDR_LEN       );  // 표준헤더부길이
		
		// 거래번호
		readString(in, buff, encoding, attributes, FlatHeaderSpec.GLOB_ID            );  // 글로벌 ID
		readString(in, buff, encoding, attributes, FlatHeaderSpec.PRGS_SRNO          );  // 진행일련번호
		
		// 전송시스템정보내용
		readString(in, buff, encoding, attributes, FlatHeaderSpec.IPAD               );  // IP주소
		readString(in, buff, encoding, attributes, FlatHeaderSpec.PRCM_MAC           );  // PC MAC주소
		readString(in, buff, encoding, attributes, FlatHeaderSpec.TRN_TRNM_NO        );  // 거래단말번호
		readString(in, buff, encoding, attributes, FlatHeaderSpec.SSO_SESN_KEY       );  // SSO 세션 KEY
		readString(in, buff, encoding, attributes, FlatHeaderSpec.FRST_TRNM_CHNL_CD  );  // 최초전송채널코드
		readString(in, buff, encoding, attributes, FlatHeaderSpec.TRNM_CHNL_CD       );  // 전송채널코드
		readString(in, buff, encoding, attributes, FlatHeaderSpec.TRNM_NODE_NO       );  // 전송노드번호
		readString(in, buff, encoding, attributes, FlatHeaderSpec.MCI_TRNM_NODE_NO   );  // MCI_전송노드번호
		readString(in, buff, encoding, attributes, FlatHeaderSpec.ENV_DVCD           );  // 환경정보구분코드

		// 전문처리내용
		readString(in, buff, encoding, attributes, FlatHeaderSpec.MESG_DMND_DTTM     );  // 전문요청일시
		readString(in, buff, encoding, attributes, FlatHeaderSpec.MESG_VRSN_DVCD     );  // 전문버전구분코드
		readString(in, buff, encoding, attributes, FlatHeaderSpec.TRN_CD             );  // 거래코드
		readString(in, buff, encoding, attributes, FlatHeaderSpec.SCRN_NO            );  // 화면번호
		readString(in, buff, encoding, attributes, FlatHeaderSpec.MESG_RESP_DTTM     );  // 전문응답일시
		readString(in, buff, encoding, attributes, FlatHeaderSpec.TRN_PTRN_DVCD     );  // 거래유형구부코드

		// FLAG정보
		readString(in, buff, encoding, attributes, FlatHeaderSpec.MESG_DVCD          );  // 전문구분코드
		readString(in, buff, encoding, attributes, FlatHeaderSpec.MESG_TYCD          );  // 전문유형코드
		readString(in, buff, encoding, attributes, FlatHeaderSpec.MESG_CNTY_SRNO     );  // 전문연속일련번호
		readString(in, buff, encoding, attributes, FlatHeaderSpec.TRTM_RSLT_CD       );  // 처리결과코드 
		readString(in, buff, encoding, attributes, FlatHeaderSpec.CMPG_RELM_USE_DVCD );  // 캠페인영역사용구분코드

		// 직원정보내용
		readString(in, buff, encoding, attributes, FlatHeaderSpec.COMP_CD            );  // 회사코드
		readString(in, buff, encoding, attributes, FlatHeaderSpec.DEPT_CD            );  // 부서코드		
		readString(in, buff, encoding, attributes, FlatHeaderSpec.BR_CD              );  // 부점코드
		readString(in, buff, encoding, attributes, FlatHeaderSpec.USER_NO            );  // 사용자번호
		readString(in, buff, encoding, attributes, FlatHeaderSpec.USER_LOCALE        );  // 사용자로케일
		readString(in, buff, encoding, attributes, FlatHeaderSpec.CTI_YN        );  // 사용자로케일

		// 시재 정보
		readString(in, buff, encoding, attributes, FlatHeaderSpec.CSHN_OCRN_YN       );  // 시제발생여부
		readString(in, buff, encoding, attributes, FlatHeaderSpec.CASH_AMT           );  // 현금금액
		readString(in, buff, encoding, attributes, FlatHeaderSpec.POINT_AMT          );  //포인트금액

		// 대외거래정보내용
		readString(in, buff, encoding, attributes, FlatHeaderSpec.XTIS_CD            );  // 대외기관코드
		readString(in, buff, encoding, attributes, FlatHeaderSpec.BZWR_SVR_CD        );  // 업무서버코드
		readString(in, buff, encoding, attributes, FlatHeaderSpec.OTSD_MESG_CD       );  // 대외전문코드
		readString(in, buff, encoding, attributes, FlatHeaderSpec.OTSD_MESG_TRTM_CD  );  // 대외전문처리코드
		readString(in, buff, encoding, attributes, FlatHeaderSpec.OTSD_TRN_UNQ_NO    );  // 대외거래고유번호
		readString(in, buff, encoding, attributes, FlatHeaderSpec.OTSD_RESP_TRN_CD   );  // 대외응답거래코드
		readString(in, buff, encoding, attributes, FlatHeaderSpec.CHNL_MSG_CD        );  // 채널메시지코드
//		readString(in, buff, encoding, attributes, FlatHeaderSpec.EAI_GLOB_ID        );  // 채널메시지코드
//		readString(in, buff, encoding, attributes, FlatHeaderSpec.EAI_INTF_ID        );  // 채널메시지코드
//		readString(in, buff, encoding, attributes, FlatHeaderSpec.EAI_RECV_SVCID        );  // 채널메시지코드
		
		// FILLER
		readString(in, buff, encoding, attributes, FlatHeaderSpec.SPR_CHRS_CNTN      );  // 예비문자열내용


		// 메시지부 (최대 10건 반복)
		readString(in, buff, encoding, attributes, FlatHeaderSpec.MSG_CCNT                 );  // 메시지 건수
		int msgCcnt = parseInt(attributes, FlatHeaderSpec.MSG_CCNT);
		for(int i=1; i<=msgCcnt; i++){
			readString(in, buff, encoding, attributes, FlatHeaderSpec.MSG_CD             , i);  // 메시지코드
			readString(in, buff, encoding, attributes, FlatHeaderSpec.MSG_CNTN           , i);  // 메시지내용
			readString(in, buff, encoding, attributes, FlatHeaderSpec.EROR_OCRN_PRRM_LINE, i);  // 오류발생프로그램라인
			readString(in, buff, encoding, attributes, FlatHeaderSpec.EROR_OCRN_PRRM_NM  , i);  // 오류발생프로그램명
		}

		return attributes;
	}
	
	public static Map<String, String> toEaiHeaderMap(DataInputStream in, byte[] buff, String encoding) throws IOException {
		Map<String, String> attributes = new HashMap<String, String>();
//		byte[] b1 = new byte[FlatHeaderSpec.WHL_MESG_LEN.length()];
//    	in.readFully(b1, 0, b1.length);
//    	int whlMesgLen = Integer.parseInt(new String(b1).trim());
    	
	    // 전문길이
//		readString(attributes, FlatHeaderSpec.WHL_MESG_LEN.name(), whlMesgLen+2+"");
    	readString(in, buff, encoding, attributes, EaiFlatHeaderSpec.STD_TGRM_LEN);  //
		
		// Global ID
		readString(in, buff, encoding, attributes, EaiFlatHeaderSpec.TGRM_DDTM       );  // 
		readString(in, buff, encoding, attributes, EaiFlatHeaderSpec.TGRM_CRT_SYSNM            );  // 
		readString(in, buff, encoding, attributes, EaiFlatHeaderSpec.TGRM_CRT_NO          );  // 
		
		//진행번호
		readString(in, buff, encoding, attributes, EaiFlatHeaderSpec.TGRM_PRG_SQNO               );  // 

		// 전송시스템정보내용                             
		readString(in, buff, encoding, attributes, EaiFlatHeaderSpec.TRMS_SYS_CD           );  // 
		
		//전문정보처리
		readString(in, buff, encoding, attributes, EaiFlatHeaderSpec.REQ_RSP_DCD        );  // 
		readString(in, buff, encoding, attributes, EaiFlatHeaderSpec.TR_SYNC_DCD       );  // 
		readString(in, buff, encoding, attributes, EaiFlatHeaderSpec.ASYNC_TR_DCD  );  // 
		readString(in, buff, encoding, attributes, EaiFlatHeaderSpec.TGRM_REQ_DTM       );  // 
		
		//서비스ID정보
		readString(in, buff, encoding, attributes, EaiFlatHeaderSpec.RCVE_SVCID       );  // 
		readString(in, buff, encoding, attributes, EaiFlatHeaderSpec.RSLT_RCEV_SVCID   );  // 
		readString(in, buff, encoding, attributes, EaiFlatHeaderSpec.EAI_INTF_ID           );  // 
                                                      
		//응답결과정보                                   
		readString(in, buff, encoding, attributes, EaiFlatHeaderSpec.TGRM_RSP_DTM     );  // 
		readString(in, buff, encoding, attributes, EaiFlatHeaderSpec.TGRM_PRCRSLT_DCD     );  // 

		//장애정보
		readString(in, buff, encoding, attributes, EaiFlatHeaderSpec.OBS_SYS_ID             );  // 
		readString(in, buff, encoding, attributes, EaiFlatHeaderSpec.TGRM_ERR_MSG_CD            );  // 

		//기타정보
		readString(in, buff, encoding, attributes, EaiFlatHeaderSpec.TGRM_VER_NO     );  // 
		readString(in, buff, encoding, attributes, EaiFlatHeaderSpec.LANG_DCD     );  // 
		readString(in, buff, encoding, attributes, EaiFlatHeaderSpec.TEST_DCD     );  // 		
		readString(in, buff, encoding, attributes, EaiFlatHeaderSpec.RSR     );  // 		
		
                                                      
		// FLAG정보      
		if("9".equals(attributes.get(EaiFlatHeaderSpec.TGRM_PRCRSLT_DCD.name()))) {
			readString(in, buff, encoding, attributes, EaiFlatHeaderSpec.MSG_CD          );  // 
			readString(in, buff, encoding, attributes, EaiFlatHeaderSpec.MSG_ID          );  // 
			readString(in, buff, encoding, attributes, EaiFlatHeaderSpec.PNP_MSG     );  // 
			readString(in, buff, encoding, attributes, EaiFlatHeaderSpec.APPD_MSG       );  // 
			readString(in, buff, encoding, attributes, EaiFlatHeaderSpec.ERR_TRRY );  // 
		}

		// 입출력개별부                                   
//		readString(in, buff, encoding, attributes, EaiFlatHeaderSpec.DATA_CD            );  // 
//		readString(in, buff, encoding, attributes, EaiFlatHeaderSpec.DATA_LNTH            );  //
		return attributes;
	}
	
	
	
	
	
	/**
	 * 표준전문헤더 객체를 Map으로 변환.
	 */
	public static Map<String, String> toHeaderMap(Map<String, String> map, ImplFlatHeader entity) {
		if (entity != null && map != null) {
			// 전문길이
			put(map, FlatHeaderSpec.WHL_MESG_LEN         , entity.getWhlMesgLen());  // 전체전문길이
			put(map, FlatHeaderSpec.STND_HDR_LEN         , entity.getStndHdrLen());  // 표준헤더부길이
			
			// 거래번호
			put(map, FlatHeaderSpec.GLOB_ID              , entity.getGlobId()); // 글로벌 ID
			put(map, FlatHeaderSpec.PRGS_SRNO            , entity.getPrgsSrno()); // 진행일련번호
			
			// 전송시스템정보내용
			put(map, FlatHeaderSpec.IPAD                 , entity.getIpad()); // IP주소
			put(map, FlatHeaderSpec.PRCM_MAC             , entity.getPrcmMac()); // PC MAC주소
			put(map, FlatHeaderSpec.TRN_TRNM_NO          , entity.getTrnTrnmNo()); // 거래단말번호
			put(map, FlatHeaderSpec.SSO_SESN_KEY         , entity.getSsoSesnKey()); // SSO 세션 KEY
			put(map, FlatHeaderSpec.FRST_TRNM_CHNL_CD    , entity.getFrstTrnmChnlCd()); // 최초전송채널코드
			put(map, FlatHeaderSpec.TRNM_CHNL_CD         , entity.getTrnmChnlCd()); // 전송채널코드
			put(map, FlatHeaderSpec.TRNM_NODE_NO         , entity.getTrnmNodeNo()); // 전송노드번호
			put(map, FlatHeaderSpec.MCI_TRNM_NODE_NO     , entity.getMciTrnmNodeNo()); // MCI 전송노드번호
			put(map, FlatHeaderSpec.ENV_DVCD             , entity.getEnvDvcd()); // 환경정보구분코드

			// 전문처리내용
			put(map, FlatHeaderSpec.MESG_DMND_DTTM       , entity.getMesgDmndDttm()); // 전문요청일시
			put(map, FlatHeaderSpec.MESG_VRSN_DVCD       , entity.getMesgVrsnDvcd()); // 전문버전구분코드
			put(map, FlatHeaderSpec.TRN_CD               , entity.getTrnCd()); // 거래코드
			put(map, FlatHeaderSpec.SCRN_NO              , entity.getScrnNo()); // 화면번호
			put(map, FlatHeaderSpec.MESG_RESP_DTTM       , entity.getMesgRespDttm()); // 전문응답일시
			put(map, FlatHeaderSpec.TRN_PTRN_DVCD       , entity.getTrnPtrnDvcd()); //거래유형구분코드				

			// FLAG정보
			put(map, FlatHeaderSpec.MESG_DVCD            , entity.getMesgDvcd()); // 전문구분코드
			put(map, FlatHeaderSpec.MESG_TYCD            , entity.getMesgTycd()); // 전문유형코드
			put(map, FlatHeaderSpec.MESG_CNTY_SRNO       , entity.getMesgCntySrno()); // 전문연속일련번호
			put(map, FlatHeaderSpec.TRTM_RSLT_CD         , entity.getTrtmRsltCd()); // 처리결과코드
			put(map, FlatHeaderSpec.CMPG_RELM_USE_DVCD   , entity.getCmpgRelmUseDvcd()); // 캠페인영역사용구분코드

			// 직원정보내용
			put(map, FlatHeaderSpec.COMP_CD              , entity.getCompCd()); // 은행코드
			put(map, FlatHeaderSpec.DEPT_CD                , entity.getDeptCd()); // 부점코드
			put(map, FlatHeaderSpec.BR_CD                , entity.getBrCd()); // 부점코드
			put(map, FlatHeaderSpec.USER_NO              , entity.getUserNo()); // 사용자번호
			put(map, FlatHeaderSpec.USER_LOCALE          , entity.getUserLocale()); // 사용자번호
			put(map, FlatHeaderSpec.CTI_YN          , entity.getCtiYn()); // CTI여부
			

			// 시재 정보
			put(map, FlatHeaderSpec.CSHN_OCRN_YN         , entity.getCshnOcrnYn()); // 시제발생여부
			put(map, FlatHeaderSpec.CASH_AMT             , entity.getCashAmt()+""); // 결제금액
			put(map, FlatHeaderSpec.POINT_AMT             , entity.getPointAmt()+""); //포인트 금액
			

			// 대외거래정보내용
			put(map, FlatHeaderSpec.XTIS_CD              , entity.getXtisCd()); // 대외기관코드
			put(map, FlatHeaderSpec.BZWR_SVR_CD          , entity.getBzwrSvrCd()); // 업무서버코드
			put(map, FlatHeaderSpec.OTSD_MESG_CD         , entity.getOtsdMesgCd()); // 대외전문코드
			put(map, FlatHeaderSpec.OTSD_MESG_TRTM_CD    , entity.getOtsdMesgTrtmCd()); // 대외전문처리코드
			put(map, FlatHeaderSpec.OTSD_TRN_UNQ_NO      , entity.getOtsdTrnUnqNo()); // 대외거래고유번호
			put(map, FlatHeaderSpec.OTSD_RESP_TRN_CD     , entity.getOtsdRespTrnCd()); // 대외응답거래코드
			put(map, FlatHeaderSpec.CHNL_MSG_CD          , entity.getChnlMsgCd()); // 채널메시지코드
//			put(map, FlatHeaderSpec.EAI_GLOB_ID          , entity.getEaiGlobId()); // EAI 글로벌 코드
//			put(map, FlatHeaderSpec.EAI_INTF_ID          , entity.getEaiIntfId()); // EAI 인터페이스 코드
//			put(map, FlatHeaderSpec.EAI_RECV_SVCID          , entity.getEaiRecvSvcid()); // EAI로부터 결과수신코드

			// FILLER
			put(map, FlatHeaderSpec.SPR_CHRS_CNTN        , entity.getSprChrsCntn()); // 예비문자열내용

			// 메시지부 - 변환하지 않음.

			// 데이타헤더 - 변환하지 않음.
		}
		return map;
	}
	
	/**
	 * Map을 표준전문헤더 객체로 변환
	 * 
	 * @return 표준전문헤더
	 */
	public static ImplFlatHeader toHeader(Map<String, String> map, ImplFlatHeader entity) {
		return toHeader(map, entity, false);
	}
	
	/**
	 * Map을 표준전문헤더 객체로 변환
	 * 
	 * @param map 표준전문헤더 Map
	 * @param entity 표준전문헤더 객체
	 * @param parseMsgList 메시지부를 표준전문헤더 객체에 설정여부
	 * @return 표준전문헤더 객체
	 */
	public static ImplFlatHeader toHeader(Map<String, String> map, ImplFlatHeader entity, boolean parseMsgList) {
		// 전문길이
		entity.setWhlMesgLen  		    (removeInt	    (map, FlatHeaderSpec.WHL_MESG_LEN)); 			// 전체전문길이
		entity.setStndHdrLen	        (removeInt	    (map, FlatHeaderSpec.STND_HDR_LEN)); 		    // 표준헤더부길이
		
		// 거래번호
		entity.setGlobId		        (removeString	(map, FlatHeaderSpec.GLOB_ID)); 		    // 글로벌 ID
		entity.setPrgsSrno		        (removeInt	    (map, FlatHeaderSpec.PRGS_SRNO)); 		    // 진행일련번호
		
		// 전송시스템정보내용
		entity.setIpad  		        (removeString	(map, FlatHeaderSpec.IPAD)); 		    // IP주소
		entity.setPrcmMac		        (removeString	(map, FlatHeaderSpec.PRCM_MAC)); 		    // PC MAC주소
		entity.setTrnTrnmNo		        (removeString	(map, FlatHeaderSpec.TRN_TRNM_NO)); 		    // 거래단말번호
		entity.setSsoSesnKey		    (removeString	(map, FlatHeaderSpec.SSO_SESN_KEY)); 		    // SSO 세션 KEY
		entity.setFrstTrnmChnlCd	    (removeString	(map, FlatHeaderSpec.FRST_TRNM_CHNL_CD)); 		    // 최초전송채널코드
		entity.setTrnmChnlCd		    (removeString	(map, FlatHeaderSpec.TRNM_CHNL_CD)); 		    // 전송채널코드
		entity.setTrnmNodeNo		    (removeInt	    (map, FlatHeaderSpec.TRNM_NODE_NO)); 		    // 전송노드번호
		entity.setMciTrnmNodeNo		    (removeInt	    (map, FlatHeaderSpec.MCI_TRNM_NODE_NO)); 		    // MCI 전송노드번호
		entity.setEnvDvcd		        (removeString	(map, FlatHeaderSpec.ENV_DVCD)); 		    // 환경정보구분코드

		// 전문처리내용
		entity.setMesgDmndDttm		    (removeString	(map, FlatHeaderSpec.MESG_DMND_DTTM)); 		    // 전문요청일시
		entity.setMesgVrsnDvcd		    (removeString	(map, FlatHeaderSpec.MESG_VRSN_DVCD)); 		    // 전문버전구분코드
		entity.setTrnCd		            (removeString	(map, FlatHeaderSpec.TRN_CD)); 		    // 거래코드
		entity.setScrnNo		        (removeString	(map, FlatHeaderSpec.SCRN_NO)); 		    // 화면ID
		entity.setMesgRespDttm		    (removeString	(map, FlatHeaderSpec.MESG_RESP_DTTM)); 		    // 전문응답일시
		entity.setTrnPtrnDvcd   (removeString	(map, FlatHeaderSpec.TRN_PTRN_DVCD)); 		    // 거래유형구분코드	


		// FLAG정보
		entity.setMesgDvcd		        (removeString	(map, FlatHeaderSpec.MESG_DVCD)); 		    // 전문구분코드
		entity.setMesgTycd		        (removeString	(map, FlatHeaderSpec.MESG_TYCD)); 		    // 전문유형코드
		entity.setMesgCntySrno		    (removeInt	    (map, FlatHeaderSpec.MESG_CNTY_SRNO)); 		    // 전문연속일련번호
		entity.setTrtmRsltCd  		    (removeString	(map, FlatHeaderSpec.TRTM_RSLT_CD)); 		    // 처리결과코드
		entity.setCmpgRelmUseDvcd		(removeString	(map, FlatHeaderSpec.CMPG_RELM_USE_DVCD)); 		    // 캠페인영역사용구분코드

		// 직원정보내용
		entity.setCompCd	            (removeString	(map, FlatHeaderSpec.COMP_CD)); 		    // 회사코드
		entity.setDeptCd					(removeString	(map, FlatHeaderSpec.DEPT_CD));//부서코드
		entity.setBrCd		            (removeString	(map, FlatHeaderSpec.BR_CD)); 		    // 부점코드
		entity.setUserNo		        (removeString	(map, FlatHeaderSpec.USER_NO)); 		    // 사용자번호
		entity.setUserLocale	        (removeString	(map, FlatHeaderSpec.USER_LOCALE)); 		    // 사용자로케일
		entity.setCtiYn	        (removeString	(map, FlatHeaderSpec.CTI_YN)); 		    // CTI 여부	

		// 시재 정보
		entity.setCshnOcrnYn	        (removeString	(map, FlatHeaderSpec.CSHN_OCRN_YN)); 		    // 시제발생여부
		entity.setCashAmt		        (removeDouble	    (map, FlatHeaderSpec.CASH_AMT)); 		    // 현금금액
		entity.setPointAmt		        (removeDouble	    (map, FlatHeaderSpec.POINT_AMT)); 		    // 포인트금액

		// 대외거래정보내용
		entity.setXtisCd		    	(removeString	(map, FlatHeaderSpec.XTIS_CD)); 		    // 대외기관코드
		entity.setBzwrSvrCd	        	(removeString	(map, FlatHeaderSpec.BZWR_SVR_CD)); 		    // 업무서버코드
		entity.setOtsdMesgCd		    (removeString	(map, FlatHeaderSpec.OTSD_MESG_CD)); 		    // 대외전문코드
		entity.setOtsdMesgTrtmCd		(removeString	(map, FlatHeaderSpec.OTSD_MESG_TRTM_CD)); 		    // 대외전문처리코드
		entity.setOtsdTrnUnqNo		    (removeString	(map, FlatHeaderSpec.OTSD_TRN_UNQ_NO)); 		    // 대외거래고유번호
		entity.setOtsdRespTrnCd			(removeString	(map, FlatHeaderSpec.OTSD_RESP_TRN_CD)); 		    // 대외응답거래코드
		entity.setChnlMsgCd		        (removeString	(map, FlatHeaderSpec.CHNL_MSG_CD)); 		    // 채널메시지코드
//		entity.setEaiGlobId				(removeString	(map, FlatHeaderSpec.EAI_GLOB_ID)); 		    // EAI 글로벌ID
//		entity.setEaiIntfId					(removeString	(map, FlatHeaderSpec.EAI_INTF_ID)); 		    // EAI 인터페이스 ID
//		entity.setEaiRecvSvcid			(removeString	(map, FlatHeaderSpec.EAI_RECV_SVCID)); 	// 채널메시지코드

		
		// FILLER
		entity.setSprChrsCntn			(removeString	(map, FlatHeaderSpec.SPR_CHRS_CNTN)); 		    // 예비문자열내용


		// 메시지부
		if(parseMsgList){
			int msgCcnt = removeInt(map, FlatHeaderSpec.MSG_CCNT);
			for (int i = 1; i <= msgCcnt; i++) {
				entity.addMsg(new TrtmRsltMsg(
						removeString(map, FlatHeaderSpec.MSG_CD, i),           // 메시지코드
						removeString(map, FlatHeaderSpec.MSG_CNTN, i),         // 메시지내용
						removeInt(map, FlatHeaderSpec.EROR_OCRN_PRRM_LINE, i), // 오류발생프로그램라인
						removeString(map, FlatHeaderSpec.EROR_OCRN_PRRM_NM, i) // 오류발생프로그램명
						));
			}
		}

		// 데이타헤더 - 변환하지 않음.
		
		return entity;
	}
	
	public static Map<String, String> toEaiHeaderMap(ImplCommonArea ca, IResultMessage resultMsg,boolean isRes) {
		Map<String, String> eaiHeaderMap =null;
		Random r = new Random();
		r.setSeed(new Date().getTime());

		if(isRes) {//Request 때 받은 EAI Header에다가 응답데이터를 추가함. 
			eaiHeaderMap = ca.getEaiHeader();
			String tgrmPrgSqno = eaiHeaderMap.get(EaiFlatHeaderSpec.TGRM_PRG_SQNO);
			int seqNo = 0;
			
			seqNo = Integer.parseInt(tgrmPrgSqno);
            seqNo++;
			
		/*	try {
				seqNo = Integer.parseInt(tgrmPrgSqno);
				seqNo++;
			} catch (NumberFormatException e) {
				//에러나면 그냥 seqNo 값인 0이 들어가도록 별도의 Exception처리는 하지않음.
			}*/
			eaiHeaderMap.put(EaiFlatHeaderSpec.TGRM_PRG_SQNO.name(), nexcore.framework.core.util.StringUtils.lpad(seqNo+"", 2, "0"));
			eaiHeaderMap.put(EaiFlatHeaderSpec.REQ_RSP_DCD.name(), "R");
			eaiHeaderMap.put(EaiFlatHeaderSpec.ASYNC_TR_DCD.name(), "Y");
			eaiHeaderMap.put(EaiFlatHeaderSpec.TGRM_RSP_DTM.name(), DateUtils.getCurrentDate("yyyyMMddHHmmss"));
			eaiHeaderMap.put(EaiFlatHeaderSpec.TGRM_PRCRSLT_DCD.name(), resultMsg.getStatus()==IResultMessage.OK?"0":"9");
			
		} else {
			eaiHeaderMap = new HashMap<String, String>(); 
			eaiHeaderMap.put(EaiFlatHeaderSpec.TGRM_DDTM.name(), DateUtils.getCurrentDate("yyyyMMddHHmmss"));
			eaiHeaderMap.put(EaiFlatHeaderSpec.TGRM_CRT_SYSNM.name(), 
																																		DmsConstants.SPC_COMPANY_CD+
																																		DmsConstants.HPC_SYSTEM_CD+
																																		HpcUtils.getWasNodeKind()+
																																		HpcUtils.getWasNodeNo()
																																		);
			eaiHeaderMap.put(EaiFlatHeaderSpec.TGRM_CRT_NO.name(), String.valueOf(Math.abs(r.nextInt() % Math.pow(10, 7))).replaceAll("\\.", "0"));
			
			//진행번호
			eaiHeaderMap.put(EaiFlatHeaderSpec.TGRM_PRG_SQNO.name(), "00");
			
			//전송시스템코드
			eaiHeaderMap.put(EaiFlatHeaderSpec.TRMS_SYS_CD.name(), DmsConstants.SPC_COMPANY_CD+DmsConstants.HPC_SYSTEM_CD);
			
			//전문정보처리
			eaiHeaderMap.put(EaiFlatHeaderSpec.REQ_RSP_DCD.name(), CommonUtils.MESG_DVCD.Q.name());
//			eaiHeaderMap.put(EaiFlatHeaderSpec.TR_SYNC_DCD.name(), isSync?CommonUtils.SYNC_ASYNC.S.name():CommonUtils.SYNC_ASYNC.A.name());
//			eaiHeaderMap.put(EaiFlatHeaderSpec.ASYNC_TR_DCD.name(), needRes?CommonUtils.Y_N.Y.name():CommonUtils.Y_N.N.name());
			eaiHeaderMap.put(EaiFlatHeaderSpec.TGRM_REQ_DTM.name(), DateUtils.getCurrentDate("yyyyMMddHHmmss"));
			
			//서비스ID정보
//			eaiHeaderMap.put(EaiFlatHeaderSpec.RCVE_SVCID.name(), targetId);
//			eaiHeaderMap.put(EaiFlatHeaderSpec.RSLT_RCEV_SVCID.name(), rsltServiceId);
//			eaiHeaderMap.put(EaiFlatHeaderSpec.EAI_INTF_ID.name(), eaiInfId);
			
			//응답결과정보
			eaiHeaderMap.put(EaiFlatHeaderSpec.TGRM_RSP_DTM.name(), "");
			eaiHeaderMap.put(EaiFlatHeaderSpec.TGRM_PRCRSLT_DCD.name(), "");

			//장애정보
			eaiHeaderMap.put(EaiFlatHeaderSpec.OBS_SYS_ID.name(), "");
			eaiHeaderMap.put(EaiFlatHeaderSpec.TGRM_ERR_MSG_CD.name(), "");
			
			//기타정보
			String runtimeMode = BaseUtils.getRuntimeMode();
			eaiHeaderMap.put(EaiFlatHeaderSpec.TGRM_VER_NO.name(), "R".equals(runtimeMode)?"P":"D");
			
			 String userLocale = ca.getUserLocale();
			 String localeCd = "";
			 if(userLocale.indexOf("KR") !=-1) {
				 localeCd="01";
			 } else if(userLocale.indexOf("EN") !=-1) {
				 localeCd="03";
			 } else if(userLocale.indexOf("CN") !=-1) {
				 localeCd="04";
			 } else if(userLocale.indexOf("JP") !=-1) {
				 localeCd="05";
			 } else {
				 localeCd="02"; //UTF-8인 경우
			 }
			eaiHeaderMap.put(EaiFlatHeaderSpec.LANG_DCD.name(), localeCd);
			eaiHeaderMap.put(EaiFlatHeaderSpec.TEST_DCD.name(), "0");
			
			StringBuffer sb = new StringBuffer();
			sb.append(StringUtils.lpad(ca.getGlobId(), FlatHeaderSpec.GLOB_ID.length(), " "));
			sb.append(StringUtils.lpad(ca.getPrgsSrno()+"", FlatHeaderSpec.PRGS_SRNO.length(), "0"));
			sb.append(StringUtils.lpad(ca.getSsoSesnKey(), FlatHeaderSpec.SSO_SESN_KEY.length(), " "));
			sb.append(StringUtils.lpad(ca.getFrstTrnmChnlCd(), FlatHeaderSpec.FRST_TRNM_CHNL_CD.length(), " "));
			eaiHeaderMap.put(EaiFlatHeaderSpec.RSR.name(), sb.toString());
			
			//메시지영역  
//			Locale locale = BaseUtils.asLocale(userLocale);
//			List<TrtmRsltMsg> msgList  = getMsgList(locale, resultMsg, ca.getMsgList());
//			TrtmRsltMsg trtmRsltMsg = null;
//			if(msgList != null && msgList.size() > 0) {
//				trtmRsltMsg = msgList.get(0) ;
//				eaiHeaderMap.put(EaiFlatHeaderSpec.MSG_CD.name(), resultMsg.getStatus()==IResultMessage.OK?"N":"E");
//				eaiHeaderMap.put(EaiFlatHeaderSpec.MSG_ID.name(), trtmRsltMsg.getMsgCd());
//				eaiHeaderMap.put(EaiFlatHeaderSpec.PNP_MSG.name(), trtmRsltMsg.getErorOcrnPrrmLine()+"");
//				eaiHeaderMap.put(EaiFlatHeaderSpec.APPD_MSG.name(), trtmRsltMsg.getMsgCntn());
//			}
		}
		return eaiHeaderMap;
	}
	
	/**
	 * 결과메시지로 처리 성공여부를 판단한다.
	 */
	public static boolean isSuccess(IResultMessage resultMessage){
		return resultMessage == null || resultMessage.getStatus() == IResultMessage.OK; 
	}
	
	/**
	 * 결과메시지를 분석하여 출력할 메시지를 생성한다.
	 */
	public static void initInboundResponseHeaders(ImplFlatHeader entity, IResultMessage resultMessage, Map<Object, Object> headers) {
		String yyyyMMddHHmmssSSS = nexcore.framework.core.util.DateUtils.dateToString(new Date(), "yyyyMMddHHmmssSSS");

		boolean isSuccess = isSuccess(resultMessage); //처리 성공 여부
		boolean isDummyReturn = BizServiceHelper.isDummyReturn(headers);     	// Dummy Return 여부
		boolean isDummyReturnReleaseOnFail = BizServiceHelper.isDummyReturnReleaseOnFail(headers); //에러시 Dummy Return 해제 여부
		boolean isRtnErrDataSet = (null==headers.get(DmsConstants.IS_RTN_ERR_DS))?false:(Boolean)headers.get(DmsConstants.IS_RTN_ERR_DS);
		// Dummy Return도 설정되고, 에러발생시 해제 옵션도 설정된 경우
		// 성공인 경우는 Dummy Return을 하고, 실패인 경우에는 하지 않는다.
		if(isDummyReturn && isDummyReturnReleaseOnFail){
			isDummyReturn = isSuccess;
		}

		int    splitIndex = 0; // 분할일련번호(0~)
		String splitKind  = null; //분할유형코드(S:분할처리시작, F:분할처리 완료, D: Dummy, null:일반)
		if (headers.containsKey(FlatConstants.BULK_FLAT_SPLIT_INDEX)) {
			splitIndex = (Integer) headers.get(FlatConstants.BULK_FLAT_SPLIT_INDEX);
		}
		if (headers.containsKey(FlatConstants.BULK_FLAT_SPLIT_STATUS)) {
			splitKind = (String) headers.get(FlatConstants.BULK_FLAT_SPLIT_STATUS);
		}

		// 전문 분할 시작
		String mesgTycd = null;
		int mesgCntySrno = 0;
		
		// - 전문유형코드  Q(1:일반거래전문, 2:대량입력시작, 3:대량입력중간, 9:대량입력마지막), 
        //               R(1:일반거래전문, 2:대량출력시작, 3:대량출력중간, 9:대량출력마지막, D:Dummy)
        //               P(S:일반화면Push, M:메시지Push, X:전일자시작, Y:전일자종료, H:CRM숨김, D:CRM표시)
		// - 전문연속일련번호 (00:일반전문, 01~99:대량출력)
		if (FlatConstants.BULK_FLAT_SPLIT_STATUS_START.equals(splitKind)) {
			mesgTycd = splitIndex == 0 ? "2" : "3";
			mesgCntySrno = splitIndex + 1;
		}
		// 전문 분할 종료
		else if (FlatConstants.BULK_FLAT_SPLIT_STATUS_FINISH.equals(splitKind)) {
			mesgTycd = "9";
			mesgCntySrno = splitIndex + 1;
		}
		// 전문 Dummy Return
		else if (FlatConstants.BULK_FLAT_SPLIT_STATUS_DUMMY.equals(splitKind)) {
			mesgTycd = "D";
			mesgCntySrno = 0;
		} else {
		// 일반 전문
		
			// 업무에서 강제로 Dummy Return 설정한 경우
			if(isDummyReturn){
				mesgTycd = "D";
				//mesgCntySrno = 0;
			} else  if(isRtnErrDataSet) {
			    mesgTycd = "E";
			    //mesgCntySrno = 0;
			}
			// 일반 전문
			else {
				mesgTycd = "1";
				//mesgCntySrno = 0;
			}
			//2015.10.13 jihooyim code inspector 점검 수정 (if/else문 body에 같은 것 반복 금지)
			mesgCntySrno = 0;
		}

//		entity.setFrstTrnmChnlCd(CommonUtils.CHNL_CD.FWK.name());  // 최초전송채널코드
//		entity.setTrnmChnlCd(CommonUtils.CHNL_CD.FWK.name());  // 전송채널코드
//		entity.setTrnmNodeNo(0); // 전송노드번호
		
		entity.setMesgRespDttm(yyyyMMddHHmmssSSS); // 전문응답일시
		
		entity.setMesgDvcd(CommonUtils.MESG_DVCD.R.name()); // 전문구분코드
		entity.setMesgTycd(mesgTycd);                       // 전문유형코드
		entity.setMesgCntySrno(mesgCntySrno);               // 전문연속일련번호
		entity.setTrtmRsltCd(isSuccess ? "0" : "1");        // 처리결과코드
	}

	public static void toStream(ImplFlatHeader entity, IResultMessage resultMessage, int bodyLength, PaddableDataOutputStream out) throws IOException{
		toStream(entity, resultMessage, bodyLength, out, false);
	}
	
	public static void toStream(ImplFlatHeader entity, IResultMessage resultMessage, int bodyLength, PaddableDataOutputStream out, boolean isFWK) throws IOException{
		boolean isSuccess = isSuccess(resultMessage); //처리 성공 여부
		boolean isPush = CommonUtils.MESG_DVCD.P.name().equals(entity.getMesgDvcd());
		boolean isResponse = CommonUtils.MESG_DVCD.R.name().equals(entity.getMesgDvcd());
		boolean isRequest = CommonUtils.MESG_DVCD.Q.name().equals(entity.getMesgDvcd());

		List<TrtmRsltMsg> msgList = null; // 메시지부
		
		// PUSH전문이거나, 응답전문중 '일반거래'/'대량출력마지막전문/에러거래에 응답Data를 return하는 경우'
		if(isPush  || (isResponse && ("E".equals(entity.getMesgTycd())||"1".equals(entity.getMesgTycd()) || "9".equals(entity.getMesgTycd()))) ){
			Locale locale = entity.getUserLocale() == null || entity.getUserLocale().trim().length() < 1 ? null : BaseUtils.asLocale(entity.getUserLocale());
			msgList = getMsgList(locale, resultMessage, entity.getMsgList());
		}
		if(msgList == null){
			msgList = new ArrayList<TrtmRsltMsg>(0);
		}
		
		toStream(entity, isSuccess, msgList, bodyLength, out, isFWK);	
	}

	public static void toStream(ImplFlatHeader entity, boolean isSuccess, List<TrtmRsltMsg> msgList, int bodyLength, PaddableDataOutputStream out, boolean isFWK) throws IOException{
//		boolean isSuccess = !"1".equals(entity.getTrtmRsltCd()); //처리 성공 여부
		boolean isPush = CommonUtils.MESG_DVCD.P.name().equals(entity.getMesgDvcd());
		boolean isResponse = CommonUtils.MESG_DVCD.R.name().equals(entity.getMesgDvcd());
		boolean isRequest = CommonUtils.MESG_DVCD.Q.name().equals(entity.getMesgDvcd());

//		List<TrtmRsltMsg> msgList = null; // 메시지부
//		List<DataHeader> dataHeaderList = null; //데이타헤더부
		
//		// PUSH전문이거나, 응답전문중 '일반거래'/'대량출력마지막전문'
//		if(isPush  || (isResponse && ("1".equals(entity.getMesgTycd()) || "9".equals(entity.getMesgTycd()))) ){
//			msgList = entity.getMsgList();
//			dataHeaderList = entity.getDataHeaderList();
//		}
//		if(msgList == null){
//			msgList = new ArrayList<TrtmRsltMsg>(0);
//		}
		
		
		int msgCcnt = msgList == null ? 0 : msgList.size();
		
		// 전체전문길이, 표준헤더부길이 사이즈 제거
		int fixedHeaderLength = FlatHeaderSpec.getTotalLength(); 
		// 메시지 영역 길이
		int headerMessageLength = msgCcnt * (FlatHeaderSpec.MSG_CD.length() + FlatHeaderSpec.MSG_CNTN.length() + FlatHeaderSpec.EROR_OCRN_PRRM_LINE.length() + FlatHeaderSpec.EROR_OCRN_PRRM_NM.length());
		//전문헤더부 길이
		int headerLength = fixedHeaderLength +  headerMessageLength ; 
		int totalLength = headerLength + bodyLength; //전문전체길이
		
		// 전문길이
		write(out, FlatHeaderSpec.WHL_MESG_LEN       , totalLength);  // 전체전문길이
		write(out, FlatHeaderSpec.STND_HDR_LEN       , headerLength);  // 표준헤더부길이
		
		// 거래번호
		write(out, FlatHeaderSpec.GLOB_ID            , entity.getGlobId());  // 글로벌 ID
		write(out, FlatHeaderSpec.PRGS_SRNO          , (isRequest ? entity.getPrgsSrno() : entity.getPrgsSrno() + 1));  // 진행일련번호
		
		// 전송시스템정보내용
		write(out, FlatHeaderSpec.IPAD               , entity.getIpad());  // IP주소
		write(out, FlatHeaderSpec.PRCM_MAC           , entity.getPrcmMac());  // PC MAC주소
		write(out, FlatHeaderSpec.TRN_TRNM_NO        , entity.getTrnTrnmNo());  // 거래단말번호
		write(out, FlatHeaderSpec.SSO_SESN_KEY       , entity.getSsoSesnKey());  // SSO 세션 KEY
		write(out, FlatHeaderSpec.FRST_TRNM_CHNL_CD  , entity.getFrstTrnmChnlCd());  // 최초전송채널코드
		write(out, FlatHeaderSpec.TRNM_CHNL_CD       , isFWK ? DmsConstants.FWK_CHN_CD : entity.getTrnmChnlCd());  // 전송채널코드
		write(out, FlatHeaderSpec.TRNM_NODE_NO       , isFWK ? 0 : entity.getTrnmNodeNo());  // 전송노드번호
		write(out, FlatHeaderSpec.MCI_TRNM_NODE_NO   , entity.getMciTrnmNodeNo());  // MCI 전송노드번호
		write(out, FlatHeaderSpec.ENV_DVCD           , entity.getEnvDvcd());  // 환경정보구분코드

		// 전문처리내용
		write(out, FlatHeaderSpec.MESG_DMND_DTTM     , entity.getMesgDmndDttm());  // 전문요청일시
		write(out, FlatHeaderSpec.MESG_VRSN_DVCD     , entity.getMesgVrsnDvcd());  // 전문버전구분코드
		write(out, FlatHeaderSpec.TRN_CD             , entity.getTrnCd());  // 거래코드
		write(out, FlatHeaderSpec.SCRN_NO            , entity.getScrnNo());  // 화면번호
		write(out, FlatHeaderSpec.MESG_RESP_DTTM     , entity.getMesgRespDttm());  // 전문응답일시
		write(out, FlatHeaderSpec.TRN_PTRN_DVCD     , entity.getTrnPtrnDvcd());  // 거래유형구분코드		

		// FLAG정보
		write(out, FlatHeaderSpec.MESG_DVCD          , entity.getMesgDvcd());  // 전문구분코드
		write(out, FlatHeaderSpec.MESG_TYCD          , entity.getMesgTycd());  // 전문유형코드
		write(out, FlatHeaderSpec.MESG_CNTY_SRNO     , entity.getMesgCntySrno());  // 전문연속일련번호
		write(out, FlatHeaderSpec.TRTM_RSLT_CD       , isSuccess ? "0" : "1");  // 처리결과코드 
		write(out, FlatHeaderSpec.CMPG_RELM_USE_DVCD , entity.getCmpgRelmUseDvcd());  // 캠페인영역사용구분코드

		// 직원정보내용
		write(out, FlatHeaderSpec.COMP_CD            , entity.getCompCd());  // 은행코드
		write(out, FlatHeaderSpec.DEPT_CD          , entity.getDeptCd());  // 부서코드
		write(out, FlatHeaderSpec.BR_CD              , entity.getBrCd());  // 부점코드
		write(out, FlatHeaderSpec.USER_NO            , entity.getUserNo());  // 사용자번호
		write(out, FlatHeaderSpec.USER_LOCALE        , entity.getUserLocale());  // 사용자로케일
		write(out, FlatHeaderSpec.CTI_YN        , entity.getCtiYn());  // CTI여부

		// 시재 정보
		write(out, FlatHeaderSpec.CSHN_OCRN_YN       , entity.getCshnOcrnYn());  // 시제발생여부
		write(out, FlatHeaderSpec.CASH_AMT           , entity.getCashAmt()+"");  // 현금금액
		write(out, FlatHeaderSpec.POINT_AMT          , entity.getPointAmt()+"");  //포인트금액

		// 대외거래정보내용
		write(out, FlatHeaderSpec.XTIS_CD            , entity.getXtisCd());  // 대외기관코드
		write(out, FlatHeaderSpec.BZWR_SVR_CD        , entity.getBzwrSvrCd());  // 업무서버코드
		write(out, FlatHeaderSpec.OTSD_MESG_CD       , entity.getOtsdMesgCd());  // 대외전문코드
		write(out, FlatHeaderSpec.OTSD_MESG_TRTM_CD  , entity.getOtsdMesgTrtmCd());  // 대외전문처리코드
		write(out, FlatHeaderSpec.OTSD_TRN_UNQ_NO    , entity.getOtsdTrnUnqNo());  // 대외거래고유번호
		write(out, FlatHeaderSpec.OTSD_RESP_TRN_CD   , entity.getOtsdRespTrnCd());  // 대외응답거래코드
		write(out, FlatHeaderSpec.CHNL_MSG_CD        , entity.getChnlMsgCd());  // 채널메시지코드
//		write(out, FlatHeaderSpec.EAI_GLOB_ID        , entity.getEaiGlobId());  // 채널메시지코드
//		write(out, FlatHeaderSpec.EAI_INTF_ID        , entity.getEaiIntfId());  // 채널메시지코드
//		write(out, FlatHeaderSpec.EAI_RECV_SVCID        , entity.getEaiRecvSvcid());  // 채널메시지코드
		
		// FILLER
		write(out, FlatHeaderSpec.SPR_CHRS_CNTN      , entity.getSprChrsCntn());  // 예비문자열내용

		// 메시지부 (최대 10건 반복)
		write(out, FlatHeaderSpec.MSG_CCNT               , msgCcnt);  // 메시지 건수
		for(int i=0; i<msgCcnt; i++){
			TrtmRsltMsg msg = msgList.get(i);
			write(out, FlatHeaderSpec.MSG_CD             , msg.getMsgCd());  // 메시지코드
			write(out, FlatHeaderSpec.MSG_CNTN           , msg.getMsgCntn());  // 메시지내용
			write(out, FlatHeaderSpec.EROR_OCRN_PRRM_LINE, msg.getErorOcrnPrrmLine());  // 오류발생프로그램라인
			write(out, FlatHeaderSpec.EROR_OCRN_PRRM_NM  , msg.getErorOcrnPrrmNm());  // 오류발생프로그램명
		}
	}
	
	public static void toEaiStream(Map<String, String> eaiEntity,  int bodyLength, PaddableDataOutputStream out) throws IOException{
		
		// 전체전문길이, 표준헤더부길이 사이즈 제거
		int fixedHeaderLength = EaiFlatHeaderSpec.getTotalLength(); 
		// 메시지 영역 길이
		int msgCcnt = 0;
		if(!"0".equals(eaiEntity.get(EaiFlatHeaderSpec.TGRM_PRCRSLT_DCD.name())) &&
				DmsConstants.RES_CD_STR.equals(eaiEntity.get(EaiFlatHeaderSpec.REQ_RSP_DCD.name()))
			) {//거래가 정상처리가 아니면서 response일 경우는 메시지 count를 1로 설정
			msgCcnt = 1;
		} 
		
		int headerMessageLength = msgCcnt * (EaiFlatHeaderSpec.MSG_CD.length() + EaiFlatHeaderSpec.MSG_ID.length() + 
																			EaiFlatHeaderSpec.PNP_MSG.length() + EaiFlatHeaderSpec.APPD_MSG.length()+
																			EaiFlatHeaderSpec.ERR_TRRY.length());
		//전문헤더부 길이
		int headerLength = fixedHeaderLength +  headerMessageLength ; 
		int totalLength = headerLength + bodyLength; //전문전체길이
		
		write(out, EaiFlatHeaderSpec.STD_TGRM_LEN, totalLength-8+2+""); //전체 길이에서 전문길이 필드 길이만큼을 빼고 tailer 사이즈만큼 더한 값을 입력함.
		
		//Global Id      
		write(out, EaiFlatHeaderSpec.TGRM_DDTM, eaiEntity.get(EaiFlatHeaderSpec.TGRM_DDTM.name())); 				
		write(out, EaiFlatHeaderSpec.TGRM_CRT_SYSNM, eaiEntity.get(EaiFlatHeaderSpec.TGRM_CRT_SYSNM.name()));
		write(out, EaiFlatHeaderSpec.TGRM_CRT_NO, eaiEntity.get(EaiFlatHeaderSpec.TGRM_CRT_NO.name()));
				
	  //진행번호                                    
		write(out, EaiFlatHeaderSpec.TGRM_PRG_SQNO, eaiEntity.get(EaiFlatHeaderSpec.TGRM_PRG_SQNO.name()));		
		                                              
		//전송시스템정보                              
		write(out, EaiFlatHeaderSpec.TRMS_SYS_CD, eaiEntity.get(EaiFlatHeaderSpec.TRMS_SYS_CD.name()));
		                                              
		//전문정보처리                                
		write(out, EaiFlatHeaderSpec.REQ_RSP_DCD, eaiEntity.get(EaiFlatHeaderSpec.REQ_RSP_DCD.name()));	
		write(out, EaiFlatHeaderSpec.TR_SYNC_DCD, eaiEntity.get(EaiFlatHeaderSpec.TR_SYNC_DCD	.name()));		
		write(out, EaiFlatHeaderSpec.ASYNC_TR_DCD, eaiEntity.get(EaiFlatHeaderSpec.ASYNC_TR_DCD.name()));			
		write(out, EaiFlatHeaderSpec.TGRM_REQ_DTM, eaiEntity.get(	EaiFlatHeaderSpec.TGRM_REQ_DTM.name()));		
	                                                
	  //서비스ID정보                                
		write(out, EaiFlatHeaderSpec.RCVE_SVCID, eaiEntity.get(EaiFlatHeaderSpec.RCVE_SVCID.name()));	
		write(out, EaiFlatHeaderSpec.RSLT_RCEV_SVCID, eaiEntity.get(EaiFlatHeaderSpec.RSLT_RCEV_SVCID.name()));	
		write(out, EaiFlatHeaderSpec.EAI_INTF_ID	, eaiEntity.get(EaiFlatHeaderSpec.EAI_INTF_ID.name()));	
	                                                
	  //응답결과정보                                
		write(out, EaiFlatHeaderSpec.TGRM_RSP_DTM, eaiEntity.get(EaiFlatHeaderSpec.TGRM_RSP_DTM.name()));			
		write(out, EaiFlatHeaderSpec.TGRM_PRCRSLT_DCD, eaiEntity.get(EaiFlatHeaderSpec.TGRM_PRCRSLT_DCD.name()));	
	                                                
	  //장애정보                                    
		write(out, EaiFlatHeaderSpec.OBS_SYS_ID, eaiEntity.get(EaiFlatHeaderSpec.OBS_SYS_ID.name()));	
		write(out, EaiFlatHeaderSpec.TGRM_ERR_MSG_CD, eaiEntity.get(EaiFlatHeaderSpec.TGRM_ERR_MSG_CD.name()));	
	                                                
	  //기타정보                                    
		write(out, EaiFlatHeaderSpec.TGRM_VER_NO, eaiEntity.get(EaiFlatHeaderSpec.TGRM_VER_NO.name()));			
		write(out, EaiFlatHeaderSpec.LANG_DCD	, eaiEntity.get(EaiFlatHeaderSpec.LANG_DCD.name()));
		write(out, EaiFlatHeaderSpec.TEST_DCD, eaiEntity.get(EaiFlatHeaderSpec.TEST_DCD.name()));
		write(out, EaiFlatHeaderSpec.RSR, eaiEntity.get(EaiFlatHeaderSpec.RSR.name()));
	                                                
		//메시지 정보
		if(msgCcnt>0) {
			write(out, EaiFlatHeaderSpec.MSG_CD, eaiEntity.get(EaiFlatHeaderSpec.MSG_CD.name()));						
			write(out, EaiFlatHeaderSpec.MSG_ID, eaiEntity.get(EaiFlatHeaderSpec.MSG_ID.name()));
			write(out, EaiFlatHeaderSpec.PNP_MSG, eaiEntity.get(EaiFlatHeaderSpec.PNP_MSG.name()));				
			write(out, EaiFlatHeaderSpec.APPD_MSG, eaiEntity.get(EaiFlatHeaderSpec.APPD_MSG.name()));
			write(out, EaiFlatHeaderSpec.ERR_TRRY, eaiEntity.get(EaiFlatHeaderSpec.ERR_TRRY.name()));
		}
		
			
	}
	/**
	 * 온라인컨텍스트 생성
	 * @param headers 표전전문헤더 Map
	 * @param isDeferred 디퍼드 여부
	 * @return 온라인컨텍스트
	 */
    public static IOnlineContext makeOnlineContext(Map<String, String> headers, boolean isDeferred){
        Date startTime = new Date();

        // **** ITransaction
        ITransaction transaction    = new Transaction(
        		headers.get(FlatHeaderSpec.GLOB_ID.name()), //request id
        		headers.get(FlatHeaderSpec.TRN_CD.name()), // tx id
        		isDeferred, startTime, true);
        
        // **** IChannel
        IChannel channel            = new Channel(
        		headers.get(FlatHeaderSpec.TRNM_CHNL_CD.name()), //전송채널코드 (채널 종류 코드(SKF0908))
        		headers.get(FlatHeaderSpec.TRN_TRNM_NO.name()), //거래단말번호 (채널 거래 기관/시스템 코드 (SKF0901))
        		IChannel.PROTOCOL_HTTP, IChannel.MSG_FIXED_LENGTH_BIN);

        // **** ITerminal
        ITerminal terminal          = new Terminal(
        		headers.get(FlatHeaderSpec.SCRN_NO.name()), // ( 화면번호)
        		headers.get(FlatHeaderSpec.BR_CD.name()), // (부점코드)
                1000);

        Locale locale = BaseUtils.asLocale(headers.get(FlatHeaderSpec.USER_LOCALE.name()));
        
        // **** IUserInfo
        UserInfo userInfo          = new UserInfo();
        userInfo.setIp(headers.get(FlatHeaderSpec.IPAD.name())); //ip
        userInfo.setLocale(locale == null ? Locale.KOREA : locale);
        userInfo.setLoginId(headers.get(FlatHeaderSpec.USER_NO.name())); //teller

        // **** IRuntimeContext
        IMethodMetaData methodMetadata = NexCoreServiceUtil.getMethodMetaData(transaction.getTxId());
        IRuntimeContext runtimeContext = new RuntimeContext(
        		methodMetadata == null ? null : methodMetadata.getComponentMetaData().getFqId()
                , methodMetadata == null ? null : methodMetadata.getBizUnitId()
        		, methodMetadata == null ? null : methodMetadata.getId());

        OnlineContext onlineCtx = new OnlineContext(transaction, userInfo, runtimeContext, channel, terminal);
        onlineCtx.setAttributesAll(headers);

        return onlineCtx;
    }
    
    /**
	 * 온라인컨텍스트 생성
	 * @param headers 표전전문헤더 Map
	 * @param isDeferred 디퍼드 여부
	 * @return 온라인컨텍스트
     * @throws IOException 
	 */
    public static IOnlineContext makeOnlineContextForEai(Map<String, String> eaiHeaders, boolean isDeferred) throws IOException{
    	Date startTime = new Date();
    	Map<String, String> flatHeader = new HashMap<String, String>();
        // **** ITransaction
        ITransaction transaction    = new Transaction(
        		HpcUtils.makeGlobId(DmsConstants.EAI_CHN_CD), //request id
        		eaiHeaders.get(EaiFlatHeaderSpec.RCVE_SVCID.name()), // tx id
        		isDeferred, startTime, true);
        
        // **** IChannel
        IChannel channel            = new Channel(
        		eaiHeaders.get(DmsConstants.EAI_CHN_CD), //전송채널코드 (채널 종류 코드(SKF0908))
        		"", //거래단말번호 (채널 거래 기관/시스템 코드 (SKF0901))
        		IChannel.PROTOCOL_HTTP, IChannel.MSG_FIXED_LENGTH_BIN);

        // **** ITerminal
        ITerminal terminal          = new Terminal(
        		"", // ( 화면번호)
        		"", // (부점 코드)
                1000);

        Locale locale = BaseUtils.asLocale("ko_KR");//EAI에서 넘어오는 요청건에 대한 로케일은 모두 한국어로 정의함. 
        
        // **** IUserInfo
        UserInfo userInfo          = new UserInfo();
        userInfo.setIp(""); //EAI쪽 전문에서 IP는 넘어오지 않음. 
        userInfo.setLocale(locale == null ? Locale.KOREA : locale);
        userInfo.setLoginId(""); //EAI에서 넘어올 때 사용자 ID는 없음. 

        // **** IRuntimeContext
        IMethodMetaData methodMetadata = NexCoreServiceUtil.getMethodMetaData(transaction.getTxId());
        IRuntimeContext runtimeContext = new RuntimeContext(
        		methodMetadata == null ? null : methodMetadata.getComponentMetaData().getFqId()
                , methodMetadata == null ? null : methodMetadata.getBizUnitId()
        		, methodMetadata == null ? null : methodMetadata.getId());

        OnlineContext onlineCtx = new OnlineContext(transaction, userInfo, runtimeContext, channel, terminal);
        convertEaiFlatToCommonFlat(flatHeader, eaiHeaders);
        onlineCtx.setAttributesAll(flatHeader);
        onlineCtx.setAttribute(DmsConstants.EAI_HEADER_STR, eaiHeaders);

        return onlineCtx;
    }
    
    /**
     * 전체전문중 업무데이타부를 조회한다.
     */
	public static ByteArrayWrap getBodyByteArrayWrap(ByteArrayWrap totalByteArrayWrap, String encoding) throws IOException {
		int allTlmsgLng = Integer.parseInt(new String(totalByteArrayWrap.getByteArray(), totalByteArrayWrap.getOffset(), FlatHeaderSpec.WHL_MESG_LEN.length()).trim());
		int stgHdrLng   = Integer.parseInt(new String(totalByteArrayWrap.getByteArray(), totalByteArrayWrap.getOffset() + FlatHeaderSpec.WHL_MESG_LEN.length(), FlatHeaderSpec.STND_HDR_LEN.length()).trim());
		
		int offset = totalByteArrayWrap.getOffset() + stgHdrLng;
		int length = allTlmsgLng - stgHdrLng;
        return new ByteArrayWrap(totalByteArrayWrap.getByteArray(), offset, length);
	}

	 /**
     * 전체전문중 업무데이타부를 조회한다.
     */
	public static ByteArrayWrap getEaiBodyByteArrayWrap(ByteArrayWrap totalByteArrayWrap, String encoding, boolean isOK) throws IOException {
		int allTlmsgLng = Integer.parseInt(new String(totalByteArrayWrap.getByteArray(), totalByteArrayWrap.getOffset(), EaiFlatHeaderSpec.STD_TGRM_LEN.length()).trim());
		int stgHdrLng   = isOK?EaiFlatHeaderSpec.getTotalLength():EaiFlatHeaderSpec.getTotalLength()+EaiFlatHeaderSpec.getMsgFieldLength();
		int offset = totalByteArrayWrap.getOffset() + stgHdrLng;
		int length = (allTlmsgLng+8-2) - stgHdrLng;//EAI에서 넘어올 때는 전체길이에 전문길이부길이만큼이 빠져서 오고 tailer길이는 넣기 때문에 그만큼을 다시 더해주고 tailer길이는 빼준다
        return new ByteArrayWrap(totalByteArrayWrap.getByteArray(), offset, length);
	}
	
	private static void convertEaiFlatToCommonFlat(Map flatHeader, Map<String, String> eaiHeader) throws IOException {
		
		//전문길이 정의
		readString(flatHeader, FlatHeaderSpec.WHL_MESG_LEN.name(), eaiHeader.get(EaiFlatHeaderSpec.STD_TGRM_LEN.name()));//전문 전체 길이
		readString(flatHeader, FlatHeaderSpec.STND_HDR_LEN.name(), DmsConstants.EAI_HEADER_SIZE);//표준헤더부 길이  
        
		/**
		 * 거래번호
		 */
		readString(flatHeader, FlatHeaderSpec.GLOB_ID.name(), HpcUtils.makeGlobId(DmsConstants.EAI_CHN_CD));//FWK용 Global Id구성
        readString(flatHeader, FlatHeaderSpec.PRGS_SRNO.name(), eaiHeader.get(EaiFlatHeaderSpec.TGRM_PRG_SQNO.name()));//진행일련번호
		
        /**
		 * 전송시스템정보내용
		 */
		readString(flatHeader, FlatHeaderSpec.IPAD.name(), "");//EAI에서 넘어오는 IP 주소없음.
		readString(flatHeader, FlatHeaderSpec.PRCM_MAC.name(), "");//EAI에서 넘어오는 MAC 주소없음.
		readString(flatHeader, FlatHeaderSpec.TRN_TRNM_NO.name(), "");//EAI에서 넘어오는 거래단말번호 없음.
		readString(flatHeader, FlatHeaderSpec.SSO_SESN_KEY.name(), "");//요청거래의 경우는 EAI에서 넘어오는 SSO 키 없음. 응답거래일 경우에는 있을 수 있음. 아래서 정의예정
		readString(flatHeader, FlatHeaderSpec.FRST_TRNM_CHNL_CD.name(), "");//요청거래의 경우는 EAI에서 넘어오는 최초채널코드 없음. 응답거래일 경우에는 있을 수 있음. 아래서 정의예정
		readString(flatHeader, FlatHeaderSpec.TRNM_CHNL_CD.name(), DmsConstants.EAI_CHN_CD);//채널코드 정의
		readString(flatHeader, FlatHeaderSpec.TRNM_NODE_NO.name(), 1+"");
		readString(flatHeader, FlatHeaderSpec.MCI_TRNM_NODE_NO.name(), 1+"");
		readString(flatHeader, FlatHeaderSpec.ENV_DVCD.name(), BaseUtils.getRuntimeMode());
		
		/**
		 * 전문처리 내용
		 */
		readString(flatHeader, FlatHeaderSpec.MESG_DMND_DTTM.name(), eaiHeader.get(EaiFlatHeaderSpec.TGRM_REQ_DTM.name())+"000");
		readString(flatHeader, FlatHeaderSpec.MESG_VRSN_DVCD.name(), eaiHeader.get(EaiFlatHeaderSpec.TGRM_VER_NO.name()));
		readString(flatHeader, FlatHeaderSpec.TRN_CD.name(), eaiHeader.get(EaiFlatHeaderSpec.RCVE_SVCID.name()));
		readString(flatHeader, FlatHeaderSpec.SCRN_NO.name(), "");
		readString(flatHeader, FlatHeaderSpec.MESG_RESP_DTTM.name(), "");
		readString(flatHeader, FlatHeaderSpec.TRN_PTRN_DVCD.name(), "");
		
		/**
		 * FLAG정보
		 */
		readString(flatHeader, FlatHeaderSpec.MESG_DVCD.name(), eaiHeader.get(EaiFlatHeaderSpec.REQ_RSP_DCD.name()));
		readString(flatHeader, FlatHeaderSpec.MESG_TYCD.name(), "1");
		readString(flatHeader, FlatHeaderSpec.MESG_CNTY_SRNO.name(), "00");

        //2015.10.13 jihooyim code inspector 점검 수정 (Primitive 타입이 아닌 경우 동일비교, 대소비교 금지) 
		readString(flatHeader, FlatHeaderSpec.TRTM_RSLT_CD.name(), "9".equals(eaiHeader.get(EaiFlatHeaderSpec.TGRM_PRCRSLT_DCD.name()))?"1":"0");
		readString(flatHeader, FlatHeaderSpec.CMPG_RELM_USE_DVCD.name(), "0");
		
		/**
		 * 직원정보내용
		 */
		readString(flatHeader, FlatHeaderSpec.COMP_CD.name(), "");
		readString(flatHeader, FlatHeaderSpec.DEPT_CD.name(), "");
		readString(flatHeader, FlatHeaderSpec.BR_CD.name(), "");
		readString(flatHeader, FlatHeaderSpec.USER_NO.name(), "");
		readString(flatHeader, FlatHeaderSpec.USER_LOCALE.name(), "ko_KR");
		readString(flatHeader, FlatHeaderSpec.CTI_YN.name(), "N");
		
		/**
		 * 시제정보
		 */
		readString(flatHeader, FlatHeaderSpec.CSHN_OCRN_YN.name(), "N");
		readString(flatHeader, FlatHeaderSpec.CASH_AMT.name(), "0");
		readString(flatHeader, FlatHeaderSpec.POINT_AMT.name(), "0");
		
		/**
		 * 채널거래정보내용
		 */
		readString(flatHeader, FlatHeaderSpec.XTIS_CD.name(), "");
		readString(flatHeader, FlatHeaderSpec.BZWR_SVR_CD.name(), "");
		readString(flatHeader, FlatHeaderSpec.OTSD_MESG_CD.name(), "");
		readString(flatHeader, FlatHeaderSpec.OTSD_MESG_TRTM_CD.name(), "");
		readString(flatHeader, FlatHeaderSpec.OTSD_TRN_UNQ_NO.name(), "");
		readString(flatHeader, FlatHeaderSpec.OTSD_RESP_TRN_CD.name(), "");
		readString(flatHeader, FlatHeaderSpec.CHNL_MSG_CD.name(), "");
		
		//EAI용 Global ID를 FWK flat header에 정의
		StringBuffer sb = new StringBuffer();
		sb.append(eaiHeader.get(EaiFlatHeaderSpec.TGRM_DDTM.name()));
		sb.append(eaiHeader.get(EaiFlatHeaderSpec.TGRM_CRT_SYSNM.name()));
		sb.append(eaiHeader.get(EaiFlatHeaderSpec.TGRM_CRT_NO.name()));
//        readString(flatHeader, FlatHeaderSpec.EAI_GLOB_ID.name(), sb.toString());
//        
//		readString(flatHeader, FlatHeaderSpec.EAI_INTF_ID.name(), eaiHeader.get(EaiFlatHeaderSpec.EAI_INTF_ID.name()));
//		readString(flatHeader, FlatHeaderSpec.EAI_RECV_SVCID.name(), eaiHeader.get(EaiFlatHeaderSpec.RSLT_RCEV_SVCID.name()));
		
		
		readString(flatHeader, FlatHeaderSpec.SPR_CHRS_CNTN.name(), eaiHeader.get(EaiFlatHeaderSpec.RSR.name()));
		if(StringUtils.isNotEmpty(eaiHeader.get(EaiFlatHeaderSpec.RSR.name()))) {
			int strIdx = 0;
			String sprChrsCntn = eaiHeader.get(EaiFlatHeaderSpec.RSR.name());
			readString(flatHeader, FlatHeaderSpec.GLOB_ID.name(), sprChrsCntn.substring(0, FlatHeaderSpec.GLOB_ID.length()));
			readString(flatHeader, FlatHeaderSpec.PRGS_SRNO.name(), sprChrsCntn.substring(strIdx+=FlatHeaderSpec.GLOB_ID.length(), FlatHeaderSpec.PRGS_SRNO.length()));
			readString(flatHeader, FlatHeaderSpec.SSO_SESN_KEY.name(), sprChrsCntn.substring(strIdx+=FlatHeaderSpec.PRGS_SRNO.length(), FlatHeaderSpec.SSO_SESN_KEY.length()));
			readString(flatHeader, FlatHeaderSpec.FRST_TRNM_CHNL_CD.name(), sprChrsCntn.substring(strIdx+=FlatHeaderSpec.SSO_SESN_KEY.length(), FlatHeaderSpec.FRST_TRNM_CHNL_CD.length()));
		}
		
		if("9".equals(eaiHeader.get(EaiFlatHeaderSpec.TGRM_PRCRSLT_DCD.name()))) {
			readString(flatHeader, FlatHeaderSpec.MSG_CCNT.name() ,"1");
			readString(flatHeader, FlatHeaderSpec.MSG_CD.name(), eaiHeader.get(EaiFlatHeaderSpec.MSG_ID.name()));
			readString(flatHeader, FlatHeaderSpec.MSG_CNTN.name() ,eaiHeader.get(EaiFlatHeaderSpec.PNP_MSG.name()));
			readString(flatHeader, FlatHeaderSpec.EROR_OCRN_PRRM_LINE.name() ,"0");
			readString(flatHeader, FlatHeaderSpec.EROR_OCRN_PRRM_NM.name() , eaiHeader.get(EaiFlatHeaderSpec.APPD_MSG.name()));
		}

	}
	private static void readString(DataInputStream in, byte[] buff, String encoding, Map<String, String> headers, FlatHeaderSpec spec) throws IOException{
		String value = FlatUtil.readString(in, buff, spec.length(), encoding);
		headers.put(spec.name(), value);
	}

	private static void readString(DataInputStream in, byte[] buff, String encoding, Map<String, String> headers, EaiFlatHeaderSpec spec) throws IOException{
		String value = FlatUtil.readString(in, buff, spec.length(), encoding);
		headers.put(spec.name(), value);
	}
	
	private static void readString(Map<String, String> headers, String flatHeaderSpecName, String value) throws IOException{
		headers.put(flatHeaderSpecName, value);
	}
	
	private static void readString(DataInputStream in, byte[] buff, String encoding, Map<String, String> headers, String flatHeaderSpecName, EaiFlatHeaderSpec spec) throws IOException{
		String value = FlatUtil.readString(in, buff, spec.length(), encoding);
		headers.put(flatHeaderSpecName, value);
	}
	
	private static void readString(DataInputStream in, byte[] buff, String encoding, Map<String, String> headers, FlatHeaderSpec spec, int index) throws IOException{
		String value = FlatUtil.readString(in, buff, spec.length(), encoding);
		headers.put(spec.name() + index, value);
	}

	private static void write(PaddableDataOutputStream out, FlatHeaderSpec spec, int value) throws IOException {
		write(out, spec, String.valueOf(value));
	}

	private static void write(PaddableDataOutputStream out, FlatHeaderSpec spec, long value) throws IOException {
		write(out, spec, String.valueOf(value));
	}
	
	private static void write(PaddableDataOutputStream out, FlatHeaderSpec spec, String value) throws IOException {
		if (spec.isNumber()) {
			String valueTrimmed = value == null ? null : value.trim();
			if (valueTrimmed != null && valueTrimmed.length() > 0 && valueTrimmed.charAt(0) == '-') { // 마이너스이면
				out.write('-');
				out.writeStringWithLPadding(valueTrimmed.substring(1), spec.length()-1, (byte) 0x30);
			}else {
				out.writeStringWithLPadding(value, spec.length(), (byte) 0x30);
			}				
		} else {
			out.writeStringWithRPadding(value, spec.length(), (byte) 0x20);
		}
	}
	
	private static void write(PaddableDataOutputStream out, EaiFlatHeaderSpec spec, String value) throws IOException {
		if (spec.isNumber()) {
			String valueTrimmed = value == null ? null : value.trim();
			if (valueTrimmed != null && valueTrimmed.length() > 0 && valueTrimmed.charAt(0) == '-') { // 마이너스이면
				out.write('-');
				out.writeStringWithLPadding(valueTrimmed.substring(1), spec.length()-1, (byte) 0x30);
			}else {
				out.writeStringWithLPadding(value, spec.length(), (byte) 0x30);
			}				
		} else {
			out.writeStringWithRPadding(value, spec.length(), (byte) 0x20);
		}
	}

	private static List<TrtmRsltMsg> getMsgList( Locale locale, IResultMessage resultMessage, List<TrtmRsltMsg> additionalList){
		List<TrtmRsltMsg> msgList = new ArrayList<TrtmRsltMsg>(FlatHeaderSpec.MSG_CCNT.max());
		addMessage(msgList, locale, resultMessage, FlatHeaderSpec.MSG_CCNT.max(), additionalList);
		return msgList;
	}
	
//	/**
//	 * 예외객체를 CommonArea 표준전문헤더 메시지 목록으로 변환 최대 입력된 건수까지만 등록한다.
//	 */
//	public static void addMessage(List<TrtmRsltMsg> list, Locale locale, IResultMessage resultMessage, List<TrtmRsltMsg> additionalList) {
//		addMessage(list, locale, resultMessage, FlatHeaderSpec.MSG_CCNT.max(), additionalList);
//	}
	
	/**
	 * 예외객체를 CommonArea 표준전문헤더 메시지 목록으로 변환 최대 입력된 건수까지만 등록한다.
	 */
	public static void addMessage(List<TrtmRsltMsg> list, Locale locale, IResultMessage resultMessage, int maxCount, List<TrtmRsltMsg> additionalList) {
		// 처리 결과 메시지 존재시
		if(resultMessage != null){
			if(isSuccess(resultMessage)){
				addMessage(list, locale, resultMessage.getMessageId(), resultMessage.getMessageParams(), maxCount);
			}
			else {
				Throwable th = resultMessage.getThrowable();
				if(th != null){
					if (th instanceof IMessageCoded){
						// 결과메시지와 최초의 예외가 동일한 내용이 아닌경우 최초 메시지로 설정한다.
						if(!equals(resultMessage, (IMessageCoded)th)){
							addMessage(list, locale, resultMessage.getMessageId(), resultMessage.getMessageParams(), maxCount);
						}
					}
					addMessage(list, locale, th, maxCount);
				}
				else {
					addMessage(list, locale, resultMessage.getMessageId(), resultMessage.getMessageParams(), maxCount);
				}
			}
		}
		// 부가 메시지 등록
		if(additionalList != null){
			for(TrtmRsltMsg m : additionalList){
				// 메시지 건수를 체크한다.
				checkMessageCount(list, maxCount);
				list.add(m);
			}
		}
	}

	public static void addMessage(List<TrtmRsltMsg> list, Locale locale, String messageId, String[] messageParams, int maxCount) {
		// 메시지 건수를 체크한다.
		checkMessageCount(list, maxCount);
		// 메시지 등록
		list.add(new TrtmRsltMsg(messageId, BaseUtils.getMessage(messageId, locale, messageParams)));
	}

	/**
	 * 예외객체를 CommonArea 표준전문헤더 메시지 목록으로 변환 최대 입력된 건수까지만 등록한다.
	 */
	public static void addMessage(List<TrtmRsltMsg> list, Locale locale, Throwable exception) {
		addMessage(list, locale, exception, FlatHeaderSpec.MSG_CCNT.max());
	}

	/**
	 * 예외객체를 CommonArea 표준전문헤더 메시지 목록으로 변환 최대 입력된 건수까지만 등록한다.
	 */
	public static void addMessage(List<TrtmRsltMsg> list, Locale locale, Throwable exception, int maxCount) {
		//InvocationTargetException은 메시지화 하지 않는다.
		if(!(exception instanceof java.lang.reflect.InvocationTargetException) ){
			// 메시지 건수를 체크한다.
			checkMessageCount(list, maxCount);
			// 메시지 등록
			list.add(getMessage(locale, exception));
		}
		
		Throwable cause = exception.getCause();
		if (cause != null) {
			addMessage(list, locale, cause, maxCount);
		}
	}
	
	private static void checkMessageCount(List<TrtmRsltMsg> list, int maxCount){
		if (list.size() >= maxCount) {
			// throw된 예외인 경우 10번째에 채우기 위해 삭제한다.
			list.remove(maxCount - 1);
		}
	}
	
	private static TrtmRsltMsg getMessage(Locale locale, Throwable exception) {
		String code = null;
		String message = null;
		if (exception instanceof IMessageCoded) {
			IMessageCoded imc = (IMessageCoded) exception;
			code = imc.getMessageId();
			message = BaseUtils.getMessage(code, locale, imc.getMessageParams());
		} else {
			code = RAW_LEVEL_ERROR_ID;
			message = exception.toString();
		}

		int erorOcrnPrrmLine = 0;
		String erorOcrnPrrmNm = null;
		StackTraceElement[] stes = exception.getStackTrace();
		if (stes != null) {
			for (StackTraceElement ste : stes) {
				int index = ste.getClassName().lastIndexOf(".");
				erorOcrnPrrmNm = (index > -1 ? ste.getClassName().substring(index + 1) : ste.getClassName()) + SLASH + ste.getMethodName();
				erorOcrnPrrmLine = ste.getLineNumber();
				break;
			}
		}
		return new TrtmRsltMsg(code, message, erorOcrnPrrmLine, erorOcrnPrrmNm);
	}
	
	/**
	 * 동일 메시지 여부 확인
	 */
	private static boolean equals(IMessageCoded source, IMessageCoded compare){
		if(!source.getMessageId().equals(compare.getMessageId())){
			return false;
		}
		
		int slen = source.getMessageParams() == null ? 0 : source.getMessageParams().length;
		int clen = compare.getMessageParams() == null ? 0 : compare.getMessageParams().length;
		if(slen != clen){
			return false;
		}
		
		for(int i=0; i<slen; i++){
			String sp = source.getMessageParams()[i];
			String cp = compare.getMessageParams()[i];
			if(sp != null && cp != null && !sp.equals(cp)){
//			if(!source.getMessageParams()[i].equals(compare.getMessageParams()[i])){
				return false;
			}
		}
		
		return true;
	}
	
	private static String removeString(Map<String, String> map, FlatHeaderSpec spec) {
		return map.remove(spec.name());
	}

	private static String removeString(Map<String, String> map, FlatHeaderSpec spec, int index) {
		return map.remove(spec.name() + index);
	}

	private static int removeInt(Map<String, String> map, FlatHeaderSpec spec) {
		return parseInt(map.remove(spec.name()));
	}

	private static int removeInt(Map<String, String> map, FlatHeaderSpec spec, int index) {
		return parseInt(map.remove(spec.name() + index));
	}

	private static long removeLong(Map<String, String> map, FlatHeaderSpec spec) {
		return parseLong(map.remove(spec.name()));
	}

	private static double removeDouble(Map<String, String> map, FlatHeaderSpec spec) {
		return parseDouble(map.remove(spec.name()));
	} 
	
	private static int parseInt(Map<String, String> map, FlatHeaderSpec spec) {
		String value = map.get(spec.name());
		if (value == null || "".equals(value)) {
			return 0;
		}
		return Integer.parseInt(value.trim());
	}

	private static int parseInt(String value) {
		if (value == null || "".equals(value)) {
			return 0;
		}
		return Integer.parseInt(value.trim());
	}

	private static long parseLong(String value) {
		if (value == null || "".equals(value)) {
			return 0;
		}
		return Long.parseLong(value.trim());
	}
	
	private static Double parseDouble(String value) {
		if (value == null || "".equals(value)) {
			return 0D;
		}
		return Double.parseDouble(value.trim());
	}
	
	private static void put(Map<String, String> map, FlatHeaderSpec spec, String value){
		map.put(spec.name(), value);
	}

	private static void put(Map<String, String> map, FlatHeaderSpec spec, int value){
		map.put(spec.name(), String.valueOf(value));
	}

	private static void put(Map<String, String> map, FlatHeaderSpec spec, long value){
		map.put(spec.name(), String.valueOf(value));
	}

	private static void put(Map<String, String> map, FlatHeaderSpec spec, int index, String value){
		map.put(spec.name() + index, value);
	}
	
}

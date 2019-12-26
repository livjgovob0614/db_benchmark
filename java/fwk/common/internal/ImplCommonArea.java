package fwk.common.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nexcore.framework.core.util.ByteArrayWrap;
import fwk.common.CommonArea;
import fwk.common.TrtmRsltMsg;
import fwk.flat.EaiFlatHeaderSpec;

/**
 * @author Administrator
 * 
 */
public class ImplCommonArea extends CommonArea implements java.io.Serializable{
    
    private static final long serialVersionUID = -2932957375736138954L;
    
	private ByteArrayWrap inptMesg; // 입력전문(전체)
	private ImplFlatHeader flatHeader;
	private ImplCommonAreaBiz commonAreaBiz; // 업무영역
	private Map<String, String> eaiHeader;// eai로 부터 넘어온 Header정보

	// I.거래일반

	// II.전송시스템

	// III.연동

	// IV.일자

	// V.사용자

	// VI. 시제

	/**
	 * 전문 헤더취득
	 * 
	 * @return
	 */
	public ImplFlatHeader getFlatHeader() {
		if (flatHeader == null) {
			flatHeader = new ImplFlatHeader();
		}
		return flatHeader;
	}

	/**
	 * 전문 헤더입력
	 * 
	 * @param hpcHeader
	 */
	public void setFlatHeader(ImplFlatHeader flatHeader) {
		this.flatHeader = flatHeader;
	}

	/**
	 * 표준전문헤더
	 * 
	 * @return 표준전문헤더
	 */
	public ImplFlatHeader existsFlatHeader() {
		return flatHeader;
	}

	public ImplCommonAreaBiz getCommonAreaBiz() {
		if (commonAreaBiz == null) {
			commonAreaBiz = new ImplCommonAreaBiz();
		}
		return commonAreaBiz;
	}

	/**
	 * EAI Header를 put한다.
	 * 
	 * @param eaiHeader
	 */
	public void putEaiHeader(Map<String, String> eaiHeader) {
		this.eaiHeader = eaiHeader;
	}

	/**
	 * EAI Header를 return한다.
	 * @return
	 */
	public Map<String, String> getEaiHeader() {
		if (eaiHeader == null) {
			eaiHeader = new HashMap<String, String>();
		}
		return eaiHeader;
	}

	/**
	 * EAI헤더에서 key에 맞는 value를 return한다.
	 * @param key
	 * @return
	 */
	public String getEaiHeaderValue(String key) {
		return getEaiHeader().get(key);
	}

	/**
	 * 입력전문(전체)
	 * 
	 * @return 입력전문(전체)
	 */
	public ByteArrayWrap getInptMesg() {
		return inptMesg;
	}

	/**
	 * 입력전문(전체)
	 * 
	 * @param inptMesg
	 *            입력전문(전체)
	 */
	public void setInptMesg(ByteArrayWrap inptMesg) {
		this.inptMesg = inptMesg;
	}

	/**
	 * 글로벌 ID를 리턴한다
	 * 
	 * @return String globId
	 */
	public String getGlobId() {
		return getFlatHeader().getGlobId();
	}

	/**
	 * 글로벌ID를 설정한다.
	 * 
	 * @param globId
	 */
	public void setGlobId(String globId) {
		this.getFlatHeader().setGlobId(globId);
	}

	/**
	 * 진행일련번호를 리턴한다
	 * 
	 * @return int prgsSrno
	 */
	public int getPrgsSrno() {
		return getFlatHeader().getPrgsSrno();
	}

	/**
	 * 진행일련번호를 입력한다.
	 * 
	 * @param prgsSrno
	 */
	public void setPrgsSrno(int prgsSrno) {
		this.getFlatHeader().setPrgsSrno(prgsSrno);
	}

	/**
	 * 환경 구분코드를 리턴한다
	 * 
	 * @return String envDvcd
	 */
	public String getEnvDvcd() {
		return getFlatHeader().getEnvDvcd();
	}

	/**
	 * 환경구분코드를 설정한다.
	 * 
	 * @param envDvcd
	 */
	public void setEnvDvcd(String envDvcd) {
		this.getFlatHeader().setEnvDvcd(envDvcd);
	}

	/**
	 * 처리결과코드를 리턴한다
	 * 
	 * @return String trtmRsltCd
	 */
	public String getTrtmRsltCd() {
		return getFlatHeader().getTrtmRsltCd();
	}

	/**
	 * 처리결과코드를 설정한다.
	 * 
	 * @param trtmRsltCd
	 */
	public void setTrtmRsltCd(String trtmRsltCd) {
		this.getFlatHeader().setTrtmRsltCd(trtmRsltCd);
	}

	/**
	 * 거래코드를 리턴한다.
	 * 
	 * @return String trnCd
	 */
	public String getTrnCd() {
		return getFlatHeader().getTrnCd();
	}

	/**
	 * 거래코드를 설정한다.
	 * 
	 * @param trnCd
	 */
	public void setTrnCd(String trnCd) {
		this.getFlatHeader().setTrnCd(trnCd);
	}

	/**
	 * 화면번호를 리턴한다.
	 * 
	 * @return String scrnNo
	 */
	public String getScrnNo() {
		return getFlatHeader().getScrnNo();
	}

	/**
	 * 화면번호를 설정한다.
	 * 
	 * @param scrnNo
	 */
	public void setScrnNo(String scrnNo) {
		this.getFlatHeader().setScrnNo(scrnNo);
	}

	/**
	 * 버전구분코드를 리턴한다.
	 * 
	 * @return String mesgVrsnDvcd
	 */
	public String getMesgVrsnDvcd() {
		return getFlatHeader().getMesgVrsnDvcd();
	}

	/**
	 * 버전구분코드를 설정한다.
	 * 
	 * @param mesgVrsnDvcd
	 */
	public void setMesgVrsnDvcd(String mesgVrsnDvcd) {
		this.getFlatHeader().setMesgVrsnDvcd(mesgVrsnDvcd);
	}

	/**
	 * 구분코드( Q:요청; R:응답; P:push)를 리턴한다.
	 * 
	 * @return String mesgDvcd
	 */
	public String getMesgDvcd() {
		return getFlatHeader().getMesgDvcd();
	}

	/**
	 * 구분코드( Q:요청; R:응답; P:push)를 설정한다.
	 * 
	 * @param mesgDvcd
	 */
	public void setMesgDvcd(String mesgDvcd) {
		this.getFlatHeader().setMesgDvcd(mesgDvcd);
	}

	/**
	 * 유형코드를 리턴한다.
	 * 
	 * @return String mesgTycd
	 */
	public String getMesgTycd() {
		return getFlatHeader().getMesgTycd();
	}

	/**
	 * 유형코드를 설정한다.
	 * 
	 * @param mesgTycd
	 */
	public void setMesgTycd(String mesgTycd) {
		this.getFlatHeader().setMesgTycd(mesgTycd);
	}

	/**
	 * 전문연속일련번호
	 * 
	 * @return
	 */
	public int getMesgCntySrno() {
		return getFlatHeader().getMesgCntySrno();
	}

	/**
	 * 전문연속일련번호
	 * 
	 * @param mesgCntySrno
	 */
	public void setMesgCntySrno(int mesgCntySrno) {
		this.getFlatHeader().setMesgCntySrno(mesgCntySrno);
	}

	/**
	 * 캠패인영역사용구분코드를 리턴한다.
	 * 
	 * @return String cmpgRelmUseDvcd
	 */
	public String getCmpgRelmUseDvcd() {
		return getFlatHeader().getCmpgRelmUseDvcd();
	}

	/**
	 * 캠패인영역사용구분코드를 설정한다.
	 * 
	 * @param cmpgRelmUseDvcd
	 */
	public void setCmpgRelmUseDvcd(String cmpgRelmUseDvcd) {
		this.getFlatHeader().setCmpgRelmUseDvcd(cmpgRelmUseDvcd);
	}

	/**
	 * 대외기관코드를 리턴한다.
	 * 
	 * @return String xtisCd
	 */
	public String getXtisCd() {
		return getFlatHeader().getXtisCd();
	}

	/**
	 * 대외기관코드를 설정한다.
	 * 
	 * @param xtisCd
	 */
	public void setXtisCd(String xtisCd) {
		this.getFlatHeader().setXtisCd(xtisCd);
	}

	/**
	 * 업무서버코드를 리턴한다.
	 * 
	 * @return String bzwrSvrCd
	 */
	public String getBzwrSvrCd() {
		return getFlatHeader().getBzwrSvrCd();
	}

	/**
	 * 업무서버코드를 설정한다.
	 * 
	 * @param bzwrSvrCd
	 */
	public void setBzwrSvrCd(String bzwrSvrCd) {
		this.getFlatHeader().setBzwrSvrCd(bzwrSvrCd);
	}

	/**
	 * 대외전문코드를 리턴한다.
	 * 
	 * @return String otsdMesgCd
	 */
	public String getOtsdMesgCd() {
		return getFlatHeader().getOtsdMesgCd();
	}

	/**
	 * 대외전문코드를 설정한다.
	 * 
	 * @param otsdMesgCd
	 */
	public void setOtsdMesgCd(String otsdMesgCd) {
		this.getFlatHeader().setOtsdMesgCd(otsdMesgCd);
	}

	/**
	 * 대외전문처리코드를 리턴한다.
	 * 
	 * @return String otsdMesgTrtmCd
	 */
	public String getOtsdMesgTrtmCd() {
		return getFlatHeader().getOtsdMesgTrtmCd();
	}

	/**
	 * 대외전문처리코드를 설정한다.
	 * 
	 * @param otsdMesgTrtmCd
	 */
	public void setOtsdMesgTrtmCd(String otsdMesgTrtmCd) {
		this.getFlatHeader().getOtsdMesgTrtmCd();
	}

	/**
	 * 대외거래고유번호를 리턴한다.
	 * 
	 * @return String otsdTrnUnqNo
	 */
	public String getOtsdTrnUnqNo() {
		return getFlatHeader().getOtsdTrnUnqNo();
	}

	/**
	 * 대외거래고유번호를 설정한다.
	 * 
	 * @param otsdTrnUnqNo
	 */
	public void setOtsdTrnUnqNo(String otsdTrnUnqNo) {
		this.getFlatHeader().setOtsdTrnUnqNo(otsdTrnUnqNo);
	}

	/**
	 * 대외응답거래코드를 리턴한다.
	 * 
	 * @return String otsdRespTrnCd
	 */
	public String getOtsdRespTrnCd() {
		return getFlatHeader().getOtsdRespTrnCd();
	}

	/**
	 * 대외응답거래코드를 설정한다.
	 * 
	 * @param otsdRespTrnCd
	 */
	public void setOtsdRespTrnCd(String otsdRespTrnCd) {
		this.getFlatHeader().setOtsdRespTrnCd(otsdRespTrnCd);
	}

	/**
	 * 채널메시지코드를 리턴한다.
	 * 
	 * @return String chnlMsgCd
	 */
	public String getChnlMsgCd() {
		return getFlatHeader().getChnlMsgCd();
	}

	/**
	 * 채널메시지코드를 설정한다.
	 * 
	 * @param chnlMsgCd
	 */
	public void setChnlMsgCd(String chnlMsgCd) {
		this.getFlatHeader().setChnlMsgCd(chnlMsgCd);
	}

	/**
	 * 메시지건수
	 * 
	 * @return 메시지건수
	 */
	public int getMsgCcnt() {
		return getFlatHeader().getMsgCcnt();
	}

	/**
	 * 메시지
	 * 
	 * @param index
	 *            순번
	 * @return 메시지
	 */
	public TrtmRsltMsg getMsg(int index) {
		return getFlatHeader().getMsg(index);
	}

	/**
	 * 메시지 등록
	 * 
	 * @param msg
	 *            메시지
	 */
	public void addMsg(TrtmRsltMsg msg) {
		getFlatHeader().addMsg(msg);
	}

	/**
	 * 메시지목록 조회
	 * 
	 * @return 메시지목록
	 */
	public List<TrtmRsltMsg> getMsgList() {
		return getFlatHeader().getMsgList();
	}

	/**
	 * 메시지목록 등록
	 * 
	 * @param msgList
	 *            메시지목록
	 */
	public void setMsgList(List<TrtmRsltMsg> msgList) {
		getFlatHeader().setMsgList(msgList);
	}

	/**
	 * 메시지 목록 초기화
	 */
	public void clearMsgList() {
		getFlatHeader().clearMsgList();
	}

	/**
	 * 예비문자열내용
	 * 
	 * @return 예비문자열내용
	 */
	public String getSprChrsCntn() {
		return getFlatHeader().getSprChrsCntn();
	}

	/**
	 * 예비문자열내용
	 * 
	 * @param sprChrsCntn
	 *            예비문자열내용
	 */
	public void setSprChrsCntn(String sprChrsCntn) {
		getFlatHeader().setSprChrsCntn(sprChrsCntn);
	}

	/**
	 * IP주소를 리턴한다.
	 * 
	 * @return String ipad
	 */
	public String getIpad() {
		return getFlatHeader().getIpad();
	}

	/**
	 * IP주소를 입력한다.
	 * 
	 * @param ipad
	 */
	public void setIpad(String ipad) {
		this.getFlatHeader().setIpad(ipad);
	}

	/**
	 * MAC Address를 리턴한다.
	 * 
	 * @return String prcmMac
	 */
	public String getPrcmMac() {
		return getFlatHeader().getPrcmMac();
	}

	/**
	 * MAC Address를 설정한다.
	 * 
	 * @param prcmMac
	 */
	public void setPrcmMac(String prcmMac) {
		this.getFlatHeader().setPrcmMac(prcmMac);
	}

	/**
	 * SSO key를 리턴한다.
	 * 
	 * @return String ssoSesnKey
	 */
	public String getSsoSesnKey() {
		return getFlatHeader().getSsoSesnKey();
	}

	/**
	 * SSO key를 설정한다.
	 * 
	 * @param ssoSesnKey
	 */
	public void setSsoSesnKey(String ssoSesnKey) {
		this.getFlatHeader().setSsoSesnKey(ssoSesnKey);
	}

	/**
	 * 최초채널코드를 리턴한다.
	 * 
	 * @return String frstTrnmChnlCd
	 */
	public String getFrstTrnmChnlCd() {
		return getFlatHeader().getFrstTrnmChnlCd();
	}

	/**
	 * 최초채널코드를 설정한다.
	 * 
	 * @param frstTrnmChnlCd
	 */
	public void setFrstTrnmChnlCd(String frstTrnmChnlCd) {
		this.getFlatHeader().setFrstTrnmChnlCd(frstTrnmChnlCd);
	}

	/**
	 * 전송채널코드를 리턴한다.
	 * 
	 * @return String trnmChnlCd
	 */
	public String getTrnmChnlCd() {
		return getFlatHeader().getTrnmChnlCd();
	}

	/**
	 * 전송채널코드를 설정한다.
	 * 
	 * @param trnmChnlCd
	 */
	public void setTrnmChnlCd(String trnmChnlCd) {
		this.getFlatHeader().setTrnmChnlCd(trnmChnlCd);
	}

	/**
	 * 전송노드번호를 리턴한다.
	 * 
	 * @return String trnmNodeNo
	 */
	public int getTrnmNodeNo() {
		return getFlatHeader().getTrnmNodeNo();
	}

	/**
	 * 전송노드번호를 설정한다.
	 * 
	 * @param trnmNodeNo
	 */
	public void setTrnmNodeNo(int trnmNodeNo) {
		this.getFlatHeader().setTrnmNodeNo(trnmNodeNo);
	}

	/**
	 * MCI노드번호
	 */
	public int getMciTrnmNodeNo() {
		return this.getFlatHeader().getMciTrnmNodeNo();
	}

	/**
	 * MCI노드번호
	 * 
	 * @param mciTrnmNodeNo
	 */
	public void setMciTrnmNodeNo(int mciTrnmNodeNo) {
		this.getFlatHeader().setMciTrnmNodeNo(mciTrnmNodeNo);
	}

	/**
	 * 거래단말번호
	 */
	public String getTrnTrnmNo() {
		return getFlatHeader().getTrnTrnmNo();
	}

	/**
	 * 거래단말번호
	 * 
	 * @param trnTrnmNo
	 */
	public void setTrnTrnmNo(String trnTrnmNo) {
		this.getFlatHeader().setTrnTrnmNo(trnTrnmNo);
	}

	/**
	 * 연동깊이를 리턴한다.
	 * 
	 * @return String iitlkDpth
	 */
	public int getItlkDpth() {
		return getCommonAreaBiz().getItlkDpth();
	}

	/**
	 * 연동깊이를 설정한다.
	 * 
	 * @param iitlkDpth
	 */
	public void setItlkDpth(int itlkDpth) {
		this.getCommonAreaBiz().setItlkDpth(itlkDpth);
	}

	/**
	 * 최초거래코드를 리턴한다.
	 * 
	 * @return String frstTrnCd
	 */
	public String getFrstTrnCd() {
		return getCommonAreaBiz().getFrstTrnCd();
	}

	/**
	 * 최초거래코드를 입력한다.
	 * 
	 * @param frstTrnCd
	 */
	public void setFrstTrnCd(String frstTrnCd) {
		this.getCommonAreaBiz().setFrstTrnCd(frstTrnCd);
	}

	/**
	 * 기동거래코드를 리턴한다.
	 * 
	 * @return String mvTrnCd
	 */
	public String getMvTrnCd() {
		return getCommonAreaBiz().getMvTrnCd();
	}

	/**
	 * 기동거래코드를 설정한다.
	 * 
	 * @param mvTrnCd
	 */
	public void setMvTrnCd(String mvTrnCd) {
		this.getCommonAreaBiz().setMvTrnCd(mvTrnCd);
	}

	/**
	 * 시재발생여부
	 * 
	 * @return
	 */
	public String getCshnOcrnYn() {
		return getFlatHeader().getCshnOcrnYn();
	}

	/**
	 * 시재발생여부
	 * 
	 * @param cshnOcrnYn
	 */
	public void setCshnOcrnYn(String cshnOcrnYn) {
		this.getFlatHeader().setCshnOcrnYn(cshnOcrnYn);
	}

	/**
	 * 결제금액
	 * 
	 * @return
	 */
	public double getCashAmt() {
		return getFlatHeader().getCashAmt();
	}

	/**
	 * 결제금액
	 * 
	 * @param cashAmt
	 */
	public void setCashAmt(double cashAmt) {
		this.getFlatHeader().setCashAmt(cashAmt);
	}

	/**
	 * 포인트 금액
	 * 
	 * @return
	 */
	public double getPointAmt() {
		return getFlatHeader().getPointAmt();
	}

	/**
	 * 포인트 금액
	 * 
	 * @param pointAmt
	 */
	public void setPointAmt(double pointAmt) {
		this.getFlatHeader().setPointAmt(pointAmt);
	}

	/**
	 * sync/async구분을 리턴한다.
	 * 
	 * @return
	 */
	public String getSyncDv() {
		return getCommonAreaBiz().getSyncDv();
	}

	/**
	 * sync/async구분을 설정한다.
	 * 
	 * @param syncDv
	 */
	public void setSyncDv(String syncDv) {
		this.getCommonAreaBiz().setSyncDv(syncDv);
	}

	/**
	 * 전문요청일시를 리턴한다.
	 * 
	 * @return String mesgDmndDttm
	 */
	public String getMesgDmndDttm() {
		return getFlatHeader().getMesgDmndDttm();
	}

	/**
	 * 전문요청일시를 설정한다.
	 * 
	 * @param mesgDmndDttm
	 */
	public void setMesgDmndDttm(String mesgDmndDttm) {
		this.getFlatHeader().setMesgDmndDttm(mesgDmndDttm);
	}

	/**
	 * 전문응답일시를 리턴한다.
	 * 
	 * @return String mesgRespDttm
	 */
	public String getMesgRespDttm() {
		return getFlatHeader().getMesgRespDttm();
	}

	/**
	 * 전문응답일시를 설정한다.
	 * 
	 * @param mesgRespDttm
	 */
	public void setMesgRespDttm(String mesgRespDttm) {
		this.getFlatHeader().setMesgRespDttm(mesgRespDttm);
	}

	/**
	 * 거래유형구분코드
	 * 
	 * @return 거래유형구분코드
	 */
	public String getTrnPtrnDvcd() {
		return getFlatHeader().getTrnPtrnDvcd();
	}

	/**
	 * 거래유형구분코드
	 */
	public void setTrnPtrnDvcd(String trnPtrnDvcd) {
		this.getFlatHeader().setTrnPtrnDvcd(trnPtrnDvcd);

	}

	/**
	 * 거래일자를 리턴한다.
	 * 
	 * @return String trnDt
	 */
	public String getTrnDt() {
		return getCommonAreaBiz().getTrnDt();
	}

	/**
	 * 거래일자를 설정한다.
	 * 
	 * @param trnDt
	 */
	public void setTrnDt(String trnDt) {
		this.getCommonAreaBiz().setTrnDt(trnDt);
	}

	/**
	 * 로그영업일자를 리턴한다.
	 * 
	 * @return String logBzopDt
	 */
	public String getLogBzopDt() {
		return getCommonAreaBiz().getLogBzopDt();
	}

	/**
	 * 로그영업일자를 설정한다.
	 * 
	 * @param logBzopDt
	 */
	public void setLogBzopDt(String logBzopDt) {
		this.getCommonAreaBiz().setLogBzopDt(logBzopDt);
	}

	/**
	 * 서비스시작일시를 리턴한다.
	 * 
	 * @return String getSvcStrnDttm
	 */
	public String getSvcStrnDttm() {
		return getCommonAreaBiz().getSvcStrnDttm();
	}

	/**
	 * 서비스시작일시를 설정한다.
	 * 
	 * @param svcStrnDttm
	 */
	public void setSvcStrnDttm(String svcStrnDttm) {
		this.getCommonAreaBiz().setSvcStrnDttm(svcStrnDttm);
	}

	/**
	 * 서비스종료일시를 리턴한다.
	 * 
	 * @return String svcEndDttm
	 */
	public String getSvcEndDttm() {
		return getCommonAreaBiz().getSvcEndDttm();
	}

	/**
	 * 서비스종료일시를 설정한다.
	 * 
	 * @param svcEndDttm
	 */
	public void setSvcEndDttm(String svcEndDttm) {
		this.getCommonAreaBiz().setSvcEndDttm(svcEndDttm);
	}

	/**
	 * 회사코드
	 * 업무팀에서 사용시에는 getCoCd()를 사용하도록 한다.
	 * @return 회사코드
	 */
	@Deprecated
	public String getCompCd() {
		return getFlatHeader().getCompCd();
	}

	/**
	 * 회사코드
	 * 업무팀에서 사용시에는 setCoCd()를 사용하도록 한다.
	 * @param compCd
	 */
	@Deprecated
	public void setCompCd(String compCd) {
		this.getFlatHeader().setCompCd(compCd);
	}

	/**
	 * 부서코드를 리턴한다.
	 * FlatHeader에서 취득하던 부서코드를 CommonAreaBiz에서 취득하도록 변경함. (2014-09-11 by PSI)
	 * @return
	 */
	public String getDeptCd() {
		return getCommonAreaBiz().getDeptCd();
	}

	/**
	 * 부서코드를 설정한다.
	 * FlatHeader에서 설정하던 부서코드를 CommonAreaBiz에서 설정하도록 변경함. (2014-09-11 by PSI)
	 * @param deptCd
	 */
	public void setDeptCd(String deptCd) {
		this.getCommonAreaBiz().setDeptCd(deptCd);
	}

	/**
	 * 사용자번호를 리턴한다.
	 * 
	 * @return String userNo
	 */
	public String getUserNo() {
		return getFlatHeader().getUserNo();
	}

	/**
	 * 사용자번호를 설정한다.
	 * 
	 * @param userNo
	 */
	public void setUserNo(String userNo) {
		this.getFlatHeader().setUserNo(userNo);
	}

	   /**
     * 사용자이름을 리턴한다.
     * 
     * @return String userNm
     */
    public String getUserNm() {
        return getCommonAreaBiz().getUserNm();
    }

    /**
     * 사용자이름 설정한다.
     * 
     * @param userNm
     */
    public void setUserNm(String userNm) {
        this.getCommonAreaBiz().setUserNm(userNm);
    }
	/**
	 * 부서구분코드를 리턴한다.
	 * 
	 * @return String deptDvcd
	 */
	public String getDeptDvcd() {
		return getCommonAreaBiz().getDeptDvcd();
	}

	/**
	 * 부서구분코드를 설정한다.
	 * 
	 * @param deptDvcd
	 */
	public void setDeptDvcd(String deptDvcd) {
		this.getCommonAreaBiz().setDeptDvcd(deptDvcd);
	}

	/**
	 * 부점코드
	 */
	public String getBrCd() {
		return getFlatHeader().getBrCd();
	}

	/**
	 * 부점코드
	 * 
	 * @param brCd
	 */
	public void setBrCd(String brCd) {
		this.getFlatHeader().setBrCd(brCd);
	}

	/**
	 * 사용자구분코드
	 * 
	 * @return 사용자구분코드
	 */
	public String getUserClCd() {
		return getCommonAreaBiz().getUserClCd();
	}

	/**
	 * 사용자구분코드
	 */
	public void setUserClCd(String userClCd) {
		this.getCommonAreaBiz().setUserClCd(userClCd);
	}

	/**
     * 직급코드
     *  
     * @return String
     */
    public String getJobrCd() {
        return this.getCommonAreaBiz().getJobrCd();
    }

    /**
     * 직급코드
     *  
     * @param jobrCd void
     */
    public void setJobrCd(String jobrCd) {
        this.getCommonAreaBiz().setJobrCd( jobrCd);
    }

    /**
     * 직책코드
     *  
     * @return String
     */
    public String getJtilCd() {
        return this.getCommonAreaBiz().getJtilCd();
    }

    /**
     * 직책코드
     *  
     * @param jtilCd void
     */
    public void setJtilCd(String jtilCd) {
        this.getCommonAreaBiz().setJtilCd(jtilCd);
    }

    /**
     * HPC프로젝트 회사코드
     *  
     * @return String
     */
    public String getCoCd() {
        return this.getCommonAreaBiz().getCoCd();
    }

    /**
     * HPC프로젝트 회사코드
     *  
     * @param coCd void
     */
    public void setCoCd(String coCd) {
        this.getCommonAreaBiz().setCoCd(coCd);
    }

    /**
     * 대리점코드
     *  
     * @return String
     */
    public String getDealCd() {
        return this.getCommonAreaBiz().getDealCd();
    }

    /**
     * 대리점코드
     *  
     * @param dealCd void
     */
    public void setDealCd(String dealCd) {
        this.getCommonAreaBiz().setDealCd(dealCd);
    }


    /**
     * 사원번호
     *  
     * @return String
     */
    public String getEmpNo() {
        return this.getCommonAreaBiz().getEmpNo();
    }

    /**
     *  사원번호
     *  
     * @param empNo void
     */
    public void setEmpNo(String empNo) {
        this.getCommonAreaBiz().setEmpNo(empNo);
    }

    /**
     * 권한회사목록
     *  
     * @return String
     */
    public List<String> getAutrCoLst() {
        return this.getCommonAreaBiz().getAutrCoLst();
    }

    /**
     * 권한회사목록
     *  
     * @param autrCoLst void
     */
    public void setAutrCoLst(List<String> autrCoLst) {
        this.getCommonAreaBiz().setAutrCoLst(autrCoLst);
    }
    
    /**
     * 권한 회사를 추가
     *  
     * @param autrCo void
     */
    public void addAutrCoLst(String autrCo) {
        this.getAutrCoLst().add(autrCo);
    }
    

    /**
     * 권한브랜드목록
     *  
     * @return String
     */
    public List<String> getAutrBrndLst() {
        return this.getCommonAreaBiz().getAutrBrndLst();
    }

    /**
     *권한브랜드목록
     *  
     * @param autrBrndLst void
     */
    public void setAutrBrndLst(List<String> autrBrndLst) {
        this.getCommonAreaBiz().setAutrBrndLst(autrBrndLst);
    }
    
    /**
     * 권한브랜드를 추가
     *  
     * @param autrBrnd void
     */
    public void addAutrBrnd(String autrBrnd) {
        this.getAutrBrndLst().add(autrBrnd);
    }

    /**
     * 권한가맹점목록
     *  
     * @return List<String>
     */
    public List<String> getAutrMchtLst() {
        return this.getCommonAreaBiz().getAutrMchtLst();
    }

    /**
     * 권한가맹점목록
     *  
     * @param autrMchtLst void
     */
    public void setAutrMchtLst(List<String> autrMchtLst) {
        this.getCommonAreaBiz().setAutrMchtLst(autrMchtLst);
    }
    
    /**
     * 권한가맹점을 추가
     *  
     * @param autrMch void
     */
    public void addAutrMch(String autrMch) {
        this.getAutrMchtLst().add(autrMch);
    }
    
    /**
     * 요청브랜드코드
     *  
     * @return String
     */
    public String getReqBrndCd() {
        return this.getFlatHeader().getReqBrndCd();
    }
    
    /**
     * 요청브랜드코드
     *  
     * @return String
     */
    public void setReqBrndCd(String reqBrndCd) {
        this.getFlatHeader().setReqBrndCd(reqBrndCd);
    }
    
    /**
     * UI요청채널코드
     *  
     * @return String
     */
    public String getReqChnlCd() {
        return this.getFlatHeader().getReqChnlCd();
    }
    
    /**
     * UI요청채널코드
     *  
     * @return String
     */
    public void setReqChnlCd(String reqChnlCd) {
        this.getFlatHeader().setReqChnlCd(reqChnlCd);
    }
    
    /**
     * 백오피스여부
     *  
     * @return String
     */
    public String getIsBckOffice() {
        return this.getFlatHeader().getIsBckOffice();
    }
    
    /**
     * 백오피스여부
     *  
     * @return String
     */
    public void setIsBckOffice(String isBckOffice) {
        this.getFlatHeader().setIsBckOffice(isBckOffice);
    }
	/**
	 * CTI여부
	 * 
	 * @return CTI여부
	 */
	public String getCtiYn() {
		return this.getFlatHeader().getCtiYn();
	}

	/**
	 * CTI여부
	 * 
	 * @param ctiYn
	 */
	public void setCtiYn(String ctiYn) {
		this.getFlatHeader().setCtiYn(ctiYn);
	}

	/**
	 * 사용자로케일
	 * 
	 * @return 사용자로케일
	 */
	public String getUserLocale() {
		return getFlatHeader().getUserLocale();
	}

	/**
	 * 사용자로케일
	 * 
	 * @param userLocale
	 */
	public void setUserLocale(String userLocale) {
		this.getFlatHeader().setUserLocale(userLocale);
	}

	/**
	 * 업무개별데이터
	 * 
	 * @return
	 */
	public String getBzwrIncsData() {
		return getCommonAreaBiz().getBzwrIncsData();
	}

	/**
	 * 업무개별데이터
	 */
	public void setBzwrIncsData(String bzwrIncsData) {
		getCommonAreaBiz().setBzwrIncsData(bzwrIncsData);
	}

	
	/**
	 * EAI Global ID
	 */
	public String getEaiGlobId() {
		StringBuffer sb = new StringBuffer();
		sb.append(getEaiHeaderValue(EaiFlatHeaderSpec.TGRM_DDTM.name()));
		sb.append(getEaiHeaderValue(EaiFlatHeaderSpec.TGRM_CRT_SYSNM.name()));
		sb.append(getEaiHeaderValue(EaiFlatHeaderSpec.TGRM_CRT_NO.name()));
		return sb.toString();
	}
	
	/**
	 *EAI 인터페이스 ID 
	 * @return
	 */
	public String getEaiIntfId() {
		return getEaiHeaderValue(EaiFlatHeaderSpec.EAI_INTF_ID.name());
	}
	
	/**
	 * 결과수신서비스ID
	 * @return
	 */
	public String getEaiRecvSvcid() {
		return getEaiHeaderValue(EaiFlatHeaderSpec.RSLT_RCEV_SVCID.name());
	}
	
	
	/**
	 * 업무응답코드
	 *@return String 
	 */
	public String getRpsCd() {
		return getCommonAreaBiz().getRpsCd();
	}
	
	/**
	 * 업무응답코드
	 */
	@Override
	public void setRpsCd(String rpsCd) {
		getCommonAreaBiz().setRpsCd(rpsCd);
	}
	/**
	 * 업무상세응답코드
	 *@return String 
	 */
	@Override
	public String getRpsDtlCd() {
		return getCommonAreaBiz().getRpsDtlCd();
	}
	
	/**
	 * 업무상세응답코드
	 */
	@Override
	public void setRpsDtlCd(String rpsDtlCd) {
		getCommonAreaBiz().setRpsDtlCd(rpsDtlCd);
	}
	/**
	 * CommonArea 복제
	 */
	public ImplCommonArea clone() {
		ImplCommonArea o = new ImplCommonArea();
		o.flatHeader = flatHeader == null ? null : flatHeader.clone();
		o.commonAreaBiz = commonAreaBiz == null ? null : commonAreaBiz.clone();
		return o;
	}

	/**
	 * CommonArea 역복제
	 */
	public void recover(ImplCommonArea source) {
		// #################
		// VI.시재
		// #################
		setCshnOcrnYn(source.getCshnOcrnYn()); // 시재발생여부
		setCashAmt(source.getCashAmt()); // 현금금액
		setPointAmt(source.getPointAmt()); // 포인트금액
//		setRpsCd(source.getRpsCd());//응답코드
//		setRpsDtlCd(source.getRpsDtlCd());//응답상세코드 

	}
}

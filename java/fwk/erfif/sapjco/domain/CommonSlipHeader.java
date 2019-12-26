/**
 * 
 */
package fwk.erfif.sapjco.domain;

import java.io.Serializable;
import java.util.Locale;

import fwk.constants.SlipConstants;
import fwk.utils.HpcUtils;


/**
 * @author greatjin
 *
 */
public class CommonSlipHeader implements Serializable{
	
   
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String serNo   ; //일련번호(전표헤더:아이템 매핑 번호)    
    String dmsTyp  ; //R사업 비즈니스 타입                
    String evdcDt  = HpcUtils.getCurrentDate(Locale.KOREA, SlipConstants.YYYYMMDD); //증빙일(검수일)	2015.06.24          
    String pstngDt = HpcUtils.getCurrentDate(Locale.KOREA, SlipConstants.YYYYMMDD); //전기일	2015.06.29                  
    String slipTyp = SlipConstants.SLIP_TYP_KR; //전표유형	KR                        
    String coCd    = SlipConstants.CO_CD_SKCC ; //회사코드	SKCC                      
    String currency= SlipConstants.CURRENCY_KRW; //통화	KRW
    String userNo  ; //카드번호(작성자 사번)
    String exCaldt ; //환산일자	                          
    String ref     ; //참조 	            
    String xref    ; //참조X
    String hdrTxt  ; //헤더텍스트	추후정의                
    String xchgrt  ; //환율	                              
    String transCd ; //트랜잭션코드	                      
	public String getSerNo() {
		return serNo;
	}
	public void setSerNo(String serNo) {
		this.serNo = serNo;
	}

	
	
	public String getDmsTyp() {
		return dmsTyp;
	}
	public void setDmsTyp(String dmsTyp) {
		this.dmsTyp = dmsTyp;
	}
	public String getEvdcDt() {
		return evdcDt;
	}
	public void setEvdcDt(String evdcDt) {
		this.evdcDt = evdcDt;
	}
	public String getPstngDt() {
		return pstngDt;
	}
	public void setPstngDt(String pstngDt) {
		this.pstngDt = pstngDt;
	}
	public String getSlipTyp() {
		return slipTyp;
	}
	public void setSlipTyp(String slipTyp) {
		this.slipTyp = slipTyp;
	}
	public String getCoCd() {
		return coCd;
	}
	public void setCoCd(String coCd) {
		this.coCd = coCd;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public String getExCaldt() {
		return exCaldt;
	}
	public void setExCaldt(String exCaldt) {
		this.exCaldt = exCaldt;
	}
	public String getRef() {
		return ref;
	}
	public void setRef(String ref) {
		this.ref = ref;
	}
	public String getHdrTxt() {
		return hdrTxt;
	}
	public void setHdrTxt(String hdrTxt) {
		this.hdrTxt = hdrTxt;
	}
	public String getXchgrt() {
		return xchgrt;
	}
	public void setXchgrt(String xchgrt) {
		this.xchgrt = xchgrt;
	}
	public String getTransCd() {
		return transCd;
	}
	public void setTransCd(String transCd) {
		this.transCd = transCd;
	}
	public String getUserNo() {
		return userNo;
	}
	public void setUserNo(String userNo) {
		this.userNo = userNo;
	}
	public String getXref() {
		return xref;
	}
	public void setXref(String xref) {
		this.xref = xref;
	}
	@Override
	public String toString() {
		return "CommonSlipHeader [serNo=" + serNo + ", dmsTyp=" + dmsTyp
				+ ", evdcDt=" + evdcDt + ", pstngDt=" + pstngDt + ", slipTyp="
				+ slipTyp + ", coCd=" + coCd + ", currency=" + currency
				+ ", userNo=" + userNo + ", exCaldt=" + exCaldt + ", ref="
				+ ref + ", xref=" + xref + ", hdrTxt=" + hdrTxt + ", xchgrt="
				+ xchgrt + ", transCd=" + transCd + "]";
	}

	
}

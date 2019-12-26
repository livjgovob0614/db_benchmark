package fwk.constants.enums.sapjco;

/**
 * sap_slip_header
 * @author greatjin
 *
 */
public enum SAP_SLIP_HEADER {
    SER_NO    ("ZFISERIAL".trim(), "serNo    ".trim())//일련번호(전표헤더:아이템 매핑 번호) 
  , DMS_TYP   ("BU_TYPE  ".trim(), "dmsTyp   ".trim())//R사업 비즈니스 타입                 
  , EVDC_DT   ("BLDAT    ".trim(), "evdcDt   ".trim())//증빙일(검수일)	2015.06.24           
  , PSTNG_DT  ("BUDAT    ".trim(), "pstngDt  ".trim())//전기일	2015.06.29                   
  , SLIP_TYP  ("BLART    ".trim(), "slipTyp  ".trim())//전표유형	KR                         
  , CO_CD     ("BUKRS    ".trim(), "coCd     ".trim())//회사코드	SKCC                       
  , CURRENCY  ("WAERS    ".trim(), "currency ".trim())//통화	KRW                            
  , USER_NO   ("CCNUM    ".trim(), "userNo   ".trim())//카드번호(작성자 사번)                            
  , EXCAL_DT  ("WWERT    ".trim(), "excaldt  ".trim())//환산일자	                           
  , REF       ("XBLNR    ".trim(), "ref      ".trim())//참조 	                             
  , HDR_TXT   ("BKTXT    ".trim(), "hdrTxt   ".trim())//헤더텍스트	추후정의                 
  //, XCHGRT    ("KURSF    ".trim(), "xchgrt   ".trim())//환율 	                               
  , TRANS_CD  ("TCODE    ".trim(), "transCd  ".trim())//트랜잭션코드	                       
   ;
	
	private String sapCol  ;
	private String var     ;
	
	SAP_SLIP_HEADER(String sapCol, String var)
	{
		this.sapCol = sapCol;
		this.var    = var;
		
	}
	
	

	public String getSapCol() {
		return sapCol;
	}



	public String getVar() {
		return var;
	}



	public String toString()
	{
		return sapCol + var;
	}
	
}

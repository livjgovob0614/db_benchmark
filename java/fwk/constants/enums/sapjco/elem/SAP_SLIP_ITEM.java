package fwk.constants.enums.sapjco.elem;

/**
 * sap_slip_header
 * @author greatjin
 *
 */
public enum SAP_SLIP_ITEM {
    SER_NO       ("ZFISERIAL".trim(), "serNo      ".trim())  //일련번호(전표헤더:아이템 매핑 번호)                                                                           
  , DMS_SEQ      ("DMSSEQ   ".trim(), "dmsSeq     ".trim())  //시퀀스
  , PSTNG_KEY    ("NEWBS    ".trim(), "pstngKey   ".trim())  //전기키	31	40	40                                                                                
  , GL_DIRC      ("NEWUM    ".trim(), "glDirc     ".trim())  //특별 G/L지시자			                                                                              
  , DEAL_CO_CD   ("NEWKO    ".trim(), "dealCoCd   ".trim())  //GL계정 및 거래처코드	4102638611(벧엘텔레콤)	217950(미수금-기타)	416101(매입부가가치세)          
  , FISCL_ACNT_CD("HKONT    ".trim(), "fisclAcntCd".trim())  //회계 계정코드			                                                                                
  , AMT          ("WRBTR    ".trim(), "amt        ".trim())  //금액 	25,932,379	23,574,890	2357489                                                             
  , LOC_AMT      ("DMBTR    ".trim(), "locAmt     ".trim())  //현지금액			                                                                                    
  , TAX_CD       ("MWSKZ    ".trim(), "taxCd      ".trim())  //세금코드	V0(매입부가세(10%)-세금계산서(일반))		V0                                              
  , TAX_AMT      ("WMWST    ".trim(), "taxAmt     ".trim())  //세금금액			                                                                                    
  , LOCTAX       ("MWSTS    ".trim(), "loctax     ".trim())  //현지세금금액			                                                                                
  , LOC_TAXSTDM  ("HWBAS    ".trim(), "locTaxstdm ".trim())  //과세 표준액(현지 통화)			                                                                      
  , ACNT_TAXSTDM ("FWBAS    ".trim(), "acntTaxstdm".trim())  //과세 표준액(전표 통화) (TC)			23,574,890                                                        
  , BIZ_AREA     ("GSBER    ".trim(), "bizArea    ".trim())  //사업영역	5018	5018	                                                                            
  , BP           ("BUPLA    ".trim(), "bp         ".trim())  //사업장(세적지)	1100	1100	1100                                                                  
  , PRFITLS_CNTR ("PRCTR    ".trim(), "prfitlsCntr".trim())  //손익센터			                                                                                    
  , WBS_ELEM     ("PROJK    ".trim(), "wbsElem    ".trim())  //WBS요소(Cost code)		신규R WBS(추후 생성)	                                                      
  , COST_CNTR    ("KOSTL    ".trim(), "costCntr   ".trim())  //코스트센터(Charge)			                                                                          
  , INR_ORDER    ("AUFNR    ".trim(), "inrOrder   ".trim())  //내부오더(Charge)			                                                                            
  , FUNC_AREA    ("FKBER    ".trim(), "funcArea   ".trim())  //기능영역			                                                                                    
  , PAY_ALT      ("EMPFB    ".trim(), "payAlt     ".trim())  //지급대체인	1138612053(에스케이 네트웍스㈜)-여신이 있는경우 		                                  
  , REKN_DT      ("ZFBDT    ".trim(), "reknDt     ".trim())  //기산일			                                                                                      
  , PAY_COND     ("ZTERM    ".trim(), "payCond    ".trim())  //지급조건	A2R0(원화F/B 2차R) 		                                                                  
  , PAY_MTHD     ("ZLSCH    ".trim(), "payMthd    ".trim())  //지급방법	B(하나 F/B현금)-입력필드여부 세무팀 확인 필요		                                        
  , PAY_RSV      ("ZLSPR    ".trim(), "payRsv     ".trim())  //지급보류	입력필드여부 세무팀 확인 필요		                                                        
  , TXT          ("SGTXT    ".trim(), "txt        ".trim())  //텍스트 	추후정의	추후정의	                                                                      
  , DSIGN_FIELD  ("ZUONR    ".trim(), "dsignField ".trim())  //지정필드 	22101185-D001-02	22101185-D001-02	22101185-D001-02                                    
  , REF1         ("XREF1    ".trim(), "ref1       ".trim())  //참조키1 			                                                                                    
  , REF2         ("XREF2    ".trim(), "ref2       ".trim())  //참조키2			                                                                                      
  , REF3         ("XREF3    ".trim(), "ref3       ".trim())  //참조키3		    	    
	;
    
	private String sapCol  ;
	private String var     ;
	
	SAP_SLIP_ITEM(String sapCol, String var)
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

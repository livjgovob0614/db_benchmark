package fwk.common.internal;

/**
 * 
 */
public enum CommonAreaSpec {

	// I.거래일반
	GLOB_ID             								 ,//글로벌 ID	                
	PRGS_SRNO            							 ,//진행일련번호	            
	ENV_DVCD             							 ,//환경구분코드	            
	TRTM_RSLT_CD        						 ,//처리결과코드	            
	TRN_CD               								 ,//거래코드	                
	SCRN_NO             								 ,//화면번호	                
	TRN_PTRN_DVCD       					 ,//거래유형구분코드	        
	MESG_VRSN_DVCD       					 ,//버전구분코드	            
	MESG_TYCD            							 ,//유형코드	   
	MESG_CNTY_SRNO						 ,//전문연속일련번호
	MESG_DVCD           							 ,//구분코드	                
	CMPG_RELM_USE_DVCD   			 ,//캠패인영역사용구분코드	  
	XTIS_CD             								 ,//대외기관코드	            
	BZWR_SVR_CD          					  	 ,//업무서버코드	            
	OTSD_MESG_CD         					 ,//대외전문코드	            
	OTSD_MESG_TRTM_CD    			 ,//대외전문처리코드	        
	OTSD_TRN_UNQ_NO      				 ,//대외거래고유번호	        
	OTSD_RESP_TRN_CD     				 ,//대외응답거래코드	        
	CHNL_MSG_CD          					 	 ,//채널메시지코드	          
	MSG_CCNT             							 ,//메시지건수	              
	MSG_CD              								 ,//메시지코드n	              
	MSG_CNTN            							 ,//메시지내용n	              
	EROR_OCRN_PRRM_LINE 			 ,//오류발생 LINEn	          
	EROR_OCRN_PRRM_NM   			 ,//오류발생 APP명n	          
	SPR_CHRS_CNTN        					 ,//예비문자열내용	          
	REQ_BRND_CD                , //요청브랜드코드
    REQ_CHNL_CD                  , //UI요청채널코드
    IS_BCK_OFFICE               , //백오피스여부
	
	// II.전송시스템
	IPAD               									,//IP주소	          
	PRCM_MAC           							,//PC MAC주소	      
	TRN_TRNM_NO        						,//거래단말번호	    
	SSO_SESN_KEY       						,//SSO 세션 KEY	    
	FRST_TRNM_CHNL_CD  				,//최초전송채널코드	
	TRNM_CHNL_CD       						,//전송채널코드	    
	TRNM_NODE_NO       					,//전송노드번호	    
	MCI_TRNM_NODE_NO				,//MCI전송노드번호
//	FEP_TRNM_NODE_NO   				,//FEP전송노드번호
	EAI_GLOB_ID									,//EAI Global ID
	EAI_INTF_ID									,//EAI 인터페이스ID
	EAI_RCEV_SVCID							,//EAI 결과수신서비스ID
	
	// III.연동
	ITLK_DPTH   									,//연동깊이	          
	FRST_TRN_CD 								,//최초거래코드	      
	MV_TRN_CD   									,//기동거래코드	      
	SYNC_DV     										,//Sync/Async구분	    

	// IV.일자
	MESG_DMND_DTTM    					,//전문요청일시	  
	MESG_RESP_DTTM    					,//전문응답일시	  
	TRN_DT            									,//거래일자	      
	LOG_BZOP_DT       							,//로그영업일자	  
	SVC_STRN_DTTM    						,//서비스시작일시	
	SVC_END_DTTM     						,//서비스종료일시	
	

	// V.사용자
	DEPT_CD            							 ,//부서코드	              
	COMP_CD           						 	 ,//회사코드
	USER_NO             							 ,//사용자번호	            
	DEPT_DVCD           						 ,//부서구분코드	          
	BR_CD               							 ,//부점코드	              
	USER_CL_CD        					     ,//사용자구분코드	        
	CTI_YN              							 ,//CTI 여부	        
	USER_LOCALE								 ,//사용자 로케일
	JOBR_CD                                       ,//직급코드        
	JTIL_CD                                         ,//직책코드        
	CO_CD                                            ,//회사코드      
	DEALCO_CD                                 ,//대리점코드   
	EMP_NO                                         ,//사원번호         


	//VII. 시제
	CSHN_OCRN_YN				,//시제여부
	CASH_AMT							,//결제금액
	POINT_AMT							,;//포인트금액
	
	
	//업무개별
//	BZWR_INCS_DATA 			,;//업무개별

    public static void main(String[] args){
    	for(CommonAreaSpec s: CommonAreaSpec.values()){
    	  //2015.10.13 jihooyim code inspector 점검 수정 (02-2.제거되지 않고 남은 디버그 코드(print))
    		//System.out.println(s.name()  );
    	}
    }
    
}

package fwk.common;

import java.util.List;

public abstract class CommonArea {
	/**
	 * 거래일반
	 */
	//글로벌 ID	                GLOB_ID
	//진행일련번호	            PRGS_SRNO
	//환경구분코드	            ENV_DVCD
	//처리결과코드	            TRTM_RSLT_CD
	//거래코드	                TRN_CD
	//화면번호	                SCRN_NO
	//거래유형구분코드	        TRN_PTRN_DVCD
	//버전구분코드	            MESG_VRSN_DVCD
	//유형코드	                MESG_TYCD
	//구분코드	                MESG_DVCD
	//캠패인영역사용구분코드	  CMPG_RELM_USE_DVCD
	//대외기관코드	            XTIS_CD
	//업무서버코드	            BZWR_SVR_CD
	//대외전문코드	            OTSD_MESG_CD
	//대외전문처리코드	        OTSD_MESG_TRTM_CD
	//대외거래고유번호	        OTSD_TRN_UNQ_NO
	//대외응답거래코드	        OTSD_RESP_TRN_CD
	//채널메시지코드	          CHNL_MSG_CD
	//메시지건수	              MSG_CCNT
	//메시지코드n	              MSG_CDn
	//메시지내용n	              MSG_CNTNn
	//오류발생 LINEn	          EROR_OCRN_PRRM_LINEn
	//오류발생 APP명n	          EROR_OCRN_PRRM_NMn
	//예비문자열내용	          SPR_CHRS_CNTN
	
	// I. 거래 일반 시작

		/**
		 * 글로벌 ID
		 * @return 글로벌 ID
		 */
		public abstract String getGlobId();

		/**
		 * 진행일련번호
		 * @return 진행일련번호
		 */
		public abstract int getPrgsSrno();

		/**
		 * 환경구분코드
		 * @return 환경구분코드
		 */
		public abstract String getEnvDvcd();

		/**
		 * 처리결과코드
		 * @return 처리결과코드
		 */
		public abstract String getTrtmRsltCd();

		/**
		 * 거래코드
		 * @return 거래코드
		 */
		public abstract String getTrnCd();

		/**
		 * 화면번호
		 * @return 화면번호
		 */
		public abstract String getScrnNo();

		/**
		 * 거래유형구분코드
		 * @return
		 */
		public abstract String getTrnPtrnDvcd();
		
		/**
		 * 전문버전구분코드
		 * @return 전문버전구분코드
		 */
		public abstract String getMesgVrsnDvcd();

		/**
		 * 구분코드
		 * @return 전문구분코드
		 */
		public abstract String getMesgDvcd();

		/**
		 *유형코드
		 * @return 전문유형코드
		 */
		public abstract String getMesgTycd();

		/**
		 * 전문일련번호
		 * @return
		 */
		public abstract int getMesgCntySrno();
		/**
		 * 캠패인영역사용구분코드
		 * @return 캠패인영역사용구분코드
		 */
		public abstract String getCmpgRelmUseDvcd();

		/**
		 * 캠패인영역사용구분코드
		 * @param cmpgRelmUseDvcd 캠패인영역사용구분코드
		 */
		public abstract void setCmpgRelmUseDvcd(String cmpgRelmUseDvcd);

		/**
		 * 대외기관코드
		 * @return 대외기관코드
		 */
		public abstract String getXtisCd();

		/**
		 * 대외기관코드
		 * @param xtisCd 대외기관코드
		 */
		public abstract void setXtisCd(String xtisCd);

		/**
		 * 업무서버코드
		 * @return 업무서버코드
		 */
		public abstract String getBzwrSvrCd();
		
		/**
		 * 업무서버코드 
		 * @param bzwrSvrCd 업무서버코드
		 */
		public abstract void setBzwrSvrCd(String bzwrSvrCd);

		/**
		 * 대외전문코드
		 * @return 대외전문코드
		 */
		public abstract String getOtsdMesgCd();

		/**
		 * 대외전문코드
		 * @param otsdMesgCd 대외전문코드
		 */
		public abstract void setOtsdMesgCd(String otsdMesgCd);

		/**
		 * 대외전문처리코드
		 * @return 대외전문처리코드
		 */
		public abstract String getOtsdMesgTrtmCd();

		/**
		 * 대외전문처리코드
		 * @param otsdMesgTrtmCd 대외전문처리코드
		 */
		public abstract void setOtsdMesgTrtmCd(String otsdMesgTrtmCd);

		/**
		 * 대외거래고유번호
		 * @return 대외거래고유번호
		 */
		public abstract String getOtsdTrnUnqNo();

		/**
		 * 대외거래고유번호
		 * @param otsdTrnUnqNo 대외거래고유번호
		 */
		public abstract void setOtsdTrnUnqNo(String otsdTrnUnqNo);

		/**
		 * 대외응답거래코드
		 * @return 대외응답거래코드
		 */
		public abstract String getOtsdRespTrnCd();

		/**
		 * 대외응답거래코드
		 * @param otsdRespTrnCd 대외응답거래코드
		 */
		public abstract void setOtsdRespTrnCd(String otsdRespTrnCd);

		/**
		 * 채널메시지코드
		 * @return 채널메시지코드
		 */
		public abstract String getChnlMsgCd();

		/**
		 * 채널메시지코드
		 * @param chnlMsgCd 채널메시지코드
		 */
		public abstract void setChnlMsgCd(String chnlMsgCd);
		
		/**
		 * 예비문자열
		 * @return
		 */
		public abstract String getSprChrsCntn();
		// I. 거래 일반 종료
		
		// II. 전송 시스템 시작

		/**
		 * IP주소
		 * @return IP주소
		 */
		public abstract String getIpad();

		/**
		 * PC MAC주소
		 * @return PC MAC주소
		 */
		public abstract String getPrcmMac();


		/**
		 * SSO 세션 KEY 
		 * @return SSO 세션 KEY 
		 */
		public abstract String getSsoSesnKey();

		/**
		 * 최초전송채널코드
		 * @return 최초전송채널코드
		 */
		public abstract String getFrstTrnmChnlCd();

		/**
		 * 전송채널코드
		 * @return 전송채널코드
		 */
		public abstract String getTrnmChnlCd();

		/**
		 * 전송노드번호
		 * @return 전송노드번호
		 */
		public abstract int getTrnmNodeNo();

		/**
		 * MCI전송노드번호
		 * @return MCI 전송노드번호
		 */
		public abstract int getMciTrnmNodeNo();
		
		/**
		 * 거래단말번호
		 * @return 거래단말번호
		 */
		public abstract String getTrnTrnmNo();
		// II. 전송 시스템 종료
		
		// III. 연동 시작

		/**
		 * 연동깊이
		 * @return 연동깊이
		 */
		public abstract int getItlkDpth();

		/**
		 * 최초거래코드
		 * @return 최초거래코드
		 */
		public abstract String getFrstTrnCd();

		/**
		 * 기동거래코드
		 * @return 기동거래코드
		 */
		public abstract String getMvTrnCd();

		/**
		 * Sync/Async구분
		 * @return Sync/Async구분
		 */
		public abstract String getSyncDv();

//		/**
//		 * WAS인스턴스ID
//		 * @return WAS인스턴스ID
//		 */
//		public abstract String getWasInstId();
		
		// III. 연동 종료
		
		// IV. 일자 시작

		/**
		 * 전문요청일시
		 * @return 전문요청일시
		 */
		public abstract String getMesgDmndDttm();

		/**
		 * 전문응답일시
		 * @return 전문응답일시
		 */
		public abstract String getMesgRespDttm();

		/**
		 * 거래일자
		 * @return 거래일자
		 */
		public abstract String getTrnDt();

		/**
		 * 거래일자
		 * @param trnDt 거래일자
		 */
		public abstract void setTrnDt(String trnDt);


		/**
		 * 로그영업일자
		 * @return 로그영업일자
		 */
		public abstract String getLogBzopDt();

		/**
		 * 로그영업일자
		 * @param logBzopDt 로그영업일자
		 */
		public abstract void setLogBzopDt(String logBzopDt);

		/**
		 * 서비스시작일시
		 * @return 서비스시작일시
		 */
		public abstract String getSvcStrnDttm();

		/**
		 * 서비스종료일시
		 * @return 서비스종료일시
		 */
		public abstract String getSvcEndDttm();


		
		// IV. 일자 종료

		// V. 사용자 시작 
		
		/**
		 * 회사코드
		 * 업무팀에서 사용시에는 getCoCd()를 사용하도록 한다. 
		 * @return
		 */
		@Deprecated
		public abstract  String getCompCd();
		
		/**
		 * 회사코드
		 * 업무팀에서 사용시에는 setCoCd()를 사용하도록 한다.
		 * @param compCd
		 */
		@Deprecated
		public abstract void setCompCd(String compCd);
		
		/**
		 * 부서코드
		 * @return 부서코드
		**/
		public abstract  String getDeptCd();	
		
		/**
		 * 부서코드
		 * @param 부서코드
		**/
		public abstract  void setDeptCd(String value);
		
		/**
		 * 사용자번호
		 * @return userNo 사용자번호
		**/
		public abstract  String getUserNo();	
		
		/**
		 * 사용자번호
		 * @param value 사용자번호
		**/
		public abstract  void setUserNo(String value);

	      /**
         * 사용자이름
         * @return userNm 사용자이름
        **/
        public abstract  String getUserNm();    
        
        /**
         * 사용자이름
         * @param value 사용자이름
        **/
        public abstract  void setUserNm(String value);
        
		/**
		 * 부서구분코드
		 * @return deptDvcd 부서구분코드
		**/
		public abstract  String getDeptDvcd();	
		
		/**
		 * 부서구분코드
		 * @param value 부서구분코드
		**/
		public abstract  void setDeptDvcd(String value);	
		
		/**
		 * 부점코드
		 * @return
		 */
		public abstract  String getBrCd();
		
		/**
		 * 부점코드
		 */
		public abstract  void setBrCd(String brCd);
		
		/**
		 * 사용자구분코드
		 * @return
		 */
		public abstract String getUserClCd();
		
		/**
		 * 사용자구분코드
		 * @param userDstcCd
		 */
		public abstract void setUserClCd(String userDstcCd);
		
		/**
	     * 직급코드
	     *  
	     * @return String
	     */
	    public abstract String getJobrCd();

	    /**
	     * 직급코드
	     *  
	     * @param jobrCd void
	     */
	    public abstract void setJobrCd(String jobrCd);

	    /**
	     * 직책코드
	     *  
	     * @return String
	     */
	    public abstract String getJtilCd();

	    /**
	     * 직책코드
	     *  
	     * @param jtilCd void
	     */
	    public abstract void setJtilCd(String jtilCd);

	    /**
	     * HPC프로젝트 회사코드
	     *  
	     * @return String
	     */
	    public abstract String getCoCd() ;

	    /**
	     * HPC프로젝트 회사코드
	     *  
	     * @param coCd void
	     */
	    public abstract void setCoCd(String coCd);

	    /**
	     * 대리점코드
	     *  
	     * @return String
	     */
	    public abstract String getDealCd() ;

	    /**
	     * 대리점코드
	     *  
	     * @param brndCd void
	     */
	    public abstract void setDealCd(String dealCd);


	    /**
	     * 사원번호
	     *  
	     * @return String
	     */
	    public abstract String getEmpNo();

	    /**
	     *  사원번호
	     *  
	     * @param empNo void
	     */
	    public abstract void setEmpNo(String empNo);

	    /**
	     * 권한회사목록
	     *  
	     * @return String
	     */
	    public abstract List<String> getAutrCoLst();

	    /**
	     * 권한회사목록
	     *  
	     * @param autrCoLst void
	     */
	    public abstract void setAutrCoLst(List<String> autrCoLst);
	    
	    /**
	     * 권한 회사를 추가
	     *  
	     * @param autrCo void
	     */
	    public abstract void addAutrCoLst(String autrCo) ;
	    

	    /**
	     * 권한브랜드목록
	     *  
	     * @return String
	     */
	    public abstract List<String> getAutrBrndLst() ;

	    /**
	     *권한브랜드목록
	     *  
	     * @param autrBrndLst void
	     */
	    public abstract void setAutrBrndLst(List<String> autrBrndLst) ;
	    
	    /**
	     * 권한브랜드를 추가
	     *  
	     * @param autrBrnd void
	     */
	    public abstract void addAutrBrnd(String autrBrnd);

	    /**
	     * 권한가맹점목록
	     *  
	     * @return List<String>
	     */
	    public abstract List<String> getAutrMchtLst() ;

	    /**
	     * 권한가맹점목록
	     *  
	     * @param autrMchtLst void
	     */
	    public abstract void setAutrMchtLst(List<String> autrMchtLst);
	    
	    /**
	     * 권한가맹점을 추가
	     *  
	     * @param autrMch void
	     */
	    public abstract void addAutrMch(String autrMch);
		
	    /**
	     * 요청브랜드코드
	     *  
	     * @return String
	     */
	    public abstract String getReqBrndCd() ;
	    
	    /**
	     * UI요청채널코드
	     *  
	     * @return String
	     */
	    public abstract String getReqChnlCd() ;
	    
	    /**
	     * 백오피스여부
	     *  
	     * @return String
	     */
	    public abstract String getIsBckOffice();
		/**
		 * CTI여부
		 * @return
		 */
		public abstract String getCtiYn();
		
		/**
		 * 사용자로케일
		 * @return 사용자로케일
		 */
		public abstract String getUserLocale();
		/**
		 * VII.시제
		 */
		
		/**
		 * 시제여부
		 * @return
		 */
		public abstract String getCshnOcrnYn();
		
		/**
		 * 시제여부
		 * @param cshnOcrnYn
		 */
		public abstract void setCshnOcrnYn(String cshnOcrnYn);
		
		/**
		 * 결제금액
		 * @return
		 */
		public abstract double getCashAmt();
		
		/**
		 * 결제금액
		 * @param cashAmt
		 */
		public abstract void setCashAmt(double cashAmt);
		
		/**
		 * 포인트 금액
		 * @return
		 */
		public abstract double getPointAmt();
		
		/**
		 * 포인트금액
		 * @param pointAmt
		 */
		public abstract void setPointAmt(double pointAmt);

		/**
		 * EAI Global ID
		 */
		public abstract String getEaiGlobId();
		
		/**
		 *EAI 인터페이스 ID 
		 * @return
		 */
		public abstract String getEaiIntfId();
		
		/**
		 * 결과수신서비스ID
		 * @return
		 */
		public abstract String getEaiRecvSvcid();
		
		/**
		 * 업무개별데이터
		 * @return
		 */
		public abstract String getBzwrIncsData();
		
		/**
		 * 응답코드 취득
		 * @return
		 */
		public abstract String getRpsCd();
		
		/**
		 * 응답코드 설정
		 * @param rpsCd
		 */
		public abstract void setRpsCd(String rpsCd);
		
		/**
		 * 응답상세코드 취득
		 * @return
		 */
		public abstract String getRpsDtlCd();
		
		/**
		 * 응답상세코드 설정
		 * @param rpsDtlCd
		 */
		public abstract void setRpsDtlCd(String rpsDtlCd);
		
		/**
		 * 업무개별데이터
		 */
		public abstract void setBzwrIncsData(String bzwrIncsData);
}

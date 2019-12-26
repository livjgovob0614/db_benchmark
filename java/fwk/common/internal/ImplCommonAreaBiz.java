package fwk.common.internal;

import java.util.ArrayList;
import java.util.List;

import fwk.common.AbsEntity;

public class ImplCommonAreaBiz extends AbsEntity {
	private static final long serialVersionUID = -7059733760328765967L;

	//#################
	// III.연동
	//#################
	private int    itlkDpth             ; // 연동깊이         
    private String frstTrnCd            ; // 최초거래코드	
	private String mvTrnCd              ; // 기동거래코드     
	private String syncDv               ; // Sync/Async구분   
//	private String wasInstId            ; // WAS인스턴스ID


	//#################
	// IV.일자
	//#################
	private String trnDt                ; // 거래일자          
	private String logBzopDt            ; // 로그영업일자      
	private String svcStrnDttm          ; // 서비스시작일시    
	private String svcEndDttm           ; // 서비스종료일시    


	//#################
	// V.사용자
	//#################
	private String deptDvcd               ; // 지점구분코드
	private String userClCd			; // 사용자구분코드
	private String userNm            ; // 사용자이름
	private String jobrCd        ;   //직급코드            
	private String jtilCd        ;   //직책코드            
	private String coCd            ;   //회사코드          
	private String deptCd        ;   //부서코드            
	private String dealCd        ;   //대리점코드              
	private String empNo           ; //사원번호              
	private List<String> autrCoLst  ;   //권한회사목록      
	private List<String> autrBrndLst;   //권한브랜드목록   
	private List<String> autrMchtLst;   //권한가맹점목록   



	//#################
	// VI.시재
	//#################


	//#################
	// VII.회계
	//#################
//	private String acngTrnDvcd          ; // 회계거래구분코드    
//	private String acngTrnStcd          ; // 회계거래상태코드    
//	private int    acngCcnt             ; // 회계건수            
//	private String slipNo               ; // 전표번호            
//	private String canCrctSlipNo        ; // 취소정정전표번호    
//	private String ifrsSlipNo           ; // IFRS전표번호        
//	private String ifrsCanCrctSlipNo    ; // IFRS취소정정전표번호
//	private long   cashWtch             ; // 현금출금            
//	private long   cashMnrc             ; // 현금입금            
//	private long   altrWtch             ; // 대체출금            
//	private long   altrMnrc             ; // 대체입금            
//	private long   otbr1WtchAmt         ; // 타점1출금금액       
//	private long   otbr1MnrcAmt         ; // 타점1입금금액       
//	private long   otbr2WtchAmt         ; // 타점2출금금액       
//	private long   otbr2MnrcAmt         ; // 타점2입금금액       
//	private List<AcngIncsData> acngIncsDataList;    //회계개별데이타목록

	//#################
	// VIII.업무공통
	//#################
//	private String acngBaseCd           ; // 회계기준코드    
//	private String custNo               ; // 고객번호        
//	private String acno                 ; // 계좌번호        
//	private long   trnAmt               ; // 거래금액        

	private String rpsCd;			//업무응답코드
	private String rpsDtlCd;		//업무상세응답코드
	//#################
	// IX.업무개별
	//#################
	private String bzwrIncsData         ; // 업무개별데이터
	

	/**
	 * 연동깊이
	 * @return 연동깊이
	 */
	public int getItlkDpth() {
		return itlkDpth;
	}

	/**
	 * 연동깊이
	 * @param itlkDpth 연동깊이
	 */
	public void setItlkDpth(int itlkDpth) {
		this.itlkDpth = itlkDpth;
	}

	/**
	 * 최초거래코드
	 * @return 최초거래코드
	 */
	public String getFrstTrnCd() {
		return frstTrnCd;
	}

	/**
	 * 최초거래코드
	 * @param frstTrnCd 최초거래코드
	 */
	public void setFrstTrnCd(String frstTrnCd) {
		this.frstTrnCd = frstTrnCd;
	}

	/**
	 * 기동거래코드
	 * @return 기동거래코드
	 */
	public String getMvTrnCd() {
		return mvTrnCd;
	}

	/**
	 * 기동거래코드
	 * @param mvTrnCd 기동거래코드
	 */
	public void setMvTrnCd(String mvTrnCd) {
		this.mvTrnCd = mvTrnCd;
	}

	/**
	 * Sync/Async구분
	 * @return Sync/Async구분
	 */
	public String getSyncDv() {
		return syncDv;
	}

	/**
	 * Sync/Async구분
	 * @param syncDv Sync/Async구분
	 */
	public void setSyncDv(String syncDv) {
		this.syncDv = syncDv;
	}

//	/**
//	 * WAS인스턴스ID
//	 * @return WAS인스턴스ID
//	 */
//	public String getWasInstId() {
//		return wasInstId;
//	}
//
//	/**
//	 * WAS인스턴스ID
//	 * @param wasInstId WAS인스턴스ID
//	 */
//	public void setWasInstId(String wasInstId) {
//		this.wasInstId = wasInstId;
//	}

	/**
	 * 거래일자
	 * @return 거래일자
	 */
	public String getTrnDt() {
		return trnDt;
	}

	/**
	 * 거래일자
	 * @param trnDt 거래일자
	 */
	public void setTrnDt(String trnDt) {
		this.trnDt = trnDt;
	}

	/**
	 * 로그영업일자
	 * @return 로그영업일자
	 */
	public String getLogBzopDt() {
		return logBzopDt;
	}

	/**
	 * 로그영업일자
	 * @param logBzopDt 로그영업일자
	 */
	public void setLogBzopDt(String logBzopDt) {
		this.logBzopDt = logBzopDt;
	}

	/**
	 * 서비스시작일시
	 * @return 서비스시작일시
	 */
	public String getSvcStrnDttm() {
		return svcStrnDttm;
	}

	/**
	 * 서비스시작일시
	 * @param svcStrnDttm 서비스시작일시
	 */
	public void setSvcStrnDttm(String svcStrnDttm) {
		this.svcStrnDttm = svcStrnDttm;
	}

	/**
	 * 서비스종료일시
	 * @return 서비스종료일시
	 */
	public String getSvcEndDttm() {
		return svcEndDttm;
	}

	/**
	 * 서비스종료일시
	 * @param svcEndDttm 서비스종료일시
	 */
	public void setSvcEndDttm(String svcEndDttm) {
		this.svcEndDttm = svcEndDttm;
	}

	
	/**
	 * 부서구분코드
	 * @return
	 */
	public String getDeptDvcd() {
		return deptDvcd;
	}

	/**
	 * 부서구분코드
	 * @param deptDvcd
	 */
	public void setDeptDvcd(String deptDvcd) {
		this.deptDvcd = deptDvcd;
	}

	/**
	 * 사용자구분코드
	 * @return
	 */
	public String getUserClCd() {
		return userClCd;
	}

	/**
	 * 사용자구분코드
	 * @param userClCd
	 */
	public void setUserClCd(String userClCd) {
		this.userClCd = userClCd;
	}

	/**
	 * 직급코드
	 *  
	 * @return String
	 */
	public String getJobrCd() {
        return jobrCd;
    }

    /**
     * 직급코드
     *  
     * @param jobrCd void
     */
    public void setJobrCd(String jobrCd) {
        this.jobrCd = jobrCd;
    }

    /**
     * 직책코드
     *  
     * @return String
     */
    public String getJtilCd() {
        return jtilCd;
    }

    /**
     * 직책코드
     *  
     * @param jtilCd void
     */
    public void setJtilCd(String jtilCd) {
        this.jtilCd = jtilCd;
    }

    /**
     * HPC프로젝트 회사코드
     *  
     * @return String
     */
    public String getCoCd() {
        return coCd;
    }

    /**
     * HPC프로젝트 회사코드
     *  
     * @param coCd void
     */
    public void setCoCd(String coCd) {
        this.coCd = coCd;
    }

    /**
     * 부서코드
     *  
     * @return String
     */
    public String getDeptCd() {
        return deptCd;
    }

    /**
     * 부서코드
     *  
     * @param deptCd void
     */
    public void setDeptCd(String deptCd) {
        this.deptCd = deptCd;
    }

    /**
     * 대리점코드
     *  
     * @return String
     */
    public String getDealCd() {
        return dealCd;
    }

    /**
     * 대리점코드
     *  
     * @param dealCd void
     */
    public void setDealCd(String dealCd) {
        this.dealCd = dealCd;
    }

    /**
     * 사용자이름
     *  
     * @return String
     */
    public String getUserNm() {
        return userNm;
    }

    /**
     * 대리점코드
     *  
     * @param dealCd void
     */
    public void setUserNm(String userNm) {
        this.userNm = userNm;
    }

    /**
     * 사원번호
     *  
     * @return String
     */
    public String getEmpNo() {
        return empNo;
    }

    /**
     *  사원번호
     *  
     * @param empNo void
     */
    public void setEmpNo(String empNo) {
        this.empNo = empNo;
    }

    /**
     * 권한회사목록
     *  
     * @return String
     */
    public List<String> getAutrCoLst() {
        if(autrCoLst==null) {
            autrCoLst = new ArrayList<String>();
        }
        return autrCoLst;
    }

    /**
     * 권한회사목록
     *  
     * @param autrCoLst void
     */
    public void setAutrCoLst(List<String> autrCoLst) {
        this.autrCoLst = autrCoLst;
    }
    
    /**
     * 권한브랜드목록
     *  
     * @return String
     */
    public List<String> getAutrBrndLst() {
        if(autrBrndLst==null) {
            autrBrndLst = new ArrayList<String>();
        }
        return autrBrndLst;
    }

    /**
     *권한브랜드목록
     *  
     * @param autrBrndLst void
     */
    public void setAutrBrndLst(List<String> autrBrndLst) {
        this.autrBrndLst = autrBrndLst;
    }
    
    /**
     * 권한가맹점목록
     *  
     * @return List<String>
     */
    public List<String> getAutrMchtLst() {
        return autrMchtLst;
    }

    /**
     * 권한가맹점목록
     *  
     * @param autrMchtLst void
     */
    public void setAutrMchtLst(List<String> autrMchtLst) {
        this.autrMchtLst = autrMchtLst;
    }

    /**
	 * 업무개별데이터
	 * @return 업무개별데이터
	 */
	public String getBzwrIncsData() {
		return bzwrIncsData;
	}

	/**
	 * 업무개별데이터
	 * @param bzwrIncsData 업무개별데이터
	 */
	public void setBzwrIncsData(String bzwrIncsData) {
		this.bzwrIncsData = bzwrIncsData;
	}
	
	
	/**
	 * 업무응답코드
	 * @return
	 */
	public String getRpsCd() {
		return rpsCd;
	}

	/**
	 * 업무응답코드
	 * @param rpsCd
	 */
	public void setRpsCd(String rpsCd) {
		this.rpsCd = rpsCd;
	}

	/**
	 * 업무응답상세코드
	 * @return
	 */
	public String getRpsDtlCd() {
		return rpsDtlCd;
	}

	/**
	 * 업무응답상세코드
	 * @param rpsDtlCd
	 */
	public void setRpsDtlCd(String rpsDtlCd) {
		this.rpsDtlCd = rpsDtlCd;
	}

	/**
	 * 객체 복제
	 */
	public ImplCommonAreaBiz clone() {
		ImplCommonAreaBiz entity = (ImplCommonAreaBiz) cloneBean();

		return entity;
	}
}

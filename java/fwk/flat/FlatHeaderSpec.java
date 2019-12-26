package fwk.flat;

public enum FlatHeaderSpec {
	
	WHL_MESG_LEN            (8  ,true) ,//전체전문길이	          
	STND_HDR_LEN            (8  ,true) ,//표준헤더부길이	        
	GLOB_ID                 (29 ,false),//글로벌 ID	              
	PRGS_SRNO               (3  ,true) ,//진행일련번호	          
	IPAD                    (15 ,false),//IP주소	                
	PRCM_MAC                (17 ,false),//PC MAC주소	            
	TRN_TRNM_NO             (9  ,false),//거래단말번호	          
	SSO_SESN_KEY            (30 ,false),//SSO 세션 KEY	          
	FRST_TRNM_CHNL_CD       (4  ,false),//최초전송채널코드	      
	TRNM_CHNL_CD            (4  ,false),//전송채널코드	          
	TRNM_NODE_NO            (2  ,true) ,//전송노드번호	          
	MCI_TRNM_NODE_NO        (2  ,true) ,//MCI전송노드번호	        
	ENV_DVCD                (1  ,false),//환경구분코드	          
	MESG_DMND_DTTM          (17 ,false),//전문요청일시	          
	MESG_VRSN_DVCD          (3  ,false),//전문버전구분코드	      
	TRN_CD                  (50  ,false),//거래코드	              
	SCRN_NO                 (10  ,false),//화면번호	              
	MESG_RESP_DTTM          (17 ,false),//전문응답일시	          
	TRN_PTRN_DVCD           (1  ,false),//거래유형구분코드	      
	MESG_DVCD               (1  ,false),//전문구분코드	          
	MESG_TYCD               (1  ,false),//전문유형코드	          
	MESG_CNTY_SRNO          (2  ,true) ,//전문연속일련번호	      
	TRTM_RSLT_CD            (1  ,false),//처리결과코드	          
	CMPG_RELM_USE_DVCD      (1  ,false),//캠패인영역사용구분코드	
	COMP_CD                 (4  ,false),//회사코드, 2014-09-05 사이즈 변경 3->4	              
	DEPT_CD                 (4  ,false),//부서코드	              
	BR_CD                   (4  ,false),//부점코드	              
	USER_NO                 (15  ,false),//사용자번호 2014-09-05 사이즈 변경 6->15 
	USER_LOCALE         (  5, false), //사용자 로케
	CTI_YN                  (1  ,false),//CTI 여부	              
	CSHN_OCRN_YN            (1  ,false),//시재발생여부	          
	CASH_AMT                (8  ,true) ,//거래금액	              
	POINT_AMT               (8  ,true) ,//포인트금액	            
	XTIS_CD                 (4  ,false),//대외기관코드	          
	BZWR_SVR_CD             (4  ,false),//업무서버코드	          
	OTSD_MESG_CD            (32 ,false),//대외전문코드	          
	OTSD_MESG_TRTM_CD       (32 ,false),//대외전문처리코드	      
	OTSD_TRN_UNQ_NO         (32 ,false),//대외거래고유번호	      
	OTSD_RESP_TRN_CD        (50  ,false),//대외응답거래코드	      
	CHNL_MSG_CD             (10 ,false),//채널메시지코드	   
//	EAI_GLOB_ID			(30, false),//EAI Global ID
//	EAI_INTF_ID				(21, false), //EAI 인터페이스 ID
//	EAI_RECV_SVCID		(20, false), //EAI 결과 수신서비스 ID
	SPR_CHRS_CNTN           (80 ,false),//예비문자열내용	        
	MSG_CCNT                (2  ,true, 10) ,//메시지건수	
	MSG_CD                  (8  ,false, true),//메시지코드          
	MSG_CNTN                (100,false, true),//메시지내용          
	EROR_OCRN_PRRM_LINE     (7  ,true, true) ,//오류발생프로그램라인
	EROR_OCRN_PRRM_NM       (100,false, true),;//오류발생프로그램명  
	                              
	
	private final int length;
    private final boolean number;
    private final int max;
    private final boolean child;
    private int offset;
    
    FlatHeaderSpec(int length, boolean number) {
    	this(length, number, false);
    }

    FlatHeaderSpec(int length, boolean number, boolean child) {
    	this(length, number, child, 0);
    }

    FlatHeaderSpec(int length, boolean number, int max) {
    	this(length, number, false, max);
    }
    
    FlatHeaderSpec(int length, boolean number, boolean child, int max) {
        this.length = length;
        this.number = number;
        this.child = child;
        this.max = max;
    }
    
    public int length() { return length; }
    public boolean isNumber() { return number; }
    public int max() { return max; }
    public boolean isChild() { return child; }
    public int offset() { return offset; }
    
    private static final int totalLength;
    static {
    	// 정의된 총 사이즈 계산
    	int l = 0;
    	for(FlatHeaderSpec s : FlatHeaderSpec.values()){
    		if(!s.isChild()){
//    			specList.add(s);
    			s.offset = l;
    			l += s.length;
    		}
    	}
    	totalLength = l;
    }
//
    public static int getTotalLength() { return totalLength; }
    
    public static void main(String[] args){
    	int i=1;
    	for(FlatHeaderSpec s: FlatHeaderSpec.values()){
    		if(!s.isChild()){
    		  //2015.10.13 jihooyim code inspector 점검 수정 (02-2.제거되지 않고 남은 디버그 코드(print))
    			//System.out.println("항목("+ (i < 10 ? "0" : "") + i +") : " + s.name() + " : " + s.offset() + " ~ " + (s.offset() + s.length));
    			i++;
    		}
    	}
    	//2015.10.13 jihooyim code inspector 점검 수정 (02-2.제거되지 않고 남은 디버그 코드(print))
    	//System.out.println("전체사이즈:" + getTotalLength());
    }
    
	
}

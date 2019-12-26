package fwk.flat;

public enum EaiFlatHeaderSpec {
	
//	HEADER_SIZE							(256, true),//EAI전문에는 포함되지는 않지만, HEADER의 길이가 필요할 때 사용할 수 있도록 구성함. 
	
	
	STD_TGRM_LEN 					(8, true), //표준전문길이
	
	//Global Id
	TGRM_DDTM 							(14, false),//전문작성일시
	TGRM_CRT_SYSNM				(8, false),//전문생성시스템명
	TGRM_CRT_NO						(8, false),//전문생성번호
	
	//진행번호
	TGRM_PRG_SQNO					(2, false),//전문진행일련번호
	
	//전송시스템정보
	TRMS_SYS_CD							(5,false), //전송시스템코드
	
	//전문정보처리
	REQ_RSP_DCD						(1, false),//요청응답구분코드
	TR_SYNC_DCD						(1, false),//거래동기구분코드
	ASYNC_TR_DCD						(1, false),//비동기거래구분코드
	TGRM_REQ_DTM					(14, false),//전문요청일시
	
	//서비스ID정보
	RCVE_SVCID							(40, false),//수신서비스 ID
	RSLT_RCEV_SVCID				(20, false),//결과 수신서비스 ID
	EAI_INTF_ID							(21, false),//EAI인터페이스ID
	
	//응답결과정보
	TGRM_RSP_DTM					(14, false),//전문응답일시
	TGRM_PRCRSLT_DCD		(1, false),//전문처리결과구분코드
	
	//장애정보
	OBS_SYS_ID							(5, false),//장애시스템코드
	TGRM_ERR_MSG_CD		(7, false),//전문오류메시지 코드
	
	//기타정보
	TGRM_VER_NO					(3, false),//전문버전번호
	LANG_DCD							(2, false),//언어구분코드
	TEST_DCD								(1, false),//테스트구분코드
	RSR											(80, false),//예비filler
	
	//메시지영역
	MSG_CD								(1, false,true),//메시지코드
	MSG_ID									(9, false, true),//메시지ID
	PNP_MSG								(256, false, true),//주메시지
	APPD_MSG							(1024, false, true),//부가메시지
	ERR_TRRY							(3, false, true),;//오류영역
	                              
	//입출력개별부
//	DATA_CD								(1, false),
//	DATA_LNTH							(8, true),;
	
	
	private final int length;
    private final boolean number;
    private final int max;
    private final boolean child;
    private int offset;
    
    EaiFlatHeaderSpec(int length, boolean number) {
    	this(length, number, false);
    }

    EaiFlatHeaderSpec(int length, boolean number, boolean child) {
    	this(length, number, child, 0);
    }

    EaiFlatHeaderSpec(int length, boolean number, int max) {
    	this(length, number, false, max);
    }
    
    EaiFlatHeaderSpec(int length, boolean number, boolean child, int max) {
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
    	for(EaiFlatHeaderSpec s : EaiFlatHeaderSpec.values()){
    		if(!s.isChild()){
//    			specList.add(s);
    			s.offset = l;
    			l += s.length;
    		}
    	}
    	totalLength = l;
    }
//
    private static final int msgFieldLength;
    static {
    	// 정의된 총 사이즈 계산
    	int l = 0;
    	for(EaiFlatHeaderSpec s : EaiFlatHeaderSpec.values()){
    		if(s.isChild()){
//    			specList.add(s);
    			s.offset = l;
    			l += s.length;
    		}
    	}
    	msgFieldLength = l;
    }
    
    public static int getTotalLength() { return totalLength; }
    public static int getMsgFieldLength() { return msgFieldLength; }
    public static void main(String[] args){
    	int i=1;
    	for(EaiFlatHeaderSpec s: EaiFlatHeaderSpec.values()){
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

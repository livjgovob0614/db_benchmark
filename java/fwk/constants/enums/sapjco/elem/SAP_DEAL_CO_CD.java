package fwk.constants.enums.sapjco.elem;

/**
 * gl계정 및 거래처 코드
 * @author greatjin
 *
 */
public enum SAP_DEAL_CO_CD {
	  LEASE_ASSET_NR   ("269107") //리스자산 신규폰 렌탈
	, UNCLT_KRW_DEV    ("217910") //미수금-원화 DEVICE사업  
	, PERSONAL         ("501000") // 개인
	, LEASE_INCOME_NR  ("506401") // 리스수익 NR
	
	, PAY_COMMISSION   ("726301") //[판매수수료] 지급수수료
	
	, ASSET_DEPRECIATION_AMT_NR ("713807")//감가상각비 NR
	, PJT_MATERIAL              ("625101")//PJT재료비
	, ALLWN_ETC                 ("444901") //충당부채기타
	, ASSET_DEPRECIATION_SUM_AMT_NR("269207") //감가상각비누계액
	;
	private String code;
		
	SAP_DEAL_CO_CD(String code)
	{
		this.code = code;
	}

	public String getCode() {
		return code;
	}

	
	public String toString()
	{
		return code;
	}
		
}

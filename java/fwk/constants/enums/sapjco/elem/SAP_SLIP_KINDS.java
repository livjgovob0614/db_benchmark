package fwk.constants.enums.sapjco.elem;

import fwk.erfif.sapjco.domain.AssetAmtAASlip;
import fwk.erfif.sapjco.domain.AssetDepreciationSlip;
import fwk.erfif.sapjco.domain.RentalARSlip;
import fwk.erfif.sapjco.domain.RentalBillingSlip;
import fwk.erfif.sapjco.domain.ReturnCommissionSlip;
import fwk.erfif.sapjco.domain.SalesCommissionSlip;
import fwk.erfif.sapjco.domain.AgencyAmtAPSlip;

/**
 * slip kinds
 * @author greatjin
 *
 */
public enum SAP_SLIP_KINDS {
	ASSET_AMT_AA       ("Z_FI_RFC_DMS_MBAP"               , "NA", "KR", AssetAmtAASlip.class       ) //단말기 자산 등록 AP발생
, 	AGENCY_AMT_AP      ("Z_FI_RFC_DMS_MBCREDIT_AP"        , "NB", "KR", AgencyAmtAPSlip.class      ) //대리점 대금 정산 AP발행 
,   SALES_COMMISSION   ("Z_FI_RFC_DMS_SALES_COMMISSION"   , "NE", "YC", SalesCommissionSlip.class  ) //판매수수료 AP발생
,   RETURN_COMMISSION  ("Z_FI_RFC_DMS_MBRETURN_COMMISSION", "NH", "YC", ReturnCommissionSlip.class ) //회수수수료
, 	RENTAL_AR          ("Z_FI_RFC_DMS_RENTAL_AR"          , "NI", "DR", RentalARSlip.class         ) //매출 추정 (AR발생) 
, 	RENTAL_BILLING     ("Z_FI_RFC_DMS_RENTAL_BILLING"     , "NJ", "DR", RentalBillingSlip.class    ) //매출 확정 청구
,   CANCEL_CHARGE      ("Z_FI_RFC_DMS_CANCEL_CHARGE"      , "NL", "DR", AssetAmtAASlip.class       ) //위약금
,   PANALTY_FEE        ("Z_FI_RFC_DMS_PANALTY_FEE"        , "NM", "DR", AssetAmtAASlip.class       ) //변상금
, 	ASSET_DEPRECIATION ("Z_FI_RFC_DMS_ASSET_DEPRECIATIO"  , "NO", "AF", AssetDepreciationSlip.class) //감가상각 
	 
	;
	
	private String funcName;
	private String dmsType;
	private String slipType;
	private Class  clz;
	
	SAP_SLIP_KINDS(String funcName, String dmsType, String slipType, Class clz)
	{
		this.funcName  = funcName;
		this.dmsType   = dmsType;
		this.slipType  = slipType;
		this.clz       = clz     ;
	}

	public String getFuncName() {
		return funcName;
	}

	public String getDmsType() {
		return dmsType;
	}


	public String getSlipType() {
		return slipType;
	}

	public Class getClz() {
		return clz;
	}

}

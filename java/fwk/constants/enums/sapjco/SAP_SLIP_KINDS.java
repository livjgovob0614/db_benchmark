package fwk.constants.enums.sapjco;

/**
 * slip kinds
 * @author greatjin
 *
 */
public enum SAP_SLIP_KINDS {
	 ASSET_AMT_AP  ("Z_FI_RFC_DMS_MBAP"       , "A") //단말기 자산 등록 AP발생
 , 	 AGENCY_AMT_AP ("Z_FI_RFC_DMS_MBCREDIT_AP", "B") //대리점 대금 정산 AP발행 
	 
	;
	
	private String funcName;
	private String dmsType;
	
	SAP_SLIP_KINDS(String funcName, String dmsType)
	{
		this.funcName = funcName;
		this.dmsType   = dmsType;
	}

	public String getFuncName() {
		return funcName;
	}

	public void setFuncName(String funcName) {
		this.funcName = funcName;
	}

	public String getDmsType() {
		return dmsType;
	}

	public void setDmsType(String dmsType) {
		this.dmsType = dmsType;
	}


	
	
	
}

package fwk.constants.enums.sapjco.elem;

/**
 * 지급방법
 * @author greatjin
 *
 */
public enum SAP_PAY_MTHD {
	 HANA_FB_CASH ("B") //하나 F/B현금
	;
	private String code;
		
	SAP_PAY_MTHD(String code)
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

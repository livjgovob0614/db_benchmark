package fwk.constants.enums.sapjco.elem;

/**
 * 세믁코드
 * @author greatjin
 *
 */
public enum SAP_TAX_CD {
	 INTAX10_TAXBILL ("V0") //매입부가사 10%-세금계산서 일반
	;
	private String code;
		
	SAP_TAX_CD(String code)
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

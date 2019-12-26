package fwk.constants.enums.sapjco;

/**
 * sap_slip
 * @author greatjin
 *
 */
public enum SAP_SLIP_ELEM {
	 SLIP_HEADER ("IT_DMS_HEADER") // Slip Header
   , SLIP_ITEM   ("IT_DMS_ITEM")   // Slip Line Item
   , SLIP_RETURN ("IT_ERP_RETURN") // Slip return Message
	;
	
	private String part;
	
	SAP_SLIP_ELEM(String part)
	{
		this.part = part;
	}

	public String getPart() {
		return part;
	}

	
	public String toString()
	{
		return part;
	}
	
}

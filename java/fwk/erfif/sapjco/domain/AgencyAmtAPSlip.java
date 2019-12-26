/**
 * 
 */
package fwk.erfif.sapjco.domain;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;

import fwk.constants.enums.sapjco.elem.SAP_BIZ_AREA;
import fwk.constants.enums.sapjco.elem.SAP_BP;
import fwk.constants.enums.sapjco.elem.SAP_DEAL_CO_CD;
import fwk.constants.enums.sapjco.elem.SAP_SLIP_KINDS;
import fwk.constants.enums.sapjco.elem.SAP_WBS_ELEM;
import fwk.constants.enums.sapjco.elem.SAP_PAY_COND;
import fwk.constants.enums.sapjco.elem.SAP_PSTNG_KEY;
import fwk.constants.enums.sapjco.elem.SAP_TAX_CD;
import fwk.erfif.sapjco.domain.CommonSlipItem;



/**
 * 단말기 대금정산
 * @author greatjin
 *
 */
public class AgencyAmtAPSlip implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	CommonSlipHeader header    ;
	CommonSlipItem[] items    = new CommonSlipItem[2] ;
	
	private String custCd; //Customer Code for init
	private String taxAmt;
	private String payAltCd; //지급대체
	
	private SAP_SLIP_KINDS slipKinds =SAP_SLIP_KINDS.AGENCY_AMT_AP; 
	private String dmsType = slipKinds.getDmsType();
	private String functionName = slipKinds.getFuncName();
	private String slipType = slipKinds.getSlipType();

	
	
	public AgencyAmtAPSlip(String zserial)
	{
		this.init(zserial);
	}
	
	public AgencyAmtAPSlip(String zserial, String custCd, String amt)
	{
		this.init(zserial, custCd, amt);
	}
	
	
	public AgencyAmtAPSlip(String zserial, String lginId, String slipDt, String custCd, String netAmt, String taxAmt, String payAltCd)
	{
		this.init(zserial, lginId,  slipDt, custCd, netAmt, taxAmt, payAltCd);
	}

	/**
	 * 초기화
	 */
	private void init(String zserial)
	{
		this.init(zserial, null, null);
	}
	
	private void init(String zserial, String custCd, String amt)
	{
		this.init(zserial, null, null, custCd, amt, null, null);
	}
	
	
	/**
	 * 초기화
	 * @param amt
	 */
	private void init(String zserial, String lginId, String slipDt, String custCd, String netAmt, String taxAmt, String payAltCd)
	{
		header = new CommonSlipHeader();
		header.setSerNo(zserial);
		header.setDmsTyp(this.dmsType);
		header.setSlipTyp(this.slipType);
		header.setUserNo(lginId);
		header.setTransCd("FBV1");
		
		this.custCd   = custCd;
		this.taxAmt   = taxAmt;
		this.payAltCd = payAltCd;
		
		if(StringUtils.isNotEmpty(slipDt)) header.setPstngDt(slipDt);
		if(StringUtils.isNotEmpty(slipDt)) header.setEvdcDt(slipDt);
		
		int idx = 1; 
		for(int i=0; i<items.length; i++)
		{
			items[i] = new CommonSlipItem();
			items[i].setSerNo(zserial);
			items[i].setDmsSeq(idx++ +"");
			items[i].setBizArea(SAP_BIZ_AREA.DEV_HQ.getCode());
			items[i].setBp(SAP_BP.SKCC_HQ.getCode());			
			items[i].setDsignField(SAP_WBS_ELEM.DEVICE_R_BIZ.getCode());
			items[i].setTaxCd(SAP_TAX_CD.INTAX10_TAXBILL.getCode());
			items[i].setAmt(netAmt);
		}
		
		initDr(items[0],header);
		initCr(items[1],header);
	}
	
	/**
	 * 차변 초기화
	 * @param one
	 */
	private void initDr(CommonSlipItem one, CommonSlipHeader header)
	{
		one.setPstngKey(SAP_PSTNG_KEY.DR.getCode());
		one.setDealCoCd(custCd);
		one.setPayCond(SAP_PAY_COND.KRW_1ST.getCode());
		long netAmt = Long.parseLong(one.getAmt());
		long taxAmt = Long.parseLong(this.taxAmt);
		one.setAmt(netAmt+taxAmt +"");
		one.setTaxAmt(taxAmt +"");
		one.setPayAlt(this.payAltCd);
//		one.setPayRsv("B");
	}
	
	/**
	 * 대변초기화
	 * @param one
	 */
	private void initCr(CommonSlipItem one, CommonSlipHeader header)
	{
		one.setPstngKey(SAP_PSTNG_KEY.CR.getCode());
		one.setDealCoCd(SAP_DEAL_CO_CD.UNCLT_KRW_DEV.getCode());
	}
	
    public CommonSlipItem getDr()
    {
    	return this.items[0];
    }
	
    public CommonSlipItem getCr()
    {
    	return this.items[1];
    }
    
    

	public CommonSlipHeader getHeader() {
		return header;
	}

	public void setHeader(CommonSlipHeader header) {
		this.header = header;
	}

	public CommonSlipItem[] getItems() {
		return items;
	}

	public void setItems(CommonSlipItem[] items) {
		this.items = items;
	}

	public String getCustCd() {
		return custCd;
	}

	public void setCustCd(String custCd) {
		this.custCd = custCd;
	}

	public String getFunctionName() {
		return functionName;
	}
	
	

}

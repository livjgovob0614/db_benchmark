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
import fwk.constants.enums.sapjco.elem.SAP_PAY_MTHD;
import fwk.constants.enums.sapjco.elem.SAP_PSTNG_KEY;
import fwk.erfif.sapjco.domain.CommonSlipItem;



/**
 * 대리점 판매수수료
 * @author greatjin
 *
 */
public class SalesCommissionSlip implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	CommonSlipHeader header    ;
	CommonSlipItem[] items    = new CommonSlipItem[2] ;
	
	private String custCd; //Customer Code for init
	
	private SAP_SLIP_KINDS slipKinds =SAP_SLIP_KINDS.SALES_COMMISSION; 
	private String dmsType = slipKinds.getDmsType();
	private String functionName = slipKinds.getFuncName();
	private String slipType = slipKinds.getSlipType();
	
	public SalesCommissionSlip(String zserial, String lginId, String slipDt, String custCd, String netAmt)
	{
		this.init(zserial, lginId,  slipDt, custCd, netAmt);
	}
	
	/**
	 * 초기화
	 * @param amt
	 */
	private void init(String zserial, String lginId, String slipDt, String custCd, String netAmt)
	{
		header = new CommonSlipHeader();
		header.setSerNo(zserial);
		header.setDmsTyp(this.dmsType);
		header.setSlipTyp(this.slipType);
		header.setUserNo(lginId);
		header.setTransCd("FBV1");
		
		this.custCd   = custCd;
		
		if(StringUtils.isNotEmpty(slipDt)) header.setPstngDt(slipDt);
		if(StringUtils.isNotEmpty(slipDt)) header.setEvdcDt(slipDt);
		
		int idx = 1; 
		for(int i=0; i<items.length; i++)
		{
			items[i] = new CommonSlipItem();
			items[i].setSerNo(zserial);
			items[i].setDmsSeq(idx++ +"");
			items[i].setBp(SAP_BP.SKCC_HQ.getCode());			
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
		one.setBizArea(SAP_BIZ_AREA.DEV_HQ.getCode());
		one.setPayCond(SAP_PAY_COND.KRW_1ST.getCode());
		one.setPayMthd(SAP_PAY_MTHD.HANA_FB_CASH.getCode());
		one.setDsignField(header.getPstngDt());
	}
	
	/**
	 * 대변초기화
	 * @param one
	 */
	private void initCr(CommonSlipItem one, CommonSlipHeader header)
	{
		one.setPstngKey(SAP_PSTNG_KEY.CR.getCode());
		one.setDealCoCd(SAP_DEAL_CO_CD.PAY_COMMISSION.getCode());
		one.setWbsElem(SAP_WBS_ELEM.DEVICE_R_BIZ.getCode());
		one.setDsignField(SAP_WBS_ELEM.DEVICE_R_BIZ.getCode());
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

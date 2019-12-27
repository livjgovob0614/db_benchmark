/*
 * Copyright (c) 2006 SK C&C. All rights reserved.
 * 
 * This software is the confidential and proprietary information of SK C&C. You
 * shall not disclose such Confidential Information and shall use it only in
 * accordance wih the terms of the license agreement you entered into with SK
 * C&C.
 */

package nexcore.framework.demo.hello.biz;

import java.util.Map;

import nexcore.framework.core.data.IDataSet;
import nexcore.framework.core.data.IOnlineContext;
import nexcore.framework.core.data.IRecordSet;
import nexcore.framework.core.data.RecordSet;
import nexcore.framework.core.util.DataSetFactory;

import com.sktst.common.base.BaseBizUnit;
import com.sktst.common.base.BaseConstants;

/**
 * <ul>
 * <li>업무 그룹명 : Nexcore/프레임워크/Demo</li>
 * <li>설 명 : </li>
 * <li>작성일 : 2009-01-15 09:31:52</li>
 * </ul>
 *
 * @author 심연정 (shimyunjung)
 */
public class DeliverFeeBiz extends BaseBizUnit {

	private static final String INSERT_FLAG = "I";

	private static final String UPDATE_FLAG = "U";

	private static final String DELETE_FLAG = "D";

	private static final String CUD_FLAG_PARAM = "nc_cud_flag";

	/**
	 * 
	 *
	 * @author 심연정 (shimyunjung)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 *	- record : ds_Dev
	 *		- field : seq [필드1] 
	 *		- field : DEAL_CO_CD [필드2] 
	 */

	/* 이력 비포함 조회 */
	public IDataSet getDeliverFee(IDataSet requestData, IOnlineContext onlineCtx) {
		IRecordSet rs = queryForRecordSet("DeliverFeeBiz.selectDeliverFee",
				requestData.getFieldMap());
		if (rs == null) {
			System.out.println("rs is null.");
			rs = new RecordSet("ds_Dev");
		}
		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });
		responseData.putRecordSet("ds_Dev", rs);
		return responseData;
	}

	/**
	 * 
	 *
	 * @author 심연정 (shimyunjung)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */

	/* 이력 포함 조회 */
	public IDataSet getDeliverFeeHst(IDataSet requestData,
			IOnlineContext onlineCtx) {

		IRecordSet rs = queryForRecordSet("DeliverFeeBiz.selectDeliverFeeHst",
				requestData.getFieldMap());

		if (rs == null) {
			System.out.println("rs is null.");
			rs = new RecordSet("ds_Dev");
		}

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });

		responseData.putRecordSet("ds_Dev", rs);
		return responseData;
	}

	/**
	 * 
	 *
	 * @author 심연정 (shimyunjung)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */

	/* 배송비 정보 입력 및 수정 */
	public IDataSet updateDeliverFee(IDataSet requestData,
			IOnlineContext onlineCtx) {

		IRecordSet rs = requestData.getRecordSet("ds_Dev");
		System.err.println(rs);
		if (rs != null) {
			//System.out.print(rs);
			for (int i = 0; i < rs.getRecordCount(); i++) {

				Map hmSave = null;
				hmSave = rs.getRecordMap(i);
				Map map = (Map) queryForObject("DeliverFeeBiz.selectSerNum", rs
						.getRecordMap(i));
				hmSave.putAll(map);
				System.err.println(hmSave);
				//int serNum = Integer.parseInt(map.get("SER_NUM").toString());
				//System.err.println(serNum);

				if (INSERT_FLAG.equalsIgnoreCase((String) hmSave
						.get(CUD_FLAG_PARAM))) {
					System.err.println("insert");
					insert("DeliverFeeBiz.insertDeliverFee", hmSave);
				} else if (UPDATE_FLAG.equalsIgnoreCase((String) hmSave
						.get(CUD_FLAG_PARAM))) {
					System.err.println("update");
					insert("DeliverFeeBiz.insertStaDt", hmSave);
					update("DeliverFeeBiz.updateEndDt", hmSave);

				}
			}
		}

		IRecordSet result = queryForRecordSet("DeliverFeeBiz.selectDeliverFee",
				requestData.getFieldMap());

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });
		responseData.putRecordSet("ds_Dev", result);
		return responseData;

	}

	/**
	 * 
	 *
	 * @author 심연정 (shimyunjung)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	/* 콤보 데이터 세팅 */
	public IDataSet getCboData(IDataSet requestData, IOnlineContext onlineCtx) {

		IRecordSet rs = queryForRecordSet("DeliverFeeBiz.selectDlvUnit",
				requestData.getFieldMap());
		IRecordSet rss = queryForRecordSet("DeliverFeeBiz.selectDlvTyp",
				requestData.getFieldMap());

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });

		responseData.putRecordSet("ds_cboDlvUnit", rs);
		responseData.putRecordSet("ds_cboDlvTyp", rss);
		return responseData;
	}

}
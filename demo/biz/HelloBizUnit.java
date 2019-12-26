/*
 * Copyright (c) 2006 SK C&C. All rights reserved.
 * 
 * This software is the confidential and proprietary information of SK C&C. You
 * shall not disclose such Confidential Information and shall use it only in
 * accordance wih the terms of the license agreement you entered into with SK
 * C&C.
 */

package nexcore.framework.demo.hello.biz;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import nexcore.framework.core.Constants;
import nexcore.framework.core.ServiceConstants;
import nexcore.framework.core.data.DataSet;
import nexcore.framework.core.data.IDataSet;
import nexcore.framework.core.data.IOnlineContext;
import nexcore.framework.core.data.IRecordSet;
import nexcore.framework.core.data.RecordSet;
import nexcore.framework.core.exception.BizRuntimeException;
import nexcore.framework.core.ioc.ComponentRegistry;
import nexcore.framework.core.log.LogManager;
import nexcore.framework.core.util.CryptoUtils;
import nexcore.framework.core.util.DataSetFactory;
import nexcore.framework.integration.db.NoRecordAffectedException;
import nexcore.framework.integration.db.NoRecordFoundException;

import org.apache.commons.logging.Log;

import com.sktst.common.base.BaseBizUnit;
import com.sktst.common.base.BaseConstants;
import com.sktst.common.file.IPsFileUploadManager;

/**
 * <ul>
 * <li>업무 그룹명 : Nexcore/프레임워크/Demo</li>
 * <li>설 명 : </li>
 * <li>작성일 : 2009-01-07 11:01:49</li>
 * </ul>
 *
 * @author admin (admin)
 */
public class HelloBizUnit extends BaseBizUnit {

	/**
	 * 
	 *
	 * @author admin (admin)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 *	- field : ID [필드1] 
	 *	- field : NAME [필드2] 
	 *	- field : MEMO [필드3] 
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet insertHello(IDataSet requestData, IOnlineContext onlineCtx) {
		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("insertHello method start");
		}
		insert("HelloBizUnit.insertHello", requestData.getFieldMap(), onlineCtx);
		return DataSetFactory.createWithOKResultMessage(
				BaseConstants.INSERT_OK_MESSAGE_ID, new String[] { "1" });
	}

	/**
	 * 
	 *
	 * @author admin (admin)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 *	- field : ID [필드1] 
	 *	- field : NAME [필드2] 
	 *	- field : MEMO [필드3] 
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet updateHello(IDataSet requestData, IOnlineContext onlineCtx) {
		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("updateHello method start");
		}
		int updateCount = update("HelloBizUnit.updateHello", requestData
				.getFieldMap(), onlineCtx);
		if (updateCount < 1) {
			throw new NoRecordAffectedException(
					BaseConstants.NO_RECORD_EXCEPTION_MESSAGE_ID);
		}
		return DataSetFactory.createWithOKResultMessage(
				BaseConstants.UPDATE_OK_MESSAGE_ID, new String[] { ""
						+ updateCount });
	}

	/**
	 * 
	 *
	 * @author admin (admin)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 *	- field : ID [필드1] 
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet deleteHello(IDataSet requestData, IOnlineContext onlineCtx) {
		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("deleteHello method start");
		}
		int deleteCount = delete("HelloBizUnit.deleteHello", requestData
				.getFieldMap(), onlineCtx);
		if (deleteCount < 1) {
			throw new NoRecordAffectedException(
					BaseConstants.NO_RECORD_EXCEPTION_MESSAGE_ID);
		}
		return DataSetFactory.createWithOKResultMessage(
				BaseConstants.DELETE_OK_MESSAGE_ID, new String[] { ""
						+ deleteCount });
	}

	/**
	 * 
	 *
	 * @author admin (admin)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 *	- field : ID [필드1] 
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 *	- field : ID [필드1] 
	 *	- field : NAME [필드2] 
	 *	- field : MEMO [필드3] 
	 */
	public IDataSet selectHello(IDataSet requestData, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("selectHello method start");
		}

		Map map = (Map) queryForObject("HelloBizUnit.selectHello", requestData
				.getFieldMap(), onlineCtx);
		if (map == null) {
			throw new NoRecordFoundException();
		}
		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { "1" });
		responseData.putFieldMap(map);
		return responseData;
	}

	/**
	 * 
	 *
	 * @author admin (admin)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 *	- record : Hello
	 *		- field : ID [필드1] 
	 *		- field : NAME [필드2] 
	 *		- field : MEMO [필드3] 
	 */
	public IDataSet selectHelloList(IDataSet requestData,
			IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("selectHelloList method start");
		}

		IRecordSet rs = queryForRecordSet("HelloBizUnit.selectHelloList",
				requestData.getFieldMap(), onlineCtx);
		if (rs == null) {
			rs = new RecordSet("Hello");
		}

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });
		responseData.putRecordSet("Hello", rs);
		return responseData;
	}

	/**
	 * 
	 *
	 * @author admin (admin)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet cudAllHello(IDataSet requestData, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("cudAllHello method start");
		}

		int cudAllCount = 0;
		int insertCount = 0;
		int updateCount = 0;
		int deleteCount = 0;

		IRecordSet rs = requestData.getRecordSet("Hello");

		if (rs != null) {
			//System.out.print(rs);
			for (int i = 0; i < rs.getRecordCount(); i++) {
				if (DELETE_FLAG.equalsIgnoreCase(rs.getRecord(i).get(
						CUD_FLAG_PARAM))) {
					System.out.println(rs.getRecord(i));
					deleteCount = deleteCount
							+ delete("HelloBizUnit.deleteHello", rs
									.getRecord(i), onlineCtx);
				}
			}
			for (int i = 0; i < rs.getRecordCount(); i++) {
				if (INSERT_FLAG.equalsIgnoreCase(rs.getRecord(i).get(
						CUD_FLAG_PARAM))) {
					insert("HelloBizUnit.insertHello", rs.getRecord(i), onlineCtx);
					insertCount++;
				} else if (UPDATE_FLAG.equalsIgnoreCase(rs.getRecord(i).get(
						CUD_FLAG_PARAM))) {
					updateCount = updateCount
							+ update("HelloBizUnit.updateHello", rs
									.getRecord(i), onlineCtx);
				}
			}
			cudAllCount = insertCount + updateCount + deleteCount;
		}
		if (cudAllCount < 1) {
			throw new NoRecordAffectedException(
					BaseConstants.NO_RECORD_EXCEPTION_MESSAGE_ID);
		}
		return DataSetFactory.createWithOKResultMessage(
				BaseConstants.UPDATEALL_OK_MESSAGE_ID, new String[] {
						"" + insertCount, "" + updateCount, "" + deleteCount });
	}

	/**
	 * 
	 *
	 * @author admin (admin)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 *	- field : NAME [필드1] 
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 *	- field : RESULT [필드1] 
	 */
	public IDataSet hello(IDataSet requestData, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("hello method start");
		}

		String name = requestData.getField("NAME");
		if (log.isDebugEnabled()) {
			log.debug("input name : " + name);
		}

		String resultStr = "Hello World - " + name;

		IDataSet result = new DataSet();
		result.putField("RESULT", resultStr);

		return result;
	}

	/**
	 * 
	 *
	 * @author admin (admin)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 *	- field : id [필드1] 
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 *	- field : result [필드1] 
	 */
	public IDataSet testHello(IDataSet requestData, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("testHello method start");
		}

		String test = CryptoUtils.encode("test");
		System.out.println(test);
		System.out.println(CryptoUtils.decode(test));

		//		System.out.println("[[같은 컴포넌트의 다른 단위업무 로직 호출의 경우]]");
		//		try {
		//			TestBiz testBiz = (TestBiz) lookupBizUnit(TestBiz.class);
		//			testBiz.test(requestData, onlineCtx);
		//		} catch (Exception ex) {
		//			System.out.println("단위업무 호출 중 예외 발생");
		//			ex.printStackTrace();
		//		}
		//
		//		System.out.println("[[다른 컴포넌트 로직 호출의 경우]]");
		//		try {
		//			Abc abc = (Abc) lookupBizComponent("framework.demo.Abc");
		//			abc.test(requestData, onlineCtx);
		//		} catch (Exception ex) {
		//			System.out.println("컴포넌트 호출 중 예외 발생");
		//			ex.printStackTrace();
		//		}

		Map testMap = new HashMap();
		testMap.put("result", new BigDecimal(10));

		IDataSet result = new DataSet();
		// TODO 업무 로직 작성 필요
		//result.putFieldMap(testMap);
		result.putFieldObjectMap(testMap);

		return result;
	}

	/**
	 * 
	 *
	 * @author admin (admin)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 *	- record : Hello
	 *		- field : ID [필드1] 
	 *		- field : NAME [필드2] 
	 *		- field : MEMO [필드3] 
	 */
	public IDataSet selectHelloListPage(IDataSet requestData,
			IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("selectHelloListPage method start");
		}

		Map fields = requestData.getFieldMap();
		setPagenatedParams(fields);

		IRecordSet rs = queryForRecordSet("HelloBizUnit.selectHelloListPage",
				fields, onlineCtx);

		if (rs == null) {
			rs = new RecordSet("Hello");
		}
		rs.setPageNo(Integer.parseInt((String) fields.get(Constants.PAGE_NO)));
		rs.setRecordCountPerPage(Integer.parseInt((String) fields
				.get(Constants.RC_COUNT_PER_PAGE)));

		Integer totalCount = (Integer) queryForObject(
				"HelloBizUnit.selectTotalCount", fields, onlineCtx);
		rs.setTotalRecordCount(totalCount);

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });

		responseData.putRecordSet("Hello", rs);

		return responseData;

	}

	/**
	 * 
	 *
	 * @author 이정현 (leejunghyun)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 *	- field : OV_ERRCODE [필드1] 
	 *	- field : OV_ERRMSG [필드2] 
	 *	- field : IV_MGMT_NO_CD [필드3] 
	 *	- field : IV_OUT_PLC_ID [필드4] 
	 *	- field : IV_IN_PLC_ID [필드5] 
	 *	- field : IV_USER_ID [필드6] 
	 *	- field : OV_MGMT_NO [필드7] 
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet testProcedure(IDataSet requestData, IOnlineContext onlineCtx) {
		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("testProcedure method start");
		}

		Map map = requestData.getFieldMap();
		queryForObject("HelloBizUnit.testProcedure", map, onlineCtx);

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { "1" });

		responseData.putFieldMap(map);
		return responseData;

	}

	/**
	 * 
	 *
	 * @author admin (admin)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet testFileManager(IDataSet requestData,
			IOnlineContext onlineCtx) {

		IPsFileUploadManager fileManager = (IPsFileUploadManager) ComponentRegistry
				.lookup(ServiceConstants.FILEUPLOAD);
		try {
			int updateCount = fileManager.saveAllFileInfo("nc_fileDs",
					requestData, onlineCtx);
			fileManager.commitFile("nc_fileDs", requestData);
			return DataSetFactory.createWithOKResultMessage(
					BaseConstants.UPDATE_OK_MESSAGE_ID, new String[] { ""
							+ updateCount });
		} catch (Exception ex) {
			fileManager.rollbackFile("nc_fileDs", requestData);
			throw new BizRuntimeException("TEST", ex);
		}
	}

	/**
	 * 
	 *
	 * @author admin (admin)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 *	- field : SCREEN_ID [필드1] 
	 *	- field : DOC_ID [필드2] 
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getFileList(IDataSet requestData, IOnlineContext onlineCtx) {

		IPsFileUploadManager fileManager = (IPsFileUploadManager) ComponentRegistry
				.lookup(ServiceConstants.FILEUPLOAD);
		return fileManager.getFileInfoList("nc_fileDs", requestData, onlineCtx);
	}

}
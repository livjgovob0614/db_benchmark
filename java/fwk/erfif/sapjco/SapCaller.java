 package fwk.erfif.sapjco;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import nexcore.framework.core.data.DataSet;
import nexcore.framework.core.data.IDataSet;
import nexcore.framework.core.data.IOnlineContext;
import nexcore.framework.core.data.IRecord;
import nexcore.framework.core.data.IRecordHeader;
import nexcore.framework.core.data.IRecordSet;
import nexcore.framework.core.data.RecordHeader;
import nexcore.framework.core.data.RecordSet;
import nexcore.framework.core.exception.BaseRuntimeException;
import nexcore.framework.core.ioc.ComponentRegistry;
import nexcore.framework.core.util.BaseUtils;
import nexcore.framework.core.util.DataSetFactory;
import nexcore.framework.integration.db.ISqlManager;

import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoField;
import com.sap.conn.jco.JCoFieldIterator;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoMetaData;
import com.sap.conn.jco.JCoParameterFieldIterator;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoRecordMetaData;
import com.sap.conn.jco.JCoTable;
import com.sap.conn.jco.ext.DestinationDataProvider;

import fwk.utils.HpcUtils;
import fwk.utils.SAPUtils;




/**
 * <pre>
 * SAP 시스템을 호출할 때 delegator 역할을 한다.
 * 이 클래스의 생성자에 System ID를 받는데 이 정보로 properties 파일을 설정정보를 읽어
 * 각 시스템의 고유 Connection 을 생성한다.
 * 따라서 다음 파일에 설정정보가 있어야 한다.
 * 
 * </pre>
 * 
 * @author greatjin
 *
 */
public class SapCaller extends Thread {
 
    //static String ABAP_AS = "ABAP_AS";
    static String ABAP_AS_NOT_POOLED = "ABAP_AS_WITHOUT_POOL";
    static String ABAP_AS_POOLED = "ABAP_AS_WITH_POOL";
    static String ABAP_MS = "ABAP_MS_WITHOUT_POOL";
    static final String QUERY_OK_MESSAGE_ID = "SKFI4008";
    
    JCoFunction function;
    JCoDestination destination;
    
    /**
     * SAP을 호출할 인터페이스객체
     * 
     * @param systemId : 해당팀의 ID.(e.g. 프로젝트팀은 PM) 이 Id로 프러퍼티 파일에서 설정정보를 읽어온다.
     * @param functionName : 호출할 SAP 함수 명.
     */
	public SapCaller(String systemId, String functionName) {
		this.init(systemId, functionName);
	}
	
	/**
	 * SAP을 호출할 인터페이스객체(배치용입니다~)
	 * 
	 * @param functionName : 호출할 SAP 함수 명.
	 */
	public SapCaller(String functionName) {
		this.init(BaseUtils.getRuntimeMode(), functionName);
	}
	
	
	/**
	 * init
	 * @param systemId
	 * @param functionName
	 */
	private void init(String systemId, String functionName)
	{
		Properties connectProperties = new Properties();
        connectProperties.setProperty(DestinationDataProvider.JCO_LANG, BaseUtils.getConfiguration("JCO_LANG"));
        connectProperties.setProperty(DestinationDataProvider.JCO_POOL_CAPACITY, BaseUtils.getConfiguration("JCO_POOL_CAPACITY"));
        connectProperties.setProperty(DestinationDataProvider.JCO_PEAK_LIMIT, BaseUtils.getConfiguration("JCO_PEAK_LIMIT"));
        connectProperties.setProperty(DestinationDataProvider.JCO_EXPIRATION_TIME, BaseUtils.getConfiguration("JCO_EXPIRATION_TIME"));

        // setup parameter according to sap target >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        SAPUtils.debug("setup parameter according to sap target >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>systemId :"+systemId);
        
//        connectProperties.setProperty(DestinationDataProvider.JCO_CLIENT, BaseUtils.getConfiguration("JCO_CLIENT_" + systemId));
//        connectProperties.setProperty(DestinationDataProvider.JCO_USER, BaseUtils.getConfiguration("JCO_USER_" + systemId));
//        connectProperties.setProperty(DestinationDataProvider.JCO_PASSWD, BaseUtils.getConfiguration("JCO_PASSWD_" + systemId));
//        connectProperties.setProperty(DestinationDataProvider.JCO_ASHOST, BaseUtils.getConfiguration("JCO_ASHOST_" + systemId));
//        connectProperties.setProperty(DestinationDataProvider.JCO_SYSNR, BaseUtils.getConfiguration("JCO_SYSNR_" + systemId));

        connectProperties.setProperty(DestinationDataProvider.JCO_CLIENT, BaseUtils.getConfiguration("JCO_CLIENT." + systemId));
        connectProperties.setProperty(DestinationDataProvider.JCO_USER, BaseUtils.getConfiguration("JCO_USER." + systemId));
        connectProperties.setProperty(DestinationDataProvider.JCO_PASSWD, BaseUtils.getConfiguration("JCO_PASSWD." + systemId));
        connectProperties.setProperty(DestinationDataProvider.JCO_ASHOST, BaseUtils.getConfiguration("JCO_ASHOST." + systemId));
        connectProperties.setProperty(DestinationDataProvider.JCO_SYSNR, BaseUtils.getConfiguration("JCO_SYSNR." + systemId));


        
        
        
        SAPUtils.debug("setup parameter according to sap connectProperties >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+connectProperties);

        SapDestinationDataProvider myProvider = SapDestinationDataProvider.getInstance();   
//        if(!Environment.isDestinationDataProviderRegistered()) Environment.registerDestinationDataProvider(myProvider);
//        myProvider.setDestinationDataEventListener(new EventListener());
        myProvider.changeProperties(ABAP_AS_POOLED+ systemId, connectProperties);
        
        SAPUtils.debug("setup parameter myProvider.getDestinationProperties "+ myProvider.getDestinationProperties(ABAP_AS_POOLED+ systemId));

        
		try {
			destination = JCoDestinationManager.getDestination(ABAP_AS_POOLED+ systemId);
			destination.ping();
		} catch (JCoException e) {
			SAPUtils.debug("getDestination error!!");
			SAPUtils.debug(e.getMessage());
			throw new BaseRuntimeException("XYZE0000", new String[] {e.getMessage()});
		}
		
		try {
			function = destination.getRepository().getFunction(functionName);
			
			JCoParameterList paramList = function.getImportParameterList();
			SAPUtils.debug(functionName);
			SAPUtils.debug("1=============-function : "+function);
			SAPUtils.debug("1=============-paramList : "+paramList);
			if(paramList !=null)
			{
				JCoFieldIterator fieldIter = paramList.getFieldIterator();
				if(fieldIter != null) {
					SAPUtils.debug("1field list");
					SAPUtils.debug("1=============");
					while(fieldIter.hasNextField()) {
						JCoField field = fieldIter.nextField();
						SAPUtils.debug(field.getName());
					}
				}
			}
			
			JCoParameterList tableList = function.getTableParameterList();
			
			if(tableList != null) {
				JCoParameterFieldIterator paramFieldIter = tableList.getParameterFieldIterator();
				while(paramFieldIter.hasNextField()) {
					JCoField field = paramFieldIter.nextField();
					JCoTable table = field.getTable();
					SAPUtils.debug("2===========================");
					SAPUtils.debug("2table name : " + field.getName());
					SAPUtils.debug("2===========================");
	
					JCoFieldIterator tableFieldIter = table.getFieldIterator();
					while(tableFieldIter.hasNextField()) {
						JCoField tableField = tableFieldIter.nextField();
						SAPUtils.debug(tableField.getName());
					}
				}
			}
		} catch (JCoException e) {
			SAPUtils.debug(e.getMessage());
			throw new BaseRuntimeException("XYZE0000", new String[] {e.getMessage()});
		}

        if(function == null)
        	throw new BaseRuntimeException("XYZE0000", new String[] {functionName + " function not found in SAP."});
	}
	
	
//	/**
//	 * SAP을 호출할 인터페이스객체(배치용입니다~)
//	 * 
//	 * @param functionName : 호출할 SAP 함수 명.
//	 */
//	public SapCaller(String functionName) {
////		ResourceBundle resource = null;
////		
////		try {
////			resource = ResourceBundle.getBundle("config.properties.common");
////			resource.getString("JCO_ASHOST");
////		} catch (Exception e1) {
////			resource = null;
////		}
//		
//		Properties connectProperties = this.connect(SAPUtils.TARGET.SCQ.toString());
////		if(resource != null) {
////			connectProperties.setProperty(DestinationDataProvider.JCO_ASHOST, resource.getString("JCO_ASHOST"));
////			connectProperties.setProperty(DestinationDataProvider.JCO_SYSNR,  resource.getString("JCO_SYSNR"));
////			connectProperties.setProperty(DestinationDataProvider.JCO_CLIENT, resource.getString("JCO_CLIENT"));
////			connectProperties.setProperty(DestinationDataProvider.JCO_USER,   resource.getString("JCO_USER"));
////			connectProperties.setProperty(DestinationDataProvider.JCO_PASSWD, resource.getString("JCO_PASSWD"));
////			connectProperties.setProperty(DestinationDataProvider.JCO_LANG,   resource.getString("JCO_LANG"));
////		} else {
////			connectProperties.setProperty(DestinationDataProvider.JCO_ASHOST, "203.235.209.131");
////			connectProperties.setProperty(DestinationDataProvider.JCO_SYSNR,  "02");
////			connectProperties.setProperty(DestinationDataProvider.JCO_CLIENT, "610");
////			connectProperties.setProperty(DestinationDataProvider.JCO_USER,   "SKVPM001");
////			connectProperties.setProperty(DestinationDataProvider.JCO_PASSWD, "#skvpm#");
////			connectProperties.setProperty(DestinationDataProvider.JCO_LANG,   "en");
////		}
////		
//		SapDestinationDataProvider provider = new SapDestinationDataProvider();
//		if(!Environment.isDestinationDataProviderRegistered()) Environment.registerDestinationDataProvider(provider);
//		provider.setDestinationDataEventListener(new EventListener());
//		provider.changePropertiesForABAP_AS_POOLED(connectProperties);
//		
//		
//		try {
//			destination = JCoDestinationManager.getDestination(SapDestinationDataProvider.ABAP_AS_POOLED);
//		} catch (JCoException e) {
//			SAPUtils.debug(e.getMessage());
//			throw new BaseRuntimeException("XYZE0000", new String[] {e.getMessage()});
//		}
//		
//		try {
//			function = destination.getRepository().getFunction(functionName);
//			
//			JCoParameterList paramList = function.getImportParameterList();
//			SAPUtils.debug("3function name");
//			SAPUtils.debug("3=============");
//			SAPUtils.debug(functionName);
//			
//			
//			JCoFieldIterator fieldIter =null;
//			
//			if( paramList!=null)  fieldIter = paramList.getFieldIterator();
//			
//			if(fieldIter != null) {
//				SAPUtils.debug("3field list");
//				SAPUtils.debug("3=============");
//				while(fieldIter.hasNextField()) {
//					JCoField field = fieldIter.nextField();
//					SAPUtils.debug(field.getName());
//				}
//			}
//			
//			JCoParameterList tableList = function.getTableParameterList();
//			
//			if(tableList != null) {
//				JCoParameterFieldIterator paramFieldIter = tableList.getParameterFieldIterator();
//				while(paramFieldIter.hasNextField()) {
//					JCoField field = paramFieldIter.nextField();
//					JCoTable table = field.getTable();
//					SAPUtils.debug("4===========================");
//					SAPUtils.debug("4table name : " + field.getName());
//					SAPUtils.debug("4===========================");
//	
//					JCoFieldIterator tableFieldIter = table.getFieldIterator();
//					while(tableFieldIter.hasNextField()) {
//						JCoField tableField = tableFieldIter.nextField();
//						SAPUtils.debug(tableField.getName());
//					}
//				}
//			}
//			
//			
//		}
//		catch (JCoException e) {
//			SAPUtils.debug(e.getMessage());
//			throw new BaseRuntimeException("XYZE0000", new String[] {e.getMessage()});
//		}
//		 catch (Exception e1) {
//				SAPUtils.debug(e1.getMessage());
//				throw new BaseRuntimeException("XYZE0000", new String[] {e1.getMessage()});
//			} 
//		if(function == null)
//			throw new BaseRuntimeException("XYZE0000", new String[] {functionName + " function not found in SAP."});
//	}

	// sqlMansger
	ISqlManager sm = null;

	private ISqlManager getManager() {
		if (sm == null) {
			sm = (ISqlManager)ComponentRegistry.lookup("pm.db.ISqlManager");
		}
		return sm;
	}

	public void setManager(ISqlManager sm) {
		this.sm = sm;
	}

	// user info set
	private String userId = null;
	private void setUserInfo(IOnlineContext onlineCtx) {
		if(onlineCtx != null) {
			if (onlineCtx == null || onlineCtx.getUserInfo() == null
					|| onlineCtx.getUserInfo().getLoginId() == null || "Anonymous".equals(onlineCtx.getUserInfo().getLoginId()))
				userId = "S0002384";
			else
				userId = onlineCtx.getUserInfo().getLoginId();
		}
	}
	
	public void setUserInfo(String userId) {
		this.userId = userId;
	}
	
	/**
	 * <pre>
	 * 하나의 함수를 호출한 후 다른 함수를 호출하고자 할 경우 사용
	 * </pre>
	 * 
	 * @author 황문수
	 * @param functionName
	 */
	public void setFunction(String functionName) {
		try {
			function = destination.getRepository().getFunction(functionName);
		} catch (JCoException e) {
			SAPUtils.debug(e.getMessage());
			throw new BaseRuntimeException("XYZE0000", new String[] {e.getMessage()});
		}

        if(function == null)
        	throw new BaseRuntimeException("XYZE0000", new String[] {functionName + " function not found in SAP."});
	}
	
	/**
	 * <pre>
	 * 단건의 데이터를 전송하고자 할 경우 사용
	 * data에 field로 데이터를 담아 호출한다.
	 * 이 때 field명은 Sap와 협의된 field name으로 셋팅해야 한다.
	 * 
	 * ex)
	 * IDataSet requsetData = new DataSet();
	 * requestData.putField("key1", "value1");
	 * requestData.putField("key2", "value2");
	 * 
	 * SapCaller caller = new SapCaller("systemId", "functionName");
	 * IDataSet responseData = caller.sendSingleData(requestData);
	 * 
	 * </pre>
	 * 
	 * @author 황문수
	 * @param data(IDataSet) : 전송할 데이터
	 * @return IDataSet
	 */
	public IDataSet sendSingleData(IDataSet data) {
		JCoParameterList paramList = function.getImportParameterList();
		
		if( paramList != null )
		{	
			JCoParameterFieldIterator keys = paramList.getParameterFieldIterator();
			JCoMetaData meta = paramList.getMetaData();
			
			SAPUtils.debug("5Field Set\n========================================");
			while(keys.hasNextField()) {
				JCoField key = keys.nextField();
				if(!data.containsField(key.getName())) continue;
				
				SAPUtils.debug(key.getName() + " : "  + data.getField(key.getName()) + "(Type:" + meta.getTypeAsString(key.getName()) + ")");
				switch(meta.getType(key.getName())) {
				case JCoMetaData.TYPE_DECF16 : break;
				case JCoMetaData.TYPE_DECF34 :  break;
				case JCoMetaData.TYPE_BCD : paramList.setValue(key.getName(), new BigDecimal(data.getField(key.getName()))); break;
				case JCoMetaData.TYPE_FLOAT : paramList.setValue(key.getName(), new Float(data.getField(key.getName())).floatValue()); break;
				case JCoMetaData.TYPE_INT :  break;
				case JCoMetaData.TYPE_INT1 :  break;
				case JCoMetaData.TYPE_INT2 : paramList.setValue(key.getName(), new Integer(data.getField(key.getName())).intValue()); break;
				default : paramList.setValue(key.getName(), data.getField(key.getName()));
				}
			}
		}
		
		
		try {
			function.execute(destination);
		} catch (JCoException e) {
			SAPUtils.debug(e.getMessage());
			throw new BaseRuntimeException("XYZE0000", new String[] {e.getMessage()});
		}
		
		IDataSet responseData = DataSetFactory.createWithOKResultMessage(QUERY_OK_MESSAGE_ID, new String[] {""});

		JCoParameterList outParamList = function.getExportParameterList();
		if(outParamList != null) {
			SAPUtils.debug("6========================================");
			SAPUtils.debug("6Received fields");
			SAPUtils.debug("6========================================");
			JCoParameterFieldIterator fieldIter = outParamList.getParameterFieldIterator();
			while(fieldIter.hasNextField()) {
				JCoField field = fieldIter.nextField();
				SAPUtils.debug(field.getName() + " : " + field.getString());
				responseData.putField(field.getName(), field.getString());
			}
		}
		
		JCoParameterList outTableList = function.getTableParameterList();
		
		if(outTableList != null) {
			SAPUtils.debug("7Received recordSet..."+outTableList);
			JCoParameterFieldIterator fieldIter = outTableList.getParameterFieldIterator();
			while(fieldIter.hasNextField()) {
				JCoField field = fieldIter.nextField();
				JCoTable table = field.getTable();

				if("I_ERET".equals(field.getName()) && table.getNumRows() > 0) {
					table.firstRow();
					if("E".equals(table.getString("TYPE"))) {
						SAPUtils.debug("8Err..."+table.getString("MESSAGE"));
						
						throw new BaseRuntimeException("XYZE0000", new String[] {table.getString("MESSAGE")});
					}
				}
				
				SAPUtils.debug("9===================================");
				SAPUtils.debug("9RecordSet Name : " + field.getName());
				SAPUtils.debug("9Record count : " + table.getNumRows());
				SAPUtils.debug("9===================================");
				convertSapTableToDataSet(field.getName(), table, responseData);
			}
		}
		
		return responseData;
	}
	
	/////////////////////////
	// thread 구현부분 start
	/////////////////////////
	private IDataSet threadRequestData = null;
	private IDataSet threadData = null;
	private CallInfo threadCallInfo = null;
	private CallInfo threadCallInfo_copy = null;
	private IOnlineContext onlineCtx = null;
	private String ERP_SPCL_ID = null;
	private String ERP_SPCL_NM = null;
	private boolean isResume = false;	// 재실행 여부
	
	public void sendDataInThread(IDataSet requestData, IDataSet data, CallInfo callInfo, IOnlineContext onlineCtx) {
		getManager();
		
		Iterator infoList = callInfo.iterator();
		while(infoList.hasNext()) {
			HashMap info = (HashMap)infoList.next();

			// ERP_SPCL_ID 확정
			if(info.containsKey(CallInfo.ERP_SPCL_ID)) {
				if(this.ERP_SPCL_ID == null) this.ERP_SPCL_ID = (String) info.get(CallInfo.ERP_SPCL_ID);
				info.remove(CallInfo.ERP_SPCL_ID);
			}
			// ERP_SPCL_ID 확정
			if(info.containsKey(CallInfo.ERP_SPCL_NM)) {
				if(this.ERP_SPCL_NM == null) this.ERP_SPCL_NM = (String) info.get(CallInfo.ERP_SPCL_NM);
				info.remove(CallInfo.ERP_SPCL_NM);
			}
		}
		
		if(this.ERP_SPCL_ID == null) {
			throw new RuntimeException("ERP 호출 전에 ERP_SPCL_ID 값을 입력하십시오.");
		}
		
		this.ERP_SPCL_ID = this.ERP_SPCL_ID + "(" + requestData.getField("proj_no") + ")";

		this.threadRequestData = requestData;
		this.threadData = data;
		this.threadCallInfo = callInfo;
		this.threadCallInfo_copy = (CallInfo)callInfo.clone();
		this.onlineCtx = onlineCtx;
		
		String resume = requestData.getField("resume");
		if(resume != null && "true".equals(resume.toLowerCase())) this.isResume = true;
		
		setUserInfo(this.onlineCtx);
		
		// thread start
		this.start();
	}
	
	private String inner_error = null;
	public void run() {
		IDataSet responseData = null;
		try {
			responseData = sendData(this.threadData, this.threadCallInfo);
		} catch(Exception e) {
			SAPUtils.debug(e.getMessage());
			
			this.inner_error = e.toString();
		}
		
		if(this.isResume) {
			// if this execution is resumption, update execution info
			modifyErpSpclInfo(responseData, this.threadCallInfo_copy);
		} else {
			// if this execution is not resumption, insert execution info
			responseData.putField("proj_no", this.threadRequestData.getField("proj_no"));
			
			addErpSpclInfo(responseData, this.threadCallInfo_copy);

			this.threadData.putField("ERP_SPCL_ID", this.threadRequestData.getField("ERP_SPCL_ID"));
			this.threadData.putField("ERP_SPCL_SNR_DT", this.threadRequestData.getField("ERP_SPCL_SNR_DT"));
			this.threadData.putField("ERP_SPCL_SNR_ORD", this.threadRequestData.getField("ERP_SPCL_SNR_ORD")); 
			
			saveRequestInfo(this.threadRequestData);
			//saveRequestInfo(this.threadData);
		}
	}
	
	private void modifyErpSpclInfo(IDataSet responseData, CallInfo callInfo) {
		Iterator infoList = callInfo.iterator();
		
		String delim = "";
		
		String ERP_SPCL_FX_NM = function.getName();
		String ERP_SPCL_DS_CD = "";
		String ERP_SPCL_SUCC_YN = "S";
		String ERP_SPCL_ERR_CNTN = "";
		
		while(infoList.hasNext()) {
			HashMap info = (HashMap)infoList.next();
			
			info.remove(CallInfo.RECORD_SET_ID);
			info.remove(CallInfo.TABLE_ID);
			
			
			/* param set */
			Iterator keys = info.keySet().iterator();
			
			if(keys != null) {
				while(keys.hasNext()) {
					ERP_SPCL_DS_CD += delim + (String) info.get(keys.next());
				}
				ERP_SPCL_DS_CD = ERP_SPCL_DS_CD.substring(0, 1);
			}
			delim = ";";
		}
		
		// 에러여부 체크
		if(responseData == null) {
			ERP_SPCL_SUCC_YN = "E";
			if(this.inner_error == null)
				ERP_SPCL_ERR_CNTN = "error";
			else if(this.inner_error.length() > 4000)
				ERP_SPCL_ERR_CNTN = this.inner_error.substring(0, 4000);
			else
				ERP_SPCL_ERR_CNTN = this.inner_error;
		} else {
			if(responseData.containsRecordSet(CallInfo.ERROR_RETURN_TABLE)) {
				
				IRecordSet I_ERET = responseData.getRecordSet(CallInfo.ERROR_RETURN_TABLE);
				for(int i = 0; i < I_ERET.getRecordCount(); i++) {
					if("E".equals(I_ERET.get(i, "TYPE"))) {
						ERP_SPCL_SUCC_YN = "E";
						ERP_SPCL_ERR_CNTN = I_ERET.get(i, "MESSAGE");
						break;	// 하나가 에러면 전체 에러로 본다.
					}
				}
			}
		}

		HashMap param = new HashMap();
		param.put("ERP_SPCL_ID", ERP_SPCL_ID);
		param.put("ERP_SPCL_FX_NM", ERP_SPCL_FX_NM);
		param.put("ERP_SPCL_DS_CD", ERP_SPCL_DS_CD);
		param.put("ERP_SPCL_SUCC_YN", ERP_SPCL_SUCC_YN);
		param.put("ERP_SPCL_ERR_CNTN", ERP_SPCL_ERR_CNTN);
		param.put("userId", this.userId);
		
		String ERP_SPCL_SNR_ORD = this.threadRequestData.getField("ERP_SPCL_SNR_ORD");	// 전송순서와 일자는 기존 데이터를 가져와야 함.
		String ERP_SPCL_SNR_DT = this.threadRequestData.getField("ERP_SPCL_SNR_DT");
		param.put("ERP_SPCL_SNR_ORD", ERP_SPCL_SNR_ORD);
		param.put("ERP_SPCL_SNR_DT", ERP_SPCL_SNR_DT);
		
		this.threadRequestData.putField("ERP_SPCL_ID", ERP_SPCL_ID);
		
		sm.insert("SapCaller.modifyErpSpclInfo", param);
	}
	
	private void addErpSpclInfo(IDataSet responseData, CallInfo callInfo) {
		Iterator infoList = callInfo.iterator();
		
		String delim = "";
		
		String PROJ_NO = "";
		String ERP_SPCL_FX_NM = function.getName();
		String ERP_SPCL_DS_CD = "";
		String ERP_SPCL_SUCC_YN = "S";
		String ERP_SPCL_ERR_CNTN = "";
		
		while(infoList.hasNext()) {
			HashMap info = (HashMap)infoList.next();
			
			info.remove(CallInfo.RECORD_SET_ID);
			info.remove(CallInfo.TABLE_ID);
			
			
			/* param set */
			Iterator keys = info.keySet().iterator();
			
			if(keys != null) {
				while(keys.hasNext()) {
					ERP_SPCL_DS_CD += delim + (String) info.get(keys.next());
				}
				ERP_SPCL_DS_CD = ERP_SPCL_DS_CD.substring(0, 1);
			}
			delim = ";";
		}
		
		// 에러여부 체크
		if(responseData == null) {
			ERP_SPCL_SUCC_YN = "E";
			if(this.inner_error == null)
				ERP_SPCL_ERR_CNTN = "error";
			else if(this.inner_error.length() > 4000)
				ERP_SPCL_ERR_CNTN = this.inner_error.substring(0, 4000);
			else
				ERP_SPCL_ERR_CNTN = this.inner_error;
		} else {
			PROJ_NO = responseData.getField("proj_no");
			if(responseData.containsRecordSet(CallInfo.ERROR_RETURN_TABLE)) {
				IRecordSet I_ERET = responseData.getRecordSet(CallInfo.ERROR_RETURN_TABLE);
				for(int i = 0; i < I_ERET.getRecordCount(); i++) {
					if("E".equals(I_ERET.get(i, "TYPE"))) {
						ERP_SPCL_SUCC_YN = "E";
						ERP_SPCL_ERR_CNTN = I_ERET.get(i, "MESSAGE");
						break;	// 하나가 에러면 전체 에러로 본다.
					}
				}
			}
		}

		HashMap param = new HashMap();
		param.put("PROJ_NO", PROJ_NO);
		param.put("ERP_SPCL_ID", ERP_SPCL_ID);
		param.put("ERP_SPCL_FX_NM", ERP_SPCL_FX_NM);
		param.put("ERP_SPCL_DS_CD", ERP_SPCL_DS_CD);
		param.put("ERP_SPCL_SUCC_YN", ERP_SPCL_SUCC_YN);
		param.put("ERP_SPCL_ERR_CNTN", ERP_SPCL_ERR_CNTN);
		param.put("userId", this.userId);
		
		Map orderMap = sm.queryForFieldMap("SapCaller.getOrder", param);

		String ERP_SPCL_SNR_ORD = (String) orderMap.get("ERP_SPCL_SNR_ORD");
		String ERP_SPCL_SNR_DT = (String) orderMap.get("ERP_SPCL_SNR_DT");
		param.put("ERP_SPCL_SNR_ORD", ERP_SPCL_SNR_ORD);
		param.put("ERP_SPCL_SNR_DT", ERP_SPCL_SNR_DT);
		
		this.threadRequestData.putField("ERP_SPCL_ID", ERP_SPCL_ID);
		this.threadRequestData.putField("ERP_SPCL_SNR_DT", ERP_SPCL_SNR_DT);
		this.threadRequestData.putField("ERP_SPCL_SNR_ORD", ERP_SPCL_SNR_ORD);
		
		sm.insert("SapCaller.addErpSpclInfo", param);
	}

	/**
	 * 재실행을 위한 정보 저장
	 * @param requestData
	 */
	private void saveRequestInfo(IDataSet requestData) {
		Map field = requestData.getFieldMap();

		String ERP_SPCL_ID = (String) field.get("ERP_SPCL_ID");
		String ERP_SPCL_SNR_DT = (String) field.get("ERP_SPCL_SNR_DT");
		String ERP_SPCL_SNR_ORD = (String) field.get("ERP_SPCL_SNR_ORD");
		
		Iterator recordIds = requestData.getRecordSetIds();
		
		Map insertData = null;

		// 1. insert field
		// 1.1. insert field header
		insertData = setCommonInfo(field);
		insertData.put("ERP_SPCL_DTST_DS_CD", "01");	// 이 데이터는 field임
		insertData.put("ERP_SPCL_DTST_ID", "field");
		
		Iterator fieldKeys = field.keySet().iterator();
		int fieldOrd = 0;
		while(fieldKeys.hasNext()) {
			String key = (String)fieldKeys.next();
			insertData.put("ERP_SPCL_DTST_DS_ORD", Integer.toString(fieldOrd));
			insertData.put("ERP_SPCL_DTST_DS_NM", key);
			
			sm.insert("SapCaller.addErpSpclHeaderInfo", insertData);
			
			String data = (String)field.get(key);
			insertData.put("ERP_SPCL_DTST_DS_PRTC_ORD", "0");
			insertData.put("ERP_SPCL_CNTN", data);
			
			sm.insert("SapCaller.addErpSpclRecordData", insertData);
			
			fieldOrd++;
		}
		
		// 2. insert recordset
		insertData = setCommonInfo(field);
		insertData.put("ERP_SPCL_DTST_DS_CD", "02");	// 이 데이터는 recordset임
		
		while(recordIds.hasNext()) {
			String dataSetID = (String) recordIds.next();
			insertData.put("ERP_SPCL_DTST_ID", dataSetID);
			
			IRecordSet dataSet = requestData.getRecordSet(dataSetID);
			for(int i = 0; i < dataSet.getHeaderCount(); i++) {
				insertData.put("ERP_SPCL_DTST_DS_ORD", Integer.toString(i));
				insertData.put("ERP_SPCL_DTST_DS_NM", dataSet.getHeader(i).getName());
				
				sm.insert("SapCaller.addErpSpclHeaderInfo", insertData);
			}
			
			for(int i = 0; i < dataSet.getRecordCount(); i++) {
				for(int j = 0; j < dataSet.getHeaderCount(); j++) {
					insertData.put("ERP_SPCL_DTST_DS_ORD", Integer.toString(j));
					insertData.put("ERP_SPCL_DTST_DS_PRTC_ORD", Integer.toString(i));
					insertData.put("ERP_SPCL_CNTN", dataSet.get(i, j));

					sm.insert("SapCaller.addErpSpclRecordData", insertData);
				}
			}
		}
	}
	
	private HashMap setCommonInfo(Map field) {
		HashMap rtn = new HashMap();
		
		rtn.put("ERP_SPCL_ID", field.get("ERP_SPCL_ID"));
		rtn.put("ERP_SPCL_SNR_DT", field.get("ERP_SPCL_SNR_DT"));
		rtn.put("ERP_SPCL_SNR_ORD", field.get("ERP_SPCL_SNR_ORD"));
		rtn.put("userId", this.userId);
		
		return rtn;
	}
	
	/////////////////////////
	// thread 구현부분 end
	/////////////////////////
	
	/**
	 * <pre>
	 * 다건의 레코드셋을 전송하고자 할 때 사용한다.
	 * IDataSet에 전송하고자 하는 레코드셋을 담고 해당 레코드 셋과 같이 전송할 파라미터을 CallInfo에 담아주면 된다.
	 * 또한 CallInfo에는 전송하고자 하는 레코드셋의 ID와 대상 Sap Table명을 명시해야한다.
	 * 
	 * ex) 
	 * IDataSet requestData = new DataSet(); // 전송할 데이터셋
	 * IRecordSet rs1 = sm.queryForRecordSet(...);  // 전송하고자 하는 레코드셋을 생성한다.
	 * IRecordSet rs2 = sm.queryForRecordSet(...);
	 * 
	 * // 레코드셋들을 전송데이터셋에 담는다.
	 * requestData.putRecordSet("rs1", rs1);
	 * requestData.putRecordSet("rs2", rs2);
	 * 
	 * // 첫번째 레코드셋과 같이 전송할 파라미터를 설정한다.
	 * HashMap params = new HashMap();
	 * params.put("key1", "value1");
	 * params.put("key2", "value2");
	 * 
	 * // 파라미터을 설정할 객체생성 - 여기에 레코드셋과 해당 테이블, 파라미터들을 담는다.
	 * CallInfo info = CallInfo.create("rs1", "table1", params);
	 * 
	 * // 두번째 레코드셋과 같이 전송할 파라미터를 설정한다.
	 * params = new HashMap();
	 * params.put("key3", "value3");
	 * params.put("key4", "value4");
	 * 
	 * info.addInfo("rs2", "table2", params); // 두번째 레코드셋의 파라미터를 담는다.
	 * 
	 * // SapCaller 객체를 생성한다. 이 객체로 Sap를 호출한다.
	 * // SystemId는 해당 시스템의 ID (ex:프로젝트팀 -> PM)
	 * // functionName은 호출할 Sap function명
	 * SapCaller caller = new SapCaller("SystemId", "functionName");
	 * 
	 * // 함수 실행
	 * IDataSet responseData = caller.sendData(requestData, info);
	 * </pre>
	 * 
	 * @author 황문수
	 * @param data(IDataSet) : 전송할 데이터
	 * @param callInfo(CallInfo) : 레코드셋 명세 및 파라미터
	 * @return IDataSet
	 */
	public IDataSet sendData(IDataSet data, CallInfo callInfo) {
		return sendData(data, callInfo, true);
	}

	public IDataSet sendData(IDataSet data, CallInfo callInfo, boolean err_proc_yn) {
		if(callInfo != null) {
			Iterator infoList = callInfo.iterator();
			
			while(infoList.hasNext()) {
				HashMap info = (HashMap)infoList.next();
				String RECORD_SET_ID = (String)info.get(CallInfo.RECORD_SET_ID);
				String TABLE_ID = (String)info.get(CallInfo.TABLE_ID);
				
				info.remove(CallInfo.RECORD_SET_ID);
				info.remove(CallInfo.TABLE_ID);
				
				/* param set */
				Iterator keys = info.keySet().iterator();
				
				if(keys != null) {
					SAPUtils.debug("8Param list\n=====================");
					JCoParameterList paramList = function.getImportParameterList();
					while(keys.hasNext()) {
						String key = (String) keys.next();
						SAPUtils.debug(key+" : " + (String)info.get(key));
						paramList.setValue(key, (String)info.get(key));
					}
				}
				
				/* record set */
				IRecordSet recordSet = data.getRecordSet(RECORD_SET_ID);
				
				JCoParameterList paramList = function.getTableParameterList();
				JCoMetaData meta = paramList.getMetaData();
				JCoTable table = function.getTableParameterList().getTable(TABLE_ID);
				Iterator records = recordSet.getRecords();
				int headercount = meta.getFieldCount();
				
				SAPUtils.debug("9===================================");
				SAPUtils.debug("9RecordSet Data("+ RECORD_SET_ID +")");
				SAPUtils.debug("9===================================");
				int j=0;
				while(records.hasNext()) {
					Iterator headers = recordSet.getHeaders();
					IRecord record = (IRecord)records.next();
					table.appendRow();
					IRecordHeader header = (IRecordHeader)headers.next();
					JCoRecordMetaData recMeta = meta.getRecordMetaData(TABLE_ID);
					
					for(int i = 0; i < recMeta.getFieldCount(); i++) {
						String headerName = recMeta.getName(i);
						
						if(!record.containsKey(headerName)) continue;
						
						SAPUtils.debug(headerName + " : "  + record.get(headerName) + "(Type:" + recMeta.getTypeAsString(i) + ")");
						switch(recMeta.getType(i)) {
						case JCoMetaData.TYPE_DECF16 : break;
						case JCoMetaData.TYPE_DECF34 : break;
						case JCoMetaData.TYPE_BCD : table.setValue(headerName, new BigDecimal(record.get(headerName))); break;
						case JCoMetaData.TYPE_FLOAT : table.setValue(headerName, new Float(record.get(headerName)).floatValue()); break;
						case JCoMetaData.TYPE_INT : break;
						case JCoMetaData.TYPE_INT1 : break;
						case JCoMetaData.TYPE_INT2 : table.setValue(headerName, new Integer(record.get(headerName)).intValue()); break;
						default : table.setValue(headerName, record.get(headerName));
						}
					}
				}
				
				try {
					function.execute(destination);
				} catch (JCoException e) {
					SAPUtils.debug(e.getMessage());
					throw new BaseRuntimeException("XYZE0000", new String[] {e.getMessage()});
				}
			}
		} else {
			JCoParameterList paramList = function.getImportParameterList();
			
			JCoParameterFieldIterator keys = paramList.getParameterFieldIterator();
			JCoMetaData meta = paramList.getMetaData();
			
			SAPUtils.debug("22Field Set\n========================================");
			while(keys.hasNextField()) {
				JCoField key = keys.nextField();
				if(!data.containsField(key.getName())) continue;
				
				SAPUtils.debug(key.getName() + " : "  + data.getField(key.getName()) + "(Type:" + meta.getTypeAsString(key.getName()) + ")");
				switch(meta.getType(key.getName())) {
				case JCoMetaData.TYPE_DECF16 :  break;
				case JCoMetaData.TYPE_DECF34 :  break;
				case JCoMetaData.TYPE_BCD : paramList.setValue(key.getName(), new BigDecimal(data.getField(key.getName()))); break;
				case JCoMetaData.TYPE_FLOAT : paramList.setValue(key.getName(), new Float(data.getField(key.getName())).floatValue()); break;
				case JCoMetaData.TYPE_INT :  break;
				case JCoMetaData.TYPE_INT1 :  break;
				case JCoMetaData.TYPE_INT2 : paramList.setValue(key.getName(), new Integer(data.getField(key.getName())).intValue()); break;
				default : paramList.setValue(key.getName(), data.getField(key.getName()));
				}
			}

			try {
				function.execute(destination);
			} catch (JCoException e) {
				SAPUtils.debug(e.getMessage());
				throw new BaseRuntimeException("XYZE0000", new String[] {e.getMessage()});
			}
		}
		
		IDataSet responseData = DataSetFactory.createWithOKResultMessage(QUERY_OK_MESSAGE_ID, new String[] {""});

		JCoParameterList outParamList = function.getExportParameterList();
		if(outParamList != null) {
			SAPUtils.debug("23========================================");
			SAPUtils.debug("23Received fields");
			SAPUtils.debug("23========================================");
			JCoParameterFieldIterator fieldIter = outParamList.getParameterFieldIterator();
			while(fieldIter.hasNextField()) {
				JCoField field = fieldIter.nextField();
				SAPUtils.debug(field.getName() + " : " + field.getString());
				responseData.putField(field.getName(), field.getString());
			}
		}
		
		JCoParameterList outTableList = function.getTableParameterList();
		
		if(outTableList != null) {
			SAPUtils.debug("24Received recordSet...");
			JCoParameterFieldIterator fieldIter = outTableList.getParameterFieldIterator();
			while(fieldIter.hasNextField()) {
				JCoField field = fieldIter.nextField();
				JCoTable table = field.getTable();
				
				// err_proc_yn : 에러를 처리할 것인지 여부. true인 경우 에러리턴이면 Exception을 throw한다.
				if("I_ERET".equals(field.getName()) && table.getNumRows() > 0 && err_proc_yn) {
					table.firstRow();
					if("E".equals(table.getString("TYPE"))) {
						//throw new BaseRuntimeException("XYZE0000", new String[] {table.getString("MESSAGE")});
					}
				}
				
				SAPUtils.debug("24===================================");
				SAPUtils.debug("24RecordSet Name : " + field.getName());
				SAPUtils.debug("24Record count : " + table.getNumRows());
				SAPUtils.debug("24===================================");
				convertSapTableToDataSet(field.getName(), table, responseData);
			}
		}
		
		return responseData;
	}
	
	/**
	 * <pre>
	 * 다건의 레코드셋을 전송하고자 할 때 사용한다.
	 * IDataSet에 전송하고자 하는 레코드셋을 담고 해당 레코드 셋과 같이 전송할 파라미터을 CallInfo에 담아주면 된다.
	 * 또한 CallInfo에는 전송하고자 하는 레코드셋의 ID와 대상 Sap Table명을 명시해야한다.
	 * 
	 * ex) 
	 * IDataSet requestData = new DataSet(); // 전송할 데이터셋
	 * IRecordSet rs1 = sm.queryForRecordSet(...);  // 전송하고자 하는 레코드셋을 생성한다.
	 * IRecordSet rs2 = sm.queryForRecordSet(...);
	 * 
	 * // 레코드셋들을 전송데이터셋에 담는다.
	 * requestData.putRecordSet("rs1", rs1);
	 * requestData.putRecordSet("rs2", rs2);
	 * 
	 * // 첫번째 레코드셋과 같이 전송할 파라미터를 설정한다.
	 * HashMap params = new HashMap();
	 * params.put("key1", "value1");
	 * params.put("key2", "value2");
	 * 
	 * // 파라미터을 설정할 객체생성 - 여기에 레코드셋과 해당 테이블, 파라미터들을 담는다.
	 * CallInfo info = CallInfo.create("rs1", "table1", params);
	 * 
	 * // 두번째 레코드셋과 같이 전송할 파라미터를 설정한다.
	 * params = new HashMap();
	 * params.put("key3", "value3");
	 * params.put("key4", "value4");
	 * 
	 * info.addInfo("rs2", "table2", params); // 두번째 레코드셋의 파라미터를 담는다.
	 * 
	 * // SapCaller 객체를 생성한다. 이 객체로 Sap를 호출한다.
	 * // SystemId는 해당 시스템의 ID (ex:프로젝트팀 -> PM)
	 * // functionName은 호출할 Sap function명
	 * SapCaller caller = new SapCaller("SystemId", "functionName");
	 * 
	 * // 함수 실행
	 * IDataSet responseData = caller.sendData(requestData, info);
	 * </pre>
	 * 
	 * @author 황문수
	 * @param data(IDataSet) : 전송할 데이터
	 * @param callInfo(CallInfo) : 레코드셋 명세 및 파라미터
	 * @return IDataSet
	 */
	public IDataSet sendData(IDataSet data, HashMap callInfo, boolean err_proc_yn) {
		HashMap<String, String> params = (HashMap)callInfo.get("params");
		Iterator keys = params.keySet().iterator();
		
		/* param set */
		JCoParameterList jcoParam = function.getImportParameterList();
		if(jcoParam != null)
		{
			while(keys.hasNext()) {
				String key = (String) keys.next();
				jcoParam.setValue(key, (String)params.get(key));
			}
		}
		
		
		Iterator<String> infoList = callInfo.keySet().iterator();
		
		while(infoList.hasNext()) {
			String key = infoList.next();
			if("params".equals(key)) continue;
			
			String RECORD_SET_ID = key;
			String TABLE_ID = (String)callInfo.get(key);
			
			/* record set */
			IRecordSet recordSet = data.getRecordSet(RECORD_SET_ID);
			
			JCoParameterList paramList = function.getTableParameterList();
			JCoMetaData meta = paramList.getMetaData();
			JCoTable table = function.getTableParameterList().getTable(TABLE_ID);
			Iterator records = recordSet.getRecords();
			int headercount = meta.getFieldCount();
			
			SAPUtils.debug("25===================================");
			SAPUtils.debug("25RecordSet Data("+ RECORD_SET_ID +")");
			SAPUtils.debug("25===================================");
			int j=0;
			while(records.hasNext()) {
				Iterator headers = recordSet.getHeaders();
				IRecord record = (IRecord)records.next();
				table.appendRow();
				IRecordHeader header = (IRecordHeader)headers.next();
				JCoRecordMetaData recMeta = meta.getRecordMetaData(TABLE_ID);
				
				for(int i = 0; i < recMeta.getFieldCount(); i++) {
					String headerName = recMeta.getName(i);
					
					if(!record.containsKey(headerName)) continue;
					
					SAPUtils.debug(headerName + " : "  + record.get(headerName) + "(Type:" + recMeta.getTypeAsString(i) + ")");
					switch(recMeta.getType(i)) {
					case JCoMetaData.TYPE_DECF16 : break;
					case JCoMetaData.TYPE_DECF34 : break;
					case JCoMetaData.TYPE_BCD : table.setValue(headerName, new BigDecimal(record.get(headerName))); break;
					case JCoMetaData.TYPE_FLOAT : table.setValue(headerName, new Float(record.get(headerName)).floatValue()); break;
					case JCoMetaData.TYPE_INT : break;
					case JCoMetaData.TYPE_INT1 : break;
					case JCoMetaData.TYPE_INT2 : table.setValue(headerName, new Integer(record.get(headerName)).intValue()); break;
					default : table.setValue(headerName, record.get(headerName));
					}
				}
			}
		}
		
		try {
			function.execute(destination);
		} catch (JCoException e) {
			SAPUtils.debug(e.getMessage());
			throw new BaseRuntimeException("XYZE0000", new String[] {e.getMessage()});
		}
		
		IDataSet responseData = DataSetFactory.createWithOKResultMessage(QUERY_OK_MESSAGE_ID, new String[] {""});

		JCoParameterList outParamList = function.getExportParameterList();
		if(outParamList != null) {
			SAPUtils.debug("26========================================");
			SAPUtils.debug("26Received fields");
			SAPUtils.debug("26========================================");
			JCoParameterFieldIterator fieldIter = outParamList.getParameterFieldIterator();
			while(fieldIter.hasNextField()) {
				JCoField field = fieldIter.nextField();
				SAPUtils.debug(field.getName() + " : " + field.getString());
				responseData.putField(field.getName(), field.getString());
			}
		}
		
		JCoParameterList outTableList = function.getTableParameterList();
		
		if(outTableList != null) {
			SAPUtils.debug("27Received recordSet...");
			JCoParameterFieldIterator fieldIter = outTableList.getParameterFieldIterator();
			while(fieldIter.hasNextField()) {
				JCoField field = fieldIter.nextField();
				JCoTable table = field.getTable();
				
				if("I_ERET".equals(field.getName()) && table.getNumRows() > 0 && err_proc_yn) {
					table.firstRow();
					if("E".equals(table.getString("TYPE"))) {
						throw new BaseRuntimeException("XYZE0000", new String[] {table.getString("MESSAGE")});
					}
				}
				
				SAPUtils.debug("27===================================");
				SAPUtils.debug("27RecordSet Name : " + field.getName());
				SAPUtils.debug("27Record count : " + table.getNumRows());
				SAPUtils.debug("27===================================");
				convertSapTableToDataSet(field.getName(), table, responseData);
			}
		}
		
		return responseData;
	}
	
	/**
	 * <pre>
	 * Sap에서 데이터를 받고자 할 경우 사용
	 * 호출할 함수에 넘길 파라미터를 data에 담아 호출한다.
	 * 리턴되는 데이터는 IDataSet에 field와 recordSet 형태로 담겨있다.
	 * 
	 * 사용법은 sendSingleData와 동일하다.
	 * 다만 리턴데이터를 IDataSet으로 변형시켜 리턴하는 점만 다르다.
	 * 
	 * ex)
	 * IDataSet requsetData = new DataSet();
	 * requestData.putField("key1", "value1");
	 * requestData.putField("key2", "value2");
	 * 
	 * SapCaller caller = new SapCaller("systemId", "functionName");
	 * IDataSet responseData = caller.requestData(requestData);
	 * 
	 * </pre>
	 * 
	 * @author 황문수
	 * @param data(IDataSet) : 호출할 함수의 파라미터
	 * @return IDataSet : SAP에서 리턴된 데이터
	 */
	public IDataSet requestData(IDataSet data) {
		JCoParameterList paramList = function.getImportParameterList();
		
		JCoParameterFieldIterator keys = paramList.getParameterFieldIterator();
		JCoMetaData meta = paramList.getMetaData();
		
		SAPUtils.debug("28========================================");
		SAPUtils.debug("28Request fields");
		SAPUtils.debug("28========================================");
		while(keys.hasNextField()) {
			JCoField key = keys.nextField();
			if(!data.containsField(key.getName())) continue;
			
			SAPUtils.debug(key.getName() + " : "  + data.getField(key.getName()) + "(Type:" + meta.getTypeAsString(key.getName()) + ")");
			switch(meta.getType(key.getName())) {
			case JCoMetaData.TYPE_DECF16 :  break;
			case JCoMetaData.TYPE_DECF34 :  break;
			case JCoMetaData.TYPE_BCD : paramList.setValue(key.getName(), new BigDecimal(data.getField(key.getName()))); break;
			case JCoMetaData.TYPE_FLOAT : paramList.setValue(key.getName(), new Float(data.getField(key.getName())).floatValue()); break;
			case JCoMetaData.TYPE_INT :  break;
			case JCoMetaData.TYPE_INT1 :  break;
			case JCoMetaData.TYPE_INT2 : paramList.setValue(key.getName(), new Integer(data.getField(key.getName())).intValue()); break;
			default : paramList.setValue(key.getName(), data.getField(key.getName()));
			}
		}
		
		try {
			function.execute(destination);
		} catch (JCoException e) {
			SAPUtils.debug(e.getMessage());
			throw new BaseRuntimeException("XYZE0000", new String[] {e.getMessage()});
		}
		
		IDataSet responseData = DataSetFactory.createWithOKResultMessage(QUERY_OK_MESSAGE_ID, new String[] {""});
		
		JCoParameterList outParamList = function.getExportParameterList();
		if(outParamList != null) {
			SAPUtils.debug("29========================================");
			SAPUtils.debug("29Received fields");
			SAPUtils.debug("29========================================");
			JCoParameterFieldIterator fieldIter = outParamList.getParameterFieldIterator();
			while(fieldIter.hasNextField()) {
				JCoField field = fieldIter.nextField();
				SAPUtils.debug(field.getName() + " : " + field.getString());
				responseData.putField(field.getName(), field.getString());
			}
		}
		
		JCoParameterList outTableList = function.getTableParameterList();
		
		if(outTableList != null) {
			SAPUtils.debug("31Received recordSet...");
			JCoParameterFieldIterator fieldIter = outTableList.getParameterFieldIterator();
			while(fieldIter.hasNextField()) {
				JCoField field = fieldIter.nextField();
				JCoTable table = field.getTable();
				
				if("I_ERET".equals(field.getName()) && table.getNumRows() > 0) {
					table.firstRow();
					if("E".equals(table.getString("TYPE"))) {
						throw new BaseRuntimeException("XYZE0000", new String[] {table.getString("MESSAGE")});
					}
				}
				
				SAPUtils.debug("31===================================");
				SAPUtils.debug("31RecordSet Name : " + field.getName());
				SAPUtils.debug("31Record count : " + table.getNumRows());
				SAPUtils.debug("31===================================");
				convertSapTableToDataSet(field.getName(), table, responseData);
			}
		}
		
		return responseData;
	}
	
	/**
	 * <pre>
	 * requestData Method에서 사용하는 내부 Method
	 * SAP에서 리턴된 Table형태의 데이터를 RecordSet으로 변환한다.
	 * 
	 * </pre>
	 * 
	 * @author 황문수
	 * @param recordSetName : target에 담길 레코드셋의 id(Table명을 사용함)
	 * @param outboundData : SAP에서 리턴된 Table형태의 데이터
	 * @param target : 레코드셋을 담을 객체
	 */
	private void convertSapTableToDataSet(String recordSetName, JCoTable outboundData, IDataSet target) {
		JCoFieldIterator fieldIter = outboundData.getFieldIterator();
		RecordSet recordSet = new RecordSet(recordSetName);
		
		while(fieldIter.hasNextField()) {
			JCoField field = fieldIter.nextField();
			recordSet.addHeader(new RecordHeader(field.getName(), field.getType()));
		}
		
		outboundData.firstRow();
		int i = 1;
		if(outboundData.getNumRows() > 0) {
			do {
				SAPUtils.debug("32Record Number : " + i++);
				IRecord record = recordSet.newRecord();
				fieldIter.reset();
				while(fieldIter.hasNextField()) {
					JCoField field = fieldIter.nextField();
					SAPUtils.debug(field.getName() + " : " + outboundData.getString(field.getName()));
					record.set(field.getName(), outboundData.getString(field.getName()));
				}
				recordSet.addRecord(record);
			} while(outboundData.nextRow());
		}
		
		target.putRecordSet(recordSetName, recordSet);
	}
	
	/**
	 * <pre>
	 * ERP에서 리턴된 데이터는 ERP에서 정의된 필드명으로 되어있다.
	 * 따라서 팀 내에서 정의된 필드로 변경할 필요가 있는데 이 때 이 메소드를 이용하면 된다.
	 * 
	 * 사용방법은 java.util.HashMap을 이용하여 바꿀 내용을 정의한 후 인자로 넘겨주면 된다.
	 * 
	 * 예를들어 ERP에서 정의된 필드와 레코드셋의 정의가 다음과 같다고 하자
	 * 
	 * field : AA, BB
	 * record set :
	 *     set name 1 : rc1
	 *     set 1 fields : rAA, rBB
	 *     
	 *     set name 2 : rc2
	 *     set 2 fields : rCC, rDD
	 * 
	 * 이 것을 다음과 같이 필드명을 바꾼다고 하자
	 * 
	 * field : aa, bb
	 * record set :
	 *     set name 1 : new_rc1
	 *     set 1 fields : raa, rbb
	 *     
	 *     set name 2 : new_rc2
	 *     set 2 fields : rcc, rdd
	 * 
	 * 그러면 HashMap에 다음과 같이 셋팅한다.
	 * 
	 * // 1. 필드정보 셋
	 * HashMap fields = new HashMap();
	 * fields.put("AA", "aa"); // key를 ERP에서 정의한 field명으로 하고 value를 새로 정의할 field명으로 셋팅한다. 말하자면 AA 필드를 aa로 바꾼다는 의미이다.
	 * fields.put("BB", "bb");
	 * 
	 * // 2. 레코드셋명 정보 셋
	 * HashMap recordSetNames = new HashMap();
	 * recordSetNames.put("rc1", "new_rc1"); // 레코드셋 명도 field명과 같은 방법이다. 즉 rc1을 new_rc1으로 바꾼다는 의미이다.
	 * recordSetNames.put("rc2", "new_rc2");
	 * 
	 * // 3. 레코드셋 필드명 정의
	 * HashMap recordSet1Fields = new HashMap();
	 * recordSet1Fields.put("rAA", "raa"); // 레코드셋내의 필드정의도 동일한 방법으로 셋팅한다.
	 * recordSet1Fields.put("rBB", "rbb");
	 * 
	 * HashMap recordSet2Fields = new HashMap();
	 * recordSet2Fields.put("rCC", "rcc"); // 레코드셋내의 필드정의도 동일한 방법으로 셋팅한다.
	 * recordSet2Fields.put("rDD", "rdd");
	 * 
	 * // 최종적으로 위 정보들을 하나의 HashMap에 담는다. (중요)여기서의 key는 정해진 명을 사용해야 한다.
	 * HashMap changeInfo = new HashMap();
	 * changeInfo.put("fieldNames", fields);
	 * changeInfo.put("recordSetNames", recordSetNames);
	 * changeInfo.put("new_rc1", recordSet1Fields);	// 레코드셋의 필드명을 정의한 것은 새로 바꿀 레코드셋 키로 담는다. 즉 위 2번에서 rc1을 new_rc1으로 바꾼다고 하였으므로 여기서 키를 new_rc1로 한다.
	 * changeInfo.put("new_rc2", recordSet2Fields); // 마찬가지이다.
	 * 
	 * // 다음을 호출하면 리턴되는 IDataSet에는 바뀐 정보로 담겨져 있다.
	 * IDataSet response = changeFieldName(data, changeInfo);
	 * 
	 * 
	 * 위 코드에서 필요없는 부분은 제외하면 된다.
	 * 즉 field가 없으면 
	 * changeInfo.put("fieldNames", fields);
	 * 라고 되어있는 부분을 제외시키고
	 * recordSet이 없으면 
	 * changeInfo.put("recordSetNames", recordSetNames);
	 * 라고 되어있는 부분을 제외시키면 된다.
	 * 
	 * ※ 참고로 위에서 정의한 필드와 레코드의 정보만을 변경하고 나머지는 제외된다.
	 *    ERP에서 정의한 필드가 AA, BB, CC였고
	 *    변경정보를 위와같이 AA -> aa, BB -> bb로만 하면
	 *    리턴되는 IDataSet에는 CC정보가 제외된다.
	 *    
	 *    단. 메세지정보는 동일하게 셋팅된다.
	 * 
	 * </pre>
	 * @param data
	 * @param changeInfo
	 * @return
	 */
	public IDataSet changeFieldName(IDataSet data, HashMap changeInfo) {
		HashMap fieldNames = null;
		if(changeInfo.containsKey("fieldNames")) fieldNames = (HashMap)changeInfo.get("fieldNames");
		
		HashMap recordSetNames = null;
		if(changeInfo.containsKey("recordSetNames")) recordSetNames = (HashMap)changeInfo.get("recordSetNames");
		
		IDataSet rtn = new DataSet();
		rtn.setResultMessage(data.getResultMessage());
		
		// field set
		if (fieldNames != null) {
			Iterator keys = fieldNames.keySet().iterator();
			while (keys.hasNext()) {
				String key = (String) keys.next();
				rtn.putField((String) fieldNames.get(key), data.getField(key));
			}
		}
		
		if (recordSetNames != null) {
			Iterator recordSetKeys = recordSetNames.keySet().iterator();
			while (recordSetKeys.hasNext()) {
				String key = (String)recordSetKeys.next();
				HashMap headerInfo = (HashMap)changeInfo.get((String)recordSetNames.get(key));
				IRecordSet dataRecordSet = data.getRecordSet(key);
				
				// header set
				IRecordSet newRecordSet = new RecordSet((String)recordSetNames.get(key));
				Iterator headerKeys = headerInfo.keySet().iterator();
				while (headerKeys.hasNext()) {
					String headerKey = (String)headerKeys.next();
					IRecordHeader header = new RecordHeader((String)headerInfo.get(headerKey), dataRecordSet.getHeader(headerKey).getType());
					newRecordSet.addHeader(header);
				}
				
				// record set
				for (int i = 0; i < dataRecordSet.getRecordCount(); i++) {
					IRecord dataRecord = dataRecordSet.getRecord(i);
					IRecord newRecord = newRecordSet.newRecord();
					
					headerKeys = headerInfo.keySet().iterator();
					while (headerKeys.hasNext()) {
						String headerKey = (String)headerKeys.next();
						newRecord.put(headerInfo.get(headerKey), dataRecord.get(headerKey));
					}
					
					newRecordSet.addRecord(newRecord);
				}
				
				rtn.putRecordSet(key, newRecordSet);
			}
		}
		
		return null;
	}
}

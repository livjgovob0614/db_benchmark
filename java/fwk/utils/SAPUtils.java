package fwk.utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import nexcore.framework.bat.IBatchContext;
import nexcore.framework.core.data.DataSet;
import nexcore.framework.core.data.IDataSet;
import nexcore.framework.core.data.IRecord;
import nexcore.framework.core.data.IRecordHeader;
import nexcore.framework.core.data.IRecordSet;
import nexcore.framework.core.data.RecordHeader;
import nexcore.framework.core.data.RecordSet;
import nexcore.framework.core.data.xml.DataSetXmlTransformer;
import nexcore.framework.core.exception.SystemRuntimeException;
import nexcore.framework.core.log.LogManager;
import nexcore.framework.core.util.BaseUtils;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;

import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoField;
import com.sap.conn.jco.JCoFieldIterator;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoStructure;
import com.sap.conn.jco.JCoTable;
import com.sap.conn.jco.ext.DestinationDataEventListener;
import com.sap.conn.jco.ext.DestinationDataProvider;
import com.sap.conn.jco.ext.Environment;

import fwk.constants.SlipConstants;
import fwk.constants.enums.sapjco.SAP_SLIP_ELEM;
import fwk.constants.enums.sapjco.SAP_SLIP_HEADER;
import fwk.erfif.sapjco.domain.CommonSlipHeader;
import fwk.utils.DomainUtils;


public class SAPUtils {

	// 접속할 대상 SAP 선언
	public static enum TARGET {   SCQ //TEST(QA)
		                        , SCP //LIVE(PRD)
		                          };
		
	//private static Log logger = LogManager.getExtendedLog("__saplog");
    private static Log logger = LogManager.getFwkLog();
	
    private static String ABAP_AS_POOLED = "ABAP_AS_WITH_POOL";
    
    static class MyDestinationDataProvider implements DestinationDataProvider   
    {
        private DestinationDataEventListener eL;      
        
		private HashMap<String, Properties> destinations;
		
		private static MyDestinationDataProvider provider;
		
		private MyDestinationDataProvider(){
				destinations = new HashMap();
		}
		
		//Static method to retrieve instance
		public static MyDestinationDataProvider getInstance(){
			//System.out.println("Getting MyDestinationDataProvider ... ");
			if(provider == null) {
				provider = new MyDestinationDataProvider();
				if(!Environment.isDestinationDataProviderRegistered())
				{
					Environment.registerDestinationDataProvider(provider);
				}
			}
			return provider;
		}
		
        public Properties getDestinationProperties(String destinationName)   
        {   
        	debug("getDestinationProperties() destinations.keySet():"+ destinations.keySet());
			if( destinations.containsKey( destinationName ) ){
				return destinations.get( destinationName );
			} else {
				throw new RuntimeException("Destination " + destinationName + " is not available");   
			}
        }   
   
        public void setDestinationDataEventListener(DestinationDataEventListener eventListener)   
        {   
            this.eL = eventListener;   
        }   
   
        public boolean supportsEvents()   
        {   
            return true;   
        }   
        
        //implementation that saves the properties in a very secure way
        void changeProperties(String destName, Properties properties)
        {
            synchronized(destinations)
            {
            	destinations.put(destName, properties);
            	SAPUtils.debug("changeProperties destinations:"+destinations);
            }
        }
    }   
	
    /**
     * 환경설정 정보를 이용하여 JCO 연결을 설정한다.
     * 
     * @throws JCoException
     */
    public static void connect(TARGET target) throws JCoException
    {
    	Properties connectProperties = new Properties();
        connectProperties.setProperty(DestinationDataProvider.JCO_LANG, BaseUtils.getConfiguration("JCO_LANG"));
        connectProperties.setProperty(DestinationDataProvider.JCO_POOL_CAPACITY, BaseUtils.getConfiguration("JCO_POOL_CAPACITY"));
        connectProperties.setProperty(DestinationDataProvider.JCO_PEAK_LIMIT, BaseUtils.getConfiguration("JCO_PEAK_LIMIT"));
        connectProperties.setProperty(DestinationDataProvider.JCO_EXPIRATION_TIME, BaseUtils.getConfiguration("JCO_EXPIRATION_TIME"));

        debug("setup parameter according to sap target >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+target);
        connectProperties.setProperty(DestinationDataProvider.JCO_CLIENT, BaseUtils.getConfiguration("JCO_CLIENT_" + target));
        connectProperties.setProperty(DestinationDataProvider.JCO_USER, BaseUtils.getConfiguration("JCO_USER_" + target));
        connectProperties.setProperty(DestinationDataProvider.JCO_PASSWD, BaseUtils.getConfiguration("JCO_PASSWD_" + target));
        connectProperties.setProperty(DestinationDataProvider.JCO_ASHOST, BaseUtils.getConfiguration("JCO_ASHOST_" + target));
        connectProperties.setProperty(DestinationDataProvider.JCO_SYSNR, BaseUtils.getConfiguration("JCO_SYSNR_" + target));

        debug("setup parameter according to sap connectProperties >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+connectProperties);

        MyDestinationDataProvider myProvider = MyDestinationDataProvider.getInstance(); 
        myProvider.changeProperties(ABAP_AS_POOLED + target, connectProperties);
        
        debug("setup parameter myProvider.getDestinationProperties "+ myProvider.getDestinationProperties(ABAP_AS_POOLED + target));
    }
    
    /**
     * SAP RFC 함수를 호출하여 결과를 리턴받는다.
     * 
     * @param target
     * 				호출할 SAP target id 지정한다.
     * @param function
     * 				함수이름을 지정한다.
     * @param inDataSet
     * 				파라미터 정보를 지정한다.
     * @return
     * 			결과를 데이터셋으로 리턴받는다.
     */
    public static IDataSet callFunction(TARGET target, String function, IDataSet inDataSet) {
    	
    	IDataSet outDataSet = new DataSet();
    	
    	try {
    		// SAP 연결정보 얻기
			connect(target);
			
			JCoDestination destination = JCoDestinationManager.getDestination(ABAP_AS_POOLED + target);
			destination.ping();
			
			// SAP Function 설정
	        JCoFunction jcoFunction = destination.getRepository().getFunction(function);

	        // Nexcore dataset 으로부터 파라미터 설정
	        setJcoParameter(jcoFunction, inDataSet);
			
			// function 실행하기
			jcoFunction.execute(destination);
			
			// 리턴된 결과를 Nexcore dataset 으로 받기
			getJcoResult(jcoFunction, outDataSet);

		} catch (Exception ex) {
    		String errMsg = "### Error in SAP callFunction ### [target: "+ target +", func: "+ function +"]";
			logger.error(errMsg + BaseUtils.getExceptionStackTrace(ex));
			throw new SystemRuntimeException(errMsg, ex);
		}
    	
    	
    	return outDataSet;
    }
    
    /**
     * 데이터셋에 전달된 파라미터를 JCO 파라미터로 변환시킨다.
     * + 필드맵 데이터는 JCO 필드 데이터로 변환
     * + 레코드셋 데이터는 JCO 테이블 데이터로 변환
     * 
     * @param jcoFunction
     * @param inDataSet
     */
    private static void setJcoParameter(JCoFunction jcoFunction, IDataSet inDataSet) {
    	
    	try {
	        // Nexcore 필드데이터를 JCO field 로 변환하기
			debug(inDataSet.getFieldCount()+"Nexcore 필드데이터를 JCO field 로 변환하기");
			for (Iterator<String> iter=inDataSet.getFieldKeys(); iter.hasNext();) {
				String key = iter.next();
				Object obj = inDataSet.getObjectField(key);
				JCoParameterList jcoParam = jcoFunction.getImportParameterList();
				
				if(obj instanceof Map) {
					debug(key+"obj is Map");
					Map stMap = (Map)obj;
					JCoStructure jcoStructure = jcoParam.getStructure(key);
					for (Iterator<String> stIter=stMap.entrySet().iterator(); stIter.hasNext();) {
						jcoStructure.setValue(stIter.next(), stMap.get(stIter.next()));
					}
				} else if(obj instanceof DataSet) {
					debug(key+"obj is DataSet");
					IDataSet set = (DataSet) obj;
					JCoStructure jcoStructure = jcoParam.getStructure(key);
					for (Iterator<String> stIter=set.getFieldKeys(); stIter.hasNext();) {
						jcoStructure.setValue(stIter.next(), set.getField(stIter.next()));
					}
				} else {
					debug(key+"obj is Object");
					jcoParam.setValue(key, obj);
				}
			}
    	} catch (Exception ex) {
    		throw new SystemRuntimeException("### 1-1 ### Error in setup parameter : field data " , ex);
    	}
		
    	try {
			// Nexcore 레코드셋을 JCO Table 로 변환하기
			for (Iterator<IRecordSet> iter=inDataSet.getRecordSets(); iter.hasNext();) {
				IRecordSet rs = iter.next();
				JCoTable jcoTable = jcoFunction.getTableParameterList().getTable(rs.getId());
				
				//레코드셋 핸들링...
				for (int i=0; i<rs.getRecordCount(); i++) {
					//레코드 추가
					IRecord record = rs.getRecord(i);
					jcoTable.appendRow();
					
					for (Iterator rsIter=rs.getHeaders(); rsIter.hasNext();) {
						IRecordHeader header = (IRecordHeader) rsIter.next();
						jcoTable.setValue(header.getName(), record.get(header.getName())); //필드값 설정
					}
					
				}
			}
    	} catch (Exception ex) {
    		throw new SystemRuntimeException("### 1-2 ### Error in setup parameter : table data " , ex);
    	}
    }
    
    /**
     * JCO 호출결과를 데이터셋으로 변환하여 얻어온다.
     * 
     * @param jcoFunction
     * @param outDataSet
     */
    private static void getJcoResult(JCoFunction jcoFunction, IDataSet outDataSet) {
    	
    	try {
	    	// JCO Fields 데이터 리턴받기 (일반필드,structure 포함)
			if (jcoFunction.getExportParameterList() != null) {
				for (JCoFieldIterator exIter=jcoFunction.getExportParameterList().getFieldIterator(); exIter.hasNextField();) {
					JCoField exField = exIter.nextField();
					
					if (exField.isTable()) {
						//skip...
						
					} else if (exField.isStructure()) { //(1) structure 데이터타입
	
						Map stMap = new HashMap();
						outDataSet.putField(exField.getName(), stMap); //structure 데이터로 저장
						
						JCoStructure st = exField.getStructure();
						for (JCoFieldIterator stIter=st.getFieldIterator(); stIter.hasNextField();) {
							JCoField stField = stIter.nextField();
							stMap.put(stField.getName(), stField.getString());
						}
						
					} else {  //(2) 일반 fields 데이터타입
						
						outDataSet.putField(exField.getName(), exField.getString()); //fields 데이터로 저장
					}
				}
			}
    	} catch (Exception ex) {
    		throw new SystemRuntimeException("### 2-1 ### Error in getting result : fields data " , ex);
    	}
		
    	try {
			// JCO Table 데이터 리턴받기
			if (jcoFunction.getTableParameterList() != null) {
				for (JCoFieldIterator tbIter=jcoFunction.getTableParameterList().getFieldIterator(); tbIter.hasNextField();) {
					JCoField tbField = tbIter.nextField();
					JCoTable jcoTable = tbField.getTable();
					
					//(1) 레코드셋 생성
					IRecordSet outRes = new RecordSet(tbField.getName());
	
					//(2) 레코드 헤더정보 생성
					for(JCoFieldIterator colIter=jcoTable.getFieldIterator(); colIter.hasNextField();) {
						outRes.addHeader(new RecordHeader(colIter.nextField().getName()));
					}
					
					//(3) 레코드 채우기...
					for(int i=0; i<jcoTable.getNumRows(); i++) {
						IRecord rsRecord= outRes.newRecord(); //레코드생성
						
						for(JCoFieldIterator colIter=jcoTable.getFieldIterator(); colIter.hasNextField();) {
							JCoField colField =colIter.nextField();
							rsRecord.set(colField.getName(), colField.getString()); //레코드값 설정
						}
						
						jcoTable.nextRow();
					}
					
					outDataSet.putRecordSet(outRes); //레코드셋 리턴
				}
			}
    	} catch (Exception ex) {
    		throw new SystemRuntimeException("### 2-2 ### Error in getting result : table data " , ex);
    	}
    }
    
    /**
     * make CallInfo 4 RFC
     * @param params
     * @return
     */
    public static HashMap makeCallInfo()
    {
	    return makeCallInfo(new HashMap());
    }
    
    /**
     * make CallInfo 4 RFC
     * @param params
     * @return
     */
    public static HashMap makeCallInfo(HashMap params)
    {
    	HashMap callInfo = new HashMap();
		callInfo.put(SlipConstants.CALLINFO_PARAMS, params);
		callInfo.put(SAP_SLIP_ELEM.SLIP_HEADER.getPart(), SAP_SLIP_ELEM.SLIP_HEADER.getPart());
		callInfo.put(SAP_SLIP_ELEM.SLIP_ITEM  .getPart(), SAP_SLIP_ELEM.SLIP_ITEM  .getPart() );
		return callInfo;
    }
    
    /**
	 * 해더 맵핑
	 * @param rs
	 * @return
	 */
	public static IRecordSet makeHeader(CommonSlipHeader header)
	{
		IRecordSet itDmsHeader = new RecordSet(SAP_SLIP_ELEM.SLIP_HEADER.getPart());
		
		for(SAP_SLIP_HEADER one: SAP_SLIP_HEADER.values())
		{
			itDmsHeader.addHeader(new RecordHeader(one.getSapCol()));
		}
    	
        IRecord recordContents = itDmsHeader.newRecord();
        
        HashMap map = DomainUtils.invokeDomainToMap(header);
        
        for(SAP_SLIP_HEADER one: SAP_SLIP_HEADER.values())
		{
			recordContents.put(one.getSapCol(), map.get(one.getVar()));
		}
		
        itDmsHeader.addRecord(recordContents);
        
		return itDmsHeader;
	}
	
	

	/**
	 * 해당이름에 해당하는 해더가 있는지 본다.
	 * @param returnRS
	 * @param headerName
	 * @return
	 */
	public static boolean hasHeaderName(IRecordSet returnRS, String headerName)
	{
		boolean returnFlag = false;
		if(   returnRS !=null
		   && returnRS.getHeaderCount() >0
		   && StringUtils.isNotEmpty(headerName)
		   )
		{
			int idx = returnRS.getHeaderIndex(headerName);
			if(idx != -1)
				returnFlag = true;
		}
		
		return returnFlag;
	}
	
	
	/**
	 * 해당이름에 해당하는  갑이 있는지 본다.
	 * @param rs
	 * @param headerName
	 * @return
	 */
	public static boolean hasValue(IRecordSet rs, int idx, String headerName)
	{
		boolean returnFlag = false;
		if(     hasHeaderName(rs, headerName)
			 && rs.get(idx, headerName) != null
		   )
		{
			returnFlag = true;
		}
		return returnFlag;
	}
	
	
	/**
	 * recordSet 에 해당데이터가 있는지 본다.
	 * @param rs
	 * @param headerName
	 * @param value
	 * @return
	 */
	public static boolean findValue(IRecordSet rs , String headerName, Object value)
	{
		boolean returnFlag = false;
		
		if(hasHeaderName(rs,headerName))
		{
			for(int i=0; i<rs.getRecordCount(); i++)
			{
				if(value.equals(rs.get(i, headerName))) 
				{
					returnFlag =true;
					break;
				}
			}
			
		}
		return returnFlag;
	}
	
	
	/**
	 * record를 dataset으로 변경
	 * @param ir
	 * @return
	 */
	public static IDataSet convertRecord2DataSet(IRecord ir)
	{
		IDataSet responseData = new DataSet();
		
		if(ir==null || ir.isEmpty()) return responseData;
		Set s = ir.keySet();
		Iterator i = s.iterator();
		
		String key = "";
		Object value;
		
		while(i.hasNext())
		{
			key = (String) i.next();
			value= ir.get(key);
			
			responseData.putField(key, value);
		}
		return responseData;
		
	}
	
	/**
	 * 해당 키에 해당하는 값이 없으면 지정값을 리턴한다.
	 * @param map
	 * @param key
	 * @return
	 */
	public static String nvl(Map map , String key, String defaultValue)
	{
		if(map!=null && map.containsKey(key))
			return (String) map.get(key);
		else
			return defaultValue;
	}
	
	/**
	 * 단하나의 값으로 맵을 만들어 리턴한다.
	 * @param key
	 * @param value
	 * @return
	 */
	public static Map newMap(String key, String value)
	{
		Map returnMap = new HashMap();
		returnMap.put(key, value);
		return returnMap;
	}
	
	/**
	 * nvl
	 * @param one
	 * @param two
	 * @return
	 */
	public static String nvl(String one, String two)
	{
		return StringUtils.isNotEmpty(one)?one:two;
	}
	
    /**
     * 입력파라미터 준비
     * @param context
     * @return
     */
    public static IDataSet prepareXMLInputParam(IBatchContext context)
    {
    	return prepareXMLInputParam(context,SlipConstants.BOD_DS);
    }
    
	
    /**
     * 입력파라미터 준비
     * @param context
     * @return
     */
    public static IDataSet prepareXMLInputParam(IBatchContext context, String parameterName)
    {
    	
    	debug("prepareXMLInputParam() context :"+context);
    	String   reqDataSetStr = context.getInParameter(parameterName);
    	IDataSet requestData    = DataSetXmlTransformer.xmlToDataSet(new java.io.StringReader(reqDataSetStr));
    	
    	debug("prepareXMLInputParam() paramMap :"+requestData);
     	return requestData;
    }
	
    /**
     * debug
     * @param o
     */
    public static void debug(Object o)
    {
//    	logger.debug(o);
    	logger.info(o);
    }
    
    /**
     * debug
     * @param o
     */
    public static void info(Object o)
    {
    	logger.info(o);
    }


}


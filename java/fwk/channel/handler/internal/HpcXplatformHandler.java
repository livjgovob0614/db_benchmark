package fwk.channel.handler.internal;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nexcore.framework.core.ServiceConstants;
import nexcore.framework.core.component.IBizComponentMetaDataRegistry;
import nexcore.framework.core.component.IIoMetaData;
import nexcore.framework.core.component.IMethodMetaData;
import nexcore.framework.core.component.IRecordSetMetaData;
import nexcore.framework.core.component.internal.DataSetMetaData;
import nexcore.framework.core.component.internal.FieldMetaData;
import nexcore.framework.core.component.internal.RecordSetMetaData;
import nexcore.framework.core.data.Channel;
import nexcore.framework.core.data.DataSet;
import nexcore.framework.core.data.IChannel;
import nexcore.framework.core.data.IDataSet;
import nexcore.framework.core.data.IOnlineContext;
import nexcore.framework.core.data.IRecord;
import nexcore.framework.core.data.IRecordHeader;
import nexcore.framework.core.data.IRecordSet;
import nexcore.framework.core.data.IRuntimeContext;
import nexcore.framework.core.data.ITerminal;
import nexcore.framework.core.data.ITransaction;
import nexcore.framework.core.data.OnlineContext;
import nexcore.framework.core.data.RecordHeader;
import nexcore.framework.core.data.RecordSet;
import nexcore.framework.core.data.RuntimeContext;
import nexcore.framework.core.data.Terminal;
import nexcore.framework.core.data.Transaction;
import nexcore.framework.core.data.user.IUserInfo;
import nexcore.framework.core.exception.SystemRuntimeException;
import nexcore.framework.core.ioc.ComponentRegistry;
import nexcore.framework.core.log.LogManager;
import nexcore.framework.core.service.front.DefaultFrontDataLog;
import nexcore.framework.core.transform.FlatUtil;
import nexcore.framework.core.util.NexCoreServiceUtil;
import nexcore.framework.online.biz.auth.IWebUserManager;
import nexcore.framework.online.channel.core.IRequestContext;
import nexcore.framework.online.channel.core.internal.DefaultRequestContext;
import nexcore.framework.online.channel.handler.internal.NewStandardXPlatformHandler;
import nexcore.framework.online.channel.util.WebUtils;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;

import com.tobesoft.xplatform.data.ColumnHeader;
import com.tobesoft.xplatform.data.DataSetList;
import com.tobesoft.xplatform.data.DataTypes;
import com.tobesoft.xplatform.data.PlatformData;
import com.tobesoft.xplatform.data.VariableList;
import com.tobesoft.xplatform.tx.HttpPlatformRequest;
import com.tobesoft.xplatform.tx.PlatformException;
import com.tobesoft.xplatform.tx.PlatformType;

import fwk.channel.XplatformHeaderHelper;
import fwk.channel.XplatformHeaderSpec;
import fwk.constants.DmsConstants;
import fwk.utils.HpcUtils;
import fwk.utils.PagenateUtils;

/**
 * <ul>
 * <li>업무 그룹명 : dev-hpc-customizing</li>
 * <li>서브 업무명 : fwk.channel.handler.internal</li>
 * <li>설  명 : XPlatform에서 넘어온 Data를 NEXCORE의 DataSet 구조로 변경하기 위한 클래스임. </li>
 * <li>작성일 : 2014. 9. 23.</li>
 * <li>작성자 : Administrator</li>
 * </ul>
 */
public class HpcXplatformHandler extends NewStandardXPlatformHandler {

	
	private final String  HPC_HEADER = "__HEADER__";

	private DefaultFrontDataLog frontDataLog;
	private IBizComponentMetaDataRegistry   cmdRegistry;
	
	public void setFrontDataLog(DefaultFrontDataLog frontDataLog) {
		this.frontDataLog = frontDataLog;
	}
	
    public IBizComponentMetaDataRegistry getCmdRegistry() {
        if(cmdRegistry == null) {
            cmdRegistry =  (IBizComponentMetaDataRegistry)ComponentRegistry.lookup("nc.core.IBizComponentMetaDataRegistry");
        }
        return cmdRegistry;
    }
	
	/* *
	 * 
	 * (non-Javadoc)
	 * @see nexcore.framework.online.channel.handler.internal.StandardXPlatformHandler#getRequestContext(java.lang.Object, java.lang.Object)
	 */
	@Override
	public IRequestContext getRequestContext(Object readProtocol, Object writeProtocol) {
		Log logger = LogManager.getFwkLog();
		logger.debug("data receive first");
		if(!(readProtocol instanceof HttpServletRequest)){
			logger.error("Read protocol interface type is't HttpServletRequest.");
			throw new RuntimeException("Read protocol interface type is't HttpServletRequest");
		}

		if(!(writeProtocol instanceof HttpServletResponse)){
			logger.error("Write protocol interface type is't HttpServletResponse");
			throw new RuntimeException("Write Protocol interface isn't HttpServletResponse.");
		}

		HttpServletRequest httpReq = (HttpServletRequest)readProtocol;
		HttpServletResponse httpResp = (HttpServletResponse)writeProtocol;

		/*
		 * HttpPlatformRequest xpReq = new HttpPlatformRequest(httpReq, 
		 *		PlatformType.CONTENT_TYPE_XML, PlatformType.DEFAULT_CHAR_SET);
		 */
		HttpPlatformRequest xpReq = new HttpPlatformRequest(httpReq);
		
		try{
			xpReq.receiveData();
		}catch(PlatformException ex){
			throw new RuntimeException(ex);
		}

		PlatformData xpData = xpReq.getData(); // XPlatform request data
		VariableList xpVars = xpData.getVariableList();
		String txId = xpVars.getString(this.getTrIdVarName());//IO를 추출하기 위해 거래ID를 취득함.
		String globId = xpVars.getString(XplatformHeaderSpec.GLOB_ID.name());//IO를 추출하기 위해 거래ID를 취득함.
		IMethodMetaData mmd = getCmdRegistry().getMethodMetaData(txId);
		if (mmd == null) {
            // 트랜잭션 아이디 {0} 에 해당하는 메소드 메타데이터가 존재하지 않습니다.
            throw new SystemRuntimeException("SKFE4011", new String[]{txId});
        }
	  
		IIoMetaData ioMetaData = mmd.getInputIoMetaData();//PM의 Input IO메타정보취득
		IDataSet req = new DataSet(); // framework data-set object for request

		// list of XPlatform dataset in the request
		DataSetList xpDatasets = xpData.getDataSetList();
		// XPlatform dataset object.
		// Note that both NEXCORE and XPlatform use nearly same word
		// (DataSet and Dataset), but their concept is somewhat different.
		com.tobesoft.xplatform.data.DataSet xpDataset = null;
		String xpDatasetName = null;
		IRecordSet recordSet = null;
		IRecord record = null;
		ColumnHeader xpColHeader = null;
		IOnlineContext onlineCtx = null;
		String cmdId ="";
		try {
		    for(int i = 0, n = xpDatasets.size(); i < n; i++){
	            xpDataset = xpDatasets.get(i);
	            xpDatasetName = xpDataset.getName();
	            if(StringUtils.equals(this.getLinearDatasetName(), xpDatasetName)){
	                transformXpfToDataSet(ioMetaData, xpDataset, req);
	            } else{ //in case of normal XPlatform dataset which can have multiple rows
	                transformXpfToRecordSet(ioMetaData, xpDataset, req, recordSet);
	            }
	        }
		    /**
	         * 심상준과장의 요청에 따라 grid type이 아닌 Argument type의 넘어오도록 하고 이를 자동으로 DataSet에 넣어주는 방식으로 변경
	         * 2014.08.20 by PSI
	         */
	        req.putField(PagenateUtils.PAGE_NO, xpVars.getString(PagenateUtils.PAGE_NO));
	        req.putField(PagenateUtils.RC_COUNT_PER_PAGE, xpVars.getString(PagenateUtils.RC_COUNT_PER_PAGE));
	                
	        //gets command 
	        cmdId = WebUtils.getCommandId(httpReq);
	        onlineCtx = makeOnlineContext(httpReq, xpVars);
	        
	        // BINARY, XML 여부에 따라서 Response 를 생성한다.
	        if(PlatformType.CONTENT_TYPE_BINARY.equals(xpReq.getContentType())) { // BINARY Response 생성
	            onlineCtx.setAttribute("CONTENT_TYPE", PlatformType.CONTENT_TYPE_BINARY);
	        } else if(PlatformType.CONTENT_TYPE_XML.equals(xpReq.getContentType())) {// XML Response 생성
	            onlineCtx.setAttribute("CONTENT_TYPE", PlatformType.CONTENT_TYPE_XML);
	        }
	        
	        // COMPRESS Protocol Type을 추가한다.
	        for(int i = 0; i < xpReq.getProtocolTypeCount(); i++) {
	            if(PlatformType.PROTOCOL_TYPE_ZLIB.equals(xpReq.getProtocolType(i))) {
	                onlineCtx.setAttribute("PROTOCOL_TYPE", PlatformType.PROTOCOL_TYPE_ZLIB);
	            }
	        }
		} catch (Exception e) {
		    if (logger.isErrorEnabled()) {
                logger.error("[" + globId  + "] ["+ txId + "] Exception on parsing request data.", e);
            }
		    throw new RuntimeException("SKFE5009", e);
		}
		return new DefaultRequestContext(cmdId, req, onlineCtx, httpReq, httpResp);
	}
	
	/**
	 * Xplatform으로 받은 데이터를 토대로 OnlineContext 생성. Xplatform종속적이라 Handler에 생성함. 
	 * @param httpReq
	 * @param xpVars
	 * @return IOnlineContext
	 */
	private IOnlineContext makeOnlineContext(HttpServletRequest httpReq, VariableList xpVars ) {
		//builds user info object.
		//@fixme WebUserManager should be dependency injected. The following lookup is not DI.
		IWebUserManager userMgr = (IWebUserManager)ComponentRegistry.lookup(ServiceConstants.BIZ_USER);
		IUserInfo user = userMgr.getUserInfo(httpReq);
		//builds transaction object
//		String reqUuid = user.getIp() + "-" + UUID.randomUUID().toString(); // UUID for tranaction occurence
		String globId = xpVars.getString(XplatformHeaderSpec.GLOB_ID.xpfName()); // UUID for tranaction occurence
		String trId = xpVars.getString(this.getTrIdVarName()); // transaction ID for the service (method of biz-unit)
		ITransaction tr = new Transaction(globId, trId, false, new Date());

		//builds channel object
		String channelXd = httpReq.getServerName() + ":" + httpReq.getServerPort();
		String systemXd =xpVars.getString(XplatformHeaderSpec.IPAD.xpfName());//L4를 두 번 거쳐올 경우, HttpServletRequest에 담겨 있는 IP주소가 첫번째 L4 IP로 변경될 수 있음. 
		if(StringUtils.isEmpty(systemXd)) {
			systemXd = httpReq.getRemoteAddr();
		}
		IChannel channel = new Channel(channelXd, systemXd, IChannel.PROTOCOL_HTTP, IChannel.MSG_HTTP);

		//builds terminal object
		String branchCode = "UNDEFINED";
		//@fixme ITerminal should be extended to have constant for XPlatform.
		//temporarily use constant for MiPlatform.
		int agentType = ITerminal.AGENT_MIPLATFORM;
		ITerminal term = new Terminal(systemXd, branchCode, agentType);

		//builds runtime context object
		String componentId = NexCoreServiceUtil.getComponentId(trId);
		String methodId = NexCoreServiceUtil.getMethodId(trId);
		IRuntimeContext runtimeCtx = new RuntimeContext(componentId, methodId);

		//builds online context
		IOnlineContext onlineCtx = new OnlineContext(tr, user, runtimeCtx, channel, term);
		
		//header parsing
		Map<String, String> header = XplatformHeaderHelper.toXpfHeaderMap(onlineCtx, xpVars);
		onlineCtx.setAttributesAll(header);
		return onlineCtx;
	}
	
	/**
	 * XPlatform파라미터를 DataSet으로 변화하는 메소드
	 *UI에서 넘어오는 IO기준이 아닌 PM에 정의된 IO만을 취득할 수 있도록 해달라는 업무팀의 요청에 따라 
	 *  14.09.22에 해당 내용을 변경함. By PSI
     * @param ioMetaData
     * @param xpDataset
     * @param req void
	 * @return Object
	 */
	private  void transformXpfToDataSet(IIoMetaData ioMetaData, com.tobesoft.xplatform.data.DataSet xpDataset, IDataSet req) {
	    List fioList = ioMetaData.getFieldMetaDataList();//필드타입의 IO List를 취득함.
	    if(fioList != null && fioList.size()==0 ) {
	        fioList = ioMetaData.getFlMetaDataList();//일반 IO가 없을 때에는 전문타입IO가 있는지 다시 확인한다. 
	    }
	    Map fields = new HashMap();
	    Iterator iter = null;
	    FieldMetaData fmd = null;
	    Object obj = null;
	    ColumnHeader xpColHeader = null;
        if(fioList!=null && fioList.size() > 0) {
            iter = fioList.iterator();
            while(iter.hasNext()) {
                obj= iter.next();//필드타입의 IO list만 취득하기 때문에 FieldMetaData 외의 객체는 없다.
                if(obj instanceof  FieldMetaData) {
                    fmd = (FieldMetaData)obj;
                    xpColHeader = xpDataset.getColumn(fmd.getId());
                    if(xpColHeader == null) continue;
                    if(xpColHeader.getDataType() != DataTypes.BLOB){
                        if(null == fmd.getType()) {//AD에서 datatype설정이 가능하기 전까지는 String으로 받도록 허용한다.
                            fields.put(fmd.getId(), xpDataset.getString(0, xpColHeader.getName()));//XSS체크
                        } else {
                            if (FieldMetaData.TYPE_BIGDECIMAL.equalsIgnoreCase(fmd.getType())) {
                                fields.put(fmd.getId(), new BigDecimal(xpDataset.getString(0, xpColHeader.getName()).trim()));//XSS체크
                            }  else if (FieldMetaData.TYPE_STRING.equalsIgnoreCase(fmd.getType())) {
                                fields.put(fmd.getId(), xpDataset.getString(0, xpColHeader.getName()));//XSS체크
                            } else if (FieldMetaData.TYPE_SHORT.equals(fmd.getType())) {
                                fields.put(fmd.getId(), new Short(xpDataset.getString(0, xpColHeader.getName())));
                            } else if (FieldMetaData.TYPE_INT.equals(fmd.getType())) {
                                fields.put(fmd.getId(), new Integer(xpDataset.getString(0, xpColHeader.getName())));
                            } else if (FieldMetaData.TYPE_LONG.equals(fmd.getType())) {
                                fields.put(fmd.getId(), new Long(xpDataset.getString(0, xpColHeader.getName())));
                            } else if (FieldMetaData.TYPE_DOUBLE.equals(fmd.getType())) {
                                fields.put(fmd.getId(), new Double(xpDataset.getString(0, xpColHeader.getName())));
                            } else if (FieldMetaData.TYPE_FLOAT.equals(fmd.getType())) {
                                fields.put(fmd.getId(), new Float(xpDataset.getString(0, xpColHeader.getName())));
                            } else {
                                throw new RuntimeException("field's type incorrect. " + "[id="+ fmd.getId() + ", type=" + fmd.getType() + "]"); 
                            }
                        }
                    } else {
                        fields.put(fmd.getId(), xpDataset.getBlob(0, xpColHeader.getName()));
                    }
                }
            }
        }
        req.putFieldMap(fields);
	}
	
	/**
     * XPlatform 그리드타입의 파라미터를 RecordSet으로 변화하는 메소드
     *UI에서 넘어오는 IO기준이 아닌 PM에 정의된 IO만을 취득할 수 있도록 해달라는 업무팀의 요청에 따라   
     * 14.09.22에 해당 내용을 변경함. By PSI
	 * @param ioMetaData
	 * @param xpDataset
	 * @param req
	 * @param recordSet void
	 */
	private void transformXpfToRecordSet(IIoMetaData ioMetaData,  com.tobesoft.xplatform.data.DataSet xpDataset, IDataSet req, IRecordSet recordSet) {
	    Log logger = LogManager.getFwkLog();
        logger.debug("data receive second");
	    DataSetMetaData dsMeta = null;
	    if(ioMetaData instanceof DataSetMetaData) {
	        dsMeta = (DataSetMetaData)ioMetaData;
	    }
//	    List alFioList = ioMetaData.getFlMetaDataList();//필드와 레코드셋을 구분하지 않는 메타데이터 목록을 반환.
	    List alFioList = dsMeta.getRecordSetMetaDataList();//필드와 레코드셋을 구분하지 않는 메타데이터 목록을 반환.
	    Iterator iter= null;
	    Object fmdObj = null;
//	    ColumnHeader xpColHeader = null;
	    String xpDatasetName = xpDataset.getName();
	    if(alFioList!=null && alFioList.size() >0) {
            iter = alFioList.iterator();
            IRecordSetMetaData rsmd  = null;
            List rsFieldList = null;
            IRecord record = null;
            IRecordHeader header = null;
            String headerName = "";
            while(iter.hasNext()) {//IRecordSet 헤더구성
                fmdObj = iter.next();
                if (fmdObj instanceof RecordSetMetaData) {//필드처리는 transformXpfToDataSet()에서 했기 때문에 RecordSet처리만 한다.
                    rsmd = (IRecordSetMetaData) fmdObj;
                    rsFieldList =   rsmd.getFieldMetaDataList();
                    if(xpDatasetName.equals(rsmd.getId())) {//Xplatform에서 넘어온 Grid의 명과 PM의 IO에 정의된 RecordSet명이 같을 경우만 값을 넣도록 수정
                        recordSet = new RecordSet(rsmd.getId());//Typical XPlatform dataset which can have multiple rows corresponds to NEXCORE record-set.
                        recordSet.addHeaders(FlatUtil.makeRecordHeader(rsFieldList));
                        
                        // rowStatus Header 추가
                        recordSet.addHeader(new RecordHeader(getRecordStatusName() , java.sql.Types.VARCHAR));
                        Object obj = null;
                        FieldMetaData fmd = null;
                        // 삭제 데이터 레코드 설정
                        for (int deleteIndex = 0; deleteIndex < xpDataset.getRemovedRowCount(); deleteIndex++) {
                            setRecordValue(recordSet, xpDataset, rsFieldList, deleteIndex, true);
                        }
                        
                        for(int rowIndex = 0; rowIndex < xpDataset.getRowCount(); rowIndex++){
                            setRecordValue(recordSet, xpDataset, rsFieldList, rowIndex, false);
                        }
                        req.putRecordSet(recordSet);
                    } 
                }
            }
        }
	}
	
	/**
	 * RecordSet의 Record정의시 반복되는 구문을 메소드로 분리시킴
	 *  
	 * @param recordSet
	 * @param xpDataset
	 * @param rsFieldList
	 * @param rowIndex
	 * @param isDelRow void
	 */
	private void setRecordValue(IRecordSet recordSet, com.tobesoft.xplatform.data.DataSet xpDataset, List rsFieldList, int rowIndex, boolean isDelRow) {
	    Log logger = LogManager.getFwkLog();
        logger.debug("data receive third");
	    IRecord record = recordSet.newRecord();
	    String headerName = "";
	    Object obj = null;
	    FieldMetaData fmd = null;
	    ColumnHeader xpColHeader = null;
        for (int headerInx = 0; headerInx < rsFieldList.size(); headerInx++) {
             obj = rsFieldList.get(headerInx);
            if(obj instanceof FieldMetaData){//Xplatform에서는 1depth의 grid 만 지원하기 때문에 RecordSet안에 또 RecordSet은 없다.
                fmd =(FieldMetaData)obj;
                headerName = fmd.getId();
                xpColHeader = xpDataset.getColumn(headerName);
                if (xpColHeader != null) {
                    if(xpColHeader.getDataType() != DataTypes.BLOB){
                        if(null == fmd.getType()) {//AD설계기에서 DataType설정이 가능토록 변경되기 전까지는 String으로 받도록 한다.
                            record.set(headerName, isDelRow?xpDataset.getRemovedStringData(rowIndex, xpColHeader.getName()):xpDataset.getString(rowIndex, xpColHeader.getName()));
                        } else {
                            if (FieldMetaData.TYPE_BIGDECIMAL.equalsIgnoreCase(fmd.getType())) {
                                record.set(headerName,  new BigDecimal(isDelRow?xpDataset.getRemovedStringData(rowIndex, xpColHeader.getName()):xpDataset.getString(rowIndex, xpColHeader.getName())));
                            }  else if (FieldMetaData.TYPE_STRING.equalsIgnoreCase(fmd.getType())) {
                                record.set(headerName, isDelRow?xpDataset.getRemovedStringData(rowIndex, xpColHeader.getName()):xpDataset.getString(rowIndex, xpColHeader.getName()));
                            } else if (FieldMetaData.TYPE_SHORT.equals(fmd.getType())) {
                                record.set(headerName, new Short(isDelRow?xpDataset.getRemovedStringData(rowIndex, xpColHeader.getName()):xpDataset.getString(rowIndex, xpColHeader.getName())));
                            } else if (FieldMetaData.TYPE_INT.equals(fmd.getType())) {
                                record.set(headerName, new Integer(isDelRow?xpDataset.getRemovedStringData(rowIndex, xpColHeader.getName()):xpDataset.getString(rowIndex, xpColHeader.getName())));
                            } else if (FieldMetaData.TYPE_LONG.equals(fmd.getType())) {
                                record.set(headerName, new Long(isDelRow?xpDataset.getRemovedStringData(rowIndex, xpColHeader.getName()):xpDataset.getString(rowIndex, xpColHeader.getName())));
                            } else if (FieldMetaData.TYPE_DOUBLE.equals(fmd.getType())) {
                                record.set(headerName, new Double(isDelRow?xpDataset.getRemovedStringData(rowIndex, xpColHeader.getName()):xpDataset.getString(rowIndex, xpColHeader.getName())));
                            } else if (FieldMetaData.TYPE_FLOAT.equals(fmd.getType())) {
                                record.set(headerName, new Float(isDelRow?xpDataset.getRemovedStringData(rowIndex, xpColHeader.getName()):xpDataset.getString(rowIndex, xpColHeader.getName())));
                            } else {
                                throw new RuntimeException("field's type incorrect. " + "[id="+ fmd.getId() + ", type=" + fmd.getType() + "]"); 
                            }
                        }
                    } else {
                        record.set(headerName, xpDataset.getBlob(rowIndex, headerName));
                    }
                }
            }
        }
        record.set(getRecordStatusName(), isDelRow?DmsConstants.STATUS_DELETED:xpDataset.getRowTypeName(rowIndex));
	}
}

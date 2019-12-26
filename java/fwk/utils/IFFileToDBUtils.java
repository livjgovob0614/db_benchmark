package fwk.utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.util.Map;

import nexcore.framework.bat.base.RecordHandlerExecutor;
import nexcore.framework.bat.util.SAMFileTool;
import nexcore.framework.core.data.DataSet;
import nexcore.framework.core.data.IDataSet;
import nexcore.framework.core.data.IRecord;
import nexcore.framework.core.data.IRecordSet;
import nexcore.framework.core.exception.BizRuntimeException;
import nexcore.framework.core.util.BaseUtils;
import nexcore.framework.core.util.DateUtils;
import nexcore.framework.core.util.StringUtils;

import org.apache.commons.logging.Log;

public class IFFileToDBUtils {
    
    private int totProcessCnt = 0;
    
    /**
     * 인터페이스파일처리
     */
    public IDataSet listFile(SAMFileTool body, Map<String, String> paramMap, Log log, RecordHandlerExecutor rhe) throws BizRuntimeException {
        
        IDataSet responseData = new DataSet();
        totProcessCnt = 0;
        
        String path=paramMap.get("FILE_LOC");
        File dirFile=new File(path);
        if (!dirFile.exists()) {
            try{
                dirFile.mkdir();
            } 
            catch(SecurityException se){
                throw new BizRuntimeException("DMS00009", se); //오류가 발생했습니다.
            }
        }
        File []fileList=dirFile.listFiles();
        int failCnt = 0;
        int seq = 0;
        String sfYN = "";
        StringBuffer sb = new StringBuffer();
        for(File tempFile : fileList) {
          if(tempFile.isFile()) {
            String tempFileName=tempFile.getName();
            if(tempFileName.indexOf(paramMap.get("TASK_ID")+".SKCC") > -1 ) {
                sfYN = "S ";
                seq++;
                try{
                    responseData.putRecordSet(String.valueOf(seq), readFile(body, paramMap, tempFile, log, rhe));
                } 
                catch(BizRuntimeException be){
                    log.error(be.getMessage());
                    failCnt++;
                    sfYN = "F ";
                }
                if(!"".equals(sb.toString())) {
                    sb.append(",  ");
                }
                sb.append(tempFileName);sb.append(" : ");sb.append(sfYN);
            }
          }
        }
        String procFileNames = sb.toString();
        if (procFileNames != null && procFileNames.length() > 200) procFileNames = procFileNames.substring(0, 200); 
        responseData.putField("TOT_PROC_CNT", totProcessCnt);
        responseData.putField("PROC_FILE_NM", procFileNames);
        if (failCnt > 0) {
            responseData.putField("BAT_TASK_PROC_ST_CD", "F");
        } else {
            responseData.putField("BAT_TASK_PROC_ST_CD", "S");
        }
        
        if (seq == 0) {
            responseData.putField("BAT_TASK_PROC_ST_CD", "E");  //파일없음
        }
        
        return responseData;
    }
    
    /**
     * 파일을 IRecordSet으로 변경
     */
    public IRecordSet readFile(SAMFileTool body, Map<String, String> paramMap, File tempFile, Log log, RecordHandlerExecutor rhe) throws BizRuntimeException {
        
        // SAM 파일 헤더부 레이아웃 정의
        SAMFileTool header = new SAMFileTool();
        header.addColumnInfo("REC_CL_CD",     1, SAMFileTool.TYPE_STRING);
        header.addColumnInfo("FILE_NM",       30, SAMFileTool.TYPE_STRING);
        header.addColumnInfo("PROC_DT",       8, SAMFileTool.TYPE_STRING);
        header.setEncoding(BaseUtils.getConfiguration("interface.file.encoding"));
        
        // SAM 파일 꼬리부 레이아웃 정의
        SAMFileTool tail = new SAMFileTool();
        tail.addColumnInfo("REC_CL_CD",     1, SAMFileTool.TYPE_STRING);
        tail.addColumnInfo("FILE_NM",       30, SAMFileTool.TYPE_STRING);
        tail.addColumnInfo("TOT_REC_CNT",   10, SAMFileTool.TYPE_STRING);
        tail.setEncoding(BaseUtils.getConfiguration("interface.file.encoding"));
        
        IRecordSet rs1 = header.makeRecordSetFromColumnInfoList("HEADER");
        IRecordSet rs2 = body.makeRecordSetFromColumnInfoList("BODY");
        IRecordSet rs3 = tail.makeRecordSetFromColumnInfoList("TRAILER");
        
        String path=paramMap.get("FILE_LOC");
        int ifSeq = Integer.parseInt(StringUtils.nvl(paramMap.get("IF_SEQ"),"1"));
        
        int hLength = Integer.parseInt(BaseUtils.getConfiguration("interface.file.header.length"));
        int bLength = Integer.parseInt(StringUtils.nvl(paramMap.get("REC_LENG"),"0"));
        int tLength = Integer.parseInt(BaseUtils.getConfiguration("interface.file.tail.length"));
        
        int processCnt = 0;
        int totCnt = 0;
        BufferedInputStream in = null;
        String fileRename = "";
        String procDt = "";
        try {
            in = new BufferedInputStream(new FileInputStream(tempFile));
            byte[] readLineBuffer = new byte[5000];
            rhe.start();
            while(true) {
                // 샘플 파일에서는 매 레코드는 EOL 문자로 구분됨.
                int readLength = SAMFileTool.readToEol(in, readLineBuffer);
                if (readLength == -1) break; // 다읽었음.
                if (readLength < 1) continue; // 의미 없는 공백 라인. skip.
                
                if (readLineBuffer[0] == 'H') { 
                    // 레코드의 첫번째 바이트가 H이면 헤더부 
                    if (hLength != 0 && readLength != hLength) {
                        throw new BizRuntimeException("DMS00107");  // 전문의 Field 길이와 실제 파일의 각각의 칼럼 길이가 맞지 않습니다.
                    }
                    IRecord rec = header.readRecordFromBytes(rs1, readLineBuffer, 0, readLength);
                    log.debug("RECORD HEADER:["+rec.get("FILE_NM")+"]");
                    fileRename = rec.get("FILE_NM");
                    procDt = rec.get("PROC_DT");
                }else if (readLineBuffer[0]  == 'B') { 
                    if (bLength != 0 && readLength != bLength) {
                        throw new BizRuntimeException("DMS00089");  // 전문의 Field 길이와 실제 파일의 각각의 칼럼 길이가 맞지 않습니다.
                    }
                    // 레코드의 첫번째 바이트가D이면 본문부 
                    IRecord rec = body.readRecordFromBytes(rs2, readLineBuffer, 0, readLength); 
                    log.debug("RECORD BODY:["+processCnt+"]");
                    rec.set("IF_PROC_DT", procDt);
                    rec.set("IF_FILE_NM", fileRename);
                    rec.set("IF_SEQ", ifSeq);
                    rhe.execute(rec);
                    //rs2.addRecord(rec);
                    processCnt++;
                    ifSeq++;
                }else if (readLineBuffer[0]  == 'T') { 
                    // 레코드의 첫번째 바이트가 T이면 꼬리부 
                    if (tLength != 0 && readLength != tLength) {
                        throw new BizRuntimeException("DMS00108");  // 전문의 Field 길이와 실제 파일의 각각의 칼럼 길이가 맞지 않습니다.
                    }
                    IRecord rec = tail.readRecordFromBytes(rs3, readLineBuffer, 0, readLength); 
                    
                    String tot = rec.get("TOT_REC_CNT").trim();
                    totCnt = Integer.parseInt(tot);
                    log.debug("TOT_REC_CNT:["+totCnt+"]");
                }
            }
            rhe.end();
            
            if(processCnt != totCnt) throw new BizRuntimeException("DMS00083"); //총건수와 처리건수가 다릅니다.
            totProcessCnt += processCnt;
            
        } catch(Exception e) {
            log.error("Exception:["+e.toString()+"]");
            if(e.getClass().equals(java.io.EOFException.class)) {
                throw new BizRuntimeException("DMS00089", e);  // 전문의 Field 길이와 실제 파일의 각각의 칼럼 길이가 맞지 않습니다.
            } else {
                throw new BizRuntimeException("DMS00009", e); //오류가 발생했습니다.
            }
        } finally {
            try {
                if (in != null) in.close();
                String backPath=path+"PROC/";
                File backDir=new File(backPath);
                if (!backDir.exists()) {
                    try{
                        backDir.mkdir();
                    } 
                    catch(SecurityException se){
                        throw new BizRuntimeException("DMS00009", se); //오류가 발생했습니다.
                    }
                }
                String tempFileNm = tempFile.getName().substring(0, tempFile.getName().lastIndexOf(".")) 
                                    + "_" + DateUtils.getDefaultCurrentDateTime().substring(8);
                if ("".equals(fileRename)) fileRename = tempFileNm+".dat";
                File fileToMove = new File(path+"PROC/"+fileRename);
                if (fileToMove.exists()) fileToMove = new File(path+"PROC/"+tempFileNm+".dat");
                tempFile.renameTo(fileToMove);
            } catch(Exception e) {
                log.error("Exception:["+e.toString()+"]");
                throw new BizRuntimeException("DMS00009", e); //오류가 발생했습니다.
            }
        }
        
        return rs2;
    }
    
    /**
     * 인터페이스파일처리
     */
    public IDataSet listFileData(SAMFileTool body, Map<String, String> paramMap, Log log, RecordHandlerExecutor rhe) throws BizRuntimeException {
        
        IDataSet responseData = new DataSet();
        totProcessCnt = 0;
        
        String path=paramMap.get("FILE_LOC");
        File dirFile=new File(path);
        if (!dirFile.exists()) {
            try{
                dirFile.mkdir();
            } 
            catch(SecurityException se){
                throw new BizRuntimeException("DMS00009", se); //오류가 발생했습니다.
            }
        }
        File []fileList=dirFile.listFiles();
        int failCnt = 0;
        int seq = 0;
        String sfYN = "";
        StringBuffer sb = new StringBuffer();
        for(File tempFile : fileList) {
          if(tempFile.isFile()) {
            String tempFileName=tempFile.getName();
            if(tempFileName.indexOf(paramMap.get("TASK_ID")+".SKCC") > -1 ) {
                sfYN = "S ";
                seq++;
                try{
                    responseData.putRecordSet(String.valueOf(seq), readFileData(body, paramMap, tempFile, log, rhe));
                } 
                catch(BizRuntimeException be){
                    log.error(be.getMessage());
                    failCnt++;
                    sfYN = "F ";
                }
                if(!"".equals(sb.toString())) {
                    sb.append(",  ");
                }
                sb.append(tempFileName);sb.append(" : ");sb.append(sfYN);
            }
          }
        }
        String procFileNames = sb.toString();
        if (procFileNames != null && procFileNames.length() > 200) procFileNames = procFileNames.substring(0, 200); 
        responseData.putField("TOT_PROC_CNT", totProcessCnt);
        responseData.putField("PROC_FILE_NM", procFileNames);
        if (failCnt > 0) {
            responseData.putField("BAT_TASK_PROC_ST_CD", "F");
        } else {
            responseData.putField("BAT_TASK_PROC_ST_CD", "S");
        }
        
        if (seq == 0) {
            responseData.putField("BAT_TASK_PROC_ST_CD", "E");  //파일없음
        }
        
        return responseData;
    }
    
    /**
     * 파일을 IRecordSet으로 변경
     */
    public IRecordSet readFileData(SAMFileTool body, Map<String, String> paramMap, File tempFile, Log log, RecordHandlerExecutor rhe) throws BizRuntimeException {
        
        IRecordSet rs2 = body.makeRecordSetFromColumnInfoList("BODY");
        
        String path=paramMap.get("FILE_LOC");
        int ifSeq = Integer.parseInt(StringUtils.nvl(paramMap.get("IF_SEQ"),"1"));
        
        int bLength = Integer.parseInt(StringUtils.nvl(paramMap.get("REC_LENG"),"0"));
        
        int processCnt = 0;
        BufferedInputStream in = null;
        String fileRename = tempFile.getName();
        String procDt = tempFile.getName().substring(10, tempFile.getName().lastIndexOf("."));
        try {
            in = new BufferedInputStream(new FileInputStream(tempFile));
            byte[] readLineBuffer = new byte[5000];
            rhe.start();
            String readLine = "";
            while(true) {
                // 샘플 파일에서는 매 레코드는 EOL 문자로 구분됨.
                int readLength = SAMFileTool.readToEol(in, readLineBuffer);
                if (readLength == -1) break; // 다읽었음.
                if (readLength < 1) continue; // 의미 없는 공백 라인. skip.
                readLine = new String(readLineBuffer);
                if (readLine == null || readLine.trim().length() == 0) continue;
                
                if (bLength != 0 && readLength != bLength) {
                    throw new BizRuntimeException("DMS00089");  // 전문의 Field 길이와 실제 파일의 각각의 칼럼 길이가 맞지 않습니다.
                }
                // 레코드의 첫번째 바이트가D이면 본문부 
                IRecord rec = body.readRecordFromBytes(rs2, readLineBuffer, 0, readLength); 
                log.debug("RECORD BODY:["+processCnt+"]");
                rec.set("IF_PROC_DT", procDt);
                rec.set("IF_FILE_NM", fileRename);
                rec.set("IF_SEQ", ifSeq);
                rhe.execute(rec);
                //rs2.addRecord(rec);
                processCnt++;
                ifSeq++;
            }
            rhe.end();
            
            totProcessCnt += processCnt;
            
        } catch(Exception e) {
            log.error("Exception:["+e.toString()+"]");
            if(e.getClass().equals(java.io.EOFException.class)) {
                throw new BizRuntimeException("DMS00089", e);  // 전문의 Field 길이와 실제 파일의 각각의 칼럼 길이가 맞지 않습니다.
            } else {
                throw new BizRuntimeException("DMS00009", e); //오류가 발생했습니다.
            }
        } finally {
            try {
                if (in != null) in.close();
                String backPath=path+"PROC/";
                File backDir=new File(backPath);
                if (!backDir.exists()) {
                    try{
                        backDir.mkdir();
                    } 
                    catch(SecurityException se){
                        throw new BizRuntimeException("DMS00009", se); //오류가 발생했습니다.
                    }
                }
                String tempFileNm = tempFile.getName().substring(0, tempFile.getName().lastIndexOf(".")) 
                                    + "_" + DateUtils.getDefaultCurrentDateTime().substring(8);
                if ("".equals(fileRename)) fileRename = tempFileNm+".dat";
                File fileToMove = new File(path+"PROC/"+fileRename);
                if (fileToMove.exists()) fileToMove = new File(path+"PROC/"+tempFileNm+".dat");
                tempFile.renameTo(fileToMove);
            } catch(Exception e) {
                log.error("Exception:["+e.toString()+"]");
                throw new BizRuntimeException("DMS00009", e); //오류가 발생했습니다.
            }
        }
        
        return rs2;
    }  
    
    /**
     * 인터페이스파일처리
     * 파일명 상관없이 무조건 파일 읽기
     */
    public IDataSet listFileDataNoName(SAMFileTool body, Map<String, String> paramMap, Log log, RecordHandlerExecutor rhe) throws BizRuntimeException {
        
        IDataSet responseData = new DataSet();
        totProcessCnt = 0;
        
        String path=paramMap.get("FILE_LOC");
        File dirFile=new File(path);
        if (!dirFile.exists()) {
            try{
                dirFile.mkdir();
            } 
            catch(SecurityException se){
                throw new BizRuntimeException("DMS00009", se); //오류가 발생했습니다.
            }
        }
        File []fileList=dirFile.listFiles();
        int failCnt = 0;
        int seq = 0;
        String sfYN = "";
        StringBuffer sb = new StringBuffer();
        for(File tempFile : fileList) {
          if(tempFile.isFile()) {
            String tempFileName=tempFile.getName();
            //if(tempFileName.indexOf("ZNGMBBAS10650") > -1 ) {
                sfYN = "S ";
                seq++;
                try{
                    responseData.putRecordSet(String.valueOf(seq), readFileData(body, paramMap, tempFile, log, rhe));
                } 
                catch(BizRuntimeException be){
                    log.error(be.getMessage());
                    failCnt++;
                    sfYN = "F ";
                }
                if(!"".equals(sb.toString())) {
                    sb.append(",  ");
                }
                sb.append(tempFileName);sb.append(" : ");sb.append(sfYN);
            //}
          }
        }
        String procFileNames = sb.toString();
        if (procFileNames != null && procFileNames.length() > 200) procFileNames = procFileNames.substring(0, 200); 
        responseData.putField("TOT_PROC_CNT", totProcessCnt);
        responseData.putField("PROC_FILE_NM", procFileNames);
        if (failCnt > 0) {
            responseData.putField("BAT_TASK_PROC_ST_CD", "F");
        } else {
            responseData.putField("BAT_TASK_PROC_ST_CD", "S");
        }
        
        if (seq == 0) {
            responseData.putField("BAT_TASK_PROC_ST_CD", "E");  //파일없음
        }
        
        return responseData;
    }
    
    /**
     * 인터페이스파일처리
     * 유통망정보-BR 코드 전용
     */
    public IDataSet listFileDataBRCd(SAMFileTool body, Map<String, String> paramMap, Log log, RecordHandlerExecutor rhe) throws BizRuntimeException {
        
        IDataSet responseData = new DataSet();
        totProcessCnt = 0;
        
        String path=paramMap.get("FILE_LOC");
        File dirFile=new File(path);
        if (!dirFile.exists()) {
            try{
                dirFile.mkdir();
            } 
            catch(SecurityException se){
                throw new BizRuntimeException("DMS00009", se); //오류가 발생했습니다.
            }
        }
        File []fileList=dirFile.listFiles();
        int failCnt = 0;
        int seq = 0;
        String sfYN = "";
        StringBuffer sb = new StringBuffer();
        for(File tempFile : fileList) {
          if(tempFile.isFile()) {
            String tempFileName=tempFile.getName();
            if(tempFileName.indexOf("ZNGMBBAS10650") > -1 ) {
                sfYN = "S ";
                seq++;
                try{
                    responseData.putRecordSet(String.valueOf(seq), readFileDataBRCd(body, paramMap, tempFile, log, rhe));
                } 
                catch(BizRuntimeException be){
                    log.error(be.getMessage());
                    failCnt++;
                    sfYN = "F ";
                }
                if(!"".equals(sb.toString())) {
                    sb.append(",  ");
                }
                sb.append(tempFileName);sb.append(" : ");sb.append(sfYN);
            }
          }
        }
        String procFileNames = sb.toString();
        if (procFileNames != null && procFileNames.length() > 200) procFileNames = procFileNames.substring(0, 200); 
        responseData.putField("TOT_PROC_CNT", totProcessCnt);
        responseData.putField("PROC_FILE_NM", procFileNames);
        if (failCnt > 0) {
            responseData.putField("BAT_TASK_PROC_ST_CD", "F");
        } else {
            responseData.putField("BAT_TASK_PROC_ST_CD", "S");
        }
        
        if (seq == 0) {
            responseData.putField("BAT_TASK_PROC_ST_CD", "E");  //파일없음
        }
        
        return responseData;
    }    

    /**
     * 파일을 IRecordSet으로 변경
     * 유통망정보-BR 코드 전용
     */
    public IRecordSet readFileDataBRCd(SAMFileTool body, Map<String, String> paramMap, File tempFile, Log log, RecordHandlerExecutor rhe) throws BizRuntimeException {
        
        IRecordSet rs2 = body.makeRecordSetFromColumnInfoList("BODY");
        
        String path=paramMap.get("FILE_LOC");
        int ifSeq = Integer.parseInt(StringUtils.nvl(paramMap.get("IF_SEQ"),"1"));
        
        int bLength = Integer.parseInt(StringUtils.nvl(paramMap.get("REC_LENG"),"0"));
        
        int processCnt = 0;
        BufferedInputStream in = null;
        String fileRename = tempFile.getName();
        String procDt = tempFile.getName().substring(14, tempFile.getName().lastIndexOf("."));
        FileReader fr = null;
        BufferedReader br = null;
        try {
            in = new BufferedInputStream(new FileInputStream(tempFile));
            byte[] readLineBuffer = new byte[5000];
            rhe.start();
            String readLine = "";
            while(true) {
                // 샘플 파일에서는 매 레코드는 EOL 문자로 구분됨.
                int readLength = SAMFileTool.readToEol(in, readLineBuffer);
                if (readLength == -1) break; // 다읽었음.
                if (readLength < 1) continue; // 의미 없는 공백 라인. skip.
                readLine = new String(readLineBuffer);
                if (readLine == null || readLine.trim().length() == 0) continue;
                
                // 전문상의 DEFAULT --> BAS_SETUP_PRICE(기본설정값) 이 "010102" 인 라인만 처리. 그외는 버린다.
                if ("010102".equals(readLine.substring(0,6))) {
                    if (bLength != 0 && readLength != bLength) {
                        throw new BizRuntimeException("DMS00089");  // 전문의 Field 길이와 실제 파일의 각각의 칼럼 길이가 맞지 않습니다.
                    }
                    // 레코드의 첫번째 바이트가D이면 본문부 
                    IRecord rec = body.readRecordFromBytes(rs2, readLineBuffer, 0, readLength); 
                    log.debug("RECORD BODY:["+processCnt+"]");
                    rec.set("IF_PROC_DT", procDt);
                    rec.set("IF_FILE_NM", fileRename);
                    rec.set("IF_SEQ", ifSeq);
                    rhe.execute(rec);
                    //rs2.addRecord(rec);
                    processCnt++;
                    ifSeq++;
                }
            }
            rhe.end();
            
            totProcessCnt += processCnt;
            
        } catch(Exception e) {
            log.error("Exception:["+e.toString()+"]");
            if(e.getClass().equals(java.io.EOFException.class)) {
                throw new BizRuntimeException("DMS00089", e);  // 전문의 Field 길이와 실제 파일의 각각의 칼럼 길이가 맞지 않습니다.
            } else {
                throw new BizRuntimeException("DMS00009", e); //오류가 발생했습니다.
            }
        } finally {
            try {
                if (in != null) in.close();
                String backPath=path+"PROC/";
                File backDir=new File(backPath);
                if (!backDir.exists()) {
                    try{
                        backDir.mkdir();
                    } 
                    catch(SecurityException se){
                        throw new BizRuntimeException("DMS00009", se); //오류가 발생했습니다.
                    }
                }
                String tempFileNm = tempFile.getName().substring(0, tempFile.getName().lastIndexOf(".")) 
                                    + "_" + DateUtils.getDefaultCurrentDateTime().substring(8);
                if ("".equals(fileRename)) fileRename = tempFileNm+".dat";
                File fileToMove = new File(path+"PROC/"+fileRename);
                if (fileToMove.exists()) fileToMove = new File(path+"PROC/"+tempFileNm+".dat");
                tempFile.renameTo(fileToMove);
            } catch(Exception e) {
                log.error("Exception:["+e.toString()+"]");
                throw new BizRuntimeException("DMS00009", e); //오류가 발생했습니다.
            }
        }
        
        return rs2;
    }    
}

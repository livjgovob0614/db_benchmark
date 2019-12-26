package fwk.crypto.internal;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.security.Key;
import java.util.Arrays;
import java.util.Map;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import nexcore.framework.core.crypt.internal.FileKeySaver;
import nexcore.framework.core.crypt.internal.NoKeyFoundException;
import nexcore.framework.core.log.LogManager;
import nexcore.framework.core.util.Base64;
import nexcore.framework.core.util.BaseUtils;
import nexcore.framework.core.util.FileUtils;

import org.apache.commons.logging.Log;

public class HpcFileKeySaver extends FileKeySaver {

    private Log log                  = LogManager.getFwkLog();
    private String  keyFile;
    private Map<String, String> keyFileMap;
    private int keyLength;
    private int ivLength;
    private String keyAlgorithm;
    private IvParameterSpec iv;
    private static final String DEFAULT_KEYFILE_NAME = "SecreteKey.ser";
    
    
    public void setKeyLength(int keyLength) {
        this.keyLength = keyLength;
    }

    public void setIvLength(int ivLength) {
        this.ivLength = ivLength;
    }

    public IvParameterSpec getIvParameterSpec() {
        return iv;
    }
    public void setKeyAlgorithm(String keyAlgorithm) {
        this.keyAlgorithm = keyAlgorithm;
    }

    public void setKeyFileMap(Map<String, String> keyFileMap) {
        String keyFilePath = keyFileMap.get(BaseUtils.getRuntimeMode());
        if (keyFilePath == null || "".equals(keyFilePath)) {
            setDefaultKeyFile();
        } else if (keyFilePath.charAt(1) == ':' || keyFilePath.startsWith("\\")
                || keyFilePath.startsWith("/")) {
            // 1. DOS C: or D: or ...
            // 2. starts with \\ or /
            // then let it be
            this.keyFile = keyFilePath;
        } else {
            // regard it as a relative path
            this.keyFile = BaseUtils.getFwkHome() + "/" + keyFilePath;
        }
    }

    private void setDefaultKeyFile() {
        this.keyFile = BaseUtils.getFwkHome() + "/" + DEFAULT_KEYFILE_NAME;
    }
    
    @Override
    public Key load() throws NoKeyFoundException {
        if (this.keyFile == null) {
            log.error("File '" + keyFile + "' is not found.");
            throw new NoKeyFoundException();
        }

        Key key = null;
        try {
            // keyFile을 읽는다. base64로 인코드된 문자열이므로 8비트 인코딩으로 해석한다.
            String encodedKeyString = FileUtils.readString(keyFile, "8859_1");
            key = getKeyFromBase64EncodedString(encodedKeyString);
            log.info("Have read the keyFile " + keyFile + " .");
        } catch (FileNotFoundException fe) {
            log.warn("File '" + keyFile + "' is not found.");
            throw new NoKeyFoundException();
        } catch (IOException e) {
            // TODO 적절한 FwkRuntimeException
            //2015.10.13 jihooyim code inspector 점검 수정 (throw할 때 exception 변수도 인자로 넣기)
            throw new RuntimeException("I/O error while reading file (to get a crypto key)",e);
        } catch (KeyTransException e) {
            log.warn("Could not get a valid key from file " + keyFile + " .");
            throw new NoKeyFoundException();
        }
        return key;
    }
    
    protected Key getKeyFromBase64EncodedString(String encKey) throws KeyTransException {
        Key key = null;
        
        byte[] b = Base64.decode(encKey);
        ByteArrayInputStream bis = new ByteArrayInputStream(b);
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(bis);
            SecretKey secretKey = (SecretKey) ois.readObject();
            key = new SecretKeySpec(Arrays.copyOf(secretKey.getEncoded(), (keyLength/8)), keyAlgorithm);
            iv = new IvParameterSpec(Arrays.copyOfRange(secretKey.getEncoded(), (keyLength/8), (keyLength/8)+ivLength));
        } catch (IOException e) {
            throw new KeyTransException(e);
        } catch (ClassNotFoundException e) {
            throw new KeyTransException(e);
        } finally {
            try {
                if(bis!=null)bis.close();
                if(ois!=null)ois.close();
            } catch (IOException e) {
                // ignore
                //2015.10.13 jihooyim code inspector 점검 수정 (02. 오류 상황 대응 부재)
                if (log.isErrorEnabled())  log.error("close error");
            }
        }
        return key;
    }
}

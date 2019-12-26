
import java.util.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class IDataSet {
  private String CNO;
  private String CST_NM;
  private String CUR_C;
  private String NW_DT;
  private String CCC_DT;
  private BigDecimal ACT_BBL;
  private BigDecimal BKB_BBL;
  private BigDecimal DFR_PSB_AMT;
  private BigDecimal 

  public IDataSet() {
    this.CNO = ""; // String initialize
    ...
    this.ACT_BBL = 0.0;
    ...
  }

  public void putAll(IDataSet ds) {
    this.CNO = ds.CNO;
    this.CST_NM = ds.CST_NM;
    ...
  }

  public <T> void put(String data, T 


}

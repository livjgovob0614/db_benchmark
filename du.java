import java.fwk.base;
import java.fwk.DataSet;
//import java.fwk.base.DataUnit; // dbSelect

public class DU { //extends DataUnit {

  @BizMethod("일반거래조회")
  public IDataSet inquire(IDataSet requestData) {
    IDataSet responseData = new DataSet();

    IRecordSet rs = dbSelect("inquire", requestData);

    if (rs != null) {
      responseData.putAll(rs);
    }

    return responseData;
  }


  public IRecordSet dbSelect(String stmtName, IDataSet requestData) {
    IRecordSet rs = null;

    List<IRecord> rl;
    IRecord r;
    r.put("CNO", "12345678");
    r.put("BKB_BBL", 1000);
    rl.put(r);

    r.put("CNO", "00001111");
    r.put("BKB_BBL", 0);
    rl.put(r);

    rs.put("1", rl);

    return rs;
  }
}

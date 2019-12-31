import java.fwk.base;
//import java.fwk.base.DataUnit; // dbSelect
import java.util.HashMap;
import java.util.Map;

/*
 @ HashMap<String, Object> ----> IRecord
 @ HashMap<String, List<IRecord> > ----> IRecordSet or IDataSet
*/


class DU {

  @BizMethod("일반거래조회")
  public HashMap<String, List<HashMap<String, Object>> > inquire(HashMap<String, List<HashMap<String, Object>> > requestData) {
    HashMap<String, List<HashMap<String, Object>> > responseData = new DataSet();

    HashMap<String, List<HashMap<String, Object>> > rs = dbSelect("inquire", requestData);

    if (rs != null) {
      responseData.putAll(rs);
    }

    return responseData;
  }


  public HashMap<String, List<HashMap<String, Object>> > dbSelect(String stmtName, HashMap<String, List<HashMap<String, Object>> > requestData) {
    HashMap<String, List<HashMap<String, Object>> > rs = null;

    List<HashMap<String, Object>> rl;
    HashMap<String, Object> r;
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


public class test {

  public static void main(String[] args) {
    DU du = new DU();
    HashMap<String, List<HashMap<String, Object>> > requestData;
    List<HashMap<String, Object>> rl;
    HashMap<String, Object> r;

    r.put("CNO", "12345678");
    rl.put(r);
    ds.put("1", rl);

    HashMap<String, List<HashMap<String, Object>> > responseData;
    responseData = du.inquire(requestData);

    if (responseData != null) {
      responseData.forEach((k, v) -> {
        System.out.println(s + ": " + v.stream().collect(Collectors.joining(", ")));
      });
    }
  }
}

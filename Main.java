//import java.fwk.base;
//import java.fwk.base.DataUnit; // dbSelect
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

/*
 @ HashMap<String, Object> ----> IRecord
 @ HashMap<String, List<IRecord> > ----> IRecordSet or IDataSet
*/


class DU {

  //@BizMethod("일반거래조회")
  public HashMap<String, List<HashMap<String, Object>> > inquire(HashMap<String, List<HashMap<String, Object>> > requestData) {
    HashMap<String, List<HashMap<String, Object>> > responseData = new HashMap<String, List<HashMap<String, Object>> >();

    HashMap<String, List<HashMap<String, Object>> > rs = dbSelect("inquire", requestData);

    if (rs != null) {
      responseData.putAll(rs);
    }

    return responseData;
  }


  public HashMap<String, List<HashMap<String, Object>> > dbSelect(String stmtName, HashMap<String, List<HashMap<String, Object>> > requestData) {
    HashMap<String, List<HashMap<String, Object>> > rs = new HashMap<String, List<HashMap<String, Object>> >();

    List<HashMap<String, Object>> rl = new ArrayList<HashMap<String,Object>>();
    HashMap<String, Object> r = new HashMap<String,Object>();
    r.put("CNO", "12345678");
    r.put("BKB_BBL", 1000);
    rl.add(r);

    r.clear();
    r.put("CNO", "00001111");
    r.put("BKB_BBL", 0);
    rl.add(r);

    rs.put("1", rl);

    return rs;
  }
}


public class Main {

  public static void main(String[] args) {
    DU du = new DU();
    HashMap<String, List<HashMap<String, Object>> > requestData = new HashMap<String, List<HashMap<String, Object>> >();
    List<HashMap<String, Object>> rl = new ArrayList<HashMap<String, Object>>();
    HashMap<String, Object> r = new HashMap<String, Object>();

    r.put("CNO", "12345678");
    rl.add(r);
    requestData.put("1", rl);

    HashMap<String, List<HashMap<String, Object>> > responseData;
    responseData = du.inquire(requestData);

    if (responseData != null) {
      responseData.forEach((k, v) -> {
        System.out.println(k + ": " + v);
      });
    }
  }
}

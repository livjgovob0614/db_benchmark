import java.fwk.base;
//import java.fwk.base.DataUnit; // dbSelect
import java.util.HashMap;
import java.util.Map;

class DU { //extends DataUnit {

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

/*
ass T2 {

  public int computeSomething (int a, int b){
    try {
      return a / b;
    } catch (ArithmeticException ax){
      return -1; // pretty lame error handling
    }
  }

  public void doSomething() {
    System.out.println("something");
  }
}

public class TestExample {

  public static void main(String[] args) {
    T1 t1 = new T1();

    assert t1.func1(1, 0) > 0;
    assert t1.func1(0, 1) < 0;

    assert t1.func2(true) == true;
    assert t1.func2(false) == false;


    T2 t2 = new T2();

    assert t2.computeSomething(42, 42) == 1.0;
  }
}

   */ 

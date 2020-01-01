import java.fwk.base;
//import java.fwk.base.DataUnit; // dbSelect
import java.util.HashMap;
import java.util.Map;


public class IRecord extends HashMap<String, Object> {
}

public class IRecordSet extends HashMap<String, List<IRecord> > {
}

public class IDataSet extends HashMap<String, List<IRecord> > {

}


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


public class test {

  public static void main(String[] args) {
    DU du = new DU();
    IDataSet requestData;
    List<IRecord> rl;
    IRecord r;

    r.put("CNO", "12345678");
    rl.put(r);
    ds.put("1", rl);

    IDataSet responseData;
    responseData = du.inquire(requestData);

    if (responseData != null) {
      responseData.forEach((k, v) -> {
        System.out.println(s + ": " + v.stream().collect(Collectors.joining(", ")));
      });
    }

    return 0;
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

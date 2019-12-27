import .DU;

public class test {

  public static int main() {
    DU du;
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

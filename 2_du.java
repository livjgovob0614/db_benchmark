/*
fu 등등 필요하긴 하지만 우선 돌아가는 SE tool 부터. . .
*/
import java.fwk.base;
import java.fwk.DataSet;

public class DU extends ProcessUnit {

  @BizMethod("일반거래조회")
  public IDataSet inquire(IDataSet requestData, ICtx ctx) {
    IDataSet responseData = new DataSet();

    IRecord record = dbSelect("inquire", requestData, ctx);

    if (record != null) {
      responseData.putAll(record);
    }

    return responseData;
  }

/*
  public IRecord dbSelect(String query, IDataSet requestData, ICtx ctx) {
    IRecord record = new IRecord();

    // 여기 이제 DelieverFeeBiz.xsql에 있는....


    return record;
  }
  */
}

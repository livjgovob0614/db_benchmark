/*
fu 등등 필요하긴 하지만 우선 돌아가는 SE tool 부터. . .
*/
import java.fwk.base;
import java.fwk.DataSet;

public class DU extends ProcessUnit {

  @BizMethod("일반거래조회")
  public IDataSet inquire(IDataSet requestData, ICtx ctx) {
    IDataSet responseData = new DataSet();

    IRecordSet rs = queryForRecordSet("DeliverFeeBiz.selectDeliverFee",
            requestData.getFieldMap());

    if (rs == null) {
      System.out.println("rs is null.");
      // 
      rs = new RecordSet("ds_Dev");
    }

		// db connection X.... ? 
    DataSet responseData = DataSetFactory.createWithOKResultMessage(
        BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
            .valueOf(rs.getRecordCount()) });
    responseData.putRecordSet("ds_Dev", rs);

		return responseData;
  }
}

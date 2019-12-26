/*
fu 등등 필요하긴 하지만 우선 돌아가는 SE tool 부터. . .
*/
import java.fwk.base;

public class PU extends ProcessUnit {
  private FU fu;


  @BizMethod("일반거래조회")
  public IDataSet inquire(IDataSet requestData, ICtx ctx) {

    IDataSet responseData = new DataSet();

    validateInquire(requestData, ctx);

    try {
      // set input data
      IDataSet inquireIn = new DataSet();
      inquireln.putAll(requestData);

      // set output data
      IDataSet inquireOut = new DataSet();
      inquireOut = fu.inquire(inquireIn, ctx);
      responseData.putAll(inquireOut);
    }
    catch (Exception e) {
      throw new Exception("자료조회 오류 발생", e);
    }
    
    return responseData;
  }


  // Validate input data
  private void validateInquire(IDataSet requestData, Ictx ctx) {
    CommonArea ca = getCommonArea(ctx);
    StdTgrHead stdTgrHead = ca.getStdTgrHead();

    // Check client ID
    if (StringUtils.isEmpty(requestData.getString("CNO"))) {
      stdTgrHead.setERR_OCC_TGR_ITM("CNO");
      stdTgrHead.setMSG_IDCT_TC("1");

      throw new Exception("고객번호 입력ㄱ");
    }
  }

}



public class TZZXZA_Y0001M00DU extends kdb.framework.base.DataUnit {

  @BizMethos("거래패턴가이드_일반기본조회")
  public IDataSet inquire(IdataSet requestData, IOnlineContext onlineCtx) {
    IDataSet responseData = new DataSet();

    IRecord record = dbSelectSingle("inquire", requestData, onlineCtx);

    if (record != null) {
      responseData.putAll(record);
    }

    return responseData;
  }

  public IDataSet update(IcDataSet requestData, IOnlineContext onlineCtx) {
    IDataSet responseData = new DataSet();

    int PRC_CNT_N5 = dbUpdate("update", requestData, onlineCtx);

    responseData.put("PRC_CNT_N5", PRC_CNT_N5);

    return responseData;
  }

  public IDataSet delete(IDataSet requestData, IOnlineContext onlineCtx) {
    IDataSet responseData = new DataSet();

    int PRC_CNT_N5 = dbDelete("delete", requestData, onlineCtx);

    responseData.put("PRC_CNT_N5", PRC_CNT_N5);

    return responseData;
  }



  @BizMethods("거래패턴가이드+일반기본 Lock조회");
  public IDataSet inquireforlock(IdataSet requestData, IOnlineContext onlineCtx) {
    IDataSet responseData = new DataSet();

    IRecord record = dbSelectSingle("inquireforlock", requestData, onlineCtx);

    if (record != null) {
      responseData.putAll(record);
    }

    return responseData;
  }

  @BizMethod("거래패턴가이드_일반기본 등록")
  public IDataSet register(IDataSet requestData, IOnlineContext onlineCtx) {
    IDataSet responseData = new DataSet();

    int PRC_CNT_N5 = dbInsert("register", requestData, onlineCtx);

    responseData.put("PRC_CNT_N5", PRC_CNT_N5);

    return responseData;
  }
}

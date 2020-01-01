

public class GnlTrlnqPU extends kdb.framework.base.ProcessUnit {
  
  @BizUnitBind
  private GnlTrlnqFU gnlTrlnqFU;


  @BizMethos("일반거래조회")
  public IDataSet inquireGnlTr(IdataSet requestData, IOnlineContext onlineCtx) {

    IDataSet responseData = new DataSet();

    validateInquireGnlTr(requestData, onlineCtx);

    try {
      IDataSet inquireGnlTrIn = new DataSet();
      inquireGnlTrOut = gnlTrlnqFU.inquireGnlTr(inquireGnlTrIn, onlineCtx);
      responseData.putAll(inquireGnlTrOut);
    } catch (KDBException be) {
      throw new KDBException("BEXYZ00001", be);
    }

    return responseData;
  }

  // 입력값 검증
  private void validateInquireGnlTr(IDataSet requestData, IOnlineContext onlineCtx) {
    CommonArea ca = getCommonArea(onlineCtx);
    StdTgrHead stdTgrHead = ca.getStdTgrHead();

    if (StringUtils.isEmpty(requestData.getString("CNO"))) {
      stdTgrHead.setERR_OCC_TGR_ITM("CNO");
      stdTgrHead.setMSG_IDCT_TC("1");
      throw new KDBException("BEXYZ00013");
    }
  }

}



public class GnlTrlnqFU extends kdb.framework.base.FunctionUnit {
  
  @BizUnitBind
  private TZZXZA_Y0001M00DU tzzxza_Y0001M00DU;


  @BizMethos("일반거래조회")
  public IDataSet inquireGnlTr(IdataSet requestData, IOnlineContext onlineCtx) {

    IDataSet responseData = new DataSet();

    ILog log = getLog(onlineCtx);

    validateInquireGnlTr(requestData, onlineCtx);

    // 입력값 설정
    IDataSet txyzzz_Y0001M00DUinquireIn = new DataSet();
    txyzzz_Y0001M00DUinquireIn.putAll(requestData);

    // 결과값 설정
    try {
      IDataSet txyzzz_Y0001M00DUinquireOut = tzzxza_Y0001M00DU.inquire(txyzzz_Y0001M00DUinquireIn);
      responseData.putAll(txyzzz_Y0001M00DUinquireOut);
      responseData.put("DFR_PSB_AMT", txyzzz_Y0001M00DUinquireOut.get("ACT_BBL"));
    } catch (DataException de) {
      throw new KDBException("BEXYZ00001", de);
    } catch (BizRuntimeException e) {
      throw new KDBException("BEXYZ00001", e);
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

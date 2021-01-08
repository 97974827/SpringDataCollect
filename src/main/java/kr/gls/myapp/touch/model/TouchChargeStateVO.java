package kr.gls.myapp.touch.model;

// gl_charger_state
public class TouchChargeStateVO {
	
	private Integer deviceNo;	// 장비 번호(외래키)
	private String kind;			// 0: 발급, 1:충전
	private String exhaustMoney; // 카드발급금액
	private String chargeType;	// 0:현금, 1:카드
	private String salesType;		// 0:매출, 1:비매출
	private String currentMoney;// 투입금액
	private String currentCreditMoney; // 신용카드 투입금액
	private String currentBonus; // 투입금액에 따른 보너스
	private String currentCharge;// 충전금액
	private String totalMoney; // 충전 후 카드 총 잔액
	private String cardNum; // 카드 번호
	private String creditNo; // 신용카드 로그번호
	private String inputDate;
	
	
	public Integer getDeviceNo() {
		return deviceNo;
	}
	public void setDeviceNo(Integer deviceNo) {
		this.deviceNo = deviceNo;
	}
	public void setKind(String kind) {
		this.kind = kind;
	}
	public void setChargeType(String chargeType) {
		this.chargeType = chargeType;
	}
	public void setSalesType(String salesType) {
		this.salesType = salesType;
	}
	public String getExhaustMoney() {
		return exhaustMoney;
	}
	public void setExhaustMoney(String exhaustMoney) {
		this.exhaustMoney = exhaustMoney;
	}
	public String getKind() {
		return kind;
	}
	public String getChargeType() {
		return chargeType;
	}
	public String getSalesType() {
		return salesType;
	}
	public String getCurrentMoney() {
		return currentMoney;
	}
	public void setCurrentMoney(String currentMoney) {
		this.currentMoney = currentMoney;
	}
	public String getCurrentCreditMoney() {
		return currentCreditMoney;
	}
	public void setCurrentCreditMoney(String currentCreditMoney) {
		this.currentCreditMoney = currentCreditMoney;
	}
	public String getCurrentBonus() {
		return currentBonus;
	}
	public void setCurrentBonus(String currentBonus) {
		this.currentBonus = currentBonus;
	}
	public String getCurrentCharge() {
		return currentCharge;
	}
	public void setCurrentCharge(String currentCharge) {
		this.currentCharge = currentCharge;
	}
	public String getTotalMoney() {
		return totalMoney;
	}
	public void setTotalMoney(String totalMoney) {
		this.totalMoney = totalMoney;
	}
	public String getCardNum() {
		return cardNum;
	}
	public void setCardNum(String cardNum) {
		this.cardNum = cardNum;
	}
	public String getCreditNo() {
		return creditNo;
	}
	public void setCreditNo(String creditNo) {
		this.creditNo = creditNo;
	}
	public String getInputDate() {
		return inputDate;
	}
	public void setInputDate(String inputDate) {
		this.inputDate = inputDate;
	}
	@Override
	public String toString() {
		return "TouchChargeStateVO [deviceNo=" + deviceNo + ", kind=" + kind + ", exhaustMoney="
				+ exhaustMoney + ", chargeType=" + chargeType + ", salesType=" + salesType + ", currentMoney="
				+ currentMoney + ", currentCreditMoney=" + currentCreditMoney + ", currentBonus=" + currentBonus
				+ ", currentCharge=" + currentCharge + ", totalMoney=" + totalMoney + ", cardNum=" + cardNum
				+ ", creditNo=" + creditNo + ", inputDate=" + inputDate + "]";
	}
	
	
	
	
}

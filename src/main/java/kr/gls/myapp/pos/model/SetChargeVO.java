package kr.gls.myapp.pos.model;

// 클라이언트 카드 충전 파싱 데이터 모델 
public class SetChargeVO {
		
	private String charge; // 충전 금액 
	private String minus;  // 차감 금액
	private String bonus;  // 보너스 금액 
	private String remain; // 카드 잔액 
	private String use;    // 사용처
	private String sales;  // 매출 
	private String income; // 수입 
	private String card_num; // 카드번호 
	
	@Override
	public String toString() {
		return "SetChargeVO [charge=" + charge + ", minus=" + minus + ", bonus=" + bonus + ", remain=" + remain
				+ ", use=" + use + ", sales=" + sales + ", income=" + income + ", card_num=" + card_num + "]";
	}

	public String getCharge() {
		return charge;
	}

	public void setCharge(String charge) {
		this.charge = charge;
	}

	public String getMinus() {
		return minus;
	}

	public void setMinus(String minus) {
		this.minus = minus;
	}

	public String getBonus() {
		return bonus;
	}

	public void setBonus(String bonus) {
		this.bonus = bonus;
	}

	public String getRemain() {
		return remain;
	}

	public void setRemain(String remain) {
		this.remain = remain;
	}

	public String getUse() {
		return use;
	}

	public void setUse(String use) {
		this.use = use;
	}

	public String getSales() {
		return sales;
	}

	public void setSales(String sales) {
		this.sales = sales;
	}

	public String getIncome() {
		return income;
	}

	public void setIncome(String income) {
		this.income = income;
	}

	public String getCard_num() {
		return card_num;
	}

	public void setCard_num(String card_num) {
		this.card_num = card_num;
	}
	
	
}

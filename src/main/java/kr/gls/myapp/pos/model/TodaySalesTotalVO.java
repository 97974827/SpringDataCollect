package kr.gls.myapp.pos.model;

// 금일 매출 총계
public class TodaySalesTotalVO {
	
	private String sales;
	private String income;
	private String cash_sales;
	private String card_sales;
	private String card_charge;
	
	@Override
	public String toString() {
		return "TodaySalesTotalVO [sales=" + sales + ", income=" + income + ", cash_sales=" + cash_sales
				+ ", card_sales=" + card_sales + ", card_charge=" + card_charge + "]";
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

	public String getCash_sales() {
		return cash_sales;
	}

	public void setCash_sales(String cash_sales) {
		this.cash_sales = cash_sales;
	}

	public String getCard_sales() {
		return card_sales;
	}

	public void setCard_sales(String card_sales) {
		this.card_sales = card_sales;
	}

	public String getCard_charge() {
		return card_charge;
	}

	public void setCard_charge(String card_charge) {
		this.card_charge = card_charge;
	}
	
	
}

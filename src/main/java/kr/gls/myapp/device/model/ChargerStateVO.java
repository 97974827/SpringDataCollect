package kr.gls.myapp.device.model;

public class ChargerStateVO {

	private Integer device_no;
	private String kind;
	private String exhaust_money;
	private String current_money;
	private String current_bonus;
	private String current_charge;
	private String total_money;
	private String card_num;
	private String input_date;
	
	@Override
	public String toString() {
		return "ChargerStateVO [device_no=" + device_no + ", kind=" + kind + ", exhaust_money=" + exhaust_money
				+ ", current_money=" + current_money + ", current_bonus=" + current_bonus + ", current_charge="
				+ current_charge + ", total_money=" + total_money + ", card_num=" + card_num + ", input_date="
				+ input_date + "]";
	}

	public Integer getDevice_no() {
		return device_no;
	}

	public void setDevice_no(Integer device_no) {
		this.device_no = device_no;
	}

	public String getKind() {
		return kind;
	}

	public void setKind(String kind) {
		this.kind = kind;
	}

	public String getExhaust_money() {
		return exhaust_money;
	}

	public void setExhaust_money(String exhaust_money) {
		this.exhaust_money = exhaust_money;
	}

	public String getCurrent_money() {
		return current_money;
	}

	public void setCurrent_money(String current_money) {
		this.current_money = current_money;
	}

	public String getCurrent_bonus() {
		return current_bonus;
	}

	public void setCurrent_bonus(String current_bonus) {
		this.current_bonus = current_bonus;
	}

	public String getCurrent_charge() {
		return current_charge;
	}

	public void setCurrent_charge(String current_charge) {
		this.current_charge = current_charge;
	}

	public String getTotal_money() {
		return total_money;
	}

	public void setTotal_money(String total_money) {
		this.total_money = total_money;
	}

	public String getCard_num() {
		return card_num;
	}

	public void setCard_num(String card_num) {
		this.card_num = card_num;
	}

	public String getInput_date() {
		return input_date;
	}

	public void setInput_date(String input_date) {
		this.input_date = input_date;
	}
	
	
	
}

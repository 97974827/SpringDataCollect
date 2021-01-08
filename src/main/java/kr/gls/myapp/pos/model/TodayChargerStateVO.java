package kr.gls.myapp.pos.model;

// 충전기 기록 모델 클래스 
public class TodayChargerStateVO {
	
	private Integer device_no; 
	private String device_name;
	private String device_addr;
	private String card_num;
	private String exhaust_money;
	private String kind;
	private String money;
	private String bonus;
	private String charge;
	private String remain_card;
	private String credit_money;
	private String input_date;
	
	@Override
	public String toString() {
		return "TodayChargerStateVO [device_no=" + device_no + ", device_name=" + device_name + ", device_addr="
				+ device_addr + ", card_num=" + card_num + ", exhaust_money=" + exhaust_money + ", kind=" + kind
				+ ", money=" + money + ", bonus=" + bonus + ", charge=" + charge + ", remain_card=" + remain_card
				+ ", credit_money=" + credit_money + ", input_date=" + input_date + "]";
	}
	
	public void setDevice_no(Integer device_no) {
		this.device_no = device_no;
	}
	
	public Integer getDevice_no() {
		return device_no;
	}
	
	public void setKind(String kind) {
		this.kind = kind;
	}
	
	public String getKind() {
		return kind;
	}
	
	public void setExhaust_money(String exhaust_money) {
		this.exhaust_money = exhaust_money;
	}
	
	public String getExhaust_money() {
		return exhaust_money;
	}
	
	public String getDevice_name() {
		return device_name;
	}

	public void setDevice_name(String device_name) {
		this.device_name = device_name;
	}

	public String getDevice_addr() {
		return device_addr;
	}

	public void setDevice_addr(String device_addr) {
		this.device_addr = device_addr;
	}

	public String getCard_num() {
		return card_num;
	}

	public void setCard_num(String card_num) {
		this.card_num = card_num;
	}

	public String getMoney() {
		return money;
	}

	public void setMoney(String money) {
		this.money = money;
	}

	public String getBonus() {
		return bonus;
	}

	public void setBonus(String bonus) {
		this.bonus = bonus;
	}

	public String getCharge() {
		return charge;
	}

	public void setCharge(String charge) {
		this.charge = charge;
	}

	public String getRemain_card() {
		return remain_card;
	}

	public void setRemain_card(String remain_card) {
		this.remain_card = remain_card;
	}

	public String getCredit_money() {
		return credit_money;
	}

	public void setCredit_money(String credit_money) {
		this.credit_money = credit_money;
	}

	public String getInput_date() {
		return input_date;
	}

	public void setInput_date(String input_date) {
		this.input_date = input_date;
	}
	
	
	/*
	 *       "device_name": "카드 충전기",
		      "device_addr": "01",
		      "card_num": "2b005302",
		      "money": 10000,
		      "bonus": 1000,
		      "charge": 10000,
		      "remain_card": 23000,
		      "credit_money": 0,
		      "input_date": 1607181646	
	 */
	
}

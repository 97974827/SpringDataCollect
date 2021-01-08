package kr.gls.myapp.pos.model;


public class SalesListViewVO { // gl_sales_list 뷰 모델 클래스
	
	private Integer no;				// 충전 기록 넘버 (DB)
	private String device_name;
	private String device_type;
	private String device_addr;
	private String card_num;
	private String time;			// 사용시간 / 카드발급여부
	private String cash;			// 현금매출 / 충전금액
	private String card;			// 카드매출 / 보너스금액
	private String remain_card;		// 카드 잔액 / 충전 후 잔액
	private String master_card; 	// 마스터 사용금액 / 카드 발급금액
	private String start_time;		// 시작 시간 / 충전 시간
	private String end_time;		// 종료 시간 / 충전 시간
	private String current_money;	// table index / 현금 투입금액
	private String credit_money;	// table index / 신용카드 결제금액
	
	@Override
	public String toString() {
		return "SalesListViewVO [no=" + no + ", device_name=" + device_name + ", device_type=" + device_type
				+ ", device_addr=" + device_addr + ", card_num=" + card_num + ", time=" + time + ", cash=" + cash
				+ ", card=" + card + ", remain_card=" + remain_card + ", master_card=" + master_card + ", start_time="
				+ start_time + ", end_time=" + end_time + ", current_money=" + current_money + ", credit_money="
				+ credit_money + "]";
	}
	
	public Integer getNo() {
		return no;
	}

	public void setNo(Integer no) {
		this.no = no;
	}

	public String getDevice_name() {
		return device_name;
	}

	public void setDevice_name(String device_name) {
		this.device_name = device_name;
	}

	public String getDevice_type() {
		return device_type;
	}

	public void setDevice_type(String device_type) {
		this.device_type = device_type;
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

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getCash() {
		return cash;
	}

	public void setCash(String cash) {
		this.cash = cash;
	}

	public String getCard() {
		return card;
	}

	public void setCard(String card) {
		this.card = card;
	}

	public String getRemain_card() {
		return remain_card;
	}

	public void setRemain_card(String remain_card) {
		this.remain_card = remain_card;
	}

	public String getMaster_card() {
		return master_card;
	}

	public void setMaster_card(String master_card) {
		this.master_card = master_card;
	}

	public String getStart_time() {
		return start_time;
	}

	public void setStart_time(String start_time) {
		this.start_time = start_time;
	}

	public String getEnd_time() {
		return end_time;
	}

	public void setEnd_time(String end_time) {
		this.end_time = end_time;
	}

	public String getCurrent_money() {
		return current_money;
	}

	public void setCurrent_money(String current_money) {
		this.current_money = current_money;
	}

	public String getCredit_money() {
		return credit_money;
	}

	public void setCredit_money(String credit_money) {
		this.credit_money = credit_money;
	}
	
	
}

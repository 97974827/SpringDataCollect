package kr.gls.myapp.device.model;

// 세차장비 동작상태 모델 클래스 - 클라이언트 json 리턴용 
public class JsonDeviceStateVO {
	
	private String state;			// 동작상태
	private String start_time;		// 시작시간
	private String current_cash;	// 투입금액 - 현금
	private String current_card;	// 투입금액 - 카드
	private String current_master;	// 투입금액 - 마스터
	private String use_cash;		// 사용금액 - 현금
	private String use_card;		// 사용금액 - 카드
	private String use_master;		// 사용금액 - 마스터
	private String remain_time; 	// 남은 시간
	private String card_num;		// 카드 번호
	private String sales;			// 당일 매출
	private Integer device_type;	// 
	private String device_addr;		// 
	private String connect;			// 통신 상태
	
	@Override
	public String toString() {
		return "DeviceStateVO [state=" + state + ", start_time=" + start_time + ", current_cash=" + current_cash
				+ ", current_card=" + current_card + ", current_master=" + current_master + ", use_cash=" + use_cash
				+ ", use_card=" + use_card + ", use_master=" + use_master + ", remain_time=" + remain_time
				+ ", card_num=" + card_num + ", sales=" + sales + ", device_type=" + device_type + ", device_addr="
				+ device_addr + ", connect=" + connect + "]";
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getStart_time() {
		return start_time;
	}

	public void setStart_time(String start_time) {
		this.start_time = start_time;
	}

	public String getCurrent_cash() {
		return current_cash;
	}

	public void setCurrent_cash(String current_cash) {
		this.current_cash = current_cash;
	}

	public String getCurrent_card() {
		return current_card;
	}

	public void setCurrent_card(String current_card) {
		this.current_card = current_card;
	}

	public String getCurrent_master() {
		return current_master;
	}

	public void setCurrent_master(String current_master) {
		this.current_master = current_master;
	}

	public String getUse_cash() {
		return use_cash;
	}

	public void setUse_cash(String use_cash) {
		this.use_cash = use_cash;
	}

	public String getUse_card() {
		return use_card;
	}

	public void setUse_card(String use_card) {
		this.use_card = use_card;
	}

	public String getUse_master() {
		return use_master;
	}

	public void setUse_master(String use_master) {
		this.use_master = use_master;
	}

	public String getRemain_time() {
		return remain_time;
	}

	public void setRemain_time(String remain_time) {
		this.remain_time = remain_time;
	}

	public String getCard_num() {
		return card_num;
	}

	public void setCard_num(String card_num) {
		this.card_num = card_num;
	}

	public String getSales() {
		return sales;
	}

	public void setSales(String sales) {
		this.sales = sales;
	}

	public Integer getDevice_type() {
		return device_type;
	}

	public void setDevice_type(Integer device_type) {
		this.device_type = device_type;
	}

	public String getDevice_addr() {
		return device_addr;
	}

	public void setDevice_addr(String device_addr) {
		this.device_addr = device_addr;
	}

	public String getConnect() {
		return connect;
	}

	public void setConnect(String connect) {
		this.connect = connect;
	}
	
	
}

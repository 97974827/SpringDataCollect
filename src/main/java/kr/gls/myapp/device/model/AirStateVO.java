package kr.gls.myapp.device.model;

// 진공 저장용 모델 클래스
public class AirStateVO {
	
	private String device_addr;
	private String card_num;
	private String air_time;
	private String air_cash;
	private String air_card;
	private String remain_card;
	private String master_card;
	private String start_time;
	private String end_time;
	
	@Override
	public String toString() {
		return "AirStateVO [device_addr=" + device_addr + ", card_num=" + card_num + ", air_time=" + air_time
				+ ", air_cash=" + air_cash + ", air_card=" + air_card + ", remain_card=" + remain_card
				+ ", master_card=" + master_card + ", start_time=" + start_time + ", end_time=" + end_time + "]";
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

	public String getAir_time() {
		return air_time;
	}

	public void setAir_time(String air_time) {
		this.air_time = air_time;
	}

	public String getAir_cash() {
		return air_cash;
	}

	public void setAir_cash(String air_cash) {
		this.air_cash = air_cash;
	}

	public String getAir_card() {
		return air_card;
	}

	public void setAir_card(String air_card) {
		this.air_card = air_card;
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
	
	
	
}

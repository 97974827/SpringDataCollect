package kr.gls.myapp.device.model;

// 매트 저장용 모델 클래스
public class MateStateVO {

	private String device_addr;
	private String card_num;
	private String mate_time;
	private String mate_cash;
	private String mate_card;
	private String remain_card;
	private String master_card;
	private String start_time;
	private String end_time;
	
	@Override
	public String toString() {
		return "MateStateVO [device_addr=" + device_addr + ", card_num=" + card_num + ", mate_time=" + mate_time
				+ ", mate_cash=" + mate_cash + ", mate_card=" + mate_card + ", remain_card=" + remain_card
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

	public String getMate_time() {
		return mate_time;
	}

	public void setMate_time(String mate_time) {
		this.mate_time = mate_time;
	}

	public String getMate_cash() {
		return mate_cash;
	}

	public void setMate_cash(String mate_cash) {
		this.mate_cash = mate_cash;
	}

	public String getMate_card() {
		return mate_card;
	}

	public void setMate_card(String mate_card) {
		this.mate_card = mate_card;
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

package kr.gls.myapp.device.model;

// 리더기 기록 저장 모델 클래스
public class ReaderStateVO {
		
	private String device_addr;
	private String card_num;
	private String reader_time;
	private String reader_cash;
	private String reader_card;
	private String remain_card;
	private String master_card;
	private String start_time;
	private String end_time;
	
	@Override
	public String toString() {
		return "ReaderStateVO [device_addr=" + device_addr + ", card_num=" + card_num + ", reader_time=" + reader_time
				+ ", reader_cash=" + reader_cash + ", reader_card=" + reader_card + ", remain_card=" + remain_card
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

	public String getReader_time() {
		return reader_time;
	}

	public void setReader_time(String reader_time) {
		this.reader_time = reader_time;
	}

	public String getReader_cash() {
		return reader_cash;
	}

	public void setReader_cash(String reader_cash) {
		this.reader_cash = reader_cash;
	}

	public String getReader_card() {
		return reader_card;
	}

	public void setReader_card(String reader_card) {
		this.reader_card = reader_card;
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

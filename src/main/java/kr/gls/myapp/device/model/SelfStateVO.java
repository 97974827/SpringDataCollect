package kr.gls.myapp.device.model;

// 셀프 DB 저장용 모델
public class SelfStateVO {
	
	private String device_addr;
	private String card_num;
	private String self_time;
	private String under_time;
	private String foam_time;
	private String coating_time;
	private String use_cash;
	private String use_card;
	private String remain_card;
	private String master_card;
	private String start_time;
	private String end_time;
	
	@Override
	public String toString() {
		return "SelfStateVO [device_addr=" + device_addr + ", card_num=" + card_num + ", self_time=" + self_time
				+ ", under_time=" + under_time + ", foam_time=" + foam_time + ", coating_time=" + coating_time
				+ ", use_cash=" + use_cash + ", use_card=" + use_card + ", remain_card=" + remain_card
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

	public String getSelf_time() {
		return self_time;
	}

	public void setSelf_time(String self_time) {
		this.self_time = self_time;
	}

	public String getUnder_time() {
		return under_time;
	}

	public void setUnder_time(String under_time) {
		this.under_time = under_time;
	}

	public String getFoam_time() {
		return foam_time;
	}

	public void setFoam_time(String foam_time) {
		this.foam_time = foam_time;
	}

	public String getCoating_time() {
		return coating_time;
	}

	public void setCoating_time(String coating_time) {
		this.coating_time = coating_time;
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

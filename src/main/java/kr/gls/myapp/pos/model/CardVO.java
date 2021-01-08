package kr.gls.myapp.pos.model;

public class CardVO {
	
	private String card_num;
	private String input_date;
	private String mb_no;
	
	@Override
	public String toString() {
		return "CardVO [card_num=" + card_num + ", input_date=" + input_date + ", mb_no=" + mb_no + "]";
	}
	
	public void setMb_no(String mb_no) {
		this.mb_no = mb_no;
	}
	
	public String getMb_no() {
		return mb_no;
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

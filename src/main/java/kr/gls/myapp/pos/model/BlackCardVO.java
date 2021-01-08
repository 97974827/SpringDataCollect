package kr.gls.myapp.pos.model;

public class BlackCardVO {
	
	private Integer no;
	private String card_num;
	private String content;
	private String input_date;
	
	@Override
	public String toString() {
		return "BlackCardVO [no=" + no + ", card_num=" + card_num + ", content=" + content + ", input_date="
				+ input_date + "]";
	}

	public Integer getNo() {
		return no;
	}

	public void setNo(Integer no) {
		this.no = no;
	}

	public String getCard_num() {
		return card_num;
	}

	public void setCard_num(String card_num) {
		this.card_num = card_num;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getInput_date() {
		return input_date;
	}

	public void setInput_date(String input_date) {
		this.input_date = input_date;
	}
	
	
	
}

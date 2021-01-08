package kr.gls.myapp.pos.model;

// 회원 카드 모델 클래스
public class MemberCardVO {
	
	private String num; // 카드번호 (기본키)
	private String mb_no;
	private String input_date;
	
	@Override
	public String toString() {
		return "MemberCardVO [num=" + num + ", mb_no=" + mb_no + ", input_date=" + input_date + "]";
	}

	public String getNum() {
		return num;
	}

	public void setNum(String num) {
		this.num = num;
	}

	public String getMb_no() {
		return mb_no;
	}

	public void setMb_no(String mb_no) {
		this.mb_no = mb_no;
	}

	public String getInput_date() {
		return input_date;
	}

	public void setInput_date(String input_date) {
		this.input_date = input_date;
	} 
	
	
}

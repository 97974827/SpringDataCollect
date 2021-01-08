package kr.gls.myapp.pos.model;

public class MasterConfigSetVO {
	
	private String manager_no;
	private String enable_card;
	private String card_binary;
	
	@Override
	public String toString() {
		return "MasterConfigSetVO [manager_no=" + manager_no + ", enable_card=" + enable_card + ", card_binary="
				+ card_binary + "]";
	}

//	public String getAuth_code() {
//		return auth_code;
//	}
//
//	public void setAuth_code(String auth_code) {
//		this.auth_code = auth_code;
//	}

	public String getManager_no() {
		return manager_no;
	}

	public void setManager_no(String manager_no) {
		this.manager_no = manager_no;
	}

	public String getEnable_card() {
		return enable_card;
	}

	public void setEnable_card(String enable_card) {
		this.enable_card = enable_card;
	}

	public String getCard_binary() {
		return card_binary;
	}

	public void setCard_binary(String card_binary) {
		this.card_binary = card_binary;
	}
	
	
}

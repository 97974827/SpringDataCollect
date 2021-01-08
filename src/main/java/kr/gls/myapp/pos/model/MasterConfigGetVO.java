package kr.gls.myapp.pos.model;

public class MasterConfigGetVO {
	
	private String auth_code; // 인증코드 request
//	private String auth; // 성공:1, 실패:0
	private String manager_name; // 공급업체명
	private String manager_no; // 공급업체명 : set_master_config 처리용 
	private String enable_card; // 카드사용여부
	private String card_binary; // 카드 저장번지
	
	@Override
	public String toString() {
		return "MasterConfigGetVO [auth_code=" + auth_code + ", manager_name=" + manager_name + ", manager_no="
				+ manager_no + ", enable_card=" + enable_card + ", card_binary=" + card_binary + "]";
	}

	public String getAuth_code() {
		return auth_code;
	}

	public void setAuth_code(String auth_code) {
		this.auth_code = auth_code;
	}

	public String getManager_name() {
		return manager_name;
	}
	
	public String getManager_no() {
		return manager_no;
	}

	public void setManager_no(String manager_no) {
		this.manager_no = manager_no;
	}

	public void setManager_name(String manager_name) {
		this.manager_name = manager_name;
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

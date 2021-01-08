package kr.gls.myapp.touch.model;

// TODO 포스 설정값 받아주는 모델클래스 : 변수명은 포스에서 파라미터 값을 이미 전부 줘버린 상태로 언더바 형식을 지정했음   
public class TouchConfigSetVO {
	
	private String device_addr;
	private String shop_pw; // 관리자 비밀번호
	private String card_price;
	private String card_min_price;
	private String bonus1;
	private String bonus2;
	private String bonus3;
	private String bonus4;
	private String bonus5;
	private String bonus6;
	private String bonus7;
	private String bonus8;
	private String bonus9;
	private String bonus10;
	private String auto_charge_enable;
	private String auto_charge_price;
	private String rf_reader_type;
	private String shop_no;
	private String name;
	private String manager_key;
	
	@Override
	public String toString() {
		return "TouchConfigSetVO [device_addr=" + device_addr + ", shop_pw=" + shop_pw + ", card_price=" + card_price
				+ ", card_min_price=" + card_min_price + ", bonus1=" + bonus1 + ", bonus2=" + bonus2 + ", bonus3="
				+ bonus3 + ", bonus4=" + bonus4 + ", bonus5=" + bonus5 + ", bonus6=" + bonus6 + ", bonus7=" + bonus7
				+ ", bonus8=" + bonus8 + ", bonus9=" + bonus9 + ", bonus10=" + bonus10 + ", auto_charge_enable="
				+ auto_charge_enable + ", auto_charge_price=" + auto_charge_price + ", rf_reader_type=" + rf_reader_type
				+ ", shop_no=" + shop_no + ", name=" + name + ", manager_key=" + manager_key + "]";
	}

	public String getDevice_addr() {
		return device_addr;
	}

	public void setDevice_addr(String device_addr) {
		this.device_addr = device_addr;
	}

	public String getShop_pw() {
		return shop_pw;
	}

	public void setShop_pw(String shop_pw) {
		this.shop_pw = shop_pw;
	}

	public String getCard_price() {
		return card_price;
	}

	public void setCard_price(String card_price) {
		this.card_price = card_price;
	}

	public String getCard_min_price() {
		return card_min_price;
	}

	public void setCard_min_price(String card_min_price) {
		this.card_min_price = card_min_price;
	}

	public String getBonus1() {
		return bonus1;
	}

	public void setBonus1(String bonus1) {
		this.bonus1 = bonus1;
	}

	public String getBonus2() {
		return bonus2;
	}

	public void setBonus2(String bonus2) {
		this.bonus2 = bonus2;
	}

	public String getBonus3() {
		return bonus3;
	}

	public void setBonus3(String bonus3) {
		this.bonus3 = bonus3;
	}

	public String getBonus4() {
		return bonus4;
	}

	public void setBonus4(String bonus4) {
		this.bonus4 = bonus4;
	}

	public String getBonus5() {
		return bonus5;
	}

	public void setBonus5(String bonus5) {
		this.bonus5 = bonus5;
	}

	public String getBonus6() {
		return bonus6;
	}

	public void setBonus6(String bonus6) {
		this.bonus6 = bonus6;
	}

	public String getBonus7() {
		return bonus7;
	}

	public void setBonus7(String bonus7) {
		this.bonus7 = bonus7;
	}

	public String getBonus8() {
		return bonus8;
	}

	public void setBonus8(String bonus8) {
		this.bonus8 = bonus8;
	}

	public String getBonus9() {
		return bonus9;
	}

	public void setBonus9(String bonus9) {
		this.bonus9 = bonus9;
	}

	public String getBonus10() {
		return bonus10;
	}

	public void setBonus10(String bonus10) {
		this.bonus10 = bonus10;
	}

	public String getAuto_charge_enable() {
		return auto_charge_enable;
	}

	public void setAuto_charge_enable(String auto_charge_enable) {
		this.auto_charge_enable = auto_charge_enable;
	}

	public String getAuto_charge_price() {
		return auto_charge_price;
	}

	public void setAuto_charge_price(String auto_charge_price) {
		this.auto_charge_price = auto_charge_price;
	}

	public String getRf_reader_type() {
		return rf_reader_type;
	}

	public void setRf_reader_type(String rf_reader_type) {
		this.rf_reader_type = rf_reader_type;
	}

	public String getShop_no() {
		return shop_no;
	}

	public void setShop_no(String shop_no) {
		this.shop_no = shop_no;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getManager_key() {
		return manager_key;
	}

	public void setManager_key(String manager_key) {
		this.manager_key = manager_key;
	}
	
	
	
	
}	

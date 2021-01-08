package kr.gls.myapp.touch.model;

import com.fasterxml.jackson.annotation.JsonInclude;

// 설정 리턴 값 VO
public class TouchConfigGetVO {
	
	private Integer no; // 장비 리스트 번호 참조키
	private Integer type;
	private String deviceAddr;
	private String shopPw; // 포스 관리자 비밀번호
	private String cardPrice;
	private String cardMinPrice;
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
	private Integer defaultBonusNo;
	private String autoChargeEnable;
	private String autoChargePrice;
	private String rfReaderType;
	private String shopNo;
	private String name;
	private String managerKey;
	private String inputDate;
	
	public Integer getNo() {
		return no;
	}
	public void setNo(Integer no) {
		this.no = no;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public String getDeviceAddr() {
		return deviceAddr;
	}
	public void setDeviceAddr(String deviceAddr) {
		this.deviceAddr = deviceAddr;
	}
	public String getShopPw() {
		return shopPw;
	}
	public void setShopPw(String shopPw) {
		this.shopPw = shopPw;
	}
	public String getCardPrice() {
		return cardPrice;
	}
	public void setCardPrice(String cardPrice) {
		this.cardPrice = cardPrice;
	}
	public String getCardMinPrice() {
		return cardMinPrice;
	}
	public void setCardMinPrice(String cardMinPrice) {
		this.cardMinPrice = cardMinPrice;
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
	public Integer getDefaultBonusNo() {
		return defaultBonusNo;
	}
	public void setDefaultBonusNo(Integer defaultBonusNo) {
		this.defaultBonusNo = defaultBonusNo;
	}
	public String getAutoChargeEnable() {
		return autoChargeEnable;
	}
	public void setAutoChargeEnable(String autoChargeEnable) {
		this.autoChargeEnable = autoChargeEnable;
	}
	public String getAutoChargePrice() {
		return autoChargePrice;
	}
	public void setAutoChargePrice(String autoChargePrice) {
		this.autoChargePrice = autoChargePrice;
	}
	public String getRfReaderType() {
		return rfReaderType;
	}
	public void setRfReaderType(String rfReaderType) {
		this.rfReaderType = rfReaderType;
	}
	public String getShopNo() {
		return shopNo;
	}
	public void setShopNo(String shopNo) {
		this.shopNo = shopNo;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getManagerKey() {
		return managerKey;
	}
	public void setManagerKey(String managerKey) {
		this.managerKey = managerKey;
	}
	public String getInputDate() {
		return inputDate;
	}
	public void setInputDate(String inputDate) {
		this.inputDate = inputDate;
	}
	
	@Override
	public String toString() {
		return "TouchConfigGetVO [no=" + no + ", type=" + type + ", deviceAddr=" + deviceAddr + ", shopPw=" + shopPw
				+ ", cardPrice=" + cardPrice + ", cardMinPrice=" + cardMinPrice + ", bonus1=" + bonus1 + ", bonus2="
				+ bonus2 + ", bonus3=" + bonus3 + ", bonus4=" + bonus4 + ", bonus5=" + bonus5 + ", bonus6=" + bonus6
				+ ", bonus7=" + bonus7 + ", bonus8=" + bonus8 + ", bonus9=" + bonus9 + ", bonus10=" + bonus10
				+ ", defaultBonusNo=" + defaultBonusNo + ", autoChargeEnable=" + autoChargeEnable + ", autoChargePrice="
				+ autoChargePrice + ", rfReaderType=" + rfReaderType + ", shopNo=" + shopNo + ", name=" + name
				+ ", managerKey=" + managerKey + ", inputDate=" + inputDate + "]";
	}
	
	
	
}

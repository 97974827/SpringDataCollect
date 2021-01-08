package kr.gls.myapp.device.model;

public class WashTotalVO { // 저장값 없을떄 total DB update model class 
	
	private String type;
	private Integer addr;
	private String cash;
	private String card;
	private String master;
	private String version;
	
	@Override
	public String toString() {
		return "DeviceSaveNoVO [type=" + type + ", addr=" + addr + ", cash=" + cash + ", card=" + card + ", master="
				+ master + ", version=" + version + "]";
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Integer getAddr() {
		return addr;
	}

	public void setAddr(Integer addr) {
		this.addr = addr;
	}

	public String getCash() {
		return cash;
	}

	public void setCash(String cash) {
		this.cash = cash;
	}

	public String getCard() {
		return card;
	}

	public void setCard(String card) {
		this.card = card;
	}

	public String getMaster() {
		return master;
	}

	public void setMaster(String master) {
		this.master = master;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}
	
	
}

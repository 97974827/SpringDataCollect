package kr.gls.myapp.common;


public class ShopInfoVO {
	
	private String name;
	private String tel;
	private String addr;
	private String ceo;
	private String business_number;
	private String manager_key;
	private String auth_code;
	private String dc_version;
	
	@Override
	public String toString() {
		return "ShopInfoVO [name=" + name + ", tel=" + tel + ", addr=" + addr + ", ceo=" + ceo + ", business_number="
				+ business_number + ", manager_key=" + manager_key + ", auth_code=" + auth_code + ", dc_version="
				+ dc_version + "]";
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public String getAddr() {
		return addr;
	}

	public void setAddr(String addr) {
		this.addr = addr;
	}

	public String getCeo() {
		return ceo;
	}

	public void setCeo(String ceo) {
		this.ceo = ceo;
	}

	public String getBusiness_number() {
		return business_number;
	}

	public void setBusiness_number(String business_number) {
		this.business_number = business_number;
	}

	public String getManager_key() {
		return manager_key;
	}

	public void setManager_key(String manager_key) {
		this.manager_key = manager_key;
	}

	public String getAuth_code() {
		return auth_code;
	}

	public void setAuth_code(String auth_code) {
		this.auth_code = auth_code;
	}

	public String getDc_version() {
		return dc_version;
	}

	public void setDc_version(String dc_version) {
		this.dc_version = dc_version;
	}
	
		
}

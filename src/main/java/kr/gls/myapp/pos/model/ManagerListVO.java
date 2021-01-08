package kr.gls.myapp.pos.model;

public class ManagerListVO {
	
	private Integer manager_no;
	private String manager_name;
	private String manager_id;
	private String manager_encrypt;
	
	@Override
	public String toString() {
		return "ManagerListVO [manager_no=" + manager_no + ", manager_name=" + manager_name + ", manager_id="
				+ manager_id + ", manager_encrypt=" + manager_encrypt + "]";
	}
	
	public Integer getManager_no() {
		return manager_no;
	}

	public void setManager_no(Integer manager_no) {
		this.manager_no = manager_no;
	}

	public String getManager_name() {
		return manager_name;
	}

	public void setManager_name(String manager_name) {
		this.manager_name = manager_name;
	}

	public String getManager_encrypt() {
		return manager_encrypt;
	}

	public void setManager_encrypt(String manager_encrypt) {
		this.manager_encrypt = manager_encrypt;
	}

	public String getManager_id() {
		return manager_id;
	}

	public void setManager_id(String manager_id) {
		this.manager_id = manager_id;
	}


}	

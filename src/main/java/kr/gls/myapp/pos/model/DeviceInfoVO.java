package kr.gls.myapp.pos.model;

public class DeviceInfoVO {

	private Integer no;
	private String type;
	private String device_name;
	
	@Override
	public String toString() {
		return "DeviceInfoVO [no=" + no + ", type=" + type + ", device_name=" + device_name + "]";
	}

	public Integer getNo() {
		return no;
	}

	public void setNo(Integer no) {
		this.no = no;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDevice_name() {
		return device_name;
	}

	public void setDevice_name(String device_name) {
		this.device_name = device_name;
	}
	
	
	
}

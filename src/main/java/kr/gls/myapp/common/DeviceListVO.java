package kr.gls.myapp.common;

public class DeviceListVO {
	
	private Integer no;
	private Integer type;
	private String addr;
	private String ip;
	
	@Override
	public String toString() {
		return "DeviceListVO [no=" + no + ", type=" + type + ", addr=" + addr + ", ip=" + ip + "]";
	}
	
	public int getNo() {
		return no;
	}
	public void setNo(int no) {
		this.no = no;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getAddr() {
		return addr;
	}
	public void setAddr(String addr) {
		this.addr = addr;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
}

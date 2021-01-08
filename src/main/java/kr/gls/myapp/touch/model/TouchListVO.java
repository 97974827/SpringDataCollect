package kr.gls.myapp.touch.model;

// 장비 리스트 : 클래스명, 위치 변경 필요 
public class TouchListVO {
	
	private int no;
	private int type;
	private String addr;
	private String ip;
	
	@Override
	public String toString() {
		return "TouchListVO [no=" + no + ", type=" + type + ", addr=" + addr + ", ip=" + ip + "]";
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

package kr.gls.myapp.pos.model;

public class CrcVO {
	
	private Integer no;
	private String crc;
	
	
	@Override
	public String toString() {
		return "CrcVO [no=" + no + ", crc=" + crc + "]";
	}


	public Integer getNo() {
		return no;
	}


	public void setNo(Integer no) {
		this.no = no;
	}


	public String getCrc() {
		return crc;
	}


	public void setCrc(String crc) {
		this.crc = crc;
	}
	
	
	
}

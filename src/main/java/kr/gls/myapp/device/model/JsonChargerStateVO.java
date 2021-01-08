package kr.gls.myapp.device.model;

// 충전장비 동작상태 모델 클래스 - 클라이언트 json 리턴용
public class JsonChargerStateVO {
	
	private Integer device_type; // 장비 타입
	private String device_addr;  // 기기 주소 
	private String connect; 	 // 통신상태 (성공:1, 실패:0)
	private String charge; 		 // 당일충전금액
	private Integer count; 		 // 카드 발급수
	
	@Override
	public String toString() {
		return "ChargerStateVO [device_type=" + device_type + ", device_addr=" + device_addr + ", connect=" + connect
				+ ", charge=" + charge + ", count=" + count + "]";
	}

	public Integer getDevice_type() {
		return device_type;
	}

	public void setDevice_type(Integer device_type) {
		this.device_type = device_type;
	}

	public String getDevice_addr() {
		return device_addr;
	}

	public void setDevice_addr(String device_addr) {
		this.device_addr = device_addr;
	}

	public String getConnect() {
		return connect;
	}

	public void setConnect(String connect) {
		this.connect = connect;
	}

	public String getCharge() {
		return charge;
	}

	public void setCharge(String charge) {
		this.charge = charge;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}
	
	
}

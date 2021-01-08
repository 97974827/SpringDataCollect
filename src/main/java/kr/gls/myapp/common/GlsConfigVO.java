package kr.gls.myapp.common;

import org.springframework.stereotype.Component;

import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortList;

// 데이터 수집장치 설정값
@Component // 객체 의존성 모델에 꼭 써야함 
public class GlsConfigVO {
	
	// 기본 장비 타입 
	private final Integer self = 0;
	private final Integer air = 1;
	private final Integer mate = 2;
	private final Integer charger = 3;
	private final Integer coin = 4;
	private final Integer bill = 5;
	private final Integer touch = 6;
	private final Integer kiosk = 7;
	private final Integer pos = 8;
	private final Integer reader = 9;
	private final Integer garage = 10;
	
	private String dc_version = "2.0.3"; // 파이썬 1.0.3 -> 스프링 2.0.3
	private boolean enable_card = false; // 카드사용여부
	private final Integer default_bonus = 2;
	
	private String manager_no = "01"; // 공급업체 번호 - 디바이스 통신 간 사용할 변수
	
	private SerialPort serialPort; // 시리얼 포트 객체
	
	// 각 장비 수량 저장  - /get_pos_config
	private Integer selfCount;
	private Integer airCount;
	private Integer mateCount;
	private Integer chargerCount;
	private Integer coinCount;
	private Integer billCount;
	private Integer touchCount;
	private Integer kioskCount;
	private Integer readerCount;
	private Integer garageCount;
	
	
	@Override
	public String toString() {
		return "GlsConfigVO [self=" + self + ", air=" + air + ", mate=" + mate + ", charger=" + charger + ", coin="
				+ coin + ", bill=" + bill + ", touch=" + touch + ", kiosk=" + kiosk + ", pos=" + pos + ", reader="
				+ reader + ", garage=" + garage + ", dc_version=" + dc_version + ", enable_card=" + enable_card
				+ ", default_bonus=" + default_bonus + ", manager_no=" + manager_no + ", serialPort=" + serialPort
				+ ", selfCount=" + selfCount + ", airCount=" + airCount + ", mateCount=" + mateCount + ", chargerCount="
				+ chargerCount + ", coinCount=" + coinCount + ", billCount=" + billCount + ", touchCount=" + touchCount
				+ ", kioskCount=" + kioskCount + ", readerCount=" + readerCount + ", garageCount=" + garageCount + "]";
	}
	
	public GlsConfigVO() {
		connectSerial();
	}

	// 시리얼 포트 열기 (jssc) 
	public void connectSerial() {
		try {
            serialPort = new SerialPort("/dev/ttyS0");
            if (!serialPort.isOpened()) {
//            	System.out.println("--- Serial Open --- ");
                serialPort.openPort();//Open serial port
                serialPort.setParams(SerialPort.BAUDRATE_9600,
                		SerialPort.DATABITS_8,
                		SerialPort.STOPBITS_1,
                		SerialPort.PARITY_NONE);
            } else {
//            	 String[] portNames = SerialPortList.getPortNames();
//            	 System.out.println(portNames);
            }
             
        } catch (Exception e) {
            e.printStackTrace();
        } 
	}
	
	// 시리얼 포트 닫기
	public void closeSerial() {
		try {
//    		System.out.println("--- Serial Close --- ");
    		if (!serialPort.isOpened()) { serialPort.closePort(); }
    	} catch(Exception e) {
    		e.printStackTrace();
    	}
	}
	
	public String getManager_no() {
		return manager_no;
	}

	public void setManager_no(String manager_no) {
		this.manager_no = manager_no;
	}

	public String getDc_version() {
		return dc_version;
	}
	public Integer getDefault_bonus() {
		return default_bonus;
	}

	public void setDc_version(String dc_version) {
		this.dc_version = dc_version;
	}
	public boolean isEnable_card() {
		return enable_card;
	}
	public void setEnable_card(boolean enable_card) {
		this.enable_card = enable_card;
	}
	public Integer getSelf() {
		return self;
	}
	public Integer getAir() {
		return air;
	}
	public Integer getMate() {
		return mate;
	}
	public Integer getCharger() {
		return charger;
	}
	public Integer getCoin() {
		return coin;
	}
	public Integer getBill() {
		return bill;
	}
	public Integer getTouch() {
		return touch;
	}
	public Integer getKiosk() {
		return kiosk;
	}
	public Integer getPos() {
		return pos;
	}
	public Integer getReader() {
		return reader;
	}
	public Integer getGarage() {
		return garage;
	}

	public SerialPort getSerialPort() {
		return serialPort;
	}

	public void setSerialPort(SerialPort serialPort) {
		this.serialPort = serialPort;
	}

	public Integer getSelfCount() {
		return selfCount;
	}

	public void setSelfCount(Integer selfCount) {
		this.selfCount = selfCount;
	}

	public Integer getAirCount() {
		return airCount;
	}

	public void setAirCount(Integer airCount) {
		this.airCount = airCount;
	}

	public Integer getMateCount() {
		return mateCount;
	}

	public void setMateCount(Integer mateCount) {
		this.mateCount = mateCount;
	}

	public Integer getChargerCount() {
		return chargerCount;
	}

	public void setChargerCount(Integer chargerCount) {
		this.chargerCount = chargerCount;
	}

	public Integer getCoinCount() {
		return coinCount;
	}

	public void setCoinCount(Integer coinCount) {
		this.coinCount = coinCount;
	}

	public Integer getBillCount() {
		return billCount;
	}

	public void setBillCount(Integer billCount) {
		this.billCount = billCount;
	}

	public Integer getTouchCount() {
		return touchCount;
	}

	public void setTouchCount(Integer touchCount) {
		this.touchCount = touchCount;
	}

	public Integer getKioskCount() {
		return kioskCount;
	}

	public void setKioskCount(Integer kioskCount) {
		this.kioskCount = kioskCount;
	}

	public Integer getReaderCount() {
		return readerCount;
	}

	public void setReaderCount(Integer readerCount) {
		this.readerCount = readerCount;
	}

	public Integer getGarageCount() {
		return garageCount;
	}

	public void setGarageCount(Integer garageCount) {
		this.garageCount = garageCount;
	}
	
	
}

package kr.gls.myapp.device.model;

// 리더기 설정 관련 모델 클래스
public class ReaderConfigVO {
	
	private String device_addr;			// 주소 
	private String reader_init_money;	// 초기 동작금액
	private String reader_con_money;	// 연속 동작 금액
	private String reader_cycle_money;	// 한 사이클 금액
	private String reader_con_enable;	// 연속 동작 사용 유무
	private String reader_init_pulse;	// 초기 펄스 갯수
	private String reader_con_pulse;	// 연속 펄스 갯수
	private String reader_pulse_duty;	// 펄스 듀티 
	private String input_date;			
	
	@Override
	public String toString() {
		return "ReaderConfigVO [device_addr=" + device_addr + ", reader_init_money=" + reader_init_money
				+ ", reader_con_money=" + reader_con_money + ", reader_cycle_money=" + reader_cycle_money
				+ ", reader_con_enable=" + reader_con_enable + ", reader_init_pulse=" + reader_init_pulse
				+ ", reader_con_pulse=" + reader_con_pulse + ", reader_pulse_duty=" + reader_pulse_duty
				+ ", input_date=" + input_date + "]";
	}

	public String getDevice_addr() {
		return device_addr;
	}

	public void setDevice_addr(String device_addr) {
		this.device_addr = device_addr;
	}

	public String getReader_init_money() {
		return reader_init_money;
	}

	public void setReader_init_money(String reader_init_money) {
		this.reader_init_money = reader_init_money;
	}

	public String getReader_con_money() {
		return reader_con_money;
	}

	public void setReader_con_money(String reader_con_money) {
		this.reader_con_money = reader_con_money;
	}

	public String getReader_cycle_money() {
		return reader_cycle_money;
	}

	public void setReader_cycle_money(String reader_cycle_money) {
		this.reader_cycle_money = reader_cycle_money;
	}

	public String getReader_con_enable() {
		return reader_con_enable;
	}

	public void setReader_con_enable(String reader_con_enable) {
		this.reader_con_enable = reader_con_enable;
	}

	public String getReader_init_pulse() {
		return reader_init_pulse;
	}

	public void setReader_init_pulse(String reader_init_pulse) {
		this.reader_init_pulse = reader_init_pulse;
	}

	public String getReader_con_pulse() {
		return reader_con_pulse;
	}

	public void setReader_con_pulse(String reader_con_pulse) {
		this.reader_con_pulse = reader_con_pulse;
	}
	
	public String getReader_pulse_duty() {
		return reader_pulse_duty;
	}

	public void setReader_pulse_duty(String reader_pulse_duty) {
		this.reader_pulse_duty = reader_pulse_duty;
	}

	public String getInput_date() {
		return input_date;
	}

	public void setInput_date(String input_date) {
		this.input_date = input_date;
	}
	
	
}

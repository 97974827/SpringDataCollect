package kr.gls.myapp.device.model;

public class SelfConfigVO { // state : 1
	
	private String device_addr; 	 // 기기 주소
	
	private String self_init_money; // 셀프 초기 금액
	private String self_init_time;	 // 셀프 초기 동작 시간
	private String self_con_enable;	 // 셀프 연속 동작 유/무
	private String self_con_money;	 // 셀프 연속 동작 금액
	private String self_con_time; 	 // 셀프 연속 동작 시간
	private String self_pause_time;	 // 셀프 일시 정지 시간
	
	private String foam_enable;		 // 폼 사용 유/무
    private String foam_con_enable;	 // 폼 연속 동작 유/무
    private String foam_speedier;	 // 폼 배속제
    private String foam_init_money; // 폼 초기 동작 금액
    private String foam_init_time;   // 폼 초기 동작 시간
    private String foam_con_money;  // 폼 연속 동작 금액
    private String foam_con_time;    // 폼 연속 동작 시간
    private String foam_pause_time;  // 폼 일시 정지 시간 
    private String foam_end_delay;   // 폼 종료 딜레이 시간 
    
    private String under_enable;	 // 하부 사용 유/무
    private String under_con_enable; // 하부 연속 동작 유/무
    private String under_speedier;	 // 하부 배속제
    private String under_init_money;// 하부 초기 동작금액
    private String under_init_time;  // 하부 초기 동작시간
    private String under_con_money; // 하부 연속 동작 금액
    private String under_con_time;   // 하부 연속 동작 시간
    private String under_pause_time; // 하부 일시정지 시간 
    
    private String coating_enable;		// 코딩 사용 유/무
    private String coating_con_enable;	// 코팅 연속 동작 유/무
    private String coating_speedier;	// 코딩 배속제
    private String coating_init_money;	// 코팅 초기 동작 금액
    private String coating_init_time;	// 코팅 초기 동작 시간
    private String coating_con_money;	// 코팅 연속 동작 금액
    private String coating_con_time;	// 코팅 연속 동작 시간
    private String coating_pause_time;	// 코팅 일시 정지 시간 
    
    private String cycle_money;		// 한 사이클 금액
    private String pay_free;		// 요금 유료:1/무료:0
    private String buzzer_time;		// 부저 동작 시간
    private String pause_count;		// 일시 정지 횟수 
    private String secret_enable;	// 비밀 모드 사용여부
    private String speedier_enable; // 결제방식 (정액:0 / 배속:1)
    private String secret_date;		// 비밀기능 일자
	private String use_type; 	    // 동작방식 (터치식:0/거치식:1)
	private String set_coating_output;	// 코딩출력 (0:코팅, 1:코팅+고압)
	private String wipping_enable;	// 위핑사용 유무 (1/0)
	private String wipping_temperature;	// 위핑온도
	private String input_date;		// 입력날짜
	
	@Override
	public String toString() {
		return "SelfConfigVO [device_addr=" + device_addr + ", self_init_money=" + self_init_money + ", self_init_time="
				+ self_init_time + ", self_con_enable=" + self_con_enable + ", self_con_money=" + self_con_money
				+ ", self_con_time=" + self_con_time + ", self_pause_time=" + self_pause_time + ", foam_enable="
				+ foam_enable + ", foam_con_enable=" + foam_con_enable + ", foam_speedier=" + foam_speedier
				+ ", foam_init_money=" + foam_init_money + ", foam_init_time=" + foam_init_time + ", foam_con_money="
				+ foam_con_money + ", foam_con_time=" + foam_con_time + ", foam_pause_time=" + foam_pause_time
				+ ", foam_end_delay=" + foam_end_delay + ", under_enable=" + under_enable + ", under_con_enable="
				+ under_con_enable + ", under_speedier=" + under_speedier + ", under_init_money=" + under_init_money
				+ ", under_init_time=" + under_init_time + ", under_con_money=" + under_con_money + ", under_con_time="
				+ under_con_time + ", under_pause_time=" + under_pause_time + ", coating_enable=" + coating_enable
				+ ", coating_con_enable=" + coating_con_enable + ", coating_speedier=" + coating_speedier
				+ ", coating_init_money=" + coating_init_money + ", coating_init_time=" + coating_init_time
				+ ", coating_con_money=" + coating_con_money + ", coating_con_time=" + coating_con_time
				+ ", coating_pause_time=" + coating_pause_time + ", cycle_money=" + cycle_money + ", pay_free="
				+ pay_free + ", buzzer_time=" + buzzer_time + ", pause_count=" + pause_count + ", secret_enable="
				+ secret_enable + ", speedier_enable=" + speedier_enable + ", secret_date=" + secret_date
				+ ", use_type=" + use_type + ", set_coating_output=" + set_coating_output + ", wipping_enable="
				+ wipping_enable + ", wipping_temperature=" + wipping_temperature + ", input_date=" + input_date + "]";
	}

	public String getDevice_addr() {
		return device_addr;
	}

	public void setDevice_addr(String device_addr) {
		this.device_addr = device_addr;
	}

	public String getSelf_init_money() {
		return self_init_money;
	}

	public void setSelf_init_money(String self_init_money) {
		this.self_init_money = self_init_money;
	}

	public String getSelf_init_time() {
		return self_init_time;
	}

	public void setSelf_init_time(String self_init_time) {
		this.self_init_time = self_init_time;
	}

	public String getSelf_con_enable() {
		return self_con_enable;
	}

	public void setSelf_con_enable(String self_con_enable) {
		this.self_con_enable = self_con_enable;
	}

	public String getSelf_con_money() {
		return self_con_money;
	}

	public void setSelf_con_money(String self_con_money) {
		this.self_con_money = self_con_money;
	}

	public String getSelf_con_time() {
		return self_con_time;
	}

	public void setSelf_con_time(String self_con_time) {
		this.self_con_time = self_con_time;
	}

	public String getSelf_pause_time() {
		return self_pause_time;
	}

	public void setSelf_pause_time(String self_pause_time) {
		this.self_pause_time = self_pause_time;
	}

	public String getFoam_enable() {
		return foam_enable;
	}

	public void setFoam_enable(String foam_enable) {
		this.foam_enable = foam_enable;
	}

	public String getFoam_con_enable() {
		return foam_con_enable;
	}

	public void setFoam_con_enable(String foam_con_enable) {
		this.foam_con_enable = foam_con_enable;
	}

	public String getFoam_speedier() {
		return foam_speedier;
	}

	public void setFoam_speedier(String foam_speedier) {
		this.foam_speedier = foam_speedier;
	}

	public String getFoam_init_money() {
		return foam_init_money;
	}

	public void setFoam_init_money(String foam_init_money) {
		this.foam_init_money = foam_init_money;
	}

	public String getFoam_init_time() {
		return foam_init_time;
	}

	public void setFoam_init_time(String foam_init_time) {
		this.foam_init_time = foam_init_time;
	}

	public String getFoam_con_money() {
		return foam_con_money;
	}

	public void setFoam_con_money(String foam_con_money) {
		this.foam_con_money = foam_con_money;
	}

	public String getFoam_con_time() {
		return foam_con_time;
	}

	public void setFoam_con_time(String foam_con_time) {
		this.foam_con_time = foam_con_time;
	}

	public String getFoam_pause_time() {
		return foam_pause_time;
	}

	public void setFoam_pause_time(String foam_pause_time) {
		this.foam_pause_time = foam_pause_time;
	}

	public String getFoam_end_delay() {
		return foam_end_delay;
	}

	public void setFoam_end_delay(String foam_end_delay) {
		this.foam_end_delay = foam_end_delay;
	}

	public String getUnder_enable() {
		return under_enable;
	}

	public void setUnder_enable(String under_enable) {
		this.under_enable = under_enable;
	}

	public String getUnder_con_enable() {
		return under_con_enable;
	}

	public void setUnder_con_enable(String under_con_enable) {
		this.under_con_enable = under_con_enable;
	}

	public String getUnder_speedier() {
		return under_speedier;
	}

	public void setUnder_speedier(String under_speedier) {
		this.under_speedier = under_speedier;
	}

	public String getUnder_init_money() {
		return under_init_money;
	}

	public void setUnder_init_money(String under_init_money) {
		this.under_init_money = under_init_money;
	}

	public String getUnder_init_time() {
		return under_init_time;
	}

	public void setUnder_init_time(String under_init_time) {
		this.under_init_time = under_init_time;
	}

	public String getUnder_con_money() {
		return under_con_money;
	}

	public void setUnder_con_money(String under_con_money) {
		this.under_con_money = under_con_money;
	}

	public String getUnder_con_time() {
		return under_con_time;
	}

	public void setUnder_con_time(String under_con_time) {
		this.under_con_time = under_con_time;
	}

	public String getUnder_pause_time() {
		return under_pause_time;
	}

	public void setUnder_pause_time(String under_pause_time) {
		this.under_pause_time = under_pause_time;
	}

	public String getCoating_enable() {
		return coating_enable;
	}

	public void setCoating_enable(String coating_enable) {
		this.coating_enable = coating_enable;
	}

	public String getCoating_con_enable() {
		return coating_con_enable;
	}

	public void setCoating_con_enable(String coating_con_enable) {
		this.coating_con_enable = coating_con_enable;
	}

	public String getCoating_speedier() {
		return coating_speedier;
	}

	public void setCoating_speedier(String coating_speedier) {
		this.coating_speedier = coating_speedier;
	}

	public String getCoating_init_money() {
		return coating_init_money;
	}

	public void setCoating_init_money(String coating_init_money) {
		this.coating_init_money = coating_init_money;
	}

	public String getCoating_init_time() {
		return coating_init_time;
	}

	public void setCoating_init_time(String coating_init_time) {
		this.coating_init_time = coating_init_time;
	}

	public String getCoating_con_money() {
		return coating_con_money;
	}

	public void setCoating_con_money(String coating_con_money) {
		this.coating_con_money = coating_con_money;
	}

	public String getCoating_con_time() {
		return coating_con_time;
	}

	public void setCoating_con_time(String coating_con_time) {
		this.coating_con_time = coating_con_time;
	}

	public String getCoating_pause_time() {
		return coating_pause_time;
	}

	public void setCoating_pause_time(String coating_pause_time) {
		this.coating_pause_time = coating_pause_time;
	}

	public String getCycle_money() {
		return cycle_money;
	}

	public void setCycle_money(String cycle_money) {
		this.cycle_money = cycle_money;
	}

	public String getPay_free() {
		return pay_free;
	}

	public void setPay_free(String pay_free) {
		this.pay_free = pay_free;
	}

	public String getBuzzer_time() {
		return buzzer_time;
	}

	public void setBuzzer_time(String buzzer_time) {
		this.buzzer_time = buzzer_time;
	}

	public String getPause_count() {
		return pause_count;
	}

	public void setPause_count(String pause_count) {
		this.pause_count = pause_count;
	}

	public String getSecret_enable() {
		return secret_enable;
	}

	public void setSecret_enable(String secret_enable) {
		this.secret_enable = secret_enable;
	}

	public String getSpeedier_enable() {
		return speedier_enable;
	}

	public void setSpeedier_enable(String speedier_enable) {
		this.speedier_enable = speedier_enable;
	}

	public String getSecret_date() {
		return secret_date;
	}

	public void setSecret_date(String secret_date) {
		this.secret_date = secret_date;
	}

	public String getUse_type() {
		return use_type;
	}

	public void setUse_type(String use_type) {
		this.use_type = use_type;
	}

	public String getSet_coating_output() {
		return set_coating_output;
	}

	public void setSet_coating_output(String set_coating_output) {
		this.set_coating_output = set_coating_output;
	}

	public String getWipping_enable() {
		return wipping_enable;
	}

	public void setWipping_enable(String wipping_enable) {
		this.wipping_enable = wipping_enable;
	}

	public String getWipping_temperature() {
		return wipping_temperature;
	}

	public void setWipping_temperature(String wipping_temperature) {
		this.wipping_temperature = wipping_temperature;
	}

	public String getInput_date() {
		return input_date;
	}

	public void setInput_date(String input_date) {
		this.input_date = input_date;
	}
	
	
	
}

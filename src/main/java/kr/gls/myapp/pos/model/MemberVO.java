package kr.gls.myapp.pos.model;

public class MemberVO {
	
	private Integer mb_no;
	private String no;  // /set_member
	private String name;
	private String mobile;
	private String car_num;
	private String card_num;
	private String group;
	private String birth;
	private String addr;
	private String vip_set;
	private String input_date; 
	private Integer level;
	private String level_name;
	
	@Override
	public String toString() {
		return "MemberVO [mb_no=" + mb_no + ", no=" + no + ", name=" + name + ", mobile=" + mobile + ", car_num="
				+ car_num + ", card_num=" + card_num + ", group=" + group + ", birth=" + birth + ", addr=" + addr
				+ ", vip_set=" + vip_set + ", input_date=" + input_date + ", level=" + level + ", level_name="
				+ level_name + "]";
	}
		
	public String getNo() {
		return no;
	}

	public void setNo(String no) {
		this.no = no;
	}

	public String getGroup() {
		return group;
	}
	
	public void setGroup(String group) {
		this.group = group;
	}

	public String getBirth() {
		return birth;
	}

	public void setBirth(String birth) {
		this.birth = birth;
	}

	public String getAddr() {
		return addr;
	}

	public void setAddr(String addr) {
		this.addr = addr;
	}

	public String getVip_set() {
		return vip_set;
	}

	public void setVip_set(String vip_set) {
		this.vip_set = vip_set;
	}

	public String getInput_date() {
		return input_date;
	}

	public void setInput_date(String input_date) {
		this.input_date = input_date;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public String getLevel_name() {
		return level_name;
	}

	public void setLevel_name(String level_name) {
		this.level_name = level_name;
	}

	public Integer getMb_no() {
		return mb_no;
	}

	public void setMb_no(Integer mb_no) {
		this.mb_no = mb_no;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getCar_num() {
		return car_num;
	}

	public void setCar_num(String car_num) {
		this.car_num = car_num;
	}

	public String getCard_num() {
		return card_num;
	}

	public void setCard_num(String card_num) {
		this.card_num = card_num;
	}
	
	
}

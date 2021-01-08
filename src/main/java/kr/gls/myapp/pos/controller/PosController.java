package kr.gls.myapp.pos.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import kr.gls.myapp.common.ResponseDataSet;
import kr.gls.myapp.common.ResultDataSet;
import kr.gls.myapp.device.model.SelfConfigVO;
import kr.gls.myapp.pos.model.MasterConfigGetVO;
import kr.gls.myapp.pos.model.MasterConfigSetVO;
import kr.gls.myapp.pos.model.MemberVO;
import kr.gls.myapp.pos.model.PosConfigVO;
import kr.gls.myapp.pos.model.SetChargeVO;
import kr.gls.myapp.pos.service.IPosCardService;
import kr.gls.myapp.pos.service.IPosConfigService;
import kr.gls.myapp.pos.service.IPosMemberService;
import kr.gls.myapp.pos.service.IPosSalesService;

@RestController
public class PosController {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private IPosConfigService posConfigService; // 설정관련 서비스
	
	@Autowired
	private IPosSalesService posSalesService; // 매출관련 서비스
	
	@Autowired
	private IPosCardService posCardService; // 카드관련 서비스
	
	@Autowired
	private IPosMemberService posMemberService; // 회원관련 서비스
	
	
	// 포스 설정 셋팅 요청
//	@PostMapping(path="/set_pos_config", consumes={ MediaType.APPLICATION_JSON_VALUE })
//	public void setPosConfig(@RequestBody PosConfigVO params) {
//		System.out.println("/set_pos_config POST 요청 : " + params.toString());
//		posConfigService.setPosConfig();
//	}
	
	// 포스 설정 셋팅 요청
	@PostMapping(path="/set_pos_config", consumes={ MediaType.APPLICATION_FORM_URLENCODED_VALUE })
	public Map<String, Object> setPosConfig(PosConfigVO params) {
		logger.info("/set_pos_config POST 요청");
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("result", posConfigService.setPosConfig(params));
		return map;
	}
	
	// 포스 설정 불러오기 요청
	@PostMapping("/get_pos_config")
	public Map<String, Map<String, Object>> getPosConfig() {
		logger.info("/get_pos_config POST 요청");
		Map<String, Map<String, Object>> map = new HashMap<>(); // 반환값 저장할 맵 (key:"pos_config")
		map.put("pos_config", posConfigService.getPosConfig());
		return map;
	}
	
	// 월간 매출 자료 조회 요청
	@PostMapping(path="/get_monthly_sales", consumes={ MediaType.APPLICATION_FORM_URLENCODED_VALUE })
	public ResponseDataSet getMonthlySales(String year, String month) {
		logger.info("/get_monthly_sales POST 요청 ");
		return new ResponseDataSet(posSalesService.getMonthlySales(year, month));
	}
	
	// 일간 매출 자료 조회 요청
	@PostMapping(path="/get_days_sales", consumes={ MediaType.APPLICATION_FORM_URLENCODED_VALUE })
	public ResponseDataSet getDaysSales(String year, String month, int days) {
		logger.info("/get_days_sales POST 요청 ");
		return new ResponseDataSet(posSalesService.getDaysSales(year, month, days));
	}
	
	// 기기별 매출 자료 조회 요청
	@PostMapping(path="/get_device_sales", consumes={ MediaType.APPLICATION_FORM_URLENCODED_VALUE })
	public ResponseDataSet getDeviceSales(String year, String month, int days) {
		logger.info("/get_device_sales POST 요청 ");
		return new ResponseDataSet(posSalesService.getDeviceSales(year, month, days));
	}
	
	// 오늘자 메인화면 세차장비 매출
	@PostMapping("/get_today_device_sales")
	public ResponseDataSet getTodayDeviceSales() {
		logger.info("/get_today_device_sales POST 요청 ");
		return new ResponseDataSet(posSalesService.getTodayDeviceSales());
	}
	
	// 오늘자 메인화면 충전장비 오늘자 매출
	@PostMapping("/get_today_charger_sales")
	public ResponseDataSet getTodayChargerSales() {
		logger.info("/get_today_charger_sales POST 요청 ");
		return new ResponseDataSet(posSalesService.getTodayChargerSales());
	}
	
	// 오늘자 메인화면 매출 총계
	@PostMapping("/get_today_sales_total")
	public Map<String, Map<String, Object>> getTodayrSalesTotal() {
		logger.info("/get_today_sales_total POST 요청 ");
		Map<String, Map<String, Object>> map = new HashMap<>(); // 반환값 저장할 맵 (key:"pos_config")
		map.put("result", posSalesService.getTodaySalesTotal());
		return map;
	}
	
	// 장비 이력 조회 요청
	@PostMapping("/get_use_device")
	public ResponseDataSet getUseDevice() {
		logger.info("/get_use_device POST 요청 ");
		return new ResponseDataSet(posSalesService.getUseDevice());
	}
	
	// 장비 세부 이력 조회 요청
	@PostMapping(path="/get_use_device_detail", consumes={ MediaType.APPLICATION_FORM_URLENCODED_VALUE })
	public ResponseDataSet getUseDeviceDetail(Integer device_type, String device_addr) {
		logger.info("/get_use_device_detail POST 요청 ");
		return new ResponseDataSet(posSalesService.getUseDeviceDetail(device_type, device_addr));
	}
	
	// 카드 등록 요청
	@PostMapping(path="/set_card", consumes={ MediaType.APPLICATION_FORM_URLENCODED_VALUE })
	public Map<String, Object> setCard(String card_num) {
		logger.info("/set_card POST 요청 ");
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("result", posCardService.setCard(card_num));
		return result;
	}
	
	// 카드 읽기요청
	@PostMapping(path="/read_card", consumes={ MediaType.APPLICATION_FORM_URLENCODED_VALUE })
	public ResponseDataSet readCard(String card_num) {
		logger.info("/read_card POST 요청 ");
		return new ResponseDataSet(posCardService.readCard(card_num));
	}
	
	// 카드 이력 조회 요청
	@PostMapping("/get_card_history")
	public ResponseDataSet getCardHistory() {
		logger.info("/get_card_history POST 요청 ");
		return new ResponseDataSet(posCardService.getCardHistory());
	}
	
	// 카드 이력 상세 조회 요청 
	@PostMapping(path="/get_card_history_detail", consumes={ MediaType.APPLICATION_FORM_URLENCODED_VALUE })
	public ResponseDataSet getCardHistoryDetail(String card_num) {
		logger.info("/get_card_history_detail POST 요청 ");
		return new ResponseDataSet(posCardService.getCardHistoryDetail(card_num));
	}
	
	// 정지 카드 조회 요청
	@PostMapping("/get_black_card")
	public ResponseDataSet getBlackCard() {
		logger.info("/get_black_card POST 요청 ");
		return new ResponseDataSet(posCardService.getBlackCard());
	}
	
	// 정지 카드 등록 요청
	@PostMapping(path="/set_black_card", consumes={ MediaType.APPLICATION_FORM_URLENCODED_VALUE })
	public Map<String, Object> setBlackCard(String card_num, String content) {
		logger.info("/set_black_card POST 요청 ");
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("result", posCardService.setBlackCard(card_num, content));
		return result;
	}
	
	// 정지 카드 해제
	@PostMapping(path="/delete_black_card", consumes={ MediaType.APPLICATION_FORM_URLENCODED_VALUE })
	public Map<String, Object> deleteBlackCard(String no) {
		logger.info("/delete_black_card POST 요청 ");
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("result", posCardService.deleteBlackCard(no));
		return result;
	}
	
	// 회원 목록 조회 요청
	@PostMapping("/get_member_list")
	public ResponseDataSet getMemberList() {
		logger.info("/get_member_list POST 요청 ");
		return new ResponseDataSet(posMemberService.getMemberList());
	}
	
	// 회원 레벨 조회 요청
	@PostMapping("/get_member_level")
	public ResponseDataSet getMemberLevel() {
		logger.info("/get_member_level POST 요청 ");
		return new ResponseDataSet(posMemberService.getMemberLevel());
	}
	
	// 회원 상세 조회 요청
	@PostMapping(path="/get_member_detail", consumes={ MediaType.APPLICATION_FORM_URLENCODED_VALUE })
	public Map<String, Map<String, Object>> getMemberDetail(String mb_no) {
		logger.info("/get_member_detail POST 요청 ");
		Map<String, Map<String, Object>> map = new HashMap<>(); 
		map.put("result", posMemberService.getMemberDetail(mb_no));
		return map;
	}
	
	// 회원 등록 및 수정 요청 
	@PostMapping(path="/set_member", consumes={ MediaType.APPLICATION_FORM_URLENCODED_VALUE })
	public Map<String, Object> setMember(MemberVO params) {
		logger.info("/set_member POST 요청 ");
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("result", posMemberService.setMember(params));
		return result;
	}
	
	// 회원 삭제 요청
	@PostMapping(path="/delete_member", consumes={ MediaType.APPLICATION_FORM_URLENCODED_VALUE })
	public Map<String, Object> deleteMember(String no) {
		logger.info("/delete_member POST 요청 ");
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("result", posMemberService.deleteMember(no));
		return result;
	}
	
	// 회원 이력 요청
	@PostMapping("/get_member_history")
	public ResponseDataSet getMemberHistory() {
		logger.info("/get_member_history POST 요청 ");
		return new ResponseDataSet(posMemberService.getMemberHistory());
	}
	
	// 회원 상세 이력 요청 
	@PostMapping(path="/get_member_history_detail", consumes={ MediaType.APPLICATION_FORM_URLENCODED_VALUE })
	public ResponseDataSet getMemberHistoryDetail(String mb_no) {
		logger.info("/get_member_history_detail POST 요청 ");
		return new ResponseDataSet(posMemberService.getMemberHistoryDetail(mb_no));
	}
	
	// 회원 검색 요청 
	@PostMapping(path="/search_member", consumes={ MediaType.APPLICATION_FORM_URLENCODED_VALUE })
	public ResponseDataSet searchMember(String name, String mobile, String car_num, String card_num) {
		logger.info("/search_member POST 요청 ");
		return new ResponseDataSet(posMemberService.searchMember(name, mobile, car_num, card_num));
	}
	
	// 카드 충전 요청 
	@PostMapping(path="/set_charge", consumes={ MediaType.APPLICATION_FORM_URLENCODED_VALUE })
	public Map<String, Object> setCharge(SetChargeVO params) {
		logger.info("/set_charge POST 요청 ");
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("result", posCardService.setCharge(params));
		return result;
	}
	
	// 카드 검색 요청 
	@PostMapping(path="/search_card", consumes={ MediaType.APPLICATION_FORM_URLENCODED_VALUE })
	public ResponseDataSet searchCard(String card_num, String mb_no, String end_time) {
		logger.info("/search_card POST 요청 ");
		return new ResponseDataSet(posCardService.searchCard(card_num, mb_no, end_time));
	}
	
	// 카드이력 초기화 요청 
	@PostMapping(path="/reset_card_history", consumes={ MediaType.APPLICATION_FORM_URLENCODED_VALUE })
	public Map<String, Object> resetCardHistory(String card_num) {
		logger.info("/reset_card_history POST 요청 ");
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("result", posCardService.resetCardHistory(card_num));
		return result;
	}
	
	// 장비 전체이력 초기화 요청 
	@PostMapping("/reset_device_history")
	public Map<String, Object> resetDeviceHistory() {
		logger.info("/reset_device_history POST 요청 ");
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("result", posCardService.resetDeviceHistory());
		return result;
	}
	
	// 관리 업체 정보 불러오기 요청
	@PostMapping("/get_manager_info")
	public ResponseDataSet getManagerInfo() {
		logger.info("/get_manager_info POST 요청 ");
		return new ResponseDataSet(posConfigService.getManagerInfo());
	}
	
	// CRC 불러오기 요청
	@PostMapping("/get_crc")
	public ResponseDataSet getCrc() {
		logger.info("/get_crc POST 요청 ");
		return new ResponseDataSet(posConfigService.getCrcTable());
	}
	
	// 등록된 장비 목록 불러오기
	@PostMapping("/get_device_list")
	public ResponseDataSet getDeviceList() {
		logger.info("/get_device_list POST 요청 ");
		return new ResponseDataSet(posConfigService.getDeviceList());
	}
	
	// 실시간 모니터링 (LAN) 요청
	@PostMapping("/get_lan_device_state")
	public ResponseDataSet getLanDeviceState() {
		logger.info("/get_lan_device_state POST 요청 ");
		return new ResponseDataSet(posSalesService.getLanDeviceState());
	}
	
//	// TODO 우수회원 보너스 가져오기 요청 - 이용 안함 
//	@PostMapping(path="/get_vip_bonus", consumes={ MediaType.APPLICATION_FORM_URLENCODED_VALUE })
//	public ResponseDataSet getVipBonus(String card_num) {
//		logger.info("/get_vip_bonus POST 요청 : ");
//		return new ResponseDataSet(posConfigService.getVipBonus(card_num));		
//	}
	
	// 멤버 보너스 설정 불러오기 요청 
	@PostMapping("/get_member_bonus")
	public ResponseDataSet getMemberBonus() {
		logger.info("/get_member_bonus POST 요청 ");
		return new ResponseDataSet(posMemberService.getMemberBonus());
	}
	
	// 멤버 보너스 설정 요청
	@PostMapping(path="/set_member_bonus", consumes={ MediaType.APPLICATION_FORM_URLENCODED_VALUE })
	public Map<String, Object> setMemberBonus(@RequestParam Map<String, Object> params) {
		logger.info("/set_member_bonus POST 요청 ");
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("result", posMemberService.setMemberBonus(params));
		return result;
	}
	
	// 마스터 카드 이력 요청 
	@PostMapping("/get_master_card_history")
	public ResponseDataSet getMasterCardHistory() {
		logger.info("/get_master_card_history POST 요청");
		return new ResponseDataSet(posSalesService.getMasterCardHistory());
	}
	
	// 공급업체 리스트 요청
	@PostMapping("/get_manager_list")
	public ResponseDataSet getManagerList() {
		logger.info("/get_manager_list POST 요청");
		return new ResponseDataSet(posConfigService.getManagerList());
	}
	
	// 마스터 설정 불러오기 요청
	@PostMapping(path="/get_master_config", consumes={ MediaType.APPLICATION_FORM_URLENCODED_VALUE })
	public ResponseDataSet getMasterConfig(MasterConfigGetVO master) {
		logger.info("/get_master_config POST 요청 : ");
		return new ResponseDataSet(posConfigService.getMasterConfig(master));		
	}
	
	// 마스터 설정 셋팅 요청
	@PostMapping(path="/set_master_config", consumes={ MediaType.APPLICATION_FORM_URLENCODED_VALUE })
	public Map<String, Object> setMasterConfig(@RequestParam Map<String, Object> params) {
		logger.info("/set_master_config POST 요청 : ");
		Map<String, Object> map = new HashMap<>();
		map.put("result", posConfigService.setMasterConfig(params));
		return map;
	}
	
	// 히든 설정 불러오기 요청
	@PostMapping(path="/get_hidden_config", consumes={ MediaType.APPLICATION_FORM_URLENCODED_VALUE })
	public ResponseDataSet getHiddenConfig(SelfConfigVO params) { // 주소값 받아옴
		logger.info("/get_hidden_config POST 요청");
		return new ResponseDataSet(posConfigService.getHiddenConfig(params));	
	}
	
	// 히든 설정 요청 
	@PostMapping(path="/set_hidden_config", consumes={ MediaType.APPLICATION_FORM_URLENCODED_VALUE })
	public Map<String, Object> setHiddenConfig(@RequestParam Map<String, Object> params) {
		logger.info("/set_hidden_config POST 요청");
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("result", posConfigService.setHiddenConfig(params));
		return map;
	}
	
	// 셀프 주소 추출 요청 
	@PostMapping("/get_self_list")
	public ResponseDataSet getSelfList() {
		logger.info("/get_self_list POST 요청");
		return new ResponseDataSet(posConfigService.getSelfList());
	}
	
}

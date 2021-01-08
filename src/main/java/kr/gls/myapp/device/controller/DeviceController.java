package kr.gls.myapp.device.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.gls.myapp.common.GlsConfigVO;
import kr.gls.myapp.common.ResponseDataSet;
import kr.gls.myapp.common.ResultDataSet;
import kr.gls.myapp.device.model.ChargerConfigVO;
import kr.gls.myapp.device.model.ReaderConfigVO;
import kr.gls.myapp.device.model.SelfConfigVO;
import kr.gls.myapp.device.service.IDeviceService;

@RestController
public class DeviceController {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass()); 
	
	@Autowired
	private IDeviceService service;
	
	@Autowired
	private GlsConfigVO glsConfig;
	
	// 실시간 485 통신 요청 
	// 기존 프로그램 요청목적  : 스레드 감시용도  
	@PostMapping("/start_thread")
	public void startThread() {
		logger.info("/start_thread POST 요청");
		service.startThread();
	}
	
	// 실시간 모니터링 요청
	@PostMapping("/get_device_state")
	public ResponseDataSet getDeviceState() {
		logger.info("/get_device_state POST 요청");
		return new ResponseDataSet(service.getDeviceState());
	}
	
	// 셀프 설정 불러오기 요청
	@PostMapping("/get_self_config")
	public ResponseDataSet getSelfConfig() {
		logger.info("/get_self_config POST 요청");
		return new ResponseDataSet(service.getSelfConfig());
	}
	
	// 셀프 설정 요청
	@PostMapping(path="/set_self_config", consumes={ MediaType.APPLICATION_FORM_URLENCODED_VALUE })
	public Map<String, Object> setSelfConfig(SelfConfigVO params) {
		logger.info("/set_self_config POST 요청");
		Map<String, Object> map = new HashMap<>();
		map.put("result", service.setSelfConfig(params));
		return map;
	}
	
	// 리더기 설정 불러오기 요청
	@PostMapping("/get_reader_config")
	public ResponseDataSet getReaderConfig() {
		logger.info("/get_reader_config POST 요청");
		return new ResponseDataSet(service.getReaderConfig());
	}
	
	// 셀프 설정 요청
	@PostMapping(path="/set_reader_config", consumes={ MediaType.APPLICATION_FORM_URLENCODED_VALUE })
	public Map<String, Object> setReaderConfig(ReaderConfigVO params) {
		logger.info("/set_reader_config POST 요청");
		Map<String, Object> map = new HashMap<>();
		map.put("result", service.setReaderConfig(params));
		return map;
	}
	
	// 충전기 설정 불러오기 요청
	@PostMapping("/get_charger_config")
	public ResponseDataSet getChargerConfig() {
		logger.info("/get_charger_config POST 요청");
		return new ResponseDataSet(service.getChargerConfig());
	}
	
	// 충전기 설정 요청
	@PostMapping(path="/set_charger_config", consumes={ MediaType.APPLICATION_FORM_URLENCODED_VALUE })
	public Map<String, Object> setChargerConfig(ChargerConfigVO params) {
		logger.info("/set_charger_config POST 요청");
		Map<String, Object> map = new HashMap<>();
		map.put("result", service.setChargerConfig(params));
		return map;
	}
	
	// 기기 주소 변경 요청
	@PostMapping(path="/change_device_addr", consumes={ MediaType.APPLICATION_FORM_URLENCODED_VALUE })
	public Map<String, Object> changeDeviceAddr(String device_type, String before_addr, String after_addr) {
		logger.info("/change_device_addr POST 요청");
		Map<String, Object> map = new HashMap<>();
		map.put("result", service.changeDeviceAddr(device_type, before_addr, after_addr));
		return map;
	} 
	
	// 누적 금액 초기화 요청 
	@PostMapping(path="/reset_total_money", consumes={ MediaType.APPLICATION_FORM_URLENCODED_VALUE })
	public Map<String, Object> resetTotalMoney(String device_type, String device_addr) {
		logger.info("/reset_total_money POST 요청");
		Map<String, Object> map = new HashMap<>();
		map.put("result", service.resetTotalMoney(device_type, device_addr));
		return map;
	}
	
	// 세차장 ID 변경 요청
	@PostMapping(path="/update_shop_no", consumes={ MediaType.APPLICATION_FORM_URLENCODED_VALUE })
	public Map<String, Object> updateShopNo(String device_type, String device_addr) {
		logger.info("/update_shop_no POST 요청");
		Map<String, Object> map = new HashMap<>();
		map.put("result", service.updateShopNo(device_type, device_addr));
		return map;
	}
	
	// 장비 설정 복사
	@PostMapping(path="/copy_device_config", consumes={ MediaType.APPLICATION_FORM_URLENCODED_VALUE })
	public Map<String, Object> copyDeviceConfig(String device_type, String current_device_addr, String copy_device_addr) {
		logger.info("/copy_device_config POST 요청");
		Map<String, Object> map = new HashMap<>();
		map.put("result", service.copyDeviceConfig(device_type, current_device_addr, copy_device_addr));
		return map;
	}
	
}

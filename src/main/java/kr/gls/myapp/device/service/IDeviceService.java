package kr.gls.myapp.device.service;

import java.util.List;
import java.util.Map;

import kr.gls.myapp.device.model.ChargerConfigVO;
import kr.gls.myapp.device.model.ReaderConfigVO;
import kr.gls.myapp.device.model.SelfConfigVO;

public interface IDeviceService {

	// 스레드 시작
	void startThread();
	
	// 시간 메서드 연결 
	void selectTimeMapper(String[] addrList, Integer type);
	// 시간 호출
	void setTime(String deviceType, String deviceAddr);
	
	String currentClockFormat(); 	// 현재시간 데이터 포매팅
	
	// 실시간 모니터링
	List<Map<String, Object>> getDeviceState();
	void writeState(String deviceType, String deviceAddr); // 시리얼 쓰기
	Map<String, Object> readState();	// 시리얼 읽기
	void writeOKSign(String deviceType, String deviceAddr); 

	// 현재 카드 사용중지, 누적금액 초기화 송신
	void writeOther(String deviceType, String deviceAddr, String command);
	
	// 셀프 설정 불러오기
	List<Map<String, Object>> getSelfConfig();
	void writeConfig(String deviceType, String deviceAddr);
	Map<String, Object> readSelfConfig(String deviceAddr);
	// 셀프 설정 셋팅
	String setSelfConfig(SelfConfigVO params);
	
	// 리더기 설정 불러오기 
	List<Map<String, Object>> getReaderConfig();
	Map<String, Object> readReaderConfig(String deviceAddr);
	// 리더기 설정 셋팅
	String setReaderConfig(ReaderConfigVO params);
	
	// 충전기 설정 불러오기
	List<Map<String, Object>> getChargerConfig();
	Map<String, Object> readChargerConfig(String deviceAddr);
	// 충전기 설정 셋팅
	String setChargerConfig(ChargerConfigVO params);
	
	// 기기 주소 변경 
	String changeDeviceAddr(String device_type, String before_addr, String after_addr);
	
	// 누적금액 초기화 
	String resetTotalMoney(String device_type, String device_addr);
	
	// 세차장 ID 변경
	String updateShopNo(String device_type, String device_addr);
	
	// 장비 설정 복사 
	String copyDeviceConfig(String type, String copy, String target);
	
}

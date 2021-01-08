package kr.gls.myapp.pos.service;

import java.util.List;
import java.util.Map;

// 포스 매출 관련 서비스 인터페이스 
public interface IPosSalesService {
	
	// 월간 매출 
	List<Map<String, Object>> getMonthlySales(String year, String month);
	
	// 일간 매출
	List<Map<String, Object>> getDaysSales(String year, String month, int days);
	
	// 기기별 매출 조회 
	List<Map<String, Object>> getDeviceSales(String year, String month, int days);
	
	// 메인화면 세차장비 오늘자 매출
	List<Map<String, Object>> getTodayDeviceSales();
	
	// 메인화면 충전장비 오늘자 매출
	List<Map<String, Object>> getTodayChargerSales();
	
	// 메인화면  매출총계 
	Map<String, Object> getTodaySalesTotal();
	
	// 장비 이력 
	List<Map<String, Object>> getUseDevice();
	
	// 장비 세부 이력
	List<Map<String, Object>> getUseDeviceDetail(Integer device_type, String device_addr);
	
	// 실시간 모니터링 (LAN)
	List<Map<String, Object>> getLanDeviceState();
	
	// 마스터 카드 이력
	List<Map<String, Object>> getMasterCardHistory();
	
}

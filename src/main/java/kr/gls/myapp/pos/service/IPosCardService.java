package kr.gls.myapp.pos.service;

import java.util.List;
import java.util.Map;

import kr.gls.myapp.pos.model.SetChargeVO;

public interface IPosCardService {
	
	// 카드 등록 
	String setCard(String card_num);
	
	// 카드 읽기 
	List<Map<String, Object>> readCard(String card_num);
	
	// 카드 이력조회 
	List<Map<String, Object>> getCardHistory();
	
	// 카드 이력 상세 조회 
	List<Map<String, Object>> getCardHistoryDetail(String card_num);
	
	// 정지 카드 조회
	List<Map<String, Object>> getBlackCard();
	
	// 정지카드 등록 
	String setBlackCard(String card_num, String content);
	
	// 정지카드 해제 
	String deleteBlackCard(String no);
	
	// 카드 충전
	String setCharge(SetChargeVO params);
	
	// 카드 검색 
	List<Map<String, Object>> searchCard(String card_num, String mb_no, String end_time);
	
	// 카드이력 초기화 
	String resetCardHistory(String card_num);
	
	// 장비 전체이력  초기화 
	String resetDeviceHistory();
}

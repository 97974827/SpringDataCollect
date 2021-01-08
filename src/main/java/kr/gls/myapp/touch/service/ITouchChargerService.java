package kr.gls.myapp.touch.service;

import java.util.Map;

import kr.gls.myapp.touch.model.TouchConfigSetVO;

public interface ITouchChargerService {
	
	// 터치 충전기 성정
	String setTouchConfig(TouchConfigSetVO params);
	
	// 터치 충전기 설정 불러오기
	Map<String, Object> getTouchConfig();
	
	// 충전 기록 가져오기
	String getChargerState();
	
	// 토탈 충전 기록 가져오기
	void getChargerTotal();
	
	// 통신 상태 테스트
	boolean getConnect(String ip);
	
	// 금액 포매팅 
	String moneyFormatting(int money);
	
	String moneyFormatting(int money, int kind);
	
}

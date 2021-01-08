package kr.gls.myapp.touch.repository;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import kr.gls.myapp.touch.model.TouchChargeStateVO;
import kr.gls.myapp.touch.model.TouchConfigGetVO;
import kr.gls.myapp.touch.model.TouchConfigSetVO;
import kr.gls.myapp.touch.model.TouchListVO;

public interface ITouchChargerMapper {
	
	// 터치 충전기 설정 요청
	void setTouchConfig();
	
	// 터치 충전기 설정 불러오기 요청
	Map<String, Object> getTouchConfig();
	
	// 충전 기록 가져오기
	void insertChargerState(@Param("state") TouchChargeStateVO state);
	
	// 토탈 충전 기록 가져오기
	void insertChargerTotal();
	
	// 통신 상태 테스트
	void getConnect();
	
	// 터치 충전기 장비 리스트 불러오기
	List<TouchListVO> getTouchList(Integer touch);
	
	// 데이터 수집장치 - 충전기 설정 불러오기
	TouchConfigGetVO getTouchConfigVO(Map<String, Object> map);
	
	// diff = 1 / 설정값 삽입
	void insertChargerConfig(TouchConfigGetVO vo);
	
	// diff = 2 / 보너스 설정값 변경
	void updateChargerBonus(TouchConfigGetVO vo);

	// SET 설정값 변경 
	// 파라미터 넘겨주기 : id값 
	void updateBonus(@Param("obj")TouchConfigSetVO vo, @Param("bonusNo")int bonusNo);
	
	// DC 설정값 변경
	void updateChargerConfig(@Param("obj")TouchConfigSetVO vo, @Param("deviceNo")int deviceNo);
	
	// 데이터 수집장치 - 터치충전기 상점ID 변경
	void updateTouchShopID(@Param("id") String id);
	
}	

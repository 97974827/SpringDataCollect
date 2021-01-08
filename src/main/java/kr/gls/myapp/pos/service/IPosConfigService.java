package kr.gls.myapp.pos.service;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestParam;

import kr.gls.myapp.device.model.SelfConfigVO;
import kr.gls.myapp.pos.model.MasterConfigGetVO;
import kr.gls.myapp.pos.model.MasterConfigSetVO;
import kr.gls.myapp.pos.model.PosConfigVO;

public interface IPosConfigService {
	
	// 포스 설정 셋팅 
	String setPosConfig(PosConfigVO params);
	// 포스 설정 가져오기 
	Map<String, Object> getPosConfig();

	// 암호화 메서드
	String encodePw(String pw);
	String decodePw(String pw);
	
	// 마스터 설정 불러오기
	List<Map<String, Object>> getMasterConfig(MasterConfigGetVO master);
	// 마스터 설정 
	String setMasterConfig(Map<String, Object> params);
	
	List<Map<String, Object>> getManagerInfo(); // 관리 업체 정보 불러오기
	List<Map<String, Object>> getCrcTable(); // crc 블러오기
	List<Map<String, Object>> getDeviceList(); // 등록된 장비 목록 불러오기
	List<Map<String, Object>> getManagerList(); // 공급업체 불러오기
	
	List<Map<String, Object>> getSelfList(); // 셀프 주소
	
	List<Map<String, Object>> getHiddenConfig(SelfConfigVO params); // 히든 설정 불러오기
	
	String setHiddenConfig(Map<String, Object> params); // 히든 설정
}

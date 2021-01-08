package kr.gls.myapp.pos.service;

import java.util.List;
import java.util.Map;

import kr.gls.myapp.pos.model.MemberVO;

public interface IPosMemberService {
	
	// 회원 목록 조회 
	List<Map<String, Object>> getMemberList();
	
	// 회원 목록 조회 
	List<Map<String, Object>> getMemberLevel();
	
	// 회원 상세 조회 
	Map<String, Object> getMemberDetail(String mb_no);
	
	// 회원 등록 및 수정 
	String setMember(MemberVO params);
	
	// 회원 삭제 
	String deleteMember(String no);
	
	// 회원 이력 조회 
	List<Map<String, Object>> getMemberHistory();
	
	// 회원 상세 이력 조회
	List<Map<String, Object>> getMemberHistoryDetail(String mb_no);
	
	// 회원 검색 
	List<Map<String, Object>> searchMember(String name, String mobile, String car_num, String card_num);
	
	// 회원 보너스 설정 불러오기 
	List<Map<String, Object>> getMemberBonus();
	
	// 회원 보너스 설정 
	String setMemberBonus(Map<String, Object> params);
}	

package kr.gls.myapp.pos.repository;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import kr.gls.myapp.common.DeviceListVO;
import kr.gls.myapp.common.GlsConfigVO;
import kr.gls.myapp.device.model.AirStateVO;
import kr.gls.myapp.device.model.ChargerStateVO;
import kr.gls.myapp.device.model.GarageStateVO;
import kr.gls.myapp.device.model.MateStateVO;
import kr.gls.myapp.device.model.ReaderStateVO;
import kr.gls.myapp.device.model.SelfConfigVO;
import kr.gls.myapp.device.model.SelfStateVO;
import kr.gls.myapp.pos.model.BlackCardVO;
import kr.gls.myapp.pos.model.CardVO;
import kr.gls.myapp.pos.model.CrcVO;
import kr.gls.myapp.pos.model.DeviceInfoVO;
import kr.gls.myapp.pos.model.ManagerListVO;
import kr.gls.myapp.pos.model.MasterConfigGetVO;
import kr.gls.myapp.pos.model.MasterConfigSetVO;
import kr.gls.myapp.pos.model.MemberBonusVO;
import kr.gls.myapp.pos.model.MemberCardVO;
import kr.gls.myapp.pos.model.MemberVO;
import kr.gls.myapp.pos.model.PosConfigVO;
import kr.gls.myapp.pos.model.SalesListViewVO;
import kr.gls.myapp.pos.model.TodayChargerStateVO;
import kr.gls.myapp.pos.model.TodaySalesTotalVO;

@Repository
public interface IPosMapper {
	
	//////////////////////////////////////////////////////////////////////////////////////////////////// 설정관련
	
	// 장비 갯수 추출
	Integer selectDeviceCount(@Param("type") Integer type);
	// 포스 기본, 기타 설정 추출
	PosConfigVO selectShopInfo();
	// 포스 테이블 업데이트 
	void updatePosConfig(@Param("pos")PosConfigVO vo);
	// 상점 테이블 업데이트
	void updateShopInfo(@Param("shop")PosConfigVO vo);
	// 가장 큰 주소 컴색
	String selectMaxAddr(@Param("type")Integer type, @Param("addr")String addr);
	// device_no 검색
	Integer selectDeviceNo(@Param("type")Integer type, @Param("addr")String addr);
	// gl_device_list add insert
	void addDeviceList(@Param("type") Integer type, @Param("addr") String addr, @Param("ip") String ip);
	// gl_device_list remove delete
	void removeDeviceList(@Param("type") Integer type, @Param("addr") String addr);
	
	// gl_wash_total add insert
	void addWashTotal(@Param("type") Integer type, @Param("addr") String addr);
	
	// gl_wash_total remove delete
	void removeWashTotal(@Param("type") Integer type, @Param("addr") String addr);
	
	// 장비 리스트 검색
	List<DeviceListVO> selectDeviceList(@Param("type") Integer type);
	
	void insertSelfConfig(@Param("addr") String addr);
	
	// 인증코드 추출
	String selectAuth(@Param("auth_code") String auth_code);
	
	// 마스터 설정 추출
	MasterConfigGetVO selectMasterConfig(@Param("auth_code") String auth_code);
	// 마스터 설정 : 상점, 포스
	void updateMasterShopInfo(@Param("auth_code") String auth_code);
	void updateMasterPosConfig(@Param("manager_no") Integer manager_no, @Param("enableCard") String enableCard, 
								@Param("cardBinary") String cardBinary);
	
	
	ManagerListVO getManagerInfo(); // 관리 업체 정보 
	// 공급업체 불러오기
	List<ManagerListVO> getManagerList();
	List<CrcVO> getCrcTable();
	List<DeviceInfoVO> selectDeviceInfo();
	// 히든 설정 불러오기
	SelfConfigVO getHiddenConfig(@Param("device_addr") String device_addr);
	// TODO 히든 설정 : setSelfConfig와 연동
	void setHiddenConfig(@Param("self") SelfConfigVO vo);
	
	String selectShopNo(); // 상점 ID 가져오기
	
	////////////////////////////////////////////////////////////////////////////////////////////////////// 설정관련 끝
	
	
	
	
	
	///////////////////////////////////////////////////////////////////////////////////////////////////// 매출관련
	
	// 금일 세차매출 데이터 셀렉트
	List<SalesListViewVO> selectTodayDeviceSales(@Param("date") String date);
	// 금일 세차매출 데이터 셀렉트
	List<TodayChargerStateVO> selectTodayChargerSales(@Param("date") String date);
	// 금일 현금/카드매출
	TodaySalesTotalVO selectTodaySales(@Param("date") String date);
	// 금일  카드충전
	TodayChargerStateVO selectTodayCardCharge(@Param("date") String date);
	
	// 월간 셀프 , 진공, 매트, 리더기, 충전기 매출 : 년월일
	SelfStateVO selectSelfState(@Param("year")String year, @Param("month")String month, @Param("day")String day);
	AirStateVO selectAirState(@Param("year")String year, @Param("month")String month, @Param("day")String day);
	MateStateVO selectMateState(@Param("year")String year, @Param("month")String month, @Param("day")String day);
	ReaderStateVO selectReaderState(@Param("year")String year, @Param("month")String month, @Param("day")String day);
	TodayChargerStateVO selectChargerState(@Param("year")String year, @Param("month")String month, @Param("day")String day);
	
	// 일간 매출
	List<SalesListViewVO> selectDaySales(@Param("year")String year, @Param("month")String month, @Param("days")String days);
	
	// 셀프 사용내역 
	SelfStateVO getSelfDetail(@Param("no") Integer no, @Param("type") String type);
//	GarageStateVO getGarageDetail(@Param("no") Integer no, @Param("type") String type);
	
	// 기기별 매출 
	SalesListViewVO selectDeviceSales(@Param("year")String year, @Param("month")String month, 
			@Param("days")String days, @Param("type") Integer type, @Param("addr") String addr);
	
	SalesListViewVO selectChargerSales(@Param("year")String year, @Param("month")String month, 
			@Param("days")String days, @Param("type") Integer type, @Param("addr") String addr);
	
	// 장비 이력 
	SelfStateVO selectUseSelfState(@Param("addr")String addr);
	AirStateVO selectUseAirState(@Param("addr")String addr);
	MateStateVO selectUseMateState(@Param("addr")String addr);
	ReaderStateVO selectUseReaderState(@Param("addr")String addr);
	TodayChargerStateVO selectUseChargerState(@Param("type")Integer type, @Param("addr")String addr);
	
	// 장비 세부 이력
	List<SalesListViewVO> selectUseDetailDevice(@Param("type")Integer type, @Param("addr")String addr);
	List<TodayChargerStateVO> selectUseDetailCharger(@Param("type")Integer type, @Param("addr")String addr);
	
	// 마스터 카드 이력
	List<SalesListViewVO> selectMasterCardHistory();
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////// 매출관련 끝
	
	
	
	
	/////////////////////////////////////////////////////////////////////////////////////////////// 카드 관련 
	
	// 카드등록
	void setCard(@Param("cardNum")String card_num, @Param("date")String date);
	
	// 등록카드, 정지카드 검사
	Integer isCard(@Param("cardNum")String cardNum);
	Integer isBlackCard(@Param("card_num")String cardNum);
	
	// 카드읽기
	String selectCardName(@Param("card_num")String card_num);
	String selectCardNum(@Param("card_num")String card_num);
	Integer countVerification(@Param("card_num")String card_num);
	String selectLastDate(@Param("card_num")String card_num);
	
	// 카드이력 
	List<CardVO> getCardHistory();
	// 카드 상세이력
	List<SalesListViewVO> selectCardHistoryDeTail(@Param("card_num")String card_num);
	
	// 정지 카드 조회 
	List<BlackCardVO> selectBlackCard();
	// 정지 카드 등록 
	void insertBlackCard(@Param("card_num")String card_num, @Param("content")String content, @Param("date")String date);
	// 정지카드 해제
	void deleteBlackCard(@Param("no")String no);
	
	void insertPosChargerState(@Param("charger") TodayChargerStateVO charger);
	void insertCreditChargerState(@Param("charger") TodayChargerStateVO charger);
	
	// 카드번호  검색 
	String selectMemberNo(@Param("num")String num); // 회원 코드검색
	String selectCardDate(@Param("card_num")String card_num); // 발급일자 검색 
	Integer selectTotalCharge(@Param("card_num")String card_num); // 누적충전금액 검색 
	SalesListViewVO selectRemainCardAndEndTime(@Param("card_num")String card_num); // 카드잔액 및 최근 사용일자 검색 
	
	// 회원 코드 검색 
	MemberCardVO selectCardNumAndEndTime(@Param("mb_no")String mb_no);
	// 최근 사용일자 검색
	String selectCardNumGroup(@Param("date") String date);
	
	// 해당 카드번호 기록 삭제 
	void deleteSelfState(@Param("card_num")String card_num);
	void deleteAirState(@Param("card_num")String card_num);
	void deleteMateState(@Param("card_num")String card_num);
	void deleteReaderState(@Param("card_num")String card_num);
//	void deleteGarageState(@Param("card_num")String card_num);
	void deleteChargerState(@Param("card_num")String card_num);
	
	void deleteNotCardSelfState(@Param("card_num")String card_num);
	void deleteNotCardAirState(@Param("card_num")String card_num);
	void deleteNotCardMateState(@Param("card_num")String card_num);
	void deleteNotCardReaderState(@Param("card_num")String card_num);
//	void deleteGarageState(@Param("card_num")String card_num);
	void deleteNotCardChargerState(@Param("card_num")String card_num);
	
	// 장비 전체이력 초기화 
	void deleteSelf();
	void deleteAir();
	void deleteMate();
	void deleteReader();
//	void deleteGarage();
	void deleteCharger();
	
	///////////////////////////////////////////////////////////////////////////////////////////// 카드 관련 끝
	
	
	
	///////////////////////////////////////////////////////////////////////////////////////////// 회원관련 
	
	// 회원 목록 조회
	List<MemberVO> selectMemberList();
	// 회원 레벨 조회
	List<MemberVO> selectMemberLevel();
	
	// 회원 상세 조회 
	Integer countCardMember(@Param("mb_no") String mb_no);
	MemberVO selectMemberInfo(@Param("mb_no") String mb_no);
	List<String> selectMemberCard(@Param("mb_no") String mb_no);
	MemberVO selectOneCardUser(@Param("mb_no") String mb_no);
	
	// 회원 등록
	void insertMember(@Param("mb") MemberVO mb);
	String selectMbNo(@Param("mb") MemberVO mb);
	void insertMemberCard(@Param("card") MemberCardVO card);
	
	void updateMember(@Param("mb") MemberVO mb, @Param("mb_no") String mb_no);
	void deleteCard(@Param("mb_no") String mb_no);
	Integer countCard(@Param("mb_no") String mb_no);
	void updateMemberCard(@Param("card") MemberCardVO card, @Param("mb_no") String mb_no);
	String selectMemberCardNum(@Param("mb_no") String mb_no);
	void deleteMemberCard(@Param("num") String card_num);
	void deleteMember(@Param("mb_no") String mb_no);
	// 회원 이력 - 누적금액 
	Integer selectMemberTotalCharge(@Param("card_num")String card_num);
	// 회원 상세 이력
	List<SalesListViewVO> selectMemberDetail(@Param("card_num")String card_num);
	
	// 회원검색 
	MemberVO selectToName(@Param("name") String name);
	MemberVO selectToMobile(@Param("mobile") String mobile);
	MemberVO selectToCar(@Param("car") String car);
	MemberVO selectToCard(@Param("card") String card);
	
	// 회원 보너스 
	List<MemberBonusVO> selectMemberBonus();
	
	//////////////////////////////////////////////////////////////////////////////////////////// 회원관련 끝 
}

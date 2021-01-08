package kr.gls.myapp.device.repository;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import kr.gls.myapp.common.DeviceListVO;
import kr.gls.myapp.common.GlsConfigVO;
import kr.gls.myapp.device.model.WashTotalVO;
import kr.gls.myapp.device.model.AirStateVO;
import kr.gls.myapp.device.model.ChargerConfigVO;
import kr.gls.myapp.device.model.ChargerStateVO;
import kr.gls.myapp.device.model.GarageStateVO;
import kr.gls.myapp.device.model.JsonChargerStateVO;
import kr.gls.myapp.device.model.MateStateVO;
import kr.gls.myapp.device.model.ReaderConfigVO;
import kr.gls.myapp.device.model.ReaderStateVO;
import kr.gls.myapp.device.model.SelfConfigVO;
import kr.gls.myapp.device.model.SelfStateVO;

@Repository
public interface IDeviceMapper {
	
	// 장비 기기주소 추출 - 단일 
	String[] selectDeviceAddr(@Param("type") Integer type);
	
	// 장비 모든 정보 추출 
//	List<DeviceListVO> selectAllDeviceAddr(@Param("gls") GlsConfigVO gls);
	
	// gl_wash_total 업데이트
	void updateWashTotal(@Param("vo") WashTotalVO vo);
	
	// 셀프 기록 저장
	void insertSelfState(@Param("self") SelfStateVO self);
	
	// 진공 기록 저장
	void insertAirState(@Param("air") AirStateVO air);
	
	// 매트 기록 저장
	void insertMateState(@Param("mate") MateStateVO mate);
	
	// 리더 기록 저장
	void insertReaderState(@Param("reader") ReaderStateVO reader);
	
	// garage 기록 저장
	void insertGarageState(@Param("garage") GarageStateVO garage);
	
	// 충전기 기록 저장
	void insertChargerState(@Param("charger") ChargerStateVO Charger);
	
	Integer selectNo(@Param("type") Integer type, @Param("addr") String addr);
	
	// 모든 장비 타입,주소,
	List<DeviceListVO> selectDeviceInfoList(@Param("gls") GlsConfigVO gls);
	
	// 장비별 금일 매출
	String selectSelfSales(@Param("today") String today, @Param("addr") String addr);
	String selectAirSales(@Param("today") String today, @Param("addr") String addr);
	String selectMateSales(@Param("today") String today, @Param("addr") String addr);
	String selectReaderSales(@Param("today") String today, @Param("addr") String addr);
	String selectGarageSales(@Param("today") String today, @Param("addr") String addr);
	String selectChargerSales(@Param("today") String today, @Param("addr") String addr);
	
	// 기기주소 변경
	void updateDeviceListAddr(@Param("type") Integer type, @Param("oldAddr") String oldAddr, 
							@Param("newAddr") String newAddr);
	
	// 상점 ID 변경
	void updateChargerConfigShopID(@Param("shopid") String shopid, @Param("no") Integer no);
	
	// 충전기 실시간 모니터링
	JsonChargerStateVO selectChargerMonitor(@Param("type") Integer type, @Param("addr") String addr);
	
	// 셀프 설정 불러오기 
	SelfConfigVO selectSelfConfig(@Param("addr") String addr);
	// 셀프 설정값 저장
	void insertSelfConfig(@Param("self") SelfConfigVO self, @Param("date") String date);
	
	// 리더기 설정 불러오기
	ReaderConfigVO selectReaderConfig(@Param("addr") String addr);
	// 리더기 설정값 저장 
	void insertReaderConfig(@Param("reader") ReaderConfigVO reader, @Param("date") String date);
	
	// 충전기 설정 불러오기 - 485
	ChargerConfigVO selectChargerConfig(@Param("type") Integer type, @Param("addr") String addr);
	// 충전기 설정값 저장
	void insertChargerConfig(@Param("charger") ChargerConfigVO charger, @Param("date") String date);
	// 보너스 업데이트
	void updateChargerBonus(@Param("charger") ChargerConfigVO charger, @Param("date") String date);
	// device_no 추출
	String selectDeviceNo(@Param("type") Integer type, @Param("addr") String addr);
	// shop_no 추출
	String selectShopNo();
}

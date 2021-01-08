package kr.gls.myapp.pos.service;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.gls.myapp.common.DeviceListVO;
import kr.gls.myapp.common.GlsConfigVO;
import kr.gls.myapp.device.model.SelfConfigVO;
import kr.gls.myapp.device.repository.IDeviceMapper;
import kr.gls.myapp.device.service.IDeviceService;
import kr.gls.myapp.pos.model.CrcVO;
import kr.gls.myapp.pos.model.DeviceInfoVO;
import kr.gls.myapp.pos.model.ManagerListVO;
import kr.gls.myapp.pos.model.MasterConfigGetVO;
import kr.gls.myapp.pos.model.MasterConfigSetVO;
import kr.gls.myapp.pos.model.PosConfigVO;
import kr.gls.myapp.pos.repository.IPosMapper;
import kr.gls.myapp.touch.repository.ITouchChargerMapper;


@Service
public class PosConfigService implements IPosConfigService {
	
	@Autowired
	private IPosMapper posMapper;
	
	@Autowired
	private IDeviceService deviceService;
	
	@Autowired
	private IDeviceMapper deviceMapper;
	
	@Autowired
	private GlsConfigVO glsConfig;  
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	/*
	 1. 포스 설정
	 - 셋팅값 가져오기
	 - 기본, 기타정보는 바로 업데이트 
	 - 설정 장비수량과 현재 DB 수량비교 
	 - 추가분이면 가장 큰 주소 +1
	 = 삭제분이면 가장 큰 주소 삭제
	 (이 때, 셀프, 진공, 매트, 리더(매트)는 gl_wash_total 테이블에도 등록 및 삭제)
	 
    * 참고 : 사용되지 않는 COIN과 BILL의 경우 별도의 config 테이블이 없기 때문에
             gl_coin_config / gl_bill_config 로 저장하도록 작성하고 주석처리 하였음
    * 참고 2 : 터치 충전기 및 키오스크는 IP 규칙에 따라 IP를 gl_device_list에 등록
    * 참고 3 : 우수회원 시스템 사용여부로 인해 터치충전기에 접속하는 부분이 추가되었음
	 */
	@Override
	public String setPosConfig(PosConfigVO params) {
		String result = "0";
		System.out.println(params.toString());
		
		// 인코딩작업 
		// : DB에 넣을떈 항상 인코딩 / 포스에 넘겨줄떈 디코딩
		params.setShop_pw(encodePw(params.getShop_pw())); 
		
		// DB update
		posMapper.updatePosConfig(params);
		posMapper.updateShopInfo(params);
		
		// 기존 장비 수량 가져오기 - DB
		Integer oldSelfCount = 0;
		Integer oldAirCount = 0;
		Integer oldMateCount = 0;
		Integer oldChargerCount = 0;
		Integer oldCoinCount = 0;
		Integer oldBillCount = 0;
		Integer oldTouchCount = 0;
		Integer oldKioskCount = 0;
		Integer oldReaderCount = 0;
		Integer oldGarageCount = 0;
		
		for(int i=glsConfig.getSelf(); i<=glsConfig.getGarage(); i++) {
			if (i == glsConfig.getSelf()) {
				oldSelfCount = posMapper.selectDeviceCount(i);
			} else if (i == glsConfig.getAir()) {
				oldAirCount = posMapper.selectDeviceCount(i);
			} else if (i == glsConfig.getMate()) {
				oldMateCount = posMapper.selectDeviceCount(i);
			} else if (i == glsConfig.getCharger()) {
				oldChargerCount = posMapper.selectDeviceCount(i);
			} else if (i == glsConfig.getCoin()) {
				oldCoinCount = posMapper.selectDeviceCount(i);
			} else if (i == glsConfig.getBill()) {
				oldBillCount = posMapper.selectDeviceCount(i);
			} else if (i == glsConfig.getTouch()) {
				oldTouchCount = posMapper.selectDeviceCount(i);
			} else if (i == glsConfig.getKiosk()) {
				oldKioskCount = posMapper.selectDeviceCount(i);
			} else if (i == glsConfig.getReader()) {
				oldReaderCount = posMapper.selectDeviceCount(i);
			} else if (i == glsConfig.getGarage()) {
				oldGarageCount = posMapper.selectDeviceCount(i);
			} 
		} // 장비수량 끝
		
		// LAN 통신장비 ip 설정
		List<String> touchIpList = new ArrayList<String>(
				Arrays.asList("192.168.0.221", "192.168.0.222", "192.168.0.223", "192.168.0.224", "192.168.0.225"));
		List<String> kioskIpList = new ArrayList<String>(
				Arrays.asList("192.168.0.226", "192.168.0.227", "192.168.0.228", "192.168.0.229", "192.168.0.230"));
		
		// 장비 갯수 비교 - 다를 시 DB액세스
		if (Integer.parseInt(params.getSelf_count()) != oldSelfCount) {
			compareDevice(Integer.parseInt(params.getSelf_count()), oldSelfCount, glsConfig.getSelf());
		} if (Integer.parseInt(params.getAir_count()) != oldAirCount) {
			compareDevice(Integer.parseInt(params.getAir_count()), oldAirCount, glsConfig.getAir());
		} if (Integer.parseInt(params.getMate_count()) != oldMateCount) {
			compareDevice(Integer.parseInt(params.getMate_count()), oldMateCount, glsConfig.getMate());
		} if (Integer.parseInt(params.getCharger_count()) != oldChargerCount) {
			compareDevice(Integer.parseInt(params.getCharger_count()), oldChargerCount, glsConfig.getCharger());
		} if (Integer.parseInt(params.getCoin_count()) != oldCoinCount) {
			compareDevice(Integer.parseInt(params.getCoin_count()), oldCoinCount, glsConfig.getCoin());
		} if (Integer.parseInt(params.getBill_count()) != oldBillCount) {
			compareDevice(Integer.parseInt(params.getBill_count()), oldBillCount, glsConfig.getBill());
		} if (Integer.parseInt(params.getTouch_count()) != oldTouchCount) {
			compareDevice(Integer.parseInt(params.getTouch_count()), oldTouchCount, glsConfig.getTouch());
		} if (Integer.parseInt(params.getKiosk_count()) != oldKioskCount) {
			compareDevice(Integer.parseInt(params.getKiosk_count()), oldKioskCount, glsConfig.getKiosk());
		} if (Integer.parseInt(params.getReader_count()) != oldReaderCount) {
			compareDevice(Integer.parseInt(params.getReader_count()), oldReaderCount, glsConfig.getReader());
		} if (Integer.parseInt(params.getGarage_count()) != oldGarageCount) {
			compareDevice(Integer.parseInt(params.getGarage_count()), oldGarageCount, glsConfig.getGarage());
		}
		result = "1";
		return result;
		
	} // TODO set_pos_config 끝
	
	/*
    2. 포스 설정 정보 불러오기(DB-> POS)
    gl_device_list ->  각 장비의 수량
    gl_shop_info -> 상점 설정 값
    gl_pos_config ->  포스에 관련된 설정 값
    * 참고 : 개발 초기에 작성된 함수라 리스트가 아닌 딕셔너리를 반환하고 있음 
	 */
	@Override
	public Map<String, Object> getPosConfig() {
		Map<String, Object> subMap = new LinkedHashMap<>(); // 반환맵
			
		// 장비 수량 추출
		for(int i=glsConfig.getSelf(); i<=glsConfig.getGarage(); i++) {
			
			if (i == glsConfig.getSelf()) {
				subMap.put("self_count", Integer.toString(posMapper.selectDeviceCount(i)));
				glsConfig.setSelfCount(Integer.parseInt(subMap.get("self_count").toString()));
			} else if (i == glsConfig.getAir()) {
				subMap.put("air_count", Integer.toString(posMapper.selectDeviceCount(i)));
				glsConfig.setAirCount(Integer.parseInt(subMap.get("air_count").toString()));
			} else if (i == glsConfig.getMate()) {
				subMap.put("mate_count", Integer.toString(posMapper.selectDeviceCount(i)));
				glsConfig.setMateCount(Integer.parseInt(subMap.get("mate_count").toString()));
			} else if (i == glsConfig.getCharger()) {
				subMap.put("charger_count", Integer.toString(posMapper.selectDeviceCount(i)));
				glsConfig.setChargerCount(Integer.parseInt(subMap.get("charger_count").toString()));
			} else if (i == glsConfig.getCoin()) {
				subMap.put("coin_count", Integer.toString(posMapper.selectDeviceCount(i)));
				glsConfig.setCoinCount(Integer.parseInt(subMap.get("coin_count").toString()));
			} else if (i == glsConfig.getBill()) {
				subMap.put("bill_count", Integer.toString(posMapper.selectDeviceCount(i)));
				glsConfig.setBillCount(Integer.parseInt(subMap.get("bill_count").toString()));
			} else if (i == glsConfig.getTouch()) {
				subMap.put("touch_count", Integer.toString(posMapper.selectDeviceCount(i)));
				glsConfig.setTouchCount(Integer.parseInt(subMap.get("touch_count").toString()));
			} else if (i == glsConfig.getKiosk()) {
				subMap.put("kiosk_count", Integer.toString(posMapper.selectDeviceCount(i)));
				glsConfig.setKioskCount(Integer.parseInt(subMap.get("kiosk_count").toString()));
			} else if (i == glsConfig.getReader()) {
				subMap.put("reader_count", Integer.toString(posMapper.selectDeviceCount(i)));
				glsConfig.setReaderCount(Integer.parseInt(subMap.get("reader_count").toString()));
			} else if (i == glsConfig.getGarage()) {
				subMap.put("garage_count", Integer.toString(posMapper.selectDeviceCount(i)));
				glsConfig.setGarageCount(Integer.parseInt(subMap.get("garage_count").toString()));
			} 
		}
		
		PosConfigVO configObj = posMapper.selectShopInfo(); // 포스 셋팅 불러오기
		System.out.println(configObj.toString());
		
		// 암호 디코딩
		configObj.setShop_pw(decodePw(configObj.getShop_pw()));
		configObj.setAdmin_pw(decodePw(configObj.getAdmin_pw()));
	
		subMap.put("shop_id", configObj.getShop_id());
		subMap.put("shop_pw", configObj.getShop_pw());
		subMap.put("shop_no", configObj.getShop_no());
		subMap.put("shop_name", configObj.getShop_name());
		subMap.put("shop_tel", configObj.getShop_tel());
		subMap.put("encry", configObj.getEncry());
		subMap.put("list_enable", configObj.getList_enable());
		subMap.put("weather_area", configObj.getWeather_area());
		subMap.put("weather_url", configObj.getWeather_url());
		subMap.put("master_card_num", configObj.getMaster_card_num());
		subMap.put("manager_no", configObj.getManager_no());
		subMap.put("addr", configObj.getAddr());
		subMap.put("ceo", configObj.getCeo());
		subMap.put("business_number", configObj.getBusiness_number());
		subMap.put("admin_pw", configObj.getAdmin_pw());
		subMap.put("set_vip", configObj.getSet_vip());
		subMap.put("dc_version", configObj.getDc_version());
		subMap.put("card_binary", configObj.getCard_binary());
		
		// 공급업체 셀렉트
		String no = Integer.toString(configObj.getManager_no());
		if (no.equals("1") || no.equals("4")) { // 길광
			glsConfig.setManager_no("01");
		} else if (no.equals("3") || no.equals("5")) { // 대진
			glsConfig.setManager_no("03");
		} else if (no.equals("2")) { // 주일
			glsConfig.setManager_no("02");
		}
		System.out.println("현재 공급업체는 ? " + glsConfig.getManager_no());
		
		return subMap;
	}
	
	// 패스워드 인코딩
	@Override
	public String encodePw(String pw){
		byte[] targetBytes = null;
		try {
			targetBytes = pw.getBytes("UTF-8");
			
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		byte[] encodeBytes = Base64.getEncoder().encode(targetBytes);
		return new String(encodeBytes);
	}
	
	// 패스워드 디코딩
	@Override
	public String decodePw(String pw){
		byte[] targetBytes = null;
		try {
			targetBytes = pw.getBytes("UTF-8");
			
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		byte[] decodeBytes = Base64.getDecoder().decode(targetBytes);
		return new String(decodeBytes);
	}
	
	// 기기주소 문자열 반환 
	public String deviceAddrFormatter(int addr) {
		String str;
		if (addr < 10) {
			str = String.format("0%d", addr);
		} else {
			str = String.format("%d", addr);
		}
//		System.out.println("주소 변환값 : " + str);
		return str;
	}
	
	// 장비 비교 갯수 로직 
	// 추가분이면 가장 큰 주소 +1
	// 삭제분이면 가장 큰 주소 삭제
	public void compareDevice(Integer newCount, Integer oldCount, Integer type) {	
		if (newCount > oldCount) { // 장비 추가 경우
			// DB - 장비 리스트 조회
			List<DeviceListVO> deviceList = new ArrayList<DeviceListVO>(); 
			deviceList.addAll(posMapper.selectDeviceList(type)); // 타입에 따른 장비 리스트 set
						
			for(int j=oldCount+1; j<=newCount; j++) {
				// gl_device_list 테이블 장비 추가
				posMapper.addDeviceList(type, deviceAddrFormatter(j), "0");			
				// gl_wash_total 테이블 장비 추가
				posMapper.addWashTotal(type, deviceAddrFormatter(j));
				// gl_self_config 추가
//				posMapper.insertSelfConfig(deviceAddrFormatter(j));
			}
			
		} else if(newCount < oldCount){ // 장비 삭제 경우
			int removeDeviceCount = oldCount - newCount; // 삭제할 장비갯수

			// DB - 장비 리스트 조회
			List<DeviceListVO> deviceList = new ArrayList<DeviceListVO>(); 
			deviceList.addAll(posMapper.selectDeviceList(type)); // 타입에 따른 장비 리스트 set
			
			for(int j=oldCount; j>newCount; j--) {
				// gl_device_list 테이블 장비 추가
				posMapper.removeDeviceList(type, deviceAddrFormatter(j));
				// gl_wash_total 테이블 장비 추가
				posMapper.removeWashTotal(type, deviceAddrFormatter(j));
				
			}		
		}
	}
	
	// 마스터 설정 서비스 - 인증코드, 공급업체, 카드사용여부, 카드저장번지 가져오기 
	@Override
	public List<Map<String, Object>> getMasterConfig(MasterConfigGetVO master) {
		List<Map<String, Object>> result = new ArrayList<>();
		
		// 포스 인증코드 추출
		String auth_code = master.getAuth_code();
		
		// DB 인증코드 추출
		String db_auth = posMapper.selectAuth(auth_code);
		
		if (auth_code.equals(db_auth)) {
			master = posMapper.selectMasterConfig(auth_code); // 마스터 설정 반환
			master.setAuth_code("1");
		} else {
			master.setAuth_code("0");
		}
		Map<String, Object> subMap = new LinkedHashMap<>(); // 반환맵
		subMap.put("auth", master.getAuth_code());
		subMap.put("manager_name", master.getManager_name());
		subMap.put("enable_card", master.getEnable_card());
		subMap.put("card_binary", master.getCard_binary());
		result.add(subMap);
		return result;
	}
	
	// 마스터 설정 
	@Override
	public String setMasterConfig(Map<String, Object> params) {
//		0002
//		3
//		0
//		2
		String result = "0"; // 리턴값
		
		try {
			String authCode = params.get("auth_code").toString();
			String managerNo = params.get("manager_no").toString();
			String enableCard = params.get("enable_card").toString();
			String cardBinary = params.get("card_binary").toString();
			
			// 카드 사용여부 추출
			if (authCode.equals("1")) glsConfig.setEnable_card(true);
			else glsConfig.setEnable_card(false);
			
			// 공급업체 번호 set
			if (managerNo.equals("1") || managerNo.equals("4")) { // 길광
				glsConfig.setManager_no("01");
			} else if (managerNo.equals("3") || managerNo.equals("5")) { // 대진
				glsConfig.setManager_no("03");
			} else if (managerNo.equals("2")) { // 주일
				glsConfig.setManager_no("02");
			} else {
				result = "0";
			}
			
			// DB update
			posMapper.updateMasterShopInfo(authCode);
			posMapper.updateMasterPosConfig(Integer.parseInt(managerNo), enableCard, cardBinary);
			
			result = "1";
		} catch (Exception e) {
			result = "0";
			e.printStackTrace();
		}
		
		return result;
	}
	
	// 관리 업체 정보 불러오기
	@Override
	public List<Map<String, Object>> getManagerInfo() {
		List<Map<String, Object>> result = new ArrayList<>();
		ManagerListVO info = posMapper.getManagerInfo(); 
		
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		map.put("manager_no", info.getManager_no());
		map.put("manager_name", info.getManager_name());
		map.put("manager_id", info.getManager_id());
		map.put("manager_encrypt", info.getManager_encrypt());
		result.add(map);
	
		return result;
	}
	
	// Crc 가져오기
	@Override
	public List<Map<String, Object>> getCrcTable() {
		List<Map<String, Object>> result = new ArrayList<>();
		List<CrcVO> crc = posMapper.getCrcTable();
		for(int i=0; i<crc.size(); i++) {
			Map<String, Object> map = new LinkedHashMap<String, Object>();
			map.put("no", crc.get(i).getNo());
			map.put("crc", crc.get(i).getCrc());
			result.add(map);
		}
		return result;
	}
	
	// 등록된 장비 목록 불러오기
	@Override
	public List<Map<String, Object>> getDeviceList() {
		List<Map<String, Object>> result = new ArrayList<>();
		List<DeviceInfoVO> deviceInfo = posMapper.selectDeviceInfo();
		
		for(int i=0; i<deviceInfo.size(); i++) {
			Map<String, Object> map = new LinkedHashMap<String, Object>();

			map.put("device_name", deviceInfo.get(i).getDevice_name());
			map.put("device_type", deviceInfo.get(i).getType());
			result.add(map);
		}
		return result;
	}
	
	// 공급업체 불러오기
	@Override
	public List<Map<String, Object>> getManagerList() {
		List<Map<String, Object>> result = new ArrayList<>();
		List<ManagerListVO> list = posMapper.getManagerList(); 
		
		for (int i=0; i<list.size(); i++) {
			Map<String, Object> map = new LinkedHashMap<String, Object>();
			map.put("manager_no", list.get(i).getManager_no());
			map.put("manager_name", list.get(i).getManager_name());
			map.put("manager_id", list.get(i).getManager_id());
			map.put("manager_encrypt", list.get(i).getManager_encrypt());
			result.add(map);
		}
		return result; 
	}
	
	// 셀프 주소 추출
	@Override
	public List<Map<String, Object>> getSelfList() {
		List<Map<String, Object>> result = new ArrayList<>();
		List<DeviceListVO> list = posMapper.selectDeviceList(glsConfig.getSelf());
		
		for (int i=0; i<list.size(); i++) {
			Map<String, Object> map = new LinkedHashMap<String, Object>();
			map.put("addr", list.get(i).getAddr());
			result.add(map);
		}	
		return result; 
	}
	
	// 히든 설정 불러오기 
	@Override
	public List<Map<String, Object>> getHiddenConfig(SelfConfigVO params) {
		List<Map<String, Object>> result = new ArrayList<>();
		Map<String, Object> resultMap = new LinkedHashMap<String, Object>();
		
		SelfConfigVO config= posMapper.getHiddenConfig(params.getDevice_addr());
				
		resultMap.put("enable_type", config.getSpeedier_enable());
		resultMap.put("pay_type", config.getUse_type());
		resultMap.put("coating_type", config.getSet_coating_output());
		resultMap.put("wipping_enable", config.getWipping_enable());
		resultMap.put("wipping_temp", config.getWipping_temperature());
		result.add(resultMap);
	
		return result;
	}
	
	// 히든 설정 
	@Override
	public String setHiddenConfig(Map<String, Object> params) {
		
		String result = "0";
//		out
//		01
//		1
//		0
//		1
//		0
//		05
    	String deviceAddr = params.get("device_addr").toString();
    	String enableType = params.get("enable_type").toString();			// 정액, 배속
    	String payType = params.get("pay_type").toString();					// 거치, 터치
    	String coatingType = params.get("coating_type").toString();			// 코팅출력
    	String wippingEnable = params.get("wipping_enable").toString();		// 위핑사용
    	String wippingTemp = params.get("wipping_temp").toString();			// 위핑온도
		
    	// DB 가장 최근 설정값 가져오기 
    	SelfConfigVO config = deviceMapper.selectSelfConfig(deviceAddr);
    	
    	logger.info("DB 셀프 설정값\n {}", config.toString());
    	
    	// 셀프 셋팅 전에 현금 값으로 바꾸고 셋팅      Ex) 2000 -> 020
    	Integer selfInitMoney = Integer.parseInt(config.getSelf_init_money()) * 100;
    	Integer selfConMoney = Integer.parseInt(config.getSelf_con_money()) * 100;
    	Integer foamInitMoney = Integer.parseInt(config.getFoam_init_money()) * 100;
    	Integer foamConMoney = Integer.parseInt(config.getFoam_con_money()) * 100;
    	Integer underInitMoney = Integer.parseInt(config.getUnder_init_money()) * 100;
    	Integer underConMoney = Integer.parseInt(config.getUnder_con_money()) * 100;
    	Integer coatingInitMoney = Integer.parseInt(config.getCoating_init_money()) * 100;
    	Integer coatingConMoney = Integer.parseInt(config.getCoating_con_money()) * 100;
    	Integer cycleMoney = Integer.parseInt(config.getCycle_money()) * 100;
    	
    	config.setSelf_init_money(selfInitMoney.toString());
    	config.setSelf_con_money(selfConMoney.toString());
    	config.setFoam_init_money(foamInitMoney.toString());
    	config.setFoam_con_money(foamConMoney.toString());
    	config.setUnder_init_money(underInitMoney.toString());
    	config.setUnder_con_money(underConMoney.toString());
    	config.setCoating_init_money(coatingInitMoney.toString());
    	config.setCoating_con_money(coatingConMoney.toString());
    	config.setCycle_money(cycleMoney.toString());
    	
		config.setSpeedier_enable(enableType);
		config.setUse_type(payType);
		config.setSet_coating_output(coatingType);
		config.setWipping_enable(wippingEnable);
		config.setWipping_temperature(wippingTemp);
		
    	deviceService.setSelfConfig(config); // 셋팅
    	result = "1";
    	
		return result;
	}
	
	
}

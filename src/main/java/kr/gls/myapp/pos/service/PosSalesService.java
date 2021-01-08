package kr.gls.myapp.pos.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.Schedules;
import org.springframework.stereotype.Service;

import kr.gls.myapp.common.DeviceListVO;
import kr.gls.myapp.common.GlsConfigVO;
import kr.gls.myapp.device.model.AirStateVO;
import kr.gls.myapp.device.model.ChargerStateVO;
import kr.gls.myapp.device.model.GarageStateVO;
import kr.gls.myapp.device.model.JsonChargerStateVO;
import kr.gls.myapp.device.model.MateStateVO;
import kr.gls.myapp.device.model.ReaderStateVO;
import kr.gls.myapp.device.model.SelfStateVO;
import kr.gls.myapp.device.repository.IDeviceMapper;
import kr.gls.myapp.pos.model.SalesListViewVO;
import kr.gls.myapp.pos.model.TodayChargerStateVO;
import kr.gls.myapp.pos.model.TodaySalesTotalVO;
import kr.gls.myapp.pos.repository.IPosMapper;
import kr.gls.myapp.touch.service.ITouchChargerService;

@Service
public class PosSalesService implements IPosSalesService {
	
	@Autowired
	private IPosMapper posSalesMapper; // 매출관련 DB mapper
	
	@Autowired
	private IDeviceMapper DeviceMapper; // 485장비관련 DB mapper
	
	@Autowired
	private GlsConfigVO glsConfig; // 디폴트 설정 관련 
	
	@Autowired
	private ITouchChargerService touchService; // 터치충전기 서비스 
	
	private Logger logger = LoggerFactory.getLogger(getClass()); 
	
	/*
	 * 월간 매출 자료 불러오기(DB -> POS)
      1~31일까지 반복문을 돌며
      gl_self_state / gl_air_state / gl_mate_state / gl_reader_state / gl_charger_state
	    위의 5개의 테이블에 각각 접속하여 매출에 관한 정보를 가져온 후
	    일별로 딕셔너리에 데이터를 저장하고 해당 딕셔너리를 리스트에 추가해서  
	    월별 매출 리스트를 생성 후 반환한다.
	 */
	@Override
	public List<Map<String, Object>> getMonthlySales(String year, String month) {
		logger.info("year : ", year);
		logger.info("month : ", month);
		List<Map<String, Object>> result = new ArrayList<>(); // 반환 리스트
		
		ArrayList<Integer> cardList = new ArrayList<>();
		ArrayList<Integer> cashList = new ArrayList<>();
		
		// 일수 반복
		for(int i=1; i<=31; i++) {
			Map<String, Object> mapDaySales = new LinkedHashMap<String, Object>(); // 일간 자료
			mapDaySales.put("days", Integer.toString(i)); // 일
			mapDaySales.put("card", 0);					  // 카드 매출
			mapDaySales.put("cash", 0);					  // 현금 매출
			mapDaySales.put("credit_money", 0);			  // 신용카드 매출
			mapDaySales.put("current_money", 0);		  // 현금 매출
			mapDaySales.put("charge", 0);				  // 충전 합계
			mapDaySales.put("bonus", 0);				  // 보너스 합계
			mapDaySales.put("card_total", 0);			  // 카드매출 누계
			mapDaySales.put("cash_total", 0);			  // 현금매출 누계
			mapDaySales.put("credit_total", 0);			  // 신용카드 매출 누계
			mapDaySales.put("money_total", 0);			  // 현금매출 누계
			
			// 데이터 베이스 사용 기록 가져오기 
			SelfStateVO self = posSalesMapper.selectSelfState(year.substring(2,4), month, String.format("%02d",i));
			AirStateVO air = posSalesMapper.selectAirState(year.substring(2,4), month, String.format("%02d", i));
			MateStateVO mate = posSalesMapper.selectMateState(year.substring(2,4), month, String.format("%02d", i));
			ReaderStateVO reader = posSalesMapper.selectReaderState(year.substring(2,4), month, String.format("%02d", i));
			TodayChargerStateVO charger = posSalesMapper.selectChargerState(year.substring(2,4), month, String.format("%02d", i));
			
			// 현금, 카드 매출
			Integer cash = 0;
			Integer card = 0;
			
			Integer selfCash = 0;
			Integer selfCard = 0;
			Integer airCash = 0;
			Integer airCard = 0;
			Integer mateCash = 0;
			Integer mateCard = 0;
			Integer readerCash = 0;
			Integer readerCard = 0;
			
			Integer charge = 0;
			Integer bonus = 0;
			Integer credit = 0;
			Integer money = 0;
					
			// 소수점 처리
			if (self != null) { 
				self.setUse_card(fmt((long) stringToDouble(self.getUse_card())));
				self.setUse_cash(fmt((long) stringToDouble(self.getUse_cash())));
				selfCard = Integer.parseInt(self.getUse_card());
				selfCash = Integer.parseInt(self.getUse_cash());
			}
			if (air != null) { 
				air.setAir_card(fmt((long) stringToDouble(air.getAir_card())));
				air.setAir_cash(fmt((long) stringToDouble(air.getAir_cash())));
				airCard = Integer.parseInt(air.getAir_card());
				airCash = Integer.parseInt(air.getAir_cash());
			}
			if (mate != null) { 
				mate.setMate_card(fmt((long) stringToDouble(mate.getMate_card())));
				mate.setMate_cash(fmt((long) stringToDouble(mate.getMate_cash())));
				mateCard = Integer.parseInt(mate.getMate_card());
				mateCash = Integer.parseInt(mate.getMate_cash());
			}
			if (reader != null) { 
				reader.setReader_card(fmt((long) stringToDouble(reader.getReader_card())));
				reader.setReader_cash(fmt((long) stringToDouble(reader.getReader_cash())));
				readerCard = Integer.parseInt(reader.getReader_card());
				readerCash = Integer.parseInt(reader.getReader_cash());
			}
			if (charger != null) {
				charger.setCharge(fmt((long) stringToDouble(charger.getCharge())));
				charger.setBonus(fmt((long) stringToDouble(charger.getBonus())));
				charger.setCredit_money(fmt((long) stringToDouble(charger.getCredit_money())));
				charger.setMoney(fmt((long) stringToDouble(charger.getMoney())));
				charge = Integer.parseInt(charger.getCharge()); 
				bonus = Integer.parseInt(charger.getBonus());
				credit = Integer.parseInt(charger.getCredit_money());
				money = Integer.parseInt(charger.getMoney());
			}
			
			// 현금 , 카드 저장
			card = selfCard + airCard + mateCard + readerCard;
			cash = selfCash + airCash + mateCash + readerCash;

			// 토탈
			Integer cardTotal = 0;
			Integer cashTotal = 0;
			Integer creditTotal = 0;
			Integer moneyTotal = 0;
			
			cardList.add(card);
			cashList.add(cash);
			
			// 현금 , 카드 누계 저장
			for(int j=0; j<cardList.size(); j++) {
				cardTotal += cardList.get(j); 
				cashTotal += cashList.get(j);
//				creditTotal += credit;
//				moneyTotal += money;
			}
			
			mapDaySales.replace("card", card);					  // 카드 매출
			mapDaySales.replace("cash", cash);					  // 현금 매출
			mapDaySales.replace("credit_money", credit);		  // 신용카드 매출
			mapDaySales.replace("current_money", money);		  // 현금 매출
			mapDaySales.replace("charge", charge);				  // 충전 합계
			mapDaySales.replace("bonus", bonus);				  // 보너스 합계
			mapDaySales.replace("card_total", cardTotal);		  // 카드매출 누계
			mapDaySales.replace("cash_total", cashTotal);		  // 현금매출 누계
			mapDaySales.replace("credit_total", creditTotal);	  // 신용카드 매출 누계
			mapDaySales.replace("money_total", moneyTotal);		  // 현금매출 누계
			result.add(mapDaySales);
		} // 일수 반복 끝
		return result;
	}
	
	/*
	 * 일간 매출 자료 불러오기
	 * gl_sales_list(view) 에서 일간 매출 자료를 검색하여 반환한다.
     * 참고 : UNION 으로 구성된 VIEW 라서 세차장비와 충전장비의 필드명이 동일함.
             이는 포스에서 device_type 으로 분기하여 다르게 표기함으로써 해결 
	 */
	@Override
	public List<Map<String, Object>> getDaysSales(String year, String month, int days) {
		logger.info("year : ", year);
		logger.info("month : ", month);
		logger.info("days : ", days);
		List<Map<String, Object>> result = new ArrayList<>(); // 반환 리스트
		
		// DB조회
		List<SalesListViewVO> salesDay = posSalesMapper.selectDaySales(year, month, String.format("%02d", days));
		
		for (SalesListViewVO day : salesDay) {
			Map<String, Object> resultMap = new LinkedHashMap<String, Object>();
			resultMap.put("enable_type", "0");
			resultMap.put("credit_money", "0");
			resultMap.put("card", 0);
			resultMap.put("remain_card", 0);
			resultMap.put("current_money", 0);
			resultMap.put("cash", 0);
			resultMap.put("master_card", 0);
			resultMap.put("device_name", "0");
			resultMap.put("device_type", "0");
			resultMap.put("device_addr", "0");
			resultMap.put("card_num", "0");
			resultMap.put("time", "0");
			resultMap.put("start_time", "0");
			resultMap.put("end_time", "0");
			
			if (Integer.parseInt(day.getDevice_type()) == glsConfig.getSelf()) {
				resultMap.replace("enable_type", getSelfDetail(day.getNo(), "self"));
				resultMap.replace("credit_money", "0");
			} else if(Integer.parseInt(day.getDevice_type()) == glsConfig.getAir()) {
				resultMap.replace("enable_type", "진공");
				resultMap.replace("credit_money", "0");
			} else if(Integer.parseInt(day.getDevice_type()) == glsConfig.getMate()) {
				resultMap.replace("enable_type", "매트");
				resultMap.replace("credit_money", "0");
			} else if(Integer.parseInt(day.getDevice_type()) == glsConfig.getReader()) {
				resultMap.replace("enable_type", "리더");
				resultMap.replace("credit_money", "0");
			} else if(Integer.parseInt(day.getDevice_type()) == glsConfig.getGarage()) {
				resultMap.replace("enable_type", " - "/*getSelfDetail(day.getNo(), "garage")*/);
				resultMap.replace("credit_money", "0");
			} else if(Integer.parseInt(day.getDevice_type()) == glsConfig.getKiosk()) {
				resultMap.replace("enable_type", " - ");
				resultMap.replace("credit_money", day.getCredit_money());
			} else if(Integer.parseInt(day.getDevice_type()) == glsConfig.getPos()) {
				resultMap.replace("enable_type", " - ");
				resultMap.replace("credit_money", day.getCredit_money());
			} else {
				resultMap.replace("enable_type", " - ");
				resultMap.replace("credit_money", "0");
			}
			
			Integer cash = Integer.parseInt(fmt((long) stringToDouble(day.getCash())));
			Integer card = Integer.parseInt(fmt((long) stringToDouble(day.getCard())));
			Integer remain_card = Integer.parseInt(fmt((long) stringToDouble(day.getRemain_card())));
			Integer master_card = Integer.parseInt(fmt((long) stringToDouble(day.getMaster_card())));
			Integer current_money = Integer.parseInt(fmt((long) stringToDouble(day.getCurrent_money())));
			
			String deviceName = day.getDevice_name();
			String deviceType = day.getDevice_type();
			String deviceAddr = day.getDevice_addr();
			String cardNum = day.getCard_num();
			String startTime = day.getStart_time();
			String endTime = day.getEnd_time();
			
			resultMap.replace("card", card);
			resultMap.replace("remain_card", remain_card);
			resultMap.replace("current_money", current_money);
			resultMap.replace("cash", cash);
			resultMap.replace("master_card", master_card);
			resultMap.replace("device_name", deviceName);
			resultMap.replace("device_type", deviceType);
			resultMap.replace("device_addr", deviceAddr);
			resultMap.replace("card_num", cardNum);
			resultMap.replace("time", day.getTime());
			resultMap.replace("start_time", startTime);
			resultMap.replace("end_time", endTime);
			
			result.add(resultMap);
		}
		
		return result;
	}
	
    /*
     * 기기별 매출 자료 불러오기(DB -> POS)
	  gl_device_list 에서 각 장비의 수량을 추출하고 해당 장비별로 수량에 맞춰 반복문을
	    실행하여 매출 값을 딕셔너리에 저장하고 이를 반환 리스트에 추가함.
	    매출은 gl_sales_list(View) 한 곳에서 참조하지만, 세차 장비와 충전장비에서 
	    서로 가져와야하는 값이 다르기 때문에 두개의 쿼리로 나눠서 작성함.
     */
	@Override
	public List<Map<String, Object>> getDeviceSales(String year, String month, int days) {
		List<Map<String, Object>> result = new ArrayList<>();
		
		// 셀프 
		for(int i=1; i<=glsConfig.getSelfCount(); i++) {
			Map<String, Object> resultMap = new LinkedHashMap<String, Object>();
			resultMap.put("device_addr", "0");
			resultMap.put("device_name", "");
			resultMap.put("device_type", 0);
			resultMap.put("cash", "0");
			resultMap.put("card", "0");
			
			// 데이터 조회
			SalesListViewVO self = posSalesMapper.selectDeviceSales(year, month, String.format("%02d", days), glsConfig.getSelf(), String.format("%02d", i));
			
			String deviceAddr = "";
			String deviceName = "";
			Integer deviceType = 0;
			String cash = "0";
			String card = "0";
			
			if (self == null) { // 데이터 존재하지 않을떄 매출 0으로 찍음
				deviceAddr = String.format("%02d", i);
				deviceName = "셀프 세차기";
				deviceType = glsConfig.getSelf();
			} else {
				deviceAddr = String.format("%02d", i);
				deviceName = "셀프 세차기";
				deviceType = glsConfig.getSelf();
				cash = fmt((long) stringToDouble(self.getCash()));
				card = fmt((long) stringToDouble(self.getCard()));
			}
			
			resultMap.replace("device_addr", deviceAddr);
			resultMap.replace("device_name", deviceName);
			resultMap.replace("device_type", deviceType);
			resultMap.replace("cash", cash);
			resultMap.replace("card", card);
			result.add(resultMap);
		}
		
		// 진공
		for(int i=1; i<=glsConfig.getAirCount(); i++) {
			Map<String, Object> resultMap = new LinkedHashMap<String, Object>();
			resultMap.put("device_addr", "0");
			resultMap.put("device_name", "");
			resultMap.put("device_type", 0);
			resultMap.put("cash", "0");
			resultMap.put("card", "0");
			
			// 데이터 조회
			SalesListViewVO air = posSalesMapper.selectDeviceSales(year, month, String.format("%02d", days), glsConfig.getAir(), String.format("%02d", i));
			
			String deviceAddr = "";
			String deviceName = "";
			Integer deviceType = 0;
			String cash = "0";
			String card = "0";
			
			if (air == null) { // 데이터 존재하지 않을떄 매출 0으로 찍음
				deviceAddr = String.format("%02d", i);
				deviceName = "진공 청소기";
				deviceType = glsConfig.getAir();
			} else {
				deviceAddr = String.format("%02d", i);
				deviceName = "진공 청소기";
				deviceType = glsConfig.getAir();
				cash = fmt((long) stringToDouble(air.getCash()));
				card = fmt((long) stringToDouble(air.getCard()));
			}
			
			resultMap.replace("device_addr", deviceAddr);
			resultMap.replace("device_name", deviceName);
			resultMap.replace("device_type", deviceType);
			resultMap.replace("cash", cash);
			resultMap.replace("card", card);
			result.add(resultMap);
		}
		
		// 매트
		for(int i=1; i<=glsConfig.getMateCount(); i++) {
			Map<String, Object> resultMap = new LinkedHashMap<String, Object>();
			resultMap.put("device_addr", "0");
			resultMap.put("device_name", "");
			resultMap.put("device_type", 0);
			resultMap.put("cash", "0");
			resultMap.put("card", "0");
			
			// 데이터 조회
			SalesListViewVO mate = posSalesMapper.selectDeviceSales(year, month, String.format("%02d", days), glsConfig.getMate(), String.format("%02d", i));
			
			String deviceAddr = "";
			String deviceName = "";
			Integer deviceType = 0;
			String cash = "0";
			String card = "0";
			
			if (mate == null) { // 데이터 존재하지 않을떄 매출 0으로 찍음
				deviceAddr = String.format("%02d", i);
				deviceName = "매트 청소기";
				deviceType = glsConfig.getMate();
			} else {
				deviceAddr = String.format("%02d", i);
				deviceName = "매트 청소기";
				deviceType = glsConfig.getMate();
				cash = fmt((long) stringToDouble(mate.getCash()));
				card = fmt((long) stringToDouble(mate.getCard()));
			}
			
			resultMap.replace("device_addr", deviceAddr);
			resultMap.replace("device_name", deviceName);
			resultMap.replace("device_type", deviceType);
			resultMap.replace("cash", cash);
			resultMap.replace("card", card);
			result.add(resultMap);
		}
		
		// 리더기
		for(int i=1; i<=glsConfig.getReaderCount(); i++) {
			Map<String, Object> resultMap = new LinkedHashMap<String, Object>();
			resultMap.put("device_addr", "0");
			resultMap.put("device_name", "");
			resultMap.put("device_type", 0);
			resultMap.put("cash", "0");
			resultMap.put("card", "0");
			
			// 데이터 조회
			SalesListViewVO reader = posSalesMapper.selectDeviceSales(year, month, String.format("%02d", days), glsConfig.getReader(), String.format("%02d", i));
			
			String deviceAddr = "";
			String deviceName = "";
			Integer deviceType = 0;
			String cash = "0";
			String card = "0";
			
			if (reader == null) { // 데이터 존재하지 않을떄 매출 0으로 찍음
				deviceAddr = String.format("%02d", i);
				deviceName = "리더기";
				deviceType = glsConfig.getReader();
			} else {
				deviceAddr = String.format("%02d", i);
				deviceName = "리더기";
				deviceType = glsConfig.getReader();
				cash = fmt((long) stringToDouble(reader.getCash()));
				card = fmt((long) stringToDouble(reader.getCard()));
			}
			
			resultMap.replace("device_addr", deviceAddr);
			resultMap.replace("device_name", deviceName);
			resultMap.replace("device_type", deviceType);
			resultMap.replace("cash", cash);
			resultMap.replace("card", card);
			result.add(resultMap);
		}
		
		// 게러지
		
		// 충전기
		for(int i=1; i<=glsConfig.getChargerCount(); i++) {
			Map<String, Object> resultMap = new LinkedHashMap<String, Object>();
			resultMap.put("device_addr", "0");
			resultMap.put("device_name", "");
			resultMap.put("device_type", 0);
			resultMap.put("charge", 0);
			resultMap.put("bonus", 0);
			resultMap.put("card_amount", 0);
			resultMap.put("card_count", 0);
			resultMap.put("credit_money", "0");
			resultMap.put("current_money", "0");
			
			SalesListViewVO charger = posSalesMapper.selectChargerSales(year, month, String.format("%02d", days), 
									glsConfig.getCharger(), String.format("%02d", i));
			
			String deviceAddr = "";
			String deviceName = "";
			Integer deviceType = 0;
			Integer charge = 0;
			Integer bonus = 0;
			Integer card_amount = 0;
			Integer card_count = 0;
			String credit_money = "0";
			String current_money = "0";
			
			if (charger == null) { // 데이터 존재하지 않을떄 매출 0으로 찍음
				deviceAddr = String.format("%02d", i);
				deviceName = "카드 충전기";
				deviceType = glsConfig.getCharger();
			} else {
				deviceAddr = String.format("%02d", i);
				deviceName = "카드 충전기";
				deviceType = glsConfig.getCharger();
				charge = Integer.parseInt(fmt((long) stringToDouble(charger.getCash())));
				bonus = Integer.parseInt(fmt((long) stringToDouble(charger.getCard())));
				card_amount = Integer.parseInt(fmt((long) stringToDouble(charger.getMaster_card())));
				card_count = Integer.parseInt(fmt((long) stringToDouble(charger.getTime())));
				credit_money = fmt((long) stringToDouble(charger.getCredit_money()));
				current_money = fmt((long) stringToDouble(charger.getCurrent_money()));
			}
			
			resultMap.replace("device_addr", deviceAddr);
			resultMap.replace("device_name", deviceName);
			resultMap.replace("device_type", deviceType);
			resultMap.replace("charge", charge);
			resultMap.replace("bonus", bonus);
			resultMap.replace("card_amount", card_amount);
			resultMap.replace("card_count", card_count);
			resultMap.replace("credit_money", credit_money);
			resultMap.replace("current_money", current_money);
			result.add(resultMap);
		}
		
		// 터치
		for(int i=1; i<=glsConfig.getTouchCount(); i++) {
			Map<String, Object> resultMap = new LinkedHashMap<String, Object>();
			resultMap.put("device_addr", "0");
			resultMap.put("device_name", "");
			resultMap.put("device_type", 0);
			resultMap.put("charge", 0);
			resultMap.put("bonus", 0);
			resultMap.put("card_amount", 0);
			resultMap.put("card_count", 0);
			resultMap.put("credit_money", "0");
			resultMap.put("current_money", "0");
			
			SalesListViewVO charger = posSalesMapper.selectChargerSales(year, month, String.format("%02d", days), 
									glsConfig.getTouch(), String.format("%02d", i));
			
			String deviceAddr = "";
			String deviceName = "터치 충전기";
			Integer deviceType = 0;
			String charge = "0";
			String bonus = "0";
			String card_amount = "0";
			Integer card_count = 0;
			String credit_money = "0";
			String current_money = "0";
			
			if (charger == null) { // 데이터 존재하지 않을떄 매출 0으로 찍음
				deviceAddr = String.format("%02d", i);
				deviceName = "터치 충전기";
				deviceType = glsConfig.getTouch();
			} else {
				deviceAddr = String.format("%02d", i);
				deviceName = "터치 충전기";
				deviceType = glsConfig.getTouch();
				charge = fmt((long) stringToDouble(charger.getCash()));
				bonus = fmt((long) stringToDouble(charger.getCard()));
				card_amount =fmt((long) stringToDouble(charger.getMaster_card()));
				card_count = Integer.parseInt(fmt((long) stringToDouble(charger.getTime())));
				credit_money = fmt((long) stringToDouble(charger.getCredit_money()));
				current_money = fmt((long) stringToDouble(charger.getCurrent_money()));
			}
			
			resultMap.replace("device_addr", deviceAddr);
			resultMap.replace("device_name", deviceName);
			resultMap.replace("device_type", deviceType);
			resultMap.replace("charge", charge);
			resultMap.replace("bonus", bonus);
			resultMap.replace("card_amount", card_amount);
			resultMap.replace("card_count", card_count);
			resultMap.replace("credit_money", credit_money);
			resultMap.replace("current_money", current_money);
			result.add(resultMap);
		}
		
		// 키오스크
		for(int i=1; i<=glsConfig.getKioskCount(); i++) {
			Map<String, Object> resultMap = new LinkedHashMap<String, Object>();
			resultMap.put("device_addr", "0");
			resultMap.put("device_name", "");
			resultMap.put("device_type", 0);
			resultMap.put("charge", 0);
			resultMap.put("bonus", 0);
			resultMap.put("card_amount", 0);
			resultMap.put("card_count", 0);
			resultMap.put("credit_money", "0");
			resultMap.put("current_money", "0");
			
			SalesListViewVO charger = posSalesMapper.selectChargerSales(year, month, String.format("%02d", days), 
									glsConfig.getKiosk(), String.format("%02d", i));
			
			String deviceAddr = "";
			String deviceName = "";
			Integer deviceType = 0;
			String charge = "0";
			String bonus = "0";
			String card_amount = "0";
			Integer card_count = 0;
			String credit_money = "0";
			String current_money = "0";
			
			if (charger == null) { // 데이터 존재하지 않을떄 매출 0으로 찍음
				deviceAddr = String.format("%02d", i);
				deviceName = "키오스크";
				deviceType = glsConfig.getKiosk();
			} else {
				deviceAddr = String.format("%02d", i);
				deviceName = "키오스크";
				deviceType = glsConfig.getKiosk();
				charge = fmt((long) stringToDouble(charger.getCash()));
				bonus = fmt((long) stringToDouble(charger.getCard()));
				card_amount = fmt((long) stringToDouble(charger.getMaster_card()));
				card_count = Integer.parseInt(fmt((long) stringToDouble(charger.getTime())));
				credit_money = fmt((long) stringToDouble(charger.getCredit_money()));
				current_money = fmt((long) stringToDouble(charger.getCurrent_money()));
			}
			
			resultMap.replace("device_addr", deviceAddr);
			resultMap.replace("device_name", deviceName);
			resultMap.replace("device_type", deviceType);
			resultMap.replace("charge", charge);
			resultMap.replace("bonus", bonus);
			resultMap.replace("card_amount", card_amount);
			resultMap.replace("card_count", card_count);
			resultMap.replace("credit_money", credit_money);
			resultMap.replace("current_money", current_money);
			result.add(resultMap);
		}
		
		return result;
	}
	
	// 메인 화면 세차장비 오늘자 매출
	@Override
	public List<Map<String, Object>> getTodayDeviceSales() {
		List<Map<String, Object>> result = new ArrayList<>();
		List<SalesListViewVO> selectTodayDeviceSales = posSalesMapper.selectTodayDeviceSales(currentClock());
		
		for(int i=0; i<selectTodayDeviceSales.size(); i++) {
			Map<String, Object> map = new LinkedHashMap<String, Object>();
			
			map.put("device_name", selectTodayDeviceSales.get(i).getDevice_name());
			map.put("device_addr", selectTodayDeviceSales.get(i).getDevice_addr());
			map.put("card_num", selectTodayDeviceSales.get(i).getCard_num());
			map.put("time", selectTodayDeviceSales.get(i).getTime());
			map.put("cash", Integer.parseInt(fmt((long) stringToDouble(selectTodayDeviceSales.get(i).getCash()))));
			map.put("card", Integer.parseInt(fmt((long) stringToDouble(selectTodayDeviceSales.get(i).getCard()))));
			map.put("master", Integer.parseInt(fmt((long) stringToDouble(selectTodayDeviceSales.get(i).getMaster_card()))));
			map.put("remain_card", Integer.parseInt(fmt((long) stringToDouble(selectTodayDeviceSales.get(i).getRemain_card()))));
			map.put("end_time", Integer.parseInt(selectTodayDeviceSales.get(i).getEnd_time()));
			
			result.add(map);
		}
		
		return result;
	}
	
	
	/*
	메인 화면 충전장비 오늘자 매출
	gl_sales_list(View)에서 현금 / 카드 매출을 구하고
    gl_charger_state 에서 카드 충전을 구함.
    	매출 합계 -> 현금 매출 + 카드 매출
    	수입 함계 -> 현금 매출 + 카드 충전
    * 참고 : 카드 충전 -> 투입금액 - 배출금액
    * 변경 : 카드 충전 -> 투입금액
	*/
	@Override
	public List<Map<String, Object>> getTodayChargerSales() {
		
		List<Map<String, Object>> result = new ArrayList<>();
		List<TodayChargerStateVO> selectTodayChargerSales = posSalesMapper.selectTodayChargerSales(currentClock());
		
		for(int i=0; i<selectTodayChargerSales.size(); i++) {
			Map<String, Object> map = new LinkedHashMap<String, Object>();
			
			map.put("device_name", selectTodayChargerSales.get(i).getDevice_name());
			map.put("device_addr", selectTodayChargerSales.get(i).getDevice_addr());
			map.put("card_num", selectTodayChargerSales.get(i).getCard_num());
			map.put("money", Integer.parseInt(fmt((long) stringToDouble(selectTodayChargerSales.get(i).getMoney()))));
			map.put("bonus", Integer.parseInt(fmt((long) stringToDouble(selectTodayChargerSales.get(i).getBonus()))));
			map.put("charge", Integer.parseInt(fmt((long) stringToDouble(selectTodayChargerSales.get(i).getCharge()))));
			map.put("remain_card", Integer.parseInt(fmt((long) stringToDouble(selectTodayChargerSales.get(i).getRemain_card()))));
			map.put("credit_money", Integer.parseInt(fmt((long) stringToDouble(selectTodayChargerSales.get(i).getCredit_money()))));
			map.put("input_date", Integer.parseInt(selectTodayChargerSales.get(i).getInput_date()));
			
			result.add(map);
		}
		return result;
	}
		
	
	/*
	 * 메인화면 매출 총계
    	gl_sales_list(View)에서 현금 / 카드 매출을 구하고
    	gl_charger_state 에서 카드 충전을 구함.
	       매출 합계 -> 현금 매출 + 카드 매출
	       수입 함계 -> 현금 매출 + 카드 충전
    * 참고 : 카드 충전 -> 투입금액 - 배출금액
    * 변경 : 카드 충전 -> 투입금액
	 */
	@Override
	public Map<String, Object> getTodaySalesTotal() {
		Map<String, Object> result = new LinkedHashMap<String, Object>();
		
		try {
			// 금일 현금 / 카드매출
			TodaySalesTotalVO selectTodaySales = posSalesMapper.selectTodaySales(currentClock());
			Integer sales = 0;
			Integer income = 0;
			String cash_sales = "0";
			Integer card_sales = 0;
			
			if (selectTodaySales == null) { // 데이터가 없을떄 처리
				TodaySalesTotalVO todaySales = new TodaySalesTotalVO();
				todaySales.setSales("0");
				todaySales.setIncome("0");
				todaySales.setCash_sales("0");
				todaySales.setCard_sales("0");
				todaySales.setCard_charge("0");
				sales = 0;
			} else { 
				sales = Integer.parseInt(fmt((long) stringToDouble(selectTodaySales.getCash_sales()))) + Integer.parseInt(fmt((long) stringToDouble(selectTodaySales.getCard_sales())));
				cash_sales = fmt((long) stringToDouble(selectTodaySales.getCash_sales()));
				card_sales = Integer.parseInt(fmt((long) stringToDouble(selectTodaySales.getCard_sales())));
			}
			
			// 금일 카드충전
			TodayChargerStateVO selectCardCharger = posSalesMapper.selectTodayCardCharge(currentClock());
			Integer card_charge = 0;
			
			if (selectCardCharger == null) { // 데이터가 없을떄 처리
				TodayChargerStateVO charge = new TodayChargerStateVO(); // DB 객체 결과 널포인터이슈로 인한 인스턴스생성
				charge.setMoney("0");
				charge.setCredit_money("0");
				card_charge = 0;
				income = 0;
			} else {
				// 카드 충전액
				card_charge = Integer.parseInt(fmt((long) stringToDouble(selectCardCharger.getMoney()))) + Integer.parseInt(fmt((long) stringToDouble(selectCardCharger.getCredit_money()))); 
				// 금일 매출 / 수입 구하기
				income = Integer.parseInt(cash_sales) + card_charge;
			}
			
			
			result.put("sales", sales);
			result.put("income", income);
			result.put("cash_sales", cash_sales);
			result.put("card_sales", card_sales);
			result.put("card_charge", card_charge);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	
		return result;
	}
	
	// 장비이력조회
	@Override
	public List<Map<String, Object>> getUseDevice() {
		List<Map<String, Object>> result = new ArrayList<>();
		
		// 셀프 
		for(int i=1; i<=glsConfig.getSelfCount(); i++) {
			Map<String, Object> resultMap = new LinkedHashMap<String, Object>();
			resultMap.put("device_addr", "0");
			resultMap.put("device_name", "");
			resultMap.put("device_type", 0);
			resultMap.put("time", "0");
			resultMap.put("cash", 0);
			resultMap.put("card", 0);
			resultMap.put("master", 0);
			
			// DB 주소별 추출 
			SelfStateVO useSelf = posSalesMapper.selectUseSelfState(String.format("%02d", i));
			String deviceAddr = String.format("%02d", i);
			String deviceName = "셀프 세차기";
			Integer deviceType = glsConfig.getSelf();
			String time = "0";
			Integer cash = 0;
			Integer card = 0;
			Integer master = 0;
			
			if (useSelf != null) {
				cash = Integer.parseInt(fmt((long) stringToDouble(useSelf.getUse_cash())));
				card = Integer.parseInt(fmt((long) stringToDouble(useSelf.getUse_card())));
				master = Integer.parseInt(fmt((long) stringToDouble(useSelf.getMaster_card())));
			}
			
			resultMap.replace("device_addr", deviceAddr);
			resultMap.replace("device_name", deviceName);
			resultMap.replace("device_type", deviceType);
			resultMap.replace("time", time);
			resultMap.replace("cash", cash);
			resultMap.replace("card", card);
			resultMap.replace("master", master);
			result.add(resultMap);
		}
		
		for(int i=1; i<=glsConfig.getAirCount(); i++) {
			Map<String, Object> resultMap = new LinkedHashMap<String, Object>();
			resultMap.put("device_addr", "0");
			resultMap.put("device_name", "");
			resultMap.put("device_type", 0);
			resultMap.put("time", "0");
			resultMap.put("cash", 0);
			resultMap.put("card", 0);
			resultMap.put("master", 0);
			
			// DB 주소별 추출 
			AirStateVO air = posSalesMapper.selectUseAirState(String.format("%02d", i));
			String deviceAddr = String.format("%02d", i);
			String deviceName = "진공 청소기";
			Integer deviceType = glsConfig.getAir();
			String time = "0";
			Integer cash = 0;
			Integer card = 0;
			Integer master = 0;
			
			if (air != null) {
				cash = Integer.parseInt(fmt((long) stringToDouble(air.getAir_cash())));
				card = Integer.parseInt(fmt((long) stringToDouble(air.getAir_card())));
				master = Integer.parseInt(fmt((long) stringToDouble(air.getMaster_card())));
			}
			
			resultMap.replace("device_addr", deviceAddr);
			resultMap.replace("device_name", deviceName);
			resultMap.replace("device_type", deviceType);
			resultMap.replace("time", time);
			resultMap.replace("cash", cash);
			resultMap.replace("card", card);
			resultMap.replace("master", master);
			result.add(resultMap);
		}
		
		for(int i=1; i<=glsConfig.getMateCount(); i++) {
			Map<String, Object> resultMap = new LinkedHashMap<String, Object>();
			resultMap.put("device_addr", "0");
			resultMap.put("device_name", "");
			resultMap.put("device_type", 0);
			resultMap.put("time", "0");
			resultMap.put("cash", 0);
			resultMap.put("card", 0);
			resultMap.put("master", 0);
			
			// DB 주소별 추출 
			MateStateVO mate = posSalesMapper.selectUseMateState(String.format("%02d", i));
			String deviceAddr = String.format("%02d", i);
			String deviceName = "매트 청소기";
			Integer deviceType = glsConfig.getMate();
			String time = "0";
			Integer cash = 0;
			Integer card = 0;
			Integer master = 0;
			
			if (mate != null) {
				cash = Integer.parseInt(fmt((long) stringToDouble(mate.getMate_cash())));
				card = Integer.parseInt(fmt((long) stringToDouble(mate.getMate_card())));
				master = Integer.parseInt(fmt((long) stringToDouble(mate.getMaster_card())));
			}
			
			resultMap.replace("device_addr", deviceAddr);
			resultMap.replace("device_name", deviceName);
			resultMap.replace("device_type", deviceType);
			resultMap.replace("time", time);
			resultMap.replace("cash", cash);
			resultMap.replace("card", card);
			resultMap.replace("master", master);
			result.add(resultMap);
		}
		
		for(int i=1; i<=glsConfig.getReaderCount(); i++) {
			Map<String, Object> resultMap = new LinkedHashMap<String, Object>();
			resultMap.put("device_addr", "0");
			resultMap.put("device_name", "");
			resultMap.put("device_type", 0);
			resultMap.put("time", "0");
			resultMap.put("cash", 0);
			resultMap.put("card", 0);
			resultMap.put("master", 0);
			
			// DB 주소별 추출 
			ReaderStateVO reader = posSalesMapper.selectUseReaderState(String.format("%02d", i));
			String deviceAddr = String.format("%02d", i);
			String deviceName = "리더기";
			Integer deviceType = glsConfig.getReader();
			String time = "0";
			Integer cash = 0;
			Integer card = 0;
			Integer master = 0;
			
			if (reader != null) {
				cash = Integer.parseInt(fmt((long) stringToDouble(reader.getReader_cash())));
				card = Integer.parseInt(fmt((long) stringToDouble(reader.getReader_card())));
				master = Integer.parseInt(fmt((long) stringToDouble(reader.getMaster_card())));
			}
			
			resultMap.replace("device_addr", deviceAddr);
			resultMap.replace("device_name", deviceName);
			resultMap.replace("device_type", deviceType);
			resultMap.replace("time", time);
			resultMap.replace("cash", cash);
			resultMap.replace("card", card);
			resultMap.replace("master", master);
			result.add(resultMap);
		}
		
		for(int i=1; i<=glsConfig.getChargerCount(); i++) {
			Map<String, Object> resultMap = new LinkedHashMap<String, Object>();
			resultMap.put("device_addr", "0");
			resultMap.put("device_name", "");
			resultMap.put("device_type", 0);
			resultMap.put("time", "0");
			resultMap.put("cash", 0);
			resultMap.put("card", 0);
			resultMap.put("master", 0);
			
			// DB 주소별 추출 
			TodayChargerStateVO charger = posSalesMapper.selectUseChargerState(glsConfig.getCharger(), String.format("%02d", i));
			String deviceAddr = String.format("%02d", i);
			String deviceName = "카드 충전기";
			Integer deviceType = glsConfig.getCharger();
			String time = "0";
			Integer cash = 0;
			Integer card = 0;
			Integer master = 0;
			
			if (charger != null) {
				cash = Integer.parseInt(fmt((long) stringToDouble(charger.getCharge())));
				card = Integer.parseInt(fmt((long) stringToDouble(charger.getExhaust_money())));
			}
			
			resultMap.replace("device_addr", deviceAddr);
			resultMap.replace("device_name", deviceName);
			resultMap.replace("device_type", deviceType);
			resultMap.replace("time", time);
			resultMap.replace("cash", cash);
			resultMap.replace("card", card);
			resultMap.replace("master", master);
			result.add(resultMap);
		}
		
		for(int i=1; i<=glsConfig.getTouchCount(); i++) {
			Map<String, Object> resultMap = new LinkedHashMap<String, Object>();
			resultMap.put("device_addr", "0");
			resultMap.put("device_name", "");
			resultMap.put("device_type", 0);
			resultMap.put("time", "0");
			resultMap.put("cash", 0);
			resultMap.put("card", 0);
			resultMap.put("master", 0);
			
			// DB 주소별 추출 
			TodayChargerStateVO charger = posSalesMapper.selectUseChargerState(glsConfig.getTouch(), String.format("%02d", i));
			String deviceAddr = String.format("%02d", i);
			String deviceName = "터치 충전기";
			Integer deviceType = glsConfig.getTouch();
			String time = "0";
			Integer cash = 0;
			Integer card = 0;
			Integer master = 0;
			
			if (charger != null) {
				cash = Integer.parseInt(fmt((long) stringToDouble(charger.getCharge())));
				card = Integer.parseInt(fmt((long) stringToDouble(charger.getExhaust_money())));
			}
			
			resultMap.replace("device_addr", deviceAddr);
			resultMap.replace("device_name", deviceName);
			resultMap.replace("device_type", deviceType);
			resultMap.replace("time", time);
			resultMap.replace("cash", cash);
			resultMap.replace("card", card);
			resultMap.replace("master", master);
			result.add(resultMap);
		}
		
		for(int i=1; i<=glsConfig.getKioskCount(); i++) {
			Map<String, Object> resultMap = new LinkedHashMap<String, Object>();
			resultMap.put("device_addr", "0");
			resultMap.put("device_name", "");
			resultMap.put("device_type", 0);
			resultMap.put("time", "0");
			resultMap.put("cash", 0);
			resultMap.put("card", 0);
			resultMap.put("master", 0);
			
			// DB 주소별 추출 
			TodayChargerStateVO charger = posSalesMapper.selectUseChargerState(glsConfig.getKiosk(), String.format("%02d", i));
			String deviceAddr = String.format("%02d", i);
			String deviceName = "키오스크";
			Integer deviceType = glsConfig.getKiosk();
			String time = "0";
			Integer cash = 0;
			Integer card = 0;
			Integer master = 0;
			
			if (charger != null) {
				cash = Integer.parseInt(fmt((long) stringToDouble(charger.getCharge())));
				card = Integer.parseInt(fmt((long) stringToDouble(charger.getExhaust_money())));
			}
			
			resultMap.replace("device_addr", deviceAddr);
			resultMap.replace("device_name", deviceName);
			resultMap.replace("device_type", deviceType);
			resultMap.replace("time", time);
			resultMap.replace("cash", cash);
			resultMap.replace("card", card);
			resultMap.replace("master", master);
			result.add(resultMap);
		}
		
		return result;
	}
	
	// 장비 세부이력 조회
	@Override
	public List<Map<String, Object>> getUseDeviceDetail(Integer device_type, String device_addr) {
		List<Map<String, Object>> result = new ArrayList<>();
		
		if (device_type==glsConfig.getSelf() || device_type==glsConfig.getAir() || device_type==glsConfig.getMate() 
				|| device_type==glsConfig.getReader() || device_type==glsConfig.getGarage()) {
			
			// 사용기록 추출
			List<SalesListViewVO> deviceList = posSalesMapper.selectUseDetailDevice(device_type, device_addr);
			
			for (SalesListViewVO device : deviceList) {
				Map<String, Object> resultMap = new LinkedHashMap<String, Object>();
				
				if (device_type==glsConfig.getSelf()) {
					resultMap.put("enable_type", getSelfDetail(device.getNo(), "self"));
				} else if(device_type==glsConfig.getAir()) {
					resultMap.put("enable_type", "진공");
				} else if(device_type==glsConfig.getMate()) {
					resultMap.put("enable_type", "매트");
				} else if(device_type==glsConfig.getReader()) {
					resultMap.put("enable_type", "리더");
				}
				
//				Integer card = 0;
//				Integer remain_card = 0;
//				Integer master = 0;
//				Integer cash = 0;
//				Integer start_time = 0;
//				Integer end_time = 0;
//				String time = "";
//				String card_num = " - ";
				
				Integer card = Integer.parseInt(fmt((long) stringToDouble(device.getCard())));;
				Integer remain_card = Integer.parseInt(fmt((long) stringToDouble(device.getRemain_card())));;
				Integer master = Integer.parseInt(fmt((long) stringToDouble(device.getMaster_card())));;
				Integer cash = Integer.parseInt(fmt((long) stringToDouble(device.getCash())));;
				Integer start_time = Integer.parseInt(fmt((long) stringToDouble(device.getStart_time())));;
				Integer end_time = Integer.parseInt(fmt((long) stringToDouble(device.getEnd_time())));;
				String time = fmt((long) stringToDouble(device.getTime()));
				String card_num = device.getCard_num();
				
				resultMap.put("card", card);
				resultMap.put("remain_card", remain_card);
				resultMap.put("master", master);
				resultMap.put("cash", cash);
				resultMap.put("start_time", start_time);
				resultMap.put("end_time", end_time);
				resultMap.put("time", time);
				resultMap.put("card_num", card_num);
				result.add(resultMap);
			}
			
		} else if(device_type==glsConfig.getCharger() || device_type==glsConfig.getTouch() || device_type==glsConfig.getKiosk()) {
			
			// 사용기록 추출
			List<TodayChargerStateVO> chargeList = posSalesMapper.selectUseDetailCharger(device_type, device_addr);
			
			for (TodayChargerStateVO charge : chargeList) {
			
				Map<String, Object> resultMap = new LinkedHashMap<String, Object>();
				
				resultMap.put("enable_type", " - ");
				resultMap.put("input_date", charge.getInput_date());
				resultMap.put("card_num", charge.getCard_num());
				resultMap.put("money", Integer.parseInt(fmt((long) stringToDouble(charge.getMoney()))));
				resultMap.put("charge", Integer.parseInt(fmt((long) stringToDouble(charge.getCharge()))));
				resultMap.put("remain_card", Integer.parseInt(fmt((long) stringToDouble(charge.getRemain_card()))));
				resultMap.put("card_price", Integer.parseInt(fmt((long) stringToDouble(charge.getExhaust_money()))));
				resultMap.put("credit_money", Integer.parseInt(fmt((long) stringToDouble(charge.getCredit_money()))));
				resultMap.put("bonus", Integer.parseInt(fmt((long) stringToDouble(charge.getBonus()))));
				
				if (charge.getKind().equals("0")) {
					resultMap.put("kind", "발급");
				} else {
					resultMap.put("kind", "충전");
				}
				result.add(resultMap);
			}
		}	
		return result;
	}
	
	/*
	 * 실시간 모니터링(LAN)  
		키오스크 및 터치충전기의 모니터링 상태를 전송하는 함수
		통신상태를 체크하는 PING에 TIMEOUT 1초가 걸려있기 때문에 485와 별도로 분리하였다.
		키오스크는 직접 데이터 베이스에 저장하고, 터치 충전기는 별도의 스레드가 있기 때문에
		장비에서 직접 동작값을 가져오는 별도의 행위는 없으며 DB에 저장된 값을 조회하여 
		전달한다. 
	 */
	@Scheduled(cron="*/30 * * * * *") // 30초에 한번씩 호출 
	@Override
	public List<Map<String, Object>> getLanDeviceState() {
		List<Map<String, Object>> result = new ArrayList<>();
		Map<String, Object> resultMap = new LinkedHashMap<String, Object>();
		
		try {
			List<DeviceListVO> deviceList = DeviceMapper.selectDeviceInfoList(glsConfig); // 장비 정보 추출 
			
			// 터치 충전기 기록 가져오기
			String res = touchService.getChargerState();   
				
			for(int i=1; i<=glsConfig.getTouchCount(); i++ ) {
				
				// TODO 2020/12/10  데이터 수집장치 : 발급일때 기록 올라가지 않음
				JsonChargerStateVO state = DeviceMapper.selectChargerMonitor(glsConfig.getTouch(), String.format("%02d", i));
				if (state.getCharge() == null) {
					state.setCharge("0");
				}
				
				resultMap.put("device_type", glsConfig.getTouch());
				resultMap.put("device_addr", String.format("%02d", i));
				resultMap.put("connect", res);
				resultMap.put("charge", Integer.parseInt(fmt((long) stringToDouble(state.getCharge()))));
				resultMap.put("count", state.getCount());
				result.add(resultMap);
			}
			
			for (int i=1; i<=glsConfig.getKioskCount(); i++ ) {
				
				JsonChargerStateVO state = DeviceMapper.selectChargerMonitor(glsConfig.getKiosk(), String.format("%02d", i));
				if (state.getCharge() == null) {
					state.setCharge("0");
				}
				
				resultMap.put("device_type", glsConfig.getKiosk());
				resultMap.put("device_addr", String.format("%02d", i));
				resultMap.put("connect", res);
				resultMap.put("charge", Integer.parseInt(fmt((long) stringToDouble(state.getCharge()))));
				resultMap.put("count", state.getCount());
				result.add(resultMap);
			}
			
		} catch (Exception e) {
			logger.info("실시간 모니터링 LAN Exception {}", e.getMessage());
		}
		
		return result;
	}
	
	// 마스터 카드 이력 조회 
	@Override
	public List<Map<String, Object>> getMasterCardHistory() {
		List<Map<String, Object>> result = new ArrayList<>();
		Map<String, Object> resultMap = new LinkedHashMap<String, Object>();
		
		try {
			// 마스터 카드 이력 가져오기
			List<SalesListViewVO> masterCardList = posSalesMapper.selectMasterCardHistory();  
			Integer totalMoney = 0;
			
			for (SalesListViewVO master : masterCardList) {
				
				String deviceName = "";
				String deviceAddr = "";
				Integer money = 0;
				String inputDate = "";
				
				if (master.getMaster_card() == null) {
					master.setMaster_card("0");
				}
				
				deviceName = master.getDevice_name();
				deviceAddr = master.getDevice_addr();
				money = Integer.parseInt(fmt((long) stringToDouble(master.getMaster_card()))) * 100;
				inputDate = master.getEnd_time();
				totalMoney += money;
				
				resultMap.put("device_name", deviceName);
				resultMap.put("device_addr", deviceAddr);
				resultMap.put("money", money);
				resultMap.put("input_date", inputDate);
				resultMap.put("total_money", totalMoney);
				result.add(resultMap);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	// 소수점 포매팅
	public double stringToDouble(String data) {
		if (data == null) {
			return Double.parseDouble("0");
		} else { 
			return Double.parseDouble(data);
		}
	}
	// 소수점 포매팅 (정수,실수 -> 문자열 리턴)
	public String fmt(double data){
	    if (data == (long) data) {
	        return String.format("%d", (long)data);
	    } else {
	        return String.format("%s", data);
	    }
	}
	
	// 현재 시간 추출 
	public String currentClock() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(new Date());
	}
	
	// 셀프 사용 내역 
	public String getSelfDetail(Integer no, String type) {
		
		String useDetail = "";
		
		if (type.equals("self")) {
			SelfStateVO self = posSalesMapper.getSelfDetail(no, type);
			
			if (!self.getSelf_time().equals("0000")) {
				useDetail += "셀프/";
			} if (!self.getFoam_time().equals("0000")) {
				useDetail += "폼/";
			} if (!self.getUnder_time().equals("0000")) {
				useDetail += "하부/";
			} if (!self.getCoating_time().equals("0000")) {
				useDetail += "코팅/";
			}  
			
			useDetail = useDetail.substring(0, useDetail.length()-1);
			
		} else if (type.equals("garage")) {
//			GarageStateVO self = posSalesMapper.getGarageDetail(no, type);
		}
		return useDetail;
	}
	
}

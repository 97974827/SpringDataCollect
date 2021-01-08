package kr.gls.myapp.test;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortList;
import kr.gls.myapp.common.DeviceListVO;
import kr.gls.myapp.common.GlsConfigVO;
import kr.gls.myapp.device.model.AirStateVO;
import kr.gls.myapp.device.model.ChargerConfigVO;
//import kr.gls.myapp.common.SchedulerService;
import kr.gls.myapp.device.model.JsonChargerStateVO;
import kr.gls.myapp.device.model.MateStateVO;
import kr.gls.myapp.device.model.ReaderStateVO;
import kr.gls.myapp.device.model.SelfStateVO;
import kr.gls.myapp.device.repository.IDeviceMapper;
import kr.gls.myapp.device.service.IDeviceService;
import kr.gls.myapp.pos.model.CardVO;
import kr.gls.myapp.pos.model.DeviceInfoVO;
import kr.gls.myapp.pos.model.MemberVO;
import kr.gls.myapp.pos.model.SalesListViewVO;
import kr.gls.myapp.pos.model.TodayChargerStateVO;
import kr.gls.myapp.pos.model.TodaySalesTotalVO;
import kr.gls.myapp.pos.repository.IPosMapper;
import kr.gls.myapp.pos.service.IPosSalesService;
import kr.gls.myapp.touch.model.TouchConfigGetVO;
import kr.gls.myapp.touch.repository.ITouchChargerMapper;
import kr.gls.myapp.touch.service.ITouchChargerService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"file:src/main/webapp/WEB-INF/spring/mvc-config.xml"})
public class JUnitTesting {
	
	// 마이바티스 주석설정 이후 단위 테스트 
	
	@Autowired
	ITouchChargerMapper mapper;
	
	@Autowired
	IDeviceMapper device;
	
	@Autowired
	IPosMapper posMapper;
	
	private SerialPort serialPort;
	
//	@Autowired
//	private SchedulerService schedulerService;
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Test
	public void mapStringCompare() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("호구는", "너");
		String na = "너";
		
		if(na.equals(map.get("호구는"))) {
			logger.info("값이 맞음 {} {}", na, map.get("호구는"));	
		}else { 
			logger.error("값이 틀림");
		}
	}
	
	@Test 
	public void stringTest() {
		String str = "20000";
		System.out.println(String.format("%04d", Integer.parseInt(str) / 100));
		
//		String str = "15FED842^16098CA2^45dd2577";
//		String[] strArr = str.split("^");
////		String str = "서울.대전.대구.부산.인천.울산";
////		String[] strArr = str.split(".");
//		for (String s : strArr) {
//			System.out.println(s);
//		}
//		for(int i=0; i<strArr.length; i++) {
//			System.out.println(i+ " > " +strArr[i]);
//		}
		
	}
	
	@Test
	public void methodDBTest() {
		JsonChargerStateVO state = device.selectChargerMonitor(6, "01");
		System.out.println(state.toString());
		
//		DeviceListVO device = device.selectDeviceInfoList(glsConfig);	
//		System.out.println(device.toString());
		
//		MemberVO member = posMapper.selectToName("강현진");
//		System.out.println(member.toString());
		
//		List<SalesListViewVO> memberList = posMapper.selectMemberDetail("43E1C5AB");
//		
//		for (SalesListViewVO member : memberList) {
//			System.out.println(member.toString());
//		}
		
//		List<String> cardNum = posMapper.selectMemberCard("2");
//		MemberVO member = posMapper.selectOneCardUser("1");
////		for (String s : cardNum) {
//			System.out.println(member.toString());
////		}
//		MemberVO member = posMapper.selectMemberInfo("1");
//		System.out.println(member.toString());
//		List<SalesListViewVO> cardDetailList = posMapper.selectCardHistoryDeTail("43EA742B"); // 카드이력 불러오기
//		
//		for(SalesListViewVO card : cardDetailList) {
//			System.out.println(card.toString());
//		}
//		List<CardVO> cardList = posMapper.getCardHistory();
//		
////		List<DeviceInfoVO> deviceInfo = posMapper.selectDeviceInfo();
////		
//		for(int i=0; i<cardList.size(); i++) {
//			System.out.println(cardList.get(i).toString());
//		}
//		List<TodayChargerStateVO> vo = posMapper.selectUseDetailCharger(3, "01");
//		
//		for(TodayChargerStateVO s : vo) {
//			System.out.println(s.toString());
//		}
		
//		SalesListViewVO charger = posMapper.selectChargerSales("2020", "12", "06", 6, String.format("%02d", 1));
//		System.out.println(charger.toString());
		
//		SalesListViewVO 
//		[no=null, device_name=null, 
//		device_type=3, device_addr=null, card_num=null, 
//		time=0, cash=25000.0, card=4000.0, remain_card=null, 
//		master_card=1000.0, start_time=null, end_time=null, 
//		current_money=22000.0, credit_money=0.0]
		
		/*for(int i=1; i<=6; i++) {
			SalesListViewVO self = posMapper.selectDeviceSales("2020", "12", "06", 0, String.format("%02d", i));
			
			if (self == null) {
				SalesListViewVO noSelf = new SalesListViewVO();
				noSelf.setCash("0");
				noSelf.setCard("0");
				noSelf.setDevice_addr(String.format("%02d", i));
				System.out.println(noSelf.toString());
			} else {
				System.out.println(self.toString());
			}
			
		}*/
	}
	
	@Test
	public void logTest() {
		Map<String, Object> result = new LinkedHashMap<String, Object>();
		
		TodaySalesTotalVO selectTodaySales = posMapper.selectTodaySales("2020-12-07");
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
		TodayChargerStateVO selectCardCharger = posMapper.selectTodayCardCharge("2020-12-07");
		Integer card_charge = 0;
		
		if (selectTodaySales == null) { // 데이터가 없을떄 처리
			TodayChargerStateVO charge = new TodayChargerStateVO(); // DB 객체 결과 널포인터이슈로 인한 인스턴스생성
			charge.setMoney("0");
			charge.setCredit_money("0");
			card_charge = 0;
			income = 0;
		} else {
			// 카드 충전액
			card_charge = Integer.parseInt(fmt((long) stringToDouble(selectCardCharger.getMoney()))); 
			// 금일 매출 / 수입 구하기
			income = Integer.parseInt(cash_sales) + card_charge;
		}
		
		
		result.put("sales", sales);
		result.put("income", income);
		result.put("cash_sales", cash_sales);
		result.put("card_sales", card_sales);
		result.put("card_charge", card_charge);
		System.out.println(result.get("sales"));
		System.out.println(result.get("income"));
		System.out.println(result.get("cash_sales"));
		System.out.println(result.get("card_sales"));
		System.out.println(result.get("card_charge"));
//        schedulerService.register();
//        schedulerService.remove();
	}
	 
	
	public double stringToDouble(String data) {
		if (data == null) {
			return Double.parseDouble("0");
		} else { 
			return Double.parseDouble(data);
		}
	}
	
	// 소수점 포매팅
	public String fmt(double data){
	    if (data == (long) data) {
	        return String.format("%d", (long)data);
	    } else {
	        return String.format("%s", data);
	    }
	}
	
	@Test
	public void dotformatt() {
		String s = "2000.0";
		System.out.println(fmt((long) stringToDouble(s)));
	}
	
	@Test
	public void selectChargerMo() {
		JsonChargerStateVO json = device.selectChargerMonitor(3, "01");
		System.out.println(json.getCharge());
		System.out.println(json.getCount());
		json.setCharge(fmt((long) stringToDouble(json.getCharge())));
		System.out.println(json.getCharge());
	}
	
	@Test
	public void todaySales() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String today = sdf.format(new Date());
		System.out.println(today);
		System.out.println(device.selectSelfSales(today, "06"));
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("git", "호ㅜ");
		System.out.println(map.get("git"));
		String dd = device.selectSelfSales(today, "04");
		System.out.println(dd);
//		String num = String.format("%d", (long)Double.parseDouble(device.selectSelfSales(today, "04")));
//		map.replace("git", num);
//		System.out.println(map.get("git"));
//		String num = String.format("%d", (long)Double.parseDouble(device.selectSelfSales(today, "06")));
//		map.replace("git", num);
//		System.out.println(map.get("git"));
//		num = String.format("%d", (long)Double.parseDouble("0"));
//		map.replace("git", num);
//		System.out.println(map.get("git"));
//		num = String.format("%d", (long)Double.parseDouble(device.selectSelfSales(today, "01")));
//		map.replace("git", num);
//		System.out.println(map.get("git"));
//		num = String.format("%d", (long)Double.parseDouble(device.selectReaderSales(today, "02")));
//		map.replace("git", num);
//		System.out.println(map.get("git"));
//		num = String.format("%d", (long)Double.parseDouble(device.selectReaderSales(today, "01")));
//		map.replace("git", num);
//		System.out.println(map.get("git"));
	}
	
	@Test
	public void testMe() {
		String addr = "0000524";
		System.out.println(addr);
		System.out.println(Integer.parseInt(addr)*100);
		
		String master = "0";
		System.out.println(master);
		System.out.println(Integer.parseInt(master)*100);
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void time() {
//		Calendar cal = Calendar.getInstance();
//		
//		int year = cal.get(Calendar.YEAR);
//		int month = cal.get(Calendar.MONTH) + 1;
//		int day = cal.get(Calendar.DAY_OF_MONTH);
//		int hour = cal.get(Calendar.HOUR_OF_DAY);
//		int min = cal.get(Calendar.MINUTE);
//		int sec = cal.get(Calendar.SECOND);
//		
//		System.out.println(String.format("%02d", year).substring(0,2));
//		System.out.println(String.format("%02d", month));
//		System.out.println(String.format("%02d", day));
//		System.out.println(String.format("%02d", hour));
//		System.out.println(String.format("%02d", min));
//		System.out.println(String.format("%02d", sec));
		
		// TODO 11-30 시작 시간 구하기 미완료
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String currentTime = sdf.format(new Date()); // 현재 시간
		try {
			System.out.println(currentTime);  
			Date reqDate = sdf.parse(currentTime); // Date formate Mon Nov 30 17:08:39 KST 2020
			long reqDateTime = reqDate.getTime(); // 초단위 환산 1606723719000
			System.out.println(reqDateTime);  
			String useTime = "1030";
			
//			System.out.println(reqDate.getSeconds());
//			System.out.println(System.currentTimeMillis());
			
			System.out.println(useTime);
//			System.out.println(reqDateTime - Long.parseLong(useTime)); // 1606724608445
//GL 064 SR 00 01 6 14 00 07 00000 02340 00000 00000 00020 00000 0026 e132f379 64 CH
			
			String start = sdf.format((reqDateTime/1000) - Long.parseLong(useTime));// / 1000.0 ) );
			System.out.println(start);
			
//			GL 077 SW 00 01 003 95 11 70 22 22 28 22 22 57 e132f379 02360 0030 0000 0000 0030 0000 0000 0000 73 CH
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void mapTest() {
		Map<String, Object> resultMap = new LinkedHashMap<String, Object>();
		resultMap.put("hi", "me");
		System.out.println(resultMap.get("hi"));
		resultMap.replace("hi", "mydddd");
		System.out.println(resultMap.get("hi"));
	}
	
	
	@Test
	public void mapListTest() {
		List<Map<String, Object>> list = new ArrayList<>();
		Map<String, Object> map = new HashMap<String, Object>();
		
			map.put("국어", "10");
			map.put("수학", "20");
			map.put("영어", "30");
	}
	
	@Test
	public void forTest() {
		for(int i=6+1; i<=8; i++) {
			System.out.println(i);
		}
		String str = "03";
		System.out.println(Integer.parseInt(str.substring(0,1)));
		System.out.println(Integer.parseInt(str.substring(1)));
	}
	
	@Test
	public void StringtoByteHex() {
		String str = "03";
		String res = "";
		String[] result = new String[str.length()];
		StringBuilder sb = new StringBuilder();
		int temp = 0;
//		char ch;
		byte[] buff = new byte[3/* str.length() */];
		
//		바이트배열 결과가 10진수로 변환됨
		for(int i=0; i<str.length(); i++) {
//			ch = str.charAt(i);
//			res = Character.toString(ch);
//			System.out.println(Integer.parseInt(res, 16));
//			buff[i] = (byte) ( (Integer) Integer.parseInt(res, 16) ).byteValue();
//			System.out.println(buff[i]);
	
//			//back String
//			int intVal = Integer.parseInt(s4, 16);
//			char charVal = (char) intVal;
//			System.out.println("The character is : " + charVal);
			
			int ch = (int) str.charAt(i);
			String s4 = Integer.toHexString(ch);
//			System.out.println(i + " output -> " + s4); // String to Hex : 30 33
			buff[i] = (byte) Integer.parseInt(s4, 16);
//			System.out.println(buff[i]);
			
//			ch = str.charAt(i);
//			res = String.format("%02d", Integer.parseInt(Character.toString(ch), 16));
//			System.out.println(res);
			
//			temp = Integer.parseInt(res);
//			System.out.println(temp);
			
//			res = String.format("0x%02X", Integer.parseInt(Character.toString(ch)));
//			System.out.println(res); // 0x00 0x03
//			buff[i] = (byte) Byte.parseByte(res);
		}
		buff[2] = (byte) 0x47;
		System.out.println(buff[0]);
		System.out.println(buff[1]);
		System.out.println(buff[2]);
	}
	
	@Test
	public void bytePrinting() {
		
//		String str = "03";
//		for(int i=0; i<str.length(); i++) {
//			Long intStr = Long.parseLong(s, 16);
//			System.out.println(intStr);
//		}
		
		
//		byte key = 0x01;
//		System.out.println(key);
	}
	
//	@Test
//	public static String bytesToHexString(byte[] bytes){ 
//		StringBuilder sb = new StringBuilder(); 
//		for(byte b : bytes){ 
//			sb.append(String.format("%02x", b&0xff)); 
//		}
//		return sb.toString(); 
//	}

	@Test
	public void moneyFormatting() {
		String rs = "1500";
		int money = 0;
		System.out.println(money);
		String card = "";
		money = Integer.parseInt(rs);
		if(money == 0) {
			card ="0";
		} else if(money > 0 && money < 10000) {
			card = String.format("0%d", (money / 100));
		} else if(money>=10000 && money<100000){
			card = String.format("%d", (money / 100));
		} else {
			card = String.format("%d", (money / 100));
		}
		System.out.println(card);
	}
	
	@Test
	public void zeroFillTest() {
		String str = "1000";
		int money = Integer.parseInt(str);
//		GL 111 SS 03 00 01 015 060 1 005 010 003 1 1 05 020 030 005 010 010 060 
//		1 1 04 020 030 005 010 010 1 1 03 020 030 005 010 010 500 1 01 30 0 030 1 0 1 0 01 00 511CH
//		System.out.println(config.length());
		
//		System.out.println(money*10);
		
		logger.info("{}", String.format("%01d", money/100));
		logger.info("{}", String.format("%02d", money/100));
		logger.info("{}", String.format("%03d", money/100));
		logger.info("{}", String.format("%04d", money));
		
//		INFO : kr.gls.myapp.test.JUnitTesting - 5
//		INFO : kr.gls.myapp.test.JUnitTesting - 05
//		INFO : kr.gls.myapp.test.JUnitTesting - 005
//		INFO : kr.gls.myapp.test.JUnitTesting - 0005
	}
	
	@Test
	public void selectChargerConfig() {
		ChargerConfigVO charger = device.selectChargerConfig(3, "01");
		System.out.println(charger.toString());
		long start = System.currentTimeMillis(); //시작하는 시점 계산
		 
		/*
		실행시간을 측정하고싶은 코드
		*/
		 
		long end = System.currentTimeMillis(); //프로그램이 끝나는 시점 계산
		System.out.println( "실행 시간 : " + ( end - start ) / 1000.0 + "초"); //실행 시간 계산 및 출력


//		출처: https://emmadeveloper.tistory.com/25 [emmaiswatson Developer]
	}
	
	/*@Test
	public void selectTest() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("type", 6);
		map.put("addr", "01");
		TouchConfigGetVO vo = mapper.getTouchConfigVO(map);
		System.out.println(vo.toString());
	}
	
	@Test
	public void insertConfigTest() {
		Map<String, Object> map = new HashMap<>(); // 전달 맵 
		map.put("type", 6);
		map.put("addr", "01");
		
		TouchConfigGetVO touch = mapper.getTouchConfigVO(map); // 데이터 수집장치 SQL호출
		
		touch.setType((Integer) map.get("type"));
		touch.setDeviceAddr((String) map.get("addr"));
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String inputDate = sdf.format(new Date());	// 오늘날짜
		touch.setInputDate(inputDate);
		
		System.out.println(touch.toString());
		mapper.insertChargerConfig(touch);
	}*/
	
	
	
}

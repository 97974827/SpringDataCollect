package kr.gls.myapp.device.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jssc.SerialPortException;
import kr.gls.myapp.common.GlsConfigVO;
import kr.gls.myapp.common.ProgrammableScheduler;
import kr.gls.myapp.device.model.AirStateVO;
import kr.gls.myapp.device.model.ChargerConfigVO;
import kr.gls.myapp.device.model.ChargerStateVO;
import kr.gls.myapp.device.model.JsonChargerStateVO;
import kr.gls.myapp.device.model.JsonDeviceStateVO;
import kr.gls.myapp.device.model.MateStateVO;
import kr.gls.myapp.device.model.ReaderConfigVO;
import kr.gls.myapp.device.model.ReaderStateVO;
import kr.gls.myapp.device.model.SelfConfigVO;
import kr.gls.myapp.device.model.SelfStateVO;
import kr.gls.myapp.device.model.WashTotalVO;
import kr.gls.myapp.device.repository.IDeviceMapper;
import kr.gls.myapp.pos.repository.IPosMapper;
import kr.gls.myapp.touch.repository.ITouchChargerMapper;

@Service
public class DeviceService implements IDeviceService { // 알고리즘 직접적으로 계산,제어해주는 서비스 (DB액세스 하기 전단계)
	
	@Autowired
	private IDeviceMapper deviceMapper; // DB접근 485 메퍼 인스턴스 
	
	@Autowired
	private IPosMapper PosConfigMapper; // DB접근 포스 매퍼 인스턴스
	
	@Autowired
	private IPosMapper PosCardMapper;
	
	@Autowired
	private ITouchChargerMapper touchMapper;
	
	@Autowired
	private GlsConfigVO glsConfig;
	
	@Autowired
    ProgrammableScheduler scheduler;
	
//	@Autowired
//	private SchedulerService schedulerService; // 스케쥴링 실행/중지 
	
	private Logger logger = LoggerFactory.getLogger(this.getClass()); // 로그 
	
	private static boolean isTime = false; // 시간 설정 논리변수 (프로그램 시작시 1번만 호출하기 위함)
	
	
	
	// 메인 진입시 호출 메서드 
	@Override
	public void startThread() { 
		String[] addrList = null; // 주소 목록 
		
		try {
			if (!isTime) {
				selectTimeMapper(addrList, glsConfig.getSelf());
				selectTimeMapper(addrList, glsConfig.getAir());
				selectTimeMapper(addrList, glsConfig.getMate());
				selectTimeMapper(addrList, glsConfig.getCharger());
				selectTimeMapper(addrList, glsConfig.getReader());
				selectTimeMapper(addrList, glsConfig.getGarage());
			}
			isTime = true; // 타임 플래그 활설화  
			
			// 485 스레드 동작 중이지 않을떄 
			if (!scheduler.getIsThread()) {
				scheduler.startScheduler(); // 스케쥴러 시작
			}
 
			
		} catch (Exception e) {
			logger.info("startThread Exception >> ");
            e.printStackTrace();
        }
	}
	
	// 시간 연결 메서드 
	@Override
	public void selectTimeMapper(String[] addrList, Integer type) {
		try {
			addrList = deviceMapper.selectDeviceAddr(type); // 주소 DB셀렉트
			Integer countEnd = 0; 
			
			if (type == glsConfig.getSelf()) {
				countEnd = glsConfig.getSelfCount();
			} else if (type == glsConfig.getAir()) {
				countEnd = glsConfig.getAirCount(); 
			} else if (type == glsConfig.getMate()) {
				countEnd = glsConfig.getMateCount(); 
			} else if (type == glsConfig.getCharger()) {
				countEnd = glsConfig.getChargerCount(); 
			} else if (type == glsConfig.getReader()) {
				countEnd = glsConfig.getReaderCount(); 
			} /*else if (type == glsConfig.getGarage()) {
				countEnd = glsConfig.getGarageCount(); 
			}*/
			
			for (int i=1; i<=countEnd; i++) {
				setTime(String.format("0%d", type), addrList[i-1]);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// 시간 호출 : set_time
	@Override
	public void setTime(String deviceType, String deviceAddr) {
		try {
			Calendar cal = Calendar.getInstance();
			
			int year = cal.get(Calendar.YEAR);
			int month = cal.get(Calendar.MONTH) + 1;
			int day = cal.get(Calendar.DAY_OF_MONTH);
			int hour = cal.get(Calendar.HOUR_OF_DAY);
			int min = cal.get(Calendar.MINUTE);
			int sec = cal.get(Calendar.SECOND);
			
			String state = "";
			state += "GL029TS";
			state += glsConfig.getManager_no();
			state += deviceType;
			state += deviceAddr;
			state += String.format("%02d", year).substring(0,2);
			state += String.format("%02d", month);
			state += String.format("%02d", day);
			state += String.format("%02d", hour);
			state += String.format("%02d", min);
			state += String.format("%02d", sec);
			state += checkSum(state);
			state += "CH";
			
			logger.info("Time Trans : {}", state);
			glsConfig.getSerialPort().writeBytes(state.getBytes()); // Write data to port
//				for(int i=0; i<state.length(); i++) {
//					glsConfig.getSerialPort().writeByte((byte) state.charAt(i));//Write data to port
//				}
			Thread.sleep(100);
			
			byte[] readByte = glsConfig.getSerialPort().readBytes(); // 데이터 담을 변수 
			
			// 데이터 수신 완료 
			if (readByte != null && readByte.length > 0) {
				String readStr = new String(readByte); // 문자열로 변환 
				readStr = readStr.replace(" ", "0");
				logger.info("Time SET : {}", readStr);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
		
	// TODO 실시간 모니터링 : 현재방법은 데이터 다받아서 한꺼번에 뿌리는 방식  
	// TODO 2021 01/06 리더기 DB 데이터 저장안됨 
	@Override
	//@Scheduled(fixedDelay=4000) //cron="*/3 * * * * *")
//	@Scheduled(fixedRateString = "${myscheduler.period}", initialDelay = 4000) 
	public List<Map<String, Object>> getDeviceState() {
		
		List<Map<String, Object>> result = new ArrayList<>();
		Map<String, Object> resultMap = new LinkedHashMap<String, Object>(); 
		
		try {
			logger.info("1 Loop Start");
			
			// device addr select
			String[] selfAddrList = deviceMapper.selectDeviceAddr(glsConfig.getSelf()); 
//			String[] airAddrList = deviceMapper.selectDeviceAddr(glsConfig.getAir()); 
//			String[] mateAddrList = deviceMapper.selectDeviceAddr(glsConfig.getMate()); 
			String[] chargerAddrList = deviceMapper.selectDeviceAddr(glsConfig.getCharger());
//			String[] coinAddrList = deviceMapper.selectDeviceAddr(glsConfig.getCoin()); 
//			String[] billAddrList = deviceMapper.selectDeviceAddr(glsConfig.getBill()); 
//			String[] touchAddrList = deviceMapper.selectDeviceAddr(glsConfig.getTouch());
//			String[] kioskAddrList = deviceMapper.selectDeviceAddr(glsConfig.getKiosk()); 
			String[] readerAddrList = deviceMapper.selectDeviceAddr(glsConfig.getReader()); 
//			String[] garageAddrList = deviceMapper.selectDeviceAddr(glsConfig.getGarage()); 
			
			for (String s : selfAddrList) {
				writeState(String.format("0%d", glsConfig.getSelf()), s);
				resultMap = readState();
				result.add(resultMap);
			}
//			for (String s : airAddrList) {
//				writeState(String.format("0%d", glsConfig.getAir()), s);
//				resultMap = readState();
//				result.add(resultMap);
//			}
//			for (String s : mateAddrList) {
//				writeState(String.format("0%d", glsConfig.getMate()), s);
//				resultMap = readState();
//				result.add(resultMap);
//			}
			for (String s : chargerAddrList) {
				writeState(String.format("0%d", glsConfig.getCharger()), s);
				resultMap = readState();
				result.add(resultMap);
			}
			for (String s : readerAddrList) {
				writeState(String.format("0%d", glsConfig.getReader()), s);
				resultMap = readState();
				result.add(resultMap);
			}
//			for (String s : garageAddrList	) {
//				writeState(String.format("%02d", glsConfig.getGarage()), s);
//				resultMap = readState();
//				result.add(resultMap);
//			}
			
		} catch (Exception e) {
			logger.info("실시간 모니터링 Exception >>");
			e.printStackTrace();
		}
		
		return result;
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
	
	// 동작 상태 호출 : get_device_state
	@Override
	public void writeState(String deviceType, String deviceAddr) {
		try {
			String state = ""; // 송신 프로토콜 
			state += "GL017RD";
			state += glsConfig.getManager_no();
			state += deviceType;
			state += deviceAddr;
			state += checkSum(state);
			state += "CH";
			
			logger.info("Tx : {}", state);
			glsConfig.getSerialPort().writeBytes(state.getBytes()); // Write data to port
//			for(int i=0; i<state.length(); i++) {
//				glsConfig.getSerialPort().writeByte((byte) state.charAt(i));//Write data to port
//			}
			Thread.sleep(200);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*
	동작상태 프로토콜 읽어와서 포스 데이터 송신 및 DB저장 메서드
	- 저장값 없을떄 : total 금액 update
	- 저장값 있을떄 : 시간설정 후 DB insert
	기본 : 맵 데이터 반환 -> 포스
	*/
	@Override
	public Map<String, Object> readState() {
		Map<String, Object> resultMap = new LinkedHashMap<String, Object>(); // 리턴값
		 
		try {
//			while (true) {
			byte[] readByte = glsConfig.getSerialPort().readBytes(); // 시리얼 읽기 
			
			// 데이터 수신 완료 
			if (readByte != null && readByte.length > 0) {
				
				String readStr = new String(readByte); // 문자열로 변환 
				readStr = readStr.replace(" ", "0");
				logger.info("Rx : {}", readStr); 
				
				// 타입 검사
				Integer tempType = Integer.parseInt(readStr.substring(8,9));  
				
				if (readStr.contains("GL")) { // 시작값 포함일떄
					// 세차장비일떄
					if (tempType==glsConfig.getSelf() || tempType==glsConfig.getAir() || tempType==glsConfig.getMate()
							|| tempType==glsConfig.getReader() || tempType==glsConfig.getGarage()) {
						
						// 리턴값 초기화
						resultMap.put("state", "0");
						resultMap.put("start_time", "0");
						resultMap.put("current_cash", "0");
						resultMap.put("current_card", "0");
						resultMap.put("current_master", "0");
						resultMap.put("use_cash", "0");
						resultMap.put("use_card", "0");
						resultMap.put("use_master", "0");
						resultMap.put("remain_time", "0");
						resultMap.put("card_num", "0");
						resultMap.put("sales", "0");
						resultMap.put("connect", "0");
						String start_date = "";					
						
						resultMap.replace("connect", "1"); // 통신 o
						
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
						String today = sdf.format(new Date());
						
						// 셀프 / garage 저장값 없을떄 
						if (readStr.substring(5,7).equals("SN") || readStr.substring(5,7).equals("GN") 
								&& readStr.length() == 41 && readStr.substring(39, 41).equals("CH")) {
	//							System.out.println("셀프 저장값 없을떄");
	//							String compareCheckSum = checkSum(readStr.substring(0,37));
						
							String type = readStr.substring(8,9);
							String addr = readStr.substring(9,11);
							int cash = Integer.parseInt(readStr.substring(12,19)) * 100;   // DB에 넣을값 변환하기
							int card = Integer.parseInt(readStr.substring(19,26)) * 100;   // DB에 넣을값 변환하기
							int master = Integer.parseInt(readStr.substring(26,33)) * 100; // DB에 넣을값 변환하기
							String version = readStr.substring(33,37);
	
							// 금일 매출저장
							if (readStr.substring(5,6).equals("S")) {
								String money = fmt( (long) stringToDouble(deviceMapper.selectSelfSales(today, addr)) );
								resultMap.replace("sales", money);
							} if (readStr.substring(5,6).equals("G")) {
								String money = fmt( (long) stringToDouble(deviceMapper.selectGarageSales(today, addr)) );
								resultMap.replace("sales", money);
							} 
							
	//							if (readStr.substring(37,39).equals(compareCheckSum)) { // 체크섬 검증
								WashTotalVO vo = new WashTotalVO();
								vo.setType(type);
								vo.setAddr(Integer.parseInt(addr));
								vo.setCash(Integer.toString(cash));
								vo.setCard(Integer.toString(card));
								vo.setMaster(Integer.toString(master));
								vo.setVersion(version);
								deviceMapper.updateWashTotal(vo); // 데이터 업데이트
								
								resultMap.put("device_type", Integer.parseInt(type));
								resultMap.put("device_addr", addr);
	//							}
						}
						
						// 진공 / 매트 / 리더기 저장값 없을떄
						if (readStr.substring(5,7).equals("VN") || readStr.substring(5,7).equals("MN") || 
							readStr.substring(5,7).equals("RN") && readStr.length()==40 && readStr.substring(38,40).equals("CH")) {
							
							String type = readStr.substring(8,9);
							String addr = readStr.substring(9,11);
							int cash = Integer.parseInt(readStr.substring(11,18)) * 100;   // DB에 넣을값 변환하기
							int card = Integer.parseInt(readStr.substring(18,25)) * 100;   // DB에 넣을값 변환하기
							int master = Integer.parseInt(readStr.substring(25,32)) * 100; // DB에 넣을값 변환하기
							String version = readStr.substring(32,36);
							
							if (readStr.substring(5,6).equals("V")) {
								String money = fmt( (long) stringToDouble(deviceMapper.selectAirSales(today, addr)) );
								resultMap.replace("sales", money);
							} if (readStr.substring(5,6).equals("M")) {
								String money = fmt( (long) stringToDouble(deviceMapper.selectMateSales(today, addr)) );
								resultMap.replace("sales", money);
							} if (readStr.substring(5,6).equals("R")) {
								String money = fmt( (long) stringToDouble(deviceMapper.selectReaderSales(today, addr)) );
								resultMap.replace("sales", money);
							}
							
							WashTotalVO vo = new WashTotalVO();
							vo.setType(type);
							vo.setAddr(Integer.parseInt(addr));
							vo.setCash(Integer.toString(cash));
							vo.setCard(Integer.toString(card));
							vo.setMaster(Integer.toString(master));
							vo.setVersion(version);
							deviceMapper.updateWashTotal(vo); // 데이터 업데이트	
							
							resultMap.put("device_type", Integer.parseInt(type));
							resultMap.put("device_addr", addr);
						}
							
						// 셀프 / garage 동작중일떄
	//						GL 064 SR 00 01 6 17 18 35 00000 02390 00000 00000 00020 00000 0016 e132f379 13 CH
						if (readStr.substring(5, 7).equals("SR") || readStr.substring(5, 7).equals("GR") 
								&& readStr.length() == 64 && readStr.substring(62, 64).equals("CH")) {
								
								
								// 실시간 리턴 데이터 생성
								JsonDeviceStateVO state = new JsonDeviceStateVO();
								
								state.setState(readStr.substring(11));
								state.setStart_time(readStr.substring(12,14) + ":" + readStr.substring(14,16) + ":" + readStr.substring(16,18));
								
								int current_cash = Integer.parseInt(readStr.substring(18,23)) * 100;
								int current_card = Integer.parseInt(readStr.substring(23,28)) * 100;
								int current_master = Integer.parseInt(readStr.substring(28,33)) * 100;
								int use_cash = Integer.parseInt(readStr.substring(33,38)) * 100;
								int use_card = Integer.parseInt(readStr.substring(38,43)) * 100;
								int use_master = Integer.parseInt(readStr.substring(43,48)) * 100;
								String type = readStr.substring(7,9);
								String addr = readStr.substring(9,11);
								
								// 카드 번호 추출해서 등록 / 정지 카드 검사 
								String card_num = readStr.substring(52,60).toUpperCase();
								// Integer countCard = PosCardMapper.isCard(card_num);  			// 등록카드
//								Integer countBlackCard = PosCardMapper.isBlackCard(card_num);	// 정치카드
								
								// 현재 카드 사용중지 
//								if (/*(countCard == 0 || */countBlackCard==1 && use_master == 0) {
//									writeOther(type, addr, "ER");
//								}
								
								state.setCurrent_cash(Integer.toString(current_cash));
								state.setCurrent_card(Integer.toString(current_card));
								state.setCurrent_master(Integer.toString(current_master));
								state.setUse_cash(Integer.toString(use_cash));
								state.setUse_card(Integer.toString(use_card));
								state.setUse_master(Integer.toString(use_master));
								state.setRemain_time(readStr.substring(48,52));
								state.setCard_num(readStr.substring(52,60));
								state.setDevice_type(Integer.parseInt(type));
								state.setDevice_addr(addr);
								state.setConnect("1");
								
								resultMap.replace("state", state.getState());
								resultMap.replace("start_time", state.getStart_time());
								resultMap.replace("current_cash", state.getCurrent_cash());
								resultMap.replace("current_card", state.getCurrent_card());
								resultMap.replace("current_master", state.getCurrent_master());
								resultMap.replace("use_cash", state.getUse_cash());
								resultMap.replace("use_card", state.getUse_card());
								resultMap.replace("use_master", state.getUse_master());
								resultMap.replace("remain_time", state.getRemain_time());
								resultMap.replace("card_num", state.getCard_num());
								resultMap.put("device_type", Integer.parseInt(type));
								resultMap.put("device_addr", state.getDevice_addr());
								resultMap.replace("connect", state.getConnect());
	//							}
						}
						
						// 진공 / 매트 / 리더 동작 중일떄 
						if (readStr.substring(5,7).equals("VR") || readStr.substring(5,7).equals("MR") 
							|| readStr.substring(5,7).equals("RR") && readStr.length()==63 && readStr.substring(61,63).equals("CH")) {
							
							JsonDeviceStateVO state = new JsonDeviceStateVO();
							
							state.setState("1");
							state.setStart_time(readStr.substring(11,13) + ":" 
							+ readStr.substring(13,15) + ":" + readStr.substring(15,17));
							
							int current_cash = Integer.parseInt(readStr.substring(17,22)) * 100;
							int current_card = Integer.parseInt(readStr.substring(22,27)) * 100;
							int current_master = Integer.parseInt(readStr.substring(27,32)) * 100;
							int use_cash = Integer.parseInt(readStr.substring(32,37)) * 100;
							int use_card = Integer.parseInt(readStr.substring(37,42)) * 100;
							int use_master = Integer.parseInt(readStr.substring(42,47)) * 100;
							String type = readStr.substring(8,9);
							String addr = readStr.substring(9,11);
							
							// 카드 번호 추출해서 등록 / 정지 카드 검사 
							String card_num = readStr.substring(52,60).toUpperCase();
							// Integer countCard = PosCardMapper.isCard(card_num);  		// 등록카드
//							Integer countBlackCard = PosCardMapper.isBlackCard(card_num);	// 정치카드
							
							// 현재 카드 사용중지 
//							if (/*(countCard == 0 || */countBlackCard==1 && use_master == 0) {
//								writeOther(type, addr, "ER");
//							}
							
							state.setCurrent_cash(Integer.toString(current_cash));
							state.setCurrent_card(Integer.toString(current_card));
							state.setCurrent_master(Integer.toString(current_master));
							state.setUse_cash(Integer.toString(use_cash));
							state.setUse_card(Integer.toString(use_card));
							state.setUse_master(Integer.toString(use_master));
							state.setRemain_time(readStr.substring(47,51));
							state.setCard_num(readStr.substring(51,59));
							state.setDevice_type(Integer.parseInt(type));
							state.setDevice_addr(addr);
							state.setConnect("1");
							
							resultMap.replace("state", state.getState());
							resultMap.replace("start_time", state.getStart_time());
							resultMap.replace("current_cash", state.getCurrent_cash());
							resultMap.replace("current_card", state.getCurrent_card());
							resultMap.replace("current_master", state.getCurrent_master());
							resultMap.replace("use_cash", state.getUse_cash());
							resultMap.replace("use_card", state.getUse_card());
							resultMap.replace("use_master", state.getUse_master());
							resultMap.replace("remain_time", state.getRemain_time());
							resultMap.replace("card_num", state.getCard_num());
							resultMap.put("device_type", Integer.parseInt(type));
							resultMap.put("device_addr", state.getDevice_addr());
							resultMap.replace("connect", state.getConnect());
						}
						
						// 셀프 저장값 있는 데이터 
			// GL 077 SW 00 03 001 20 12 01 15 20 04 15 20 43 43ea742b 06730 0020 0000 0000 0040 0000 0000 0000 58 CH	
			// GL 077 SW 00 01 002 95 11 70 17 18 35 17 19 05 e132f379 02390 0020 0000 0000 0000 0030 0000 0000 68 CH
						if (readStr.substring(5,7).equals("SW") && readStr.length() == 77 
								&& readStr.substring(75,77).equals("CH")) {
	//							String compareCheckSum = checkSum(readStr.substring(73));
	//							System.out.println("compareCheckSum : " + compareCheckSum);
	//							System.out.println("체크섬                       : " + readStr.substring(73,75));
	//							if (readStr.substring(73,75).equals(compareCheckSum)) { // 체크섬 검증
	//								System.out.println("체크섬 인증 성공");
								SelfStateVO self = new SelfStateVO();
								
								String year = readStr.substring(14,16);
	                            String month = readStr.substring(16,18);
	                            String day = readStr.substring(18,20);
	                            String s_h = readStr.substring(20,22);
	                    		String s_m = readStr.substring(22,24);
	            				String s_s = readStr.substring(24,26);
	    						String e_h = readStr.substring(26,28);
								String e_m = readStr.substring(28,30);
								String e_s = readStr.substring(30,32);
								String card_num = readStr.substring(32,40);
								String remain_card = readStr.substring(40,45);
								String card_use = readStr.substring(45,49);
								String cash_use = readStr.substring(49,53);
								String master_use = readStr.substring(53,57);
								String self_time = readStr.substring(57,61);
								String foam_time = readStr.substring(61,65);
								String under_time = readStr.substring(65,69);
								String coating_time = readStr.substring(69,73);
								
								// 한 사이클 사용시간 계산 
								String use_time = Integer.toString((Integer.parseInt(self_time) + Integer.parseInt(foam_time) +
										Integer.parseInt(under_time) + Integer.parseInt(coating_time)));
								
								String type = readStr.substring(7,9);
								String addr = readStr.substring(9,11);
								
								// 모델 클래스 setter
								self.setDevice_addr(addr);
								self.setCard_num(card_num);
								self.setSelf_time(self_time);
								self.setFoam_time(foam_time);
								self.setUnder_time(under_time);
								self.setCoating_time(coating_time);
								self.setUse_cash(cash_use);
								self.setUse_card(card_use);
								self.setRemain_card(remain_card);
								self.setMaster_card(master_use);
								
								Calendar cal = Calendar.getInstance();
								
								int y = cal.get(Calendar.YEAR);
								int m = cal.get(Calendar.MONTH) + 1;
								int d = cal.get(Calendar.DAY_OF_MONTH);
								int hour = cal.get(Calendar.HOUR_OF_DAY);
								int min = cal.get(Calendar.MINUTE);
								int sec = cal.get(Calendar.SECOND);
								logger.info("끝난시간 가져오기\ny : {}", y);
								logger.info("m : {}", m);
								logger.info("d : {}", d);
								logger.info("hour : {}", hour);
								logger.info("min : {}", min);
								logger.info("sec : {}", sec);
								String start_time = currentClockFormat();
								String end_time = currentClockFormat();
								self.setStart_time(start_time);
								self.setEnd_time(end_time);
								
								deviceMapper.insertSelfState(self);
								writeOKSign(type, addr);
								setTime(type, addr);
							}
							
							// 진공 저장값 있는 데이터 
							if (readStr.substring(5,7).equals("VW") && readStr.length() == 61 
									&& readStr.substring(59,61).equals("CH")) {
								
								String type = readStr.substring(7,9);
								String addr = readStr.substring(9,11);
								String year = readStr.substring(14,16);
	                            String month = readStr.substring(16,18);
	                            String day = readStr.substring(18,20);
	                            String s_h = readStr.substring(20,22);
	                    		String s_m = readStr.substring(22,24);
	            				String s_s = readStr.substring(24,26);
	    						String e_h = readStr.substring(26,28);
								String e_m = readStr.substring(28,30);
								String e_s = readStr.substring(30,32);
								String card_num = readStr.substring(32,40);
								String remain_card = readStr.substring(40,45);
								String card_use = readStr.substring(45,49);
								String cash_use = readStr.substring(49,53);
								String master_use = readStr.substring(53,57);
								String start_time = "20" + year + "-" + month + "-" + day + " " + s_h + ":" + s_m + ":" + s_s; 
								String end_time = "20" + year + "-" + month + "-" + day + " " + e_h + ":" + e_m + ":" + e_s;
								
								AirStateVO air = new AirStateVO();
								air.setDevice_addr(addr);
								air.setCard_num(card_num);
								air.setAir_cash(cash_use);
								air.setAir_card(card_use);
								air.setRemain_card(remain_card);
								air.setMaster_card(master_use);
								air.setStart_time(start_time);
								air.setEnd_time(end_time);
								
								deviceMapper.insertAirState(air);
								writeOKSign(type, addr);
								setTime(type, addr);
							}
							
							// 매트 저장값 있는 데이터 
							if (readStr.substring(5,7).equals("MW") && readStr.length() == 61 
									&& readStr.substring(59,61).equals("CH")) {
								
								String type = readStr.substring(7,9);
								String addr = readStr.substring(9,11);
								String year = readStr.substring(14,16);
	                            String month = readStr.substring(16,18);
	                            String day = readStr.substring(18,20);
	                            String s_h = readStr.substring(20,22);
	                    		String s_m = readStr.substring(22,24);
	            				String s_s = readStr.substring(24,26);
	    						String e_h = readStr.substring(26,28);
								String e_m = readStr.substring(28,30);
								String e_s = readStr.substring(30,32);
								String card_num = readStr.substring(32,40);
								String remain_card = readStr.substring(40,45);
								String card_use = readStr.substring(45,49);
								String cash_use = readStr.substring(49,53);
								String master_use = readStr.substring(53,57);
								String start_time = "20" + year + "-" + month + "-" + day + " " + s_h + ":" + s_m + ":" + s_s; 
								String end_time = "20" + year + "-" + month + "-" + day + " " + e_h + ":" + e_m + ":" + e_s;
								
								MateStateVO mate = new MateStateVO();
								mate.setDevice_addr(addr);
								mate.setCard_num(card_num);
								mate.setMate_cash(cash_use);
								mate.setMate_card(card_use);
								mate.setRemain_card(remain_card);
								mate.setMaster_card(master_use);
								mate.setStart_time(start_time);
								mate.setEnd_time(end_time);
								
								deviceMapper.insertMateState(mate);
								writeOKSign(type, addr);
								setTime(type, addr);
							}
							
							// 리더기 저장값 있는 데이터 
							if (readStr.substring(5,7).equals("RW") && readStr.length() == 61 
									&& readStr.substring(59,61).equals("CH")) {
							
								String type = readStr.substring(7,9);
								String addr = readStr.substring(9,11);
								String year = readStr.substring(14,16);
	                            String month = readStr.substring(16,18);
	                            String day = readStr.substring(18,20);
	                            String s_h = readStr.substring(20,22);
	                    		String s_m = readStr.substring(22,24);
	            				String s_s = readStr.substring(24,26);
	    						String e_h = readStr.substring(26,28);
								String e_m = readStr.substring(28,30);
								String e_s = readStr.substring(30,32);
								String card_num = readStr.substring(32,40);
								String remain_card = readStr.substring(40,45);
								String card_use = readStr.substring(45,49);
								String cash_use = readStr.substring(49,53);
								String master_use = readStr.substring(53,57);
								String start_time = "20" + year + "-" + month + "-" + day + " " + s_h + ":" + s_m + ":" + s_s; 
								String end_time = "20" + year + "-" + month + "-" + day + " " + e_h + ":" + e_m + ":" + e_s;
								
								ReaderStateVO reader = new ReaderStateVO();
								reader.setDevice_addr(addr);
								reader.setCard_num(card_num);
								reader.setReader_cash(cash_use);
								reader.setReader_card(card_use);
								reader.setRemain_card(remain_card);
								reader.setMaster_card(master_use);
								reader.setStart_time(start_time);
								reader.setEnd_time(end_time);
								
								deviceMapper.insertReaderState(reader);
								writeOKSign(type, addr);
								setTime(type, addr);
							}
							
							// Garage 저장값 있는 데이터 
							if (readStr.substring(5,7).equals("GW") && readStr.length() == 91 && readStr.substring(89,91).equals("CH")) {
							
							}
						} // 세차장비일떄 끝
					    
						// 충전장비일때	
						else if(tempType == glsConfig.getCharger()) { 
							// 리턴값 초기화

							resultMap.put("device_type", Integer.parseInt(readStr.substring(8,9)));	
							resultMap.put("device_addr", readStr.substring(9,11));
							resultMap.put("connect", "1");
							resultMap.put("charge", "0");		// 충전금액 
							resultMap.put("count", 0);			// 발급수
							
							// 실시간 모니터링 값 가져오기
							JsonChargerStateVO json = deviceMapper.selectChargerMonitor(Integer.parseInt(readStr.substring(8,9)), readStr.substring(9,11));
							
							json.setCharge(fmt((long) stringToDouble(json.getCharge())));
							resultMap.replace("charge", json.getCharge());		// 충전금액 
							resultMap.replace("count", json.getCount());		// 발급수
							
							// 충전기 저장값 있을떄 
							if (readStr.substring(5,7).equals("CW") && readStr.substring(59,61).equals("CH") 
									&& readStr.length()==61) {
								
								String type = readStr.substring(7,9);
								String addr = readStr.substring(9,11);
								String year = readStr.substring(14,16);
					            String month = readStr.substring(16,18);
					            String day = readStr.substring(18,20);
					            String hour = readStr.substring(20,22);
					    		String min = readStr.substring(22,24);
								String sec = readStr.substring(24,26);
								String current_money = "";
								String current_charge = readStr.substring(30,34);
								String remain_card = readStr.substring(34,39);
								String current_bonus = readStr.substring(39,44);
								String card_price = readStr.substring(44,49);
								String card_num = readStr.substring(49,57);
								String kind = "";
								
								String input_date = "20" + year + "-" + month + "-" + day + " " + hour + ":" + min + ":" + sec; 
								
								// 카드번호가 없다면 발급
								if (card_num.equals("00000000")) {
									kind = "0";
									
									// (조건) 카드발급금액 > 보너스금액
					                // -> 투입금액 = 카드발급금액 - 보너스금액
					                if (Integer.parseInt(card_price) > Integer.parseInt(current_bonus)){
					                    current_money = Integer.toString(Integer.parseInt(card_price) - Integer.parseInt(current_bonus));
					                } else {
					                    current_money = "0000";
					                    current_bonus = card_price;
					                }
								} else {
									current_money = readStr.substring(26,30);
									kind = "1";
								}
								
								Integer intType = Integer.parseInt(readStr.substring(8,9));
								Integer device_no = deviceMapper.selectNo(intType, addr);
								
								ChargerStateVO charger = new ChargerStateVO();
								charger.setDevice_no(device_no);
								charger.setKind(kind);
								charger.setExhaust_money(card_price);
								charger.setCurrent_money(current_money);
								charger.setCurrent_bonus(current_bonus);
								charger.setCurrent_charge(current_charge);
								charger.setTotal_money(remain_card);
								charger.setCard_num(card_num);
								charger.setInput_date(input_date);
								
								
								deviceMapper.insertChargerState(charger); // DB insert
								writeOKSign(type, addr);
								setTime(type, addr);
							} // 충전기 저장값 끝 
						} // 충전장비 일떄 끝
					} // 수신 완료 : 시작값 조건 끝
				} // 데이터 수신 끝
			//} 
//			}
		} catch (Exception e) {
			logger.info("readSerial Exception >>");
			e.printStackTrace();
		}
		
		return resultMap;
	}
	
	// OK SIGN
	@Override
	public void writeOKSign(String deviceType, String deviceAddr) {
		try {
			String state = ""; // 송신 프로토콜 
			state += "GL017OK";
			state += glsConfig.getManager_no();
			state += deviceType;
			state += deviceAddr;
			state += checkSum(state);
			state += "CH";
			
			logger.info("OK Sign : {} ", state);
			glsConfig.getSerialPort().writeBytes(state.getBytes()); // Write data to port
			Thread.sleep(200); // delay
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// 현재시간 데이터 포매팅
	@Override
	public String currentClockFormat() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA);
		return formatter.format(new Date());
	}
	
	public String checkSum(String state) {
		int sum = 0;
		
		for(int i=0; i<state.length(); i++) {
			char alpa = state.charAt(i);
			int ialpa = (int) alpa; // 문자 하나씩 10진수 변환
			sum += ialpa; // 합 구함
		}
//		logger.info("함수에서 체크섬 구한값 : {}", Integer.toString(sum%100));
		return Integer.toString(sum%100);
	}
	
	// 셀프 설정 불러오기
	/*
	 PCB로부터 직접 장비 설정 값을 호출하여 포스에게 전달하는 함수
          각 장비로부터 설정값을 불러와 데이터베이스에 저장된 값과 비교를 하여
          다른 부분이 있으면 새로운 설정을 데이터베이스에 입력한 후 포스에 전달한다.
     * 참고 : config는 update가 아닌 insert로 log 형식으로 누적한다.
	 */
	@Override
	public List<Map<String, Object>> getSelfConfig() {
//		schedulerService.remove();
		
		// 485 스레드 동작 중일떄 
		if (scheduler.getIsThread()) {
			scheduler.stopScheduler(); // 스케쥴러 중지
		}
		
		List<Map<String, Object>> result = new ArrayList<>();
		Map<String, Object> resultMap = new LinkedHashMap<String, Object>(); 
		
		String[] addrList = deviceMapper.selectDeviceAddr(glsConfig.getSelf()); // 주소 DB셀렉트
		
		for(int i=1; i<=glsConfig.getSelfCount(); i++) {
			writeConfig(String.format("0%d", glsConfig.getSelf()), addrList[i-1]);
			resultMap = readSelfConfig(addrList[i-1]);
			result.add(resultMap);
		}
		
		return result;
	}
	
	// 현재카드 사용중지, 누적금액 초기화 
	@Override
	public void writeOther(String deviceType, String deviceAddr, String command) {
		try {
			String config = ""; // 송신 프로토콜 
			config += "GL017";
			config += command;
			config += glsConfig.getManager_no();
			config += deviceType;
			config += deviceAddr;
			config += checkSum(config);
			config += "CH";

			logger.info("{}", config);
			glsConfig.getSerialPort().writeBytes(config.getBytes()); // Write data to port
			Thread.sleep(500);
			
			byte[] readByte = glsConfig.getSerialPort().readBytes(); // 데이터 담을 변수
				
			// 데이터 수신 완료 
			if (readByte != null && readByte.length > 0) {
				String readStr = new String(readByte); // 문자열로 변환  
				readStr = readStr.replace(" ", "0"); 
				logger.info("Other Rx : {}", readStr);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// 세팅 상태 호출 
	@Override
	public void writeConfig(String deviceType, String deviceAddr) {
		try {
			String config = ""; // 송신 프로토콜 
			config += "GL017CL";
			config += glsConfig.getManager_no();
			config += deviceType;
			config += deviceAddr;
			config += checkSum(config);
			config += "CH";

			logger.info("{}", config);
			glsConfig.getSerialPort().writeBytes(config.getBytes()); // Write data to port
			Thread.sleep(500);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// 셀프 세팅 시리얼값 읽기
	@Override
	public Map<String, Object> readSelfConfig(String deviceAddr) {
//		schedulerService.remove();
		Map<String, Object> resultMap = new LinkedHashMap<String, Object>(); // 반환값 
		
		byte[] readByte;
		
		try {
			readByte = glsConfig.getSerialPort().readBytes(); // 데이터 담을 변수
			
			// 데이터 수신 완료 
			if (readByte != null && readByte.length > 0) {
				
				String readStr = new String(readByte); // 문자열로 변환 
				String tempStr = readStr.replace(" ", ""); // 체크섬 비교할 문자열 만들기 
				readStr = readStr.replace(" ", "0"); 
				logger.info("Config Rx : {}", readStr); 
	
//				String stx = readStr.substring(0,2);
//				String etx = readStr.substring(107,109);
				Integer length = readStr.length();
//				String checkSum = readStr.substring(0, tempStr.length()-4); // 체크섬 계산
//				checkSum = checkSum(checkSum);
//				String compareCheckSum = readStr.substring(105,107); 		// 프로토콜 자른 값 
//				logger.info("checkSum {}", checkSum);
//				logger.info("compare  {}", compareCheckSum);
				
				// GL 109 CL 00 01 015 030 1 005 010 003 1 1 05 020 030 005 010 010 060 
				// 1 1 04 020 030 005 010 010 1 1 03 020 030 005 010 010 500 1 01 30 0 030 1 0 1 0 05 20 CH
				
				// 수신 완료시
				if (readStr.substring(0,2).equals("GL") && readStr.substring(107,109).equals("CH") && length == 109/* && checkSum.equals(compareCheckSum) */) { 
					
					resultMap.put("device_addr", readStr.substring(9,11)); // 디바이스 주소
					resultMap.put("state", "1");	// 통신 상태
					
					// 셀프 설정
					resultMap.put("self_init_money", Integer.parseInt(readStr.substring(11,14)) * 100);
					resultMap.put("self_init_time", readStr.substring(14,17));
					resultMap.put("self_con_enable", readStr.substring(17,18));
					resultMap.put("self_con_money", Integer.parseInt(readStr.substring(18,21)) * 100);
					resultMap.put("self_con_time", readStr.substring(21,24));
					resultMap.put("self_pause_time", readStr.substring(24,27));
					
					// 폼 설정
					resultMap.put("foam_enable", readStr.substring(27,28));
					resultMap.put("foam_con_enable", readStr.substring(28,29));
					resultMap.put("foam_speedier", readStr.substring(29,31));
					resultMap.put("foam_init_money", Integer.parseInt(readStr.substring(31,34)) * 100);
					resultMap.put("foam_init_time", readStr.substring(34,37));
					resultMap.put("foam_con_money", Integer.parseInt(readStr.substring(37,40)) * 100);
					resultMap.put("foam_con_time", readStr.substring(40,43));
					resultMap.put("foam_pause_time", readStr.substring(43,46));
					resultMap.put("foam_end_delay", readStr.substring(46,49));
					
					// 하부 설정
					resultMap.put("under_enable", readStr.substring(49,50));
					resultMap.put("under_con_enable", readStr.substring(50,51));
					resultMap.put("under_speedier", readStr.substring(51,53));
					resultMap.put("under_init_money", Integer.parseInt(readStr.substring(53,56)) * 100);
					resultMap.put("under_init_time", readStr.substring(56,59));
					resultMap.put("under_con_money", Integer.parseInt(readStr.substring(59,62)) * 100);
					resultMap.put("under_con_time", readStr.substring(62,65));
					resultMap.put("under_pause_time", readStr.substring(65,68));
					
					// 코팅 설정 
					resultMap.put("coating_enable", readStr.substring(68,69));
					resultMap.put("coating_con_enable", readStr.substring(69,70));
					resultMap.put("coating_speedier", readStr.substring(70,72));
					resultMap.put("coating_init_money", Integer.parseInt(readStr.substring(72,75)) * 100);
					resultMap.put("coating_init_time", readStr.substring(75,78));
					resultMap.put("coating_con_money", Integer.parseInt(readStr.substring(78,81)) * 100);
					resultMap.put("coating_con_time", readStr.substring(81,84));
					resultMap.put("coating_pause_time", readStr.substring(84,87));
					
					// 기타설정
					resultMap.put("cycle_money", Integer.parseInt(readStr.substring(87,90)) * 100);
					resultMap.put("pay_free", readStr.substring(90,91));
					resultMap.put("buzzer_time", readStr.substring(91,93));
					resultMap.put("pause_count", readStr.substring(93,95));
					resultMap.put("secret_enable", readStr.substring(95,96));
					resultMap.put("secret_date", readStr.substring(96,99));
					resultMap.put("speedier_enable", readStr.substring(99,100));
					resultMap.put("use_type", readStr.substring(100,101));
					resultMap.put("set_coating_output", readStr.substring(101,102));
					resultMap.put("wipping_enable", readStr.substring(102,103));
					resultMap.put("wipping_temperature", readStr.substring(103,105));
					
				} else { // 수신 실패시
					resultMap.put("device_addr", deviceAddr);
					resultMap.put("state", "0");
				}
				
				// DB - 주소별 셀프 설정 불러오기 
				SelfConfigVO selfConfig = deviceMapper.selectSelfConfig(deviceAddr);
				/*
				// 포스에 보낼 금액 데이터 포매팅 (010 - 1000)
				selfConfig.setSelf_init_money(resultMap.get("self_init_money").toString());
				selfConfig.setSelf_con_money(resultMap.get("self_con_money").toString());
				selfConfig.setFoam_init_money(resultMap.get("foam_init_money").toString());
				selfConfig.setFoam_con_money(resultMap.get("foam_con_money").toString());
				selfConfig.setUnder_init_money(resultMap.get("under_init_money").toString());
				selfConfig.setUnder_con_money(resultMap.get("under_con_money").toString());
				selfConfig.setCoating_init_money(resultMap.get("coating_init_money").toString());
				selfConfig.setCoating_con_money(resultMap.get("coating_con_money").toString());
				selfConfig.setCycle_money(resultMap.get("cycle_money").toString());
				
				String diff = "0"; // 설정 이상 비교 플래그

				// 통신 상태 On일떄
				if (!resultMap.get("state").equals("0")) { 	
					
					// 수신데이터와 DB데이터 비교 
					if (!selfConfig.getDevice_addr().equals(resultMap.get("device_addr"))) {
						diff = "1";
						resultMap.replace("state", "2");
						selfConfig.setDevice_addr(resultMap.get("device_addr").toString());
					} 
					// 셀프
					if (!selfConfig.getSelf_init_money().equals(resultMap.get("self_init_money"))) {
						diff = "1";
						resultMap.replace("state", "2");
						selfConfig.setSelf_init_money(moneyFormatting(Integer.parseInt(resultMap.get("self_init_money").toString())));
					}
					if (!selfConfig.getSelf_init_time().equals(resultMap.get("self_init_time"))) {
						diff = "1";
						resultMap.replace("state", "2");
						selfConfig.setSelf_init_time(resultMap.get("self_init_time").toString());
					}
					if (!selfConfig.getSelf_con_enable().equals(resultMap.get("self_con_enable"))) {
						diff = "1";
						resultMap.replace("state", "2");
						selfConfig.setSelf_con_enable(resultMap.get("self_conf_enable").toString());
					}
					if (!selfConfig.getSelf_con_money().equals(resultMap.get("self_con_money"))) {
						diff = "1";
						resultMap.replace("state", "2");
						selfConfig.setSelf_con_money(moneyFormatting(Integer.parseInt(resultMap.get("self_con_money").toString())));
					}
					if (!selfConfig.getSelf_con_time().equals(resultMap.get("self_con_time"))) {
						diff = "1";
						resultMap.replace("state", "2");
						selfConfig.setSelf_con_time(resultMap.get("self_con_time").toString());
					}
					if (!selfConfig.getSelf_pause_time().equals(resultMap.get("self_pause_time"))) {
						diff = "1";
						resultMap.replace("state", "2");
						selfConfig.setSelf_pause_time(resultMap.get("self_pause_time").toString());
					}
					
					// 폼 
					if (!selfConfig.getFoam_enable().equals(resultMap.get("foam_enable"))) {
						diff = "1";
						resultMap.replace("state", "2");
						selfConfig.setFoam_enable(resultMap.get("foam_enable").toString());
					}
					if (!selfConfig.getFoam_con_enable().equals(resultMap.get("foam_con_enable"))) {
						diff = "1";
						resultMap.replace("state", "2");
						selfConfig.setFoam_con_enable(resultMap.get("foam_con_enable").toString());
					}
					if (!selfConfig.getFoam_speedier().equals(resultMap.get("foam_speedier"))) {
						diff = "1";
						resultMap.replace("state", "2");
						selfConfig.setFoam_speedier(resultMap.get("foam_speedier").toString());
					}
					if (!selfConfig.getFoam_init_money().equals(resultMap.get("foam_init_money"))) {
						diff = "1";
						resultMap.replace("state", "2");
						selfConfig.setFoam_init_money(moneyFormatting(Integer.parseInt(resultMap.get("foam_init_money").toString())));
					}
					if (!selfConfig.getFoam_init_time().equals(resultMap.get("foam_init_time"))) {
						diff = "1";
						resultMap.replace("state", "2");
						selfConfig.setFoam_init_time(resultMap.get("foam_init_time").toString());
					}
					if (!selfConfig.getFoam_con_money().equals(resultMap.get("foam_con_money"))) {
						diff = "1";
						resultMap.replace("state", "2");
						selfConfig.setFoam_con_money(moneyFormatting(Integer.parseInt(resultMap.get("foam_con_money").toString())));
					}
					if (!selfConfig.getFoam_con_time().equals(resultMap.get("foam_con_time"))) {
						diff = "1";
						resultMap.replace("state", "2");
						selfConfig.setFoam_con_time(resultMap.get("foam_con_time").toString());
					}
					if (!selfConfig.getFoam_pause_time().equals(resultMap.get("foam_pause_time"))) {
						diff = "1";
						resultMap.replace("state", "2");
						selfConfig.setFoam_pause_time(resultMap.get("foam_pause_time").toString());
					}
					if (!selfConfig.getFoam_end_delay().equals(resultMap.get("foam_end_delay"))) {
						diff = "1";
						resultMap.replace("state", "2");
						selfConfig.setFoam_end_delay(resultMap.get("foam_end_delay").toString());
					}
					
					// 하부 
					if (!selfConfig.getUnder_enable().equals(resultMap.get("under_enable"))) {
						diff = "1";
						resultMap.replace("state", "2");
						selfConfig.setUnder_enable(resultMap.get("under_enable").toString());
					}
					if (!selfConfig.getUnder_con_enable().equals(resultMap.get("under_con_enable"))) {
						diff = "1";
						resultMap.replace("state", "2");
						selfConfig.setFoam_con_enable(resultMap.get("under_con_enable").toString());
					}
					if (!selfConfig.getUnder_speedier().equals(resultMap.get("under_speedier"))) {
						diff = "1";
						resultMap.replace("state", "2");
						selfConfig.setUnder_speedier(resultMap.get("under_speedier").toString());
					}
					if (!selfConfig.getUnder_init_money().equals(resultMap.get("under_init_money"))) {
						diff = "1";
						resultMap.replace("state", "2");
						selfConfig.setUnder_init_money(moneyFormatting(Integer.parseInt(resultMap.get("under_init_money").toString())));
					}
					if (!selfConfig.getUnder_init_time().equals(resultMap.get("under_init_time"))) {
						diff = "1";
						resultMap.replace("state", "2");
						selfConfig.setUnder_init_time(resultMap.get("under_init_time").toString());
					}
					if (!selfConfig.getUnder_con_money().equals(resultMap.get("under_con_money"))) {
						diff = "1";
						resultMap.replace("state", "2");
						selfConfig.setUnder_con_money(moneyFormatting(Integer.parseInt(resultMap.get("under_con_money").toString())));
					}
					if (!selfConfig.getUnder_con_time().equals(resultMap.get("under_con_time"))) {
						diff = "1";
						resultMap.replace("state", "2");
						selfConfig.setUnder_con_time(resultMap.get("under_con_time").toString());
					}
					if (!selfConfig.getUnder_pause_time().equals(resultMap.get("under_pause_time"))) {
						diff = "1";
						resultMap.replace("state", "2");
						selfConfig.setUnder_pause_time(resultMap.get("under_pause_time").toString());
					}
					
					// 코팅 
					if (!selfConfig.getCoating_enable().equals(resultMap.get("coating_enable"))) {
						diff = "1";
						resultMap.replace("state", "2");
						selfConfig.setCoating_enable(resultMap.get("coating_enable").toString());
					}
					if (!selfConfig.getCoating_con_enable().equals(resultMap.get("coating_con_enable"))) {
						diff = "1";
						resultMap.replace("state", "2");
						selfConfig.setCoating_con_enable(resultMap.get("coating_con_enable").toString());
					}
					if (!selfConfig.getCoating_speedier().equals(resultMap.get("coating_speedier"))) {
						diff = "1";
						resultMap.replace("state", "2");
						selfConfig.setCoating_speedier(resultMap.get("coating_speedier").toString());
					}
					if (!selfConfig.getCoating_init_money().equals(resultMap.get("coating_init_money"))) {
						diff = "1";
						resultMap.replace("state", "2");
						selfConfig.setCoating_init_money(moneyFormatting(Integer.parseInt(resultMap.get("coating_init_money").toString())));
					}
					if (!selfConfig.getCoating_init_time().equals(resultMap.get("coating_init_time"))) {
						diff = "1";
						resultMap.replace("state", "2");
						selfConfig.setCoating_init_time(resultMap.get("coating_init_time").toString());
					}
					if (!selfConfig.getCoating_con_money().equals(resultMap.get("coating_con_money"))) {
						diff = "1";
						resultMap.replace("state", "2");
						selfConfig.setCoating_con_money(moneyFormatting(Integer.parseInt(resultMap.get("coating_con_money").toString())));
					}
					if (!selfConfig.getCoating_con_time().equals(resultMap.get("coating_con_time"))) {
						diff = "1";
						resultMap.replace("state", "2");
						selfConfig.setCoating_con_time(resultMap.get("coating_con_time").toString());
					}
					if (!selfConfig.getCoating_pause_time().equals(resultMap.get("coating_pause_time"))) {
						diff = "1";
						resultMap.replace("state", "2");
						selfConfig.setCoating_pause_time(resultMap.get("coating_pause_time").toString());
					}
					
					// 기타
					if (!selfConfig.getCycle_money().equals(resultMap.get("cycle_money"))) {
						diff = "1";
						resultMap.replace("state", "2");
						selfConfig.setCycle_money(moneyFormatting(Integer.parseInt(resultMap.get("cycle_money").toString())));
					}
					if (!selfConfig.getPay_free().equals(resultMap.get("pay_free"))) {
						diff = "1";
						resultMap.replace("state", "2");
						selfConfig.setPay_free(resultMap.get("pay_free").toString());
					}
					if (!selfConfig.getBuzzer_time().equals(resultMap.get("buzzer_time"))) {
						diff = "1";
						resultMap.replace("state", "2");
						selfConfig.setBuzzer_time(resultMap.get("buzzer_time").toString());
					}
					if (!selfConfig.getPause_count().equals(resultMap.get("pause_count"))) {
						diff = "1";
						resultMap.replace("state", "2");
						selfConfig.setPause_count(resultMap.get("pause_count").toString());
					}
					if (!selfConfig.getSecret_enable().equals(resultMap.get("secret_enable"))) {
						diff = "1";
						resultMap.replace("state", "2");
						selfConfig.setSecret_enable(resultMap.get("secret_enable").toString());
					}
					if (!selfConfig.getSecret_date().equals(resultMap.get("secret_date"))) {
						diff = "1";
						resultMap.replace("state", "2");
						selfConfig.setSecret_date(resultMap.get("secret_date").toString());
					}
					if (!selfConfig.getSpeedier_enable().equals(resultMap.get("speedier_enable"))) {
						diff = "1";
						resultMap.replace("state", "2");
						selfConfig.setSpeedier_enable(resultMap.get("speedier_enable").toString());
					}
					if (!selfConfig.getUse_type().equals(resultMap.get("use_type"))) {
						diff = "1";
						resultMap.replace("state", "2");
						selfConfig.setUse_type(resultMap.get("use_type").toString());
					}
					if (!selfConfig.getSet_coating_output().equals(resultMap.get("set_coating_output"))) {
						diff = "1";
						resultMap.replace("state", "2");
						selfConfig.setSet_coating_output(resultMap.get("set_coating_output").toString());
					}
					if (!selfConfig.getWipping_enable().equals(resultMap.get("wipping_enable"))) {
						diff = "1";
						resultMap.replace("state", "2");
						selfConfig.setWipping_enable(resultMap.get("wipping_enable").toString());
					}
					if (!selfConfig.getWipping_temperature().equals(resultMap.get("wipping_temperature"))) {
						diff = "1";
						resultMap.replace("state", "2");
						selfConfig.setWipping_temperature(resultMap.get("wipping_temperature").toString());
					}
				}
				
				// 장비 설정값이 데이터베이스와 다를 떄 -> DB insert
				if (diff.equals("1")) {
					String clock = currentClockFormat();
					deviceMapper.insertSelfConfig(selfConfig, currentClockFormat());
				}*/
				
			} // 데이터 수신 완료끝

			
		} catch (SerialPortException e) {
			e.printStackTrace();
		} 
		
		return resultMap;
	} // 셀프 세팅 시리얼값 읽기 메서드 끝
	
	// 셀프 설정 셋팅
	@Override
	public String setSelfConfig(SelfConfigVO params) {
		// 485 스레드 동작 중일떄 
		if (scheduler.getIsThread()) {
			scheduler.stopScheduler(); // 스케쥴러 중지
		}
		String result = "0"; // 반환값 : 성공 1 실패 0
		
		logger.info("{}", params.toString());
		
		/*
		 * SelfConfigVO params 
		 * [device_addr=01, self_init_money=1500, self_init_time=30, self_con_enable=1, 
		 * self_con_money=500, self_con_time=10, self_pause_time=3, foam_enable=1, foam_con_enable=1, 
		 * foam_speedier=5, foam_init_money=2000, foam_init_time=30, foam_con_money=500, foam_con_time=10, 
		 * foam_pause_time=10, foam_end_delay=60, under_enable=1, under_con_enable=1, under_speedier=4, 
		 * under_init_money=2000, under_init_time=30, under_con_money=500, under_con_time=10, under_pause_time=10, 
		 * coating_enable=1, coating_con_enable=1, coating_speedier=3, coating_init_money=2000, 
		 * coating_init_time=30, coating_con_money=500, coating_con_time=10, coating_pause_time=10, 
		 * cycle_money=50000, pay_free=1, buzzer_time=1, pause_count=30, secret_enable=0, 
		 * speedier_enable=1, secret_date=30, use_type=0, 
		 * set_coating_output=1, wipping_enable=0, wipping_temperature=5, input_date=null]
		 */

		// 프로토콜에 맞게 값 변경 
		params.setSelf_init_money(String.format("%03d", (Integer.parseInt(params.getSelf_init_money()) / 100)));
		params.setSelf_init_time(String.format("%03d", (Integer.parseInt(params.getSelf_init_time()))));
		params.setSelf_con_money(String.format("%03d", (Integer.parseInt(params.getSelf_con_money()) / 100)));
		params.setSelf_con_time(String.format("%03d", (Integer.parseInt(params.getSelf_con_time()))));
		params.setSelf_pause_time(String.format("%03d", (Integer.parseInt(params.getSelf_pause_time()))));
		
		params.setFoam_speedier(String.format("%02d", (Integer.parseInt(params.getFoam_speedier()))));
		params.setFoam_init_money(String.format("%03d", (Integer.parseInt(params.getFoam_init_money()) / 100)));
		params.setFoam_init_time(String.format("%03d", (Integer.parseInt(params.getFoam_init_time()))));
		params.setFoam_con_money(String.format("%03d", (Integer.parseInt(params.getFoam_con_money()) / 100)));
		params.setFoam_con_time(String.format("%03d", (Integer.parseInt(params.getFoam_con_time()))));
		params.setFoam_pause_time(String.format("%03d", (Integer.parseInt(params.getFoam_pause_time()))));
		params.setFoam_end_delay(String.format("%03d", (Integer.parseInt(params.getFoam_end_delay()))));
		
		params.setUnder_speedier(String.format("%02d", (Integer.parseInt(params.getUnder_speedier()))));
		params.setUnder_init_money(String.format("%03d", (Integer.parseInt(params.getUnder_init_money()) / 100)));
		params.setUnder_init_time(String.format("%03d", (Integer.parseInt(params.getUnder_init_time()))));
		params.setUnder_con_money(String.format("%03d", (Integer.parseInt(params.getUnder_con_money()) / 100)));
		params.setUnder_con_time(String.format("%03d", (Integer.parseInt(params.getUnder_con_time()))));
		params.setUnder_pause_time(String.format("%03d", (Integer.parseInt(params.getUnder_pause_time()))));
		
		params.setCoating_speedier(String.format("%02d", (Integer.parseInt(params.getCoating_speedier()))));
		params.setCoating_init_money(String.format("%03d", (Integer.parseInt(params.getCoating_init_money()) / 100)));
		params.setCoating_init_time(String.format("%03d", (Integer.parseInt(params.getCoating_init_time()))));
		params.setCoating_con_money(String.format("%03d", (Integer.parseInt(params.getCoating_con_money()) / 100)));
		params.setCoating_con_time(String.format("%03d", (Integer.parseInt(params.getCoating_con_time()))));
		params.setCoating_pause_time(String.format("%03d", (Integer.parseInt(params.getCoating_pause_time()))));
		
		params.setCycle_money(String.format("%03d", (Integer.parseInt(params.getCycle_money()) / 100)));
		params.setBuzzer_time(String.format("%02d", (Integer.parseInt(params.getBuzzer_time()))));
		params.setPause_count(String.format("%02d", (Integer.parseInt(params.getPause_count()))));
		params.setSecret_date(String.format("%03d", (Integer.parseInt(params.getSecret_date()))));
		params.setWipping_temperature(String.format("%02d", (Integer.parseInt(params.getWipping_temperature()))));
		
		String clock = currentClockFormat();
		deviceMapper.insertSelfConfig(params, currentClockFormat());
		
		// 설정 변경 프로토콜 
		String newConfig = "GL111SS";
		newConfig += glsConfig.getManager_no();
		newConfig += String.format("%02d", glsConfig.getSelf());
		newConfig += params.getDevice_addr();
		newConfig += params.getSelf_init_money();
		newConfig += params.getSelf_init_time();
		newConfig += params.getSelf_con_enable();
		newConfig += params.getSelf_con_money();
		newConfig += params.getSelf_con_time();
		newConfig += params.getSelf_pause_time();
		
		newConfig += params.getFoam_enable();
		newConfig += params.getFoam_con_enable();
		newConfig += params.getFoam_speedier();
		newConfig += params.getFoam_init_money();
		newConfig += params.getFoam_init_time();
		newConfig += params.getFoam_con_money();
		newConfig += params.getFoam_con_time();
		newConfig += params.getFoam_pause_time();
		newConfig += params.getFoam_end_delay();
		
		newConfig += params.getUnder_enable();
		newConfig += params.getUnder_con_enable();
		newConfig += params.getUnder_speedier();
		newConfig += params.getUnder_init_money();
		newConfig += params.getUnder_init_time();
		newConfig += params.getUnder_con_money();
		newConfig += params.getUnder_con_time();
		newConfig += params.getUnder_pause_time();
		
		newConfig += params.getCoating_enable();
		newConfig += params.getCoating_con_enable();
		newConfig += params.getCoating_speedier();
		newConfig += params.getCoating_init_money();
		newConfig += params.getCoating_init_time();
		newConfig += params.getCoating_con_money();
		newConfig += params.getCoating_con_time();
		newConfig += params.getCoating_pause_time();
		
		newConfig += params.getCycle_money();
		newConfig += params.getPay_free();
		newConfig += params.getBuzzer_time();
		newConfig += params.getPause_count();
		newConfig += params.getSecret_enable();
		newConfig += params.getSecret_date();
		
		// 히든 설정 
		newConfig += params.getSpeedier_enable();
		newConfig += params.getUse_type();
		newConfig += params.getSet_coating_output();
		newConfig += params.getWipping_enable();
		newConfig += params.getWipping_temperature();
		newConfig += checkSum(newConfig);
		newConfig += "CH";
		
		result = "1";
		
		try {
			logger.info("{}", newConfig);
			glsConfig.getSerialPort().writeBytes(newConfig.getBytes());
			Thread.sleep(500);

			byte[] readByte = glsConfig.getSerialPort().readBytes(); // 데이터 담을 변수 
			
			// 데이터 수신 완료 
			if (readByte != null && readByte.length > 0) {
				String readStr = new String(readByte); // 문자열로 변환 
				readStr = readStr.replace(" ", "0");
				logger.info("Config SET : {}", readStr);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
		
	} // 셀프 설정 셋팅 끝
	
	// 리더기 설정 불러오기 
	@Override
	public List<Map<String, Object>> getReaderConfig() {
		List<Map<String, Object>> result = new ArrayList<>();
		Map<String, Object> resultMap = new LinkedHashMap<String, Object>(); 
		
		String[] addrList = deviceMapper.selectDeviceAddr(glsConfig.getReader()); // 주소 DB셀렉트
		
		for(int i=1; i<=glsConfig.getReaderCount(); i++) {
			writeConfig(String.format("0%d", glsConfig.getReader()), addrList[i-1]);
			resultMap = readReaderConfig(addrList[i-1]);
			result.add(resultMap);
		}
		
		return result;
	}
	
	// 리더기 세팅 시리얼값 읽기
	@Override
	public Map<String, Object> readReaderConfig(String deviceAddr) {
		Map<String, Object> resultMap = new LinkedHashMap<String, Object>(); // 반환값 
		
		byte[] readByte;
		
		try {
			readByte = glsConfig.getSerialPort().readBytes(); // 데이터 담을 변수
			
			// 데이터 수신 완료 
			if (readByte != null && readByte.length > 0) {
				
				String readStr = new String(readByte); // 문자열로 변환 
				String tempStr = readStr.replace(" ", ""); // 체크섬 비교할 문자열 만들기 
				readStr = readStr.replace(" ", "0"); 
				logger.info("Config Rx : {}", readStr); 
	
				Integer length = readStr.length();
//					String checkSum = readStr.substring(0, tempStr.length()-4); // 체크섬 계산
//					checkSum = checkSum(checkSum);
//					String compareCheckSum = readStr.substring(105,107); 		// 프로토콜 자른 값 
//					logger.info("checkSum {}", checkSum);
//					logger.info("compare  {}", compareCheckSum);
				
//				GL 033 RL 09 01 010 010 100 1 02 01 0100 17 CH
				// 수신 완료시
				if (readStr.substring(0,2).equals("GL") && readStr.substring(31,33).equals("CH") && length == 33/* && checkSum.equals(compareCheckSum) */) {
					
					resultMap.put("device_addr", deviceAddr);
					resultMap.put("state", "1");
					
					resultMap.put("reader_init_money", Integer.parseInt(readStr.substring(11,14)) * 100);
					resultMap.put("reader_con_money", Integer.parseInt(readStr.substring(14,17)) * 100);
					resultMap.put("reader_cycle_money", Integer.parseInt(readStr.substring(17,20)) * 100);
					resultMap.put("reader_con_enable",Integer.parseInt(readStr.substring(20,21)));
					resultMap.put("reader_init_pulse", Integer.parseInt(readStr.substring(21,23)));
					resultMap.put("reader_con_pulse", Integer.parseInt(readStr.substring(23,25)));
					resultMap.put("reader_pulse_duty", Integer.parseInt(readStr.substring(25,29)));
					
				} else { // 수신 실패
					resultMap.put("device_addr", deviceAddr);
					resultMap.put("state", "0");
				}
				
				// DB = PCB 값 비교 
				if(!resultMap.get("state").equals("0")) {
					ReaderConfigVO reader = deviceMapper.selectReaderConfig(deviceAddr);
					
					String diff = "0"; // 설정 이상 비교 플래그
					
					if (!readStr.substring(11,14).equals(reader.getReader_init_money())) {
						diff = "1";
						reader.setReader_init_money(readStr.substring(11,14));
					}
					if (!readStr.substring(14,17).equals(reader.getReader_con_money())) {
						diff = "1";
						reader.setReader_con_money(readStr.substring(14,17));
					}
					if (!readStr.substring(17,20).equals(reader.getReader_cycle_money())) {
						diff = "1";
						reader.setReader_cycle_money(readStr.substring(17,20));
					}
					if (!readStr.substring(20,21).equals(reader.getReader_con_enable())) {
						diff = "1";
						reader.setReader_con_enable(readStr.substring(20,21));
					}
					if (!readStr.substring(21,23).equals(reader.getReader_init_pulse())) {
						diff = "1";
						reader.setReader_init_pulse(readStr.substring(21,23));
					}
					if (!readStr.substring(23,25).equals(reader.getReader_con_pulse())) {
						diff = "1";
						reader.setReader_con_pulse(readStr.substring(23,25));
					}
					if (!readStr.substring(25,29).equals(reader.getReader_pulse_duty())) {
						diff = "1";
						reader.setReader_pulse_duty(readStr.substring(25,29));
					}
					
					if (diff.equals("1")) {
						logger.info("설정값 바뀜");
						deviceMapper.insertReaderConfig(reader, currentClockFormat());
					}
				}
			}// 데이터 수신완료 끝
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return resultMap;
	}
	
	// 리더기 설정 
	@Override
	public String setReaderConfig(ReaderConfigVO params) {
		
		String result = "0"; // 반환값 : 성공 1 실패 0
		
		logger.info("{}", params.toString());
		
		/*
		 ReaderConfigVO 
		 [device_addr=01, reader_init_money=1000, reader_con_money=1000, 
		 reader_cycle_money=10000, reader_con_enable=1, reader_init_pulse=2, 
		 reader_con_pulse=1, reader_pulse_duty=100, input_date=null] 
		 */

		params.setReader_init_money(String.format("%03d", (Integer.parseInt(params.getReader_init_money()) / 100)));
		params.setReader_con_money(String.format("%03d", (Integer.parseInt(params.getReader_con_money()) / 100)));
		params.setReader_cycle_money(String.format("%03d", (Integer.parseInt(params.getReader_cycle_money()) / 100)));
		params.setReader_init_pulse(String.format("%02d", (Integer.parseInt(params.getReader_init_pulse()))));
		params.setReader_con_pulse(String.format("%02d", (Integer.parseInt(params.getReader_con_pulse()))));
		params.setReader_pulse_duty(String.format("%04d", (Integer.parseInt(params.getReader_pulse_duty()))));
		
		String clock = currentClockFormat();
		deviceMapper.insertReaderConfig(params, currentClockFormat());
		
		// 설정 변경 프로토콜 
		String newConfig = "GL035RS";
		newConfig += glsConfig.getManager_no();
		newConfig += String.format("%02d", glsConfig.getReader());
		newConfig += params.getDevice_addr();
		newConfig += params.getReader_init_money();
		newConfig += params.getReader_con_money();
		newConfig += params.getReader_cycle_money();
		newConfig += params.getReader_con_enable();
		newConfig += params.getReader_init_pulse();
		newConfig += params.getReader_con_pulse();
		newConfig += params.getReader_pulse_duty();
		newConfig += checkSum(newConfig);
		newConfig += "CH";
		
		result = "1";
		
		try {
			logger.info("{}", newConfig);
			glsConfig.getSerialPort().writeBytes(newConfig.getBytes());
			Thread.sleep(500);

			byte[] readByte = glsConfig.getSerialPort().readBytes(); // 데이터 담을 변수 
			
			// 데이터 수신 완료 
			if (readByte != null && readByte.length > 0) {
				String readStr = new String(readByte); // 문자열로 변환 
				readStr = readStr.replace(" ", "0");
				logger.info("Config SET : {}", readStr);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	// 충전기 설정 불러오기
	/*
	 * (non-Javadoc)
	 * @see kr.gls.myapp.device.service.IDeviceService#getChargerConfig()
	 * PCB로부터 직접 장비 설정 값을 호출하여 포스에게 전달하는 함수
   	  각 장비로부터 설정값을 불러와 데이터베이스에 저장된 값과 비교를 하여
         다른 부분이 있으면 새로운 설정을 데이터베이스에 입력한 후 포스에 전달한다.
     * 참고 : 1. config는 update가 아닌 insert로 log 형식으로 누적한다.
            2. bonus가 변경될 경우 default_bonus를 변경하며 전체 장비에 영향을 끼친다. 
	 */
	@Override
	public List<Map<String, Object>> getChargerConfig() {
		List<Map<String, Object>> result = new ArrayList<>();
		Map<String, Object> resultMap = new LinkedHashMap<String, Object>(); 
		
		String[] addrList = deviceMapper.selectDeviceAddr(glsConfig.getCharger()); // 주소 DB셀렉트
		
		for(int i=1; i<=glsConfig.getChargerCount(); i++) {
			writeConfig(String.format("0%d", glsConfig.getCharger()), addrList[i-1]);
			resultMap = readChargerConfig(addrList[i-1]);
			result.add(resultMap);
		}
		
		return result;
	}
	
	// 충전기 세팅 시리얼값 읽기 
	@Override
	public Map<String, Object> readChargerConfig(String deviceAddr) {
		Map<String, Object> resultMap = new LinkedHashMap<String, Object>(); // 반환값 
		
		byte[] readByte;
		
		try {
			readByte = glsConfig.getSerialPort().readBytes(); // 데이터 담을 변수
			
			// 데이터 수신 완료 
			if (readByte != null && readByte.length > 0) {
				
				String readStr = new String(readByte); // 문자열로 변환 
				String tempStr = readStr.replace(" ", ""); // 체크섬 비교할 문자열 만들기 
				readStr = readStr.replace(" ", "0"); 
				logger.info("Config Rx : {}", readStr); 
	
				Integer length = readStr.length();
//				GL 055 CL 03 01 050 010 0 100 010 030 050 070 100 110 130 150 170 200 78 CH
				// 수신 완료시
				if (readStr.substring(0,2).equals("GL") && readStr.substring(53,55).equals("CH") && length==55/* && checkSum.equals(compareCheckSum) */) {
					
					resultMap.put("device_addr", deviceAddr);
					resultMap.put("state", "1");
					
					resultMap.put("card_min_price", Integer.parseInt(readStr.substring(11,14)) * 100);
					resultMap.put("card_price", Integer.parseInt(readStr.substring(14,17)) * 100);
					resultMap.put("auto_charge_enable", readStr.substring(17,18));
					resultMap.put("auto_charge_price", Integer.parseInt(readStr.substring(18,21)) * 100);
					resultMap.put("bonus1", Integer.parseInt(readStr.substring(21,24)) * 100);
					resultMap.put("bonus2", Integer.parseInt(readStr.substring(24,27)) * 100);
					resultMap.put("bonus3", Integer.parseInt(readStr.substring(27,30)) * 100);
					resultMap.put("bonus4", Integer.parseInt(readStr.substring(30,33)) * 100);
					resultMap.put("bonus5", Integer.parseInt(readStr.substring(33,36)) * 100);
					resultMap.put("bonus6", Integer.parseInt(readStr.substring(36,39)) * 100);
					resultMap.put("bonus7", Integer.parseInt(readStr.substring(39,42)) * 100);
					resultMap.put("bonus8", Integer.parseInt(readStr.substring(42,45)) * 100);
					resultMap.put("bonus9", Integer.parseInt(readStr.substring(45,48)) * 100);
					resultMap.put("bonus10", Integer.parseInt(readStr.substring(48,51)) * 100);	
					
				} else { // 수신 실패 시
					resultMap.put("device_addr", deviceAddr);
					resultMap.put("state", "0");
				}
				
				// DB - PCB 비교 
				if (!resultMap.get("state").equals("0")) {
					/*
					ChargerConfigVO 
					[device_no=403, shop_pw=MTIzNA==, card_price=010, card_min_price=100, 
					auto_charge_enable=0, auto_charge_price=100, exhaust_charge_enable=0, 
					bonus1=010, bonus2=020, bonus3=040, bonus4=070, bonus5=100, 
					bonus6=110, bonus7=130, bonus8=150, bonus9=170, bonus10=200, device_addr=01]
					*/	
					ChargerConfigVO charger = deviceMapper.selectChargerConfig(glsConfig.getCharger(), deviceAddr);
					String diff = "0"; // 설정 이상 비교 플래그
					
					if (!readStr.substring(11,14).equals(charger.getCard_min_price())) {
						diff = "1";
						charger.setCard_min_price(readStr.substring(11,14));
					}
					if (!readStr.substring(14,17).equals(charger.getCard_price())) {
						diff = "1";
						charger.setCard_min_price(readStr.substring(14,17));
					}
					if (!readStr.substring(17,18).equals(charger.getAuto_charge_enable())) {
						diff = "1";
						charger.setAuto_charge_enable(readStr.substring(17,18));
					}
					if (!readStr.substring(18,21).equals(charger.getAuto_charge_price())) {
						diff = "1";
						charger.setAuto_charge_price(readStr.substring(18,21));
					}
					if (!readStr.substring(21,24).equals(charger.getBonus1())) {
						diff = "1";
						charger.setBonus1(readStr.substring(21,24));
					}
					if (!readStr.substring(24,27).equals(charger.getBonus2())) {
						diff = "1";
						charger.setBonus2(readStr.substring(24,27));
					}
					if (!readStr.substring(27,30).equals(charger.getBonus3())) {
						diff = "1";
						charger.setBonus3(readStr.substring(27,30));
					}
					if (!readStr.substring(30,33).equals(charger.getBonus4())) {
						diff = "1";
						charger.setBonus4(readStr.substring(30,33));
					}
					if (!readStr.substring(33,36).equals(charger.getBonus5())) {
						diff = "1";
						charger.setBonus5(readStr.substring(33,36));
					}
					if (!readStr.substring(36,39).equals(charger.getBonus6())) {
						diff = "1";
						charger.setBonus6(readStr.substring(36,39));
					}
					if (!readStr.substring(39,42).equals(charger.getBonus7())) {
						diff = "1";
						charger.setBonus7(readStr.substring(39,42));
					}
					if (!readStr.substring(42,45).equals(charger.getBonus8())) {
						diff = "1";
						charger.setBonus8(readStr.substring(42,45));
					}
					if (!readStr.substring(45,48).equals(charger.getBonus9())) {
						diff = "1";
						charger.setBonus9(readStr.substring(45,48));
					}
					if (!readStr.substring(48,51).equals(charger.getBonus10())) {
						diff = "1";
						charger.setBonus10(readStr.substring(48,51));
					}
					
					if (diff.equals("1")) {
						logger.info("설정값 다름");
						deviceMapper.insertChargerConfig(charger, currentClockFormat());
					}
					
					if (diff.equals("2")) {
						logger.info("보너스 설정 다름");
						deviceMapper.updateChargerBonus(charger, currentClockFormat());
					}
					
				} // DB - PCB 비교 끝
				
			} // 시리얼 수신 완료 끝
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return resultMap;
	}
	
	
	// 충전기 설정 
	@Override
	public String setChargerConfig(ChargerConfigVO params) {
		String result = "0"; // 반환값 : 성공 1 실패 0
		
		//logger.info("{}", params.toString());
		/*
		 ChargerConfigVO 포스에서 가져온 값
		 [device_no=null, shop_pw=null, card_price=1000, card_min_price=5000, 
		 auto_charge_enable=0, auto_charge_price=10000, exhaust_charge_enable=null, 
		 bonus1=1000, bonus2=3000, bonus3=5000, bonus4=7000, bonus5=10000, 
		 bonus6=11000, bonus7=13000, bonus8=15000, bonus9=17000, bonus10=20000, device_addr=01]
		 */

		try {
			
			// 포스에서 가져온값 DB값으로 알맞게 파싱
			params.setCard_price(String.format("%03d", (Integer.parseInt(params.getCard_price()) / 100)));
			params.setCard_min_price(String.format("%03d", (Integer.parseInt(params.getCard_min_price()) / 100)));
			params.setAuto_charge_price(String.format("%03d", (Integer.parseInt(params.getAuto_charge_price()) / 100)));
			params.setBonus1(String.format("%03d", (Integer.parseInt(params.getBonus1()) / 100)));
			params.setBonus2(String.format("%03d", (Integer.parseInt(params.getBonus2()) / 100)));
			params.setBonus3(String.format("%03d", (Integer.parseInt(params.getBonus3()) / 100)));
			params.setBonus4(String.format("%03d", (Integer.parseInt(params.getBonus4()) / 100)));
			params.setBonus5(String.format("%03d", (Integer.parseInt(params.getBonus5()) / 100)));
			params.setBonus6(String.format("%03d", (Integer.parseInt(params.getBonus6()) / 100)));
			params.setBonus7(String.format("%03d", (Integer.parseInt(params.getBonus7()) / 100)));
			params.setBonus8(String.format("%03d", (Integer.parseInt(params.getBonus8()) / 100)));
			params.setBonus9(String.format("%03d", (Integer.parseInt(params.getBonus9()) / 100)));
			params.setBonus10(String.format("%03d", (Integer.parseInt(params.getBonus10()) / 100)));
			
			
			String deviceNo = deviceMapper.selectDeviceNo(glsConfig.getCharger(), params.getDevice_addr());
			String shopNo = deviceMapper.selectShopNo();
			
			params.setDevice_no(Integer.parseInt(deviceNo));
			params.setShop_no(shopNo);
			
			deviceMapper.insertChargerConfig(params, currentClockFormat());
			deviceMapper.updateChargerBonus(params, currentClockFormat());
			
			String newConfig = "GL057CS";
			newConfig += glsConfig.getManager_no();
			newConfig += String.format("%02d", glsConfig.getCharger());
			newConfig += params.getDevice_addr(); 
			newConfig += params.getCard_min_price();
			newConfig += params.getCard_price();
			newConfig += params.getAuto_charge_enable();
			newConfig += params.getAuto_charge_price();
			newConfig += params.getBonus1();
			newConfig += params.getBonus2();
			newConfig += params.getBonus3();
			newConfig += params.getBonus4();
			newConfig += params.getBonus5();
			newConfig += params.getBonus6();
			newConfig += params.getBonus7();
			newConfig += params.getBonus8();
			newConfig += params.getBonus9();
			newConfig += params.getBonus10();
			newConfig += checkSum(newConfig);
			newConfig += "CH";
			
			result = "1";
			
			logger.info("{}", newConfig);
			glsConfig.getSerialPort().writeBytes(newConfig.getBytes());
			Thread.sleep(500);

			byte[] readByte = glsConfig.getSerialPort().readBytes(); // 데이터 담을 변수 
			
			// 데이터 수신 완료 
			if (readByte != null && readByte.length > 0) {
				String readStr = new String(readByte); // 문자열로 변환 
				readStr = readStr.replace(" ", "0");
				logger.info("Config SET : {}", readStr);
			}
				
		} catch (Exception e) {
			e.printStackTrace();
		}
			
		return result;
	}
	
	
	/*
	 * 기기 주소 변경
	  device_type, before_addr, after_addr 3개의 인자를 받아 장비의 주소를 변경한다.
	  after_addr을 1씩 늘려가며 현재 설정되어 있는 가장 큰 주소값을 찾고,
	    그 주소에 1을 더해 임시 저장 주소로 사용한다.
	    임시 저장 주소가 변경하고자하는 주소와 같을 경우는 바로 변경 작업을 하고,
	    다를 경우 아래와 같은 순서로 변경을 실시한다. 
    1. after -> temp
    2. before -> after
    3. temp -> before
	 */
	@Override
	public String changeDeviceAddr(String device_type, String before_addr, String after_addr) {
//		System.out.println(device_type); // TODO 포스이슈 : 리더기 타입값이 넘어오지않음 
//		System.out.println(before_addr);
//		System.out.println(after_addr);
//		0
//		08
//		09
		
		// 485 스레드 동작 중일떄 
		if (scheduler.getIsThread()) {
			scheduler.stopScheduler(); // 스케쥴러 중지
		}
		String result = "0";
		
		String tempAddr = after_addr; // 임시 저장 주소 
		
		try {
			Integer count = 0; // 시도횟수 
			
			while (true) {
				count++;
				
//				for() { // TODO DB - 전체 장비 갯수 구하기 
					// 응답이 있을떄 
					if (writeAddr(device_type, before_addr, after_addr, count)) {
					
						if (count == 3) { // 어드레스 변경 
							deviceMapper.updateDeviceListAddr(Integer.parseInt(device_type), before_addr, after_addr);
							result = "1";
							return result; 
						}
					} 
//				}
			}
			
			
		} catch(Exception e) {
			result = "0";
			e.printStackTrace();
		}
		
		return result;
	}
	
	// 기기 주소 프로토콜 만들기
	public boolean writeAddr(String deviceType, String beforeAddr, String afterAddr, Integer count) {
		try {
			String trans = ""; // 송신 프로토콜 
			trans += "GL020AD";
			trans += glsConfig.getManager_no();
			trans += String.format("%02d", Integer.parseInt(deviceType));
			trans += beforeAddr;
			trans += afterAddr;
			trans += Integer.toString(count);
			trans += checkSum(trans);
			trans += "CH";

			logger.info("{}", trans);
			glsConfig.getSerialPort().writeBytes(trans.getBytes()); // Write data to port
			Thread.sleep(500);
			
			byte[] readByte = glsConfig.getSerialPort().readBytes(); // 데이터 담을 변수
				
			// 데이터 수신 완료 
			if (readByte != null && readByte.length > 0) {
				String readStr = new String(readByte); // 문자열로 변환  
				readStr = readStr.replace(" ", "0"); 
				logger.info("Addr Rx : {}", readStr);
				return true;
			} else {
				return false;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} 
	}
	
	/*
	 * 누적금액 초기화
	     포스에서 원활한 UI 제어를 위해 한번에 전체 장비를 초기화하지 않고
	     포스로부터 디바이스 타입과 주소를 넘겨 받아 해당 장비의 누적금액을 초기화한 후
	   '성공', '실패'를 리턴한다.
	 */
	@Override
	public String resetTotalMoney(String device_type, String device_addr) {
		// 485 스레드 동작 중일떄 
		if (scheduler.getIsThread()) {
			scheduler.stopScheduler(); // 스케쥴러 중지
		}
		
		String result = "실패"; 
		
		try {
			// 485 장비
			if (Integer.parseInt(device_type) == glsConfig.getSelf() || Integer.parseInt(device_type) == glsConfig.getAir()
					|| Integer.parseInt(device_type) == glsConfig.getMate() || Integer.parseInt(device_type) == glsConfig.getCharger() 
					|| Integer.parseInt(device_type) == glsConfig.getReader()) {
				
				writeOther(device_type, device_addr, "IN");
				
			} else if (Integer.parseInt(device_type) == glsConfig.getTouch() 
					|| Integer.parseInt(device_type) == glsConfig.getKiosk()) { // LAN 장비
				
//				Integer deviceNo = deviceMapper.selectNo(device_type, device_addr); // device_no 추출
				
				
			}
			
		} catch (Exception e) {
			result = "실패";
			e.printStackTrace();
		}
			
		result = "성공";
		return result;
	}
	
	/*
	 * 세차장 ID 변경
	    각 장비의 상점 ID를 변경한다.
	    누적금액 초기화와 같은 이유로 한번에 전체 장비를 초기화하지 않고
	    포스로부터 디바이스 타입과 주소를 넘겨 받아 해당 장비의 상점 ID를 변경한 후
     '성공', '실패'를 리턴한다.
     * 참고 : 본 함수는 포스 설정에서 이미 변경되어 있는 상점 ID를 장비에 입력하는 기능
	 */
	@Override
	public String updateShopNo(String device_type, String device_addr) {
		// 485 스레드 동작 중일떄 
		if (scheduler.getIsThread()) {
			scheduler.stopScheduler(); // 스케쥴러 중지
		}
		
		String result = "실패"; 
		String shopNo = PosConfigMapper.selectShopNo(); // 변경할 상점 ID 가져오기
		String transByte = ""; // 프로토콜 바이트보낼값
		String readStr; // 읽어온 값 문자열로 치환 
		
		try {
			// 485 장비
			if (Integer.parseInt(device_type) == glsConfig.getSelf() || Integer.parseInt(device_type) == glsConfig.getAir()
					|| Integer.parseInt(device_type) == glsConfig.getMate() || Integer.parseInt(device_type) == glsConfig.getCharger() 
					|| Integer.parseInt(device_type) == glsConfig.getReader()) {
				
				transByte = "GL021ID";
				transByte += glsConfig.getManager_no();
				transByte += String.format("%02d", Integer.parseInt(device_type));
				transByte += device_addr;
				transByte += shopNo;
				transByte += checkSum(transByte);
				transByte += "CH";

				logger.info("{}", transByte);
				glsConfig.getSerialPort().writeBytes(transByte.getBytes()); // Write data to port
				Thread.sleep(200);
				
				byte[] readByte = glsConfig.getSerialPort().readBytes(); // 데이터 담을 변수
					
				// 데이터 수신 완료 
				if (readByte != null && readByte.length > 0) {
					readStr = new String(readByte); // 문자열로 변환  
					readStr = readStr.replace(" ", "0"); 
					logger.info("SHOP ID Rx : {}", readStr);
				}
				
			} else if (Integer.parseInt(device_type) == glsConfig.getTouch()) { // 터치충전기
				
				// device_no 추출
				Integer deviceNo = deviceMapper.selectNo(Integer.parseInt(device_type), device_addr);
				
				// 데이터 수집장치 업데이트 
				deviceMapper.updateChargerConfigShopID(shopNo, deviceNo);
				
				// 터치충전기 DB 업데이트 
				touchMapper.updateTouchShopID(shopNo);
				
			} else if (Integer.parseInt(device_type) == glsConfig.getKiosk()) { // 키오스크
				
			}
			
		} catch (Exception e) {
			result = "실패";
			e.printStackTrace();
		}
			
		result = "성공";
		return result;
	}
	
	// 장비 설정 복사 
	@Override
	public String copyDeviceConfig(String type, String copy, String target) {
		System.out.println(type);
		System.out.println(copy);
		System.out.println(target);
//		0
//		01
//		02

		String result = "0";
		
		try {
			
			if (Integer.parseInt(type) == glsConfig.getSelf()) {
				
			}
			
			if (Integer.parseInt(type) == glsConfig.getAir()) {
					
			}
			
			if (Integer.parseInt(type) == glsConfig.getMate()) {
				
			}
			
			if (Integer.parseInt(type) == glsConfig.getCharger()) {
				
			}
			
			if (Integer.parseInt(type) == glsConfig.getTouch()) {
				
			}
			
			if (Integer.parseInt(type) == glsConfig.getKiosk()) {
				
			}
			
			if (Integer.parseInt(type) == glsConfig.getReader()) {
				
			}

			if (Integer.parseInt(type) == glsConfig.getGarage()) {
				
			}
			
		} catch (Exception e) {
			result = "0";
			logger.info("copyDeviceConfig Exception {}", e.getMessage());
		}
		
		return result;
	}
	
	public String moneyFormatting(int money, int kind) {
		String strMoney = "";

		if (money==0) {
			strMoney = "0";
		} else if (money > 0 && money < 10000) {
			strMoney = String.format("00%d", (money / 100));
		} else if(money>=10000 && money<100000){
			strMoney = String.format("0%d", (money / 100));
		} else {
			strMoney = String.format("%d", (money / 100));
//				System.out.println(card);
		}
		return strMoney;
	}
	
	
	
}

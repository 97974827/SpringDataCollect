package kr.gls.myapp.touch.service;

import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import kr.gls.myapp.common.GlsConfigVO;
import kr.gls.myapp.device.model.ChargerStateVO;
import kr.gls.myapp.device.repository.IDeviceMapper;
import kr.gls.myapp.touch.model.TouchChargeStateVO;
import kr.gls.myapp.touch.model.TouchConfigSetVO;
import kr.gls.myapp.touch.model.TouchListVO;
import kr.gls.myapp.touch.repository.ITouchChargerMapper;

@Service
public class TouchChargerService implements ITouchChargerService{
	
	@Autowired
	private ITouchChargerMapper mapper; // mapper DAO
	
	@Autowired
	private IDeviceMapper deviceMapper;
	
	@Autowired
	private GlsConfigVO glsConfig; // 데이터 수집장치 디폴트 값 객체 주입 
	
	// Touch DB
	private String driver = "com.mysql.cj.jdbc.Driver"; 
	private String url = "jdbc:mysql://192.168.0.224:3306/glstech?serverTimezone=Asia/Seoul";
	private String uid = "pi";
	private String upw = "1234";
	
	private boolean STATE_TOUCH_THREAD = true; // 30초 마다 한번씩 
	
	/*
	 * 충전기 설정값 설정
	 1. 포스에서 설정값 바꿀 데이터 가져오기 
	 2. DC DB에 바꿀 데이터 적용 update
	 3. 충전기 DB접속 
	 4. 충전기 설정값 update
	 * 보너스 설정은 기본 보너스 설정값을 업데이트 함으로 다른 기기에 영향을 미침
	 */
	@Override
	public String setTouchConfig(TouchConfigSetVO params) {
		//System.out.println("params : " + params.toString());
		String res = "0"; // 결과 1 : 성공  / 0 : 실패
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		List<TouchListVO> ipList = mapper.getTouchList(glsConfig.getTouch()); // 장비 리스트
		
		if (getConnect(ipList.get(0).getIp())) {
			
			// 원본 파싱 데이터 - 터치충전기 쏴주는 용도 
			String bonus1 = params.getBonus1();
			String bonus2 = params.getBonus2();
			String bonus3 = params.getBonus3();
			String bonus4 = params.getBonus4();
			String bonus5 = params.getBonus5();
			String bonus6 = params.getBonus6();
			String bonus7 = params.getBonus7();
			String bonus8 = params.getBonus8();
			String bonus9 = params.getBonus9();
			String bonus10 = params.getBonus10();
			String cardPrice = params.getCard_price();
			String cardMinPrice = params.getCard_min_price();
			String autoChargePrice = params.getAuto_charge_price();

			// 금액 자릿수 조절
			params.setBonus1(moneyFormatting(Integer.parseInt(params.getBonus1())));
			params.setBonus2(moneyFormatting(Integer.parseInt(params.getBonus2())));
			params.setBonus3(moneyFormatting(Integer.parseInt(params.getBonus3())));
			params.setBonus4(moneyFormatting(Integer.parseInt(params.getBonus4())));
			params.setBonus5(moneyFormatting(Integer.parseInt(params.getBonus5())));
			params.setBonus6(moneyFormatting(Integer.parseInt(params.getBonus6())));
			params.setBonus7(moneyFormatting(Integer.parseInt(params.getBonus7())));
			params.setBonus8(moneyFormatting(Integer.parseInt(params.getBonus8())));
			params.setBonus9(moneyFormatting(Integer.parseInt(params.getBonus9())));
			params.setBonus10(moneyFormatting(Integer.parseInt(params.getBonus10())));
			params.setAuto_charge_price(moneyFormatting(Integer.parseInt(params.getAuto_charge_price())));
			params.setCard_price(moneyFormatting(Integer.parseInt(params.getCard_price())));
			params.setCard_min_price(moneyFormatting(Integer.parseInt(params.getCard_min_price())));
			params.setDevice_addr(params.getDevice_addr());
			params.setRf_reader_type(params.getRf_reader_type());
			params.setShop_no(params.getShop_no());
			
			try {
				byte[] targetBytes = params.getShop_pw().getBytes("UTF-8");
				byte[] encodedBytes = Base64.getEncoder().encode(targetBytes);
				params.setShop_pw(new String(encodedBytes));
			} catch (Exception e) {}
			
			// 수집장치 update
			mapper.updateChargerConfig(params, ipList.get(0).getNo()); // 터치충전기 참조키 번호
			mapper.updateBonus(params, glsConfig.getDefault_bonus()); // 새로운 설정값 셋팅 

			res = "1";
			
			// 터치충전기 update
			String sql = "UPDATE config SET device_addr=?, admin_password=?, " + 
					"card_price=?, min_card_price=?, bonus1=?, bonus2=?, "+
					"bonus3=?, bonus4=?, bonus5=?, bonus6=?, bonus7=?, "+
					"bonus8=?, bonus9=?, "+ 
                    "bonus10=?, auto_charge_state=?, auto_charge_price=?";
						
			try {
				Class.forName(driver);
				conn = DriverManager.getConnection(url, uid, upw);
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, params.getDevice_addr());
				pstmt.setString(2, params.getShop_pw());
				pstmt.setString(3, cardPrice);
				pstmt.setString(4, cardMinPrice);
				pstmt.setString(5, bonus1);
				pstmt.setString(6, bonus2);
				pstmt.setString(7, bonus3);
				pstmt.setString(8, bonus4);
				pstmt.setString(9, bonus5);
				pstmt.setString(10, bonus6);
				pstmt.setString(11, bonus7);
				pstmt.setString(12, bonus8);
				pstmt.setString(13, bonus9);
				pstmt.setString(14, bonus10);
				pstmt.setString(15, params.getAuto_charge_enable());
				pstmt.setString(16, autoChargePrice);
				
				pstmt.executeUpdate();
				
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try { 
					rs.close();
					pstmt.close();
					conn.close();
				} catch(Exception e) {}
			}
		} // 연결 성공 
		else {
			res = "0";
		}
		return res;
	}

	/*
	 * 충전기 설정값 가져오기
	 1. 충전기 ip 가져오기 
	 2. 커넥트 테스트 - getConnect
	 3. 충전기 DB접속 - 설정값 가져오기 - DB대로 자리수 조절 
	 4. 수집 장치 DB접속 - 설정값 가져오기  - DB대로 자리수 조절
	 5. 비교 
	 6. 같으면 같은값 그대로 리턴 , 다르면 insert update 처리후 반환
	 */
	@Override
	public Map<String, Object> getTouchConfig() {
		
		Map<String, Object> touchConfig = new HashMap<String, Object>(); // 터치 = DC 값 비교 MAP
		Map<String, Object> tempConfig = new LinkedHashMap<String, Object>(); // 원본 데이터 넘길 Map
		
		String diff = "0"; // 설정 이상 플래그 
		
		List<TouchListVO> vo = mapper.getTouchList(glsConfig.getTouch()); // 장비 리스트
		System.out.println(vo.toString());
		
		for (TouchListVO t : vo) { 
			// 연결 성공 시 터치충전기 DB접속 
			if (getConnect(t.getIp())) {
				
				Connection conn = null;
				PreparedStatement pstmt = null;
				ResultSet rs = null;
				
				String sql = "SELECT device_addr AS 'addr', `master_password` AS 'admin_pw', "
						+ "`gil_password` AS 'manager_pw', `admin_password` AS 'shop_pw', "
						+ "`card_price`, `min_card_price`, `bonus1`, `bonus2`, `bonus3`, "
						+ "`bonus4`, `bonus5`, `bonus6`, `bonus7`, `bonus8`, `bonus9`, `bonus10`, "
						+ "`auto_charge_state` AS 'auto_charge_enable', `auto_charge_price`, "
						+ "`rf_reader_type`, `id` AS 'shop_no', `shop_name` AS 'name', "
						+ "`shop_id` AS 'manager_key' FROM config";
				String shop_pw = "";
				try {
					Class.forName(driver);
					conn = DriverManager.getConnection(url, uid, upw);
					pstmt = conn.prepareStatement(sql);
					rs = pstmt.executeQuery();
					
					if (rs.next()) { 
						tempConfig.put("device_addr", rs.getString("addr"));
						tempConfig.put("state", "1");
						shop_pw = rs.getString("shop_pw");
						byte[] decoded = Base64.getDecoder().decode(shop_pw);
						shop_pw = new String(decoded, StandardCharsets.UTF_8);
						tempConfig.put("shop_pw", shop_pw);
						tempConfig.put("card_price", rs.getInt("card_price"));
						tempConfig.put("card_min_price", rs.getInt("min_card_price"));
						tempConfig.put("bonus1", rs.getString("bonus1"));
						tempConfig.put("bonus2", rs.getString("bonus2"));
						tempConfig.put("bonus3", rs.getString("bonus3"));
						tempConfig.put("bonus4", rs.getString("bonus4"));
						tempConfig.put("bonus5", rs.getString("bonus5"));
						tempConfig.put("bonus6", rs.getString("bonus6"));
						tempConfig.put("bonus7", rs.getString("bonus7"));
						tempConfig.put("bonus8", rs.getString("bonus8"));
						tempConfig.put("bonus9", rs.getString("bonus9"));
						tempConfig.put("bonus10", rs.getString("bonus10"));
						tempConfig.put("auto_charge_enable", rs.getString("auto_charge_enable"));
						tempConfig.put("auto_charge_price", rs.getInt("auto_charge_price"));
						tempConfig.put("rf_reader_type", rs.getString("rf_reader_type"));
						tempConfig.put("shop_no", rs.getString("shop_no"));
						tempConfig.put("name", rs.getString("name"));
						tempConfig.put("manager_key", rs.getString("manager_key"));
						
						String addr = rs.getString("addr");
						String state = "1";
						
						String admin_pw = rs.getString("admin_pw");
						String manager_pw = rs.getString("manager_pw");
//						String shop_pw = rs.getString("shop_pw");
						decoded = Base64.getDecoder().decode(admin_pw);
						admin_pw = new String(decoded, StandardCharsets.UTF_8);
						decoded = Base64.getDecoder().decode(manager_pw);
						manager_pw = new String(decoded, StandardCharsets.UTF_8);

//						tempConfig.put("admin_pw", admin_pw);
//						tempConfig.put("manager_pw", manager_pw);
						
						int card = rs.getInt("card_price");
						int min_card = rs.getInt("min_card_price");
						String bonus1 = moneyFormatting(Integer.parseInt(rs.getString("bonus1")));
						String bonus2 = moneyFormatting(Integer.parseInt(rs.getString("bonus2")));
						String bonus3 = moneyFormatting(Integer.parseInt(rs.getString("bonus3")));
						String bonus4 = moneyFormatting(Integer.parseInt(rs.getString("bonus4")));
						String bonus5 = moneyFormatting(Integer.parseInt(rs.getString("bonus5")));
						String bonus6 = moneyFormatting(Integer.parseInt(rs.getString("bonus6")));
						String bonus7 = moneyFormatting(Integer.parseInt(rs.getString("bonus7")));
						String bonus8 = moneyFormatting(Integer.parseInt(rs.getString("bonus8")));
						String bonus9 = moneyFormatting(Integer.parseInt(rs.getString("bonus9")));
						String bonus10 = moneyFormatting(Integer.parseInt(rs.getString("bonus10")));
						String auto_charge_enable = rs.getString("auto_charge_enable");
						String auto_charge_price = rs.getString("auto_charge_price");
						String rf_reader_type = rs.getString("rf_reader_type");
						String shop_no = rs.getString("shop_no");
						String name = rs.getString("name");
						String manager_key = rs.getString("manager_key");
						
						String card_price = moneyFormatting(card);
						String card_min_price = moneyFormatting(min_card);
						
						touchConfig.put("addr", addr);
						touchConfig.put("state", state);
						
						touchConfig.put("admin_pw", admin_pw);
						touchConfig.put("manager_pw", manager_pw);
						touchConfig.put("shop_pw", shop_pw);
						touchConfig.put("card_price", card_price);
						touchConfig.put("card_min_price", card_min_price);
						touchConfig.put("bonus1", bonus1);
						touchConfig.put("bonus2", bonus2);
						touchConfig.put("bonus3", bonus3);
						touchConfig.put("bonus4", bonus4);
						touchConfig.put("bonus5", bonus5);
						touchConfig.put("bonus6", bonus6);
						touchConfig.put("bonus7", bonus7);
						touchConfig.put("bonus8", bonus8);
						touchConfig.put("bonus9", bonus9);
						touchConfig.put("bonus10", bonus10);
						touchConfig.put("auto_charge_enable", auto_charge_enable);
						touchConfig.put("auto_charge_price", auto_charge_price);
						touchConfig.put("rf_reader_type", rf_reader_type);
						touchConfig.put("shop_no", shop_no);
						touchConfig.put("name", name);
						touchConfig.put("manager_key", manager_key);
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				} finally{
					try { 
						rs.close(); 
						pstmt.close();
						conn.close();
					} catch(Exception e) {}
				}
				/*
				// 데이터 수집장치 설정 불러오기
				Map<String, Object> map = new HashMap<>(); // 전달 맵 
				map.put("type", glsConfig.getTouch());
				map.put("addr", touchConfig.get("addr"));
				
				TouchConfigGetVO touch = mapper.getTouchConfigVO(map); // 데이터 수집장치 SQL호출
				touch.setNo(t.getNo());
				touch.setType((Integer) map.get("type"));
				touch.setDeviceAddr((String) touchConfig.get("addr"));
				String auto_charge_price = touchConfig.get("auto_charge_price").toString();
				touch.setAutoChargePrice(auto_charge_price);

				System.out.println(touch.toString());
				// 터치DB = DC DB 비교
				if (!touch.getDeviceAddr().equals(touchConfig.get("addr"))) {
					diff = "1";
				} if(!touch.getShopPw().equals(touchConfig.get("shop_pw"))) {
					diff = "1";
				} if(!touch.getCardPrice().equals(touchConfig.get("card_price"))) {
					diff = "1";
				} if(!touch.getCardMinPrice().equals(touchConfig.get("card_min_price"))) {
					diff = "1";
				} if(!touch.getAutoChargeEnable().equals(touchConfig.get("auto_charge_enable"))) {
					diff = "1";
				} if(!touch.getAutoChargePrice().equals(touchConfig.get("auto_charge_price"))) {
					auto_charge_price = moneyFormatting(Integer.parseInt(auto_charge_price));
					touch.setAutoChargePrice(auto_charge_price);
					diff = "1";
				} if(!touch.getBonus1().equals(touchConfig.get("bonus1"))) {
					diff = "2";
				} if(!touch.getBonus2().equals(touchConfig.get("bonus2"))) {
					diff = "2";
				} if(!touch.getBonus3().equals(touchConfig.get("bonus3"))) {
					diff = "2";
				} if(!touch.getBonus4().equals(touchConfig.get("bonus4"))) {
					diff = "2";
				} if(!touch.getBonus5().equals(touchConfig.get("bonus5"))) {
					diff = "2";
				} if(!touch.getBonus6().equals(touchConfig.get("bonus6"))) {
					diff = "2";
				} if(!touch.getBonus7().equals(touchConfig.get("bonus7"))) {
					diff = "2";
				} if(!touch.getBonus8().equals(touchConfig.get("bonus8"))) {
					diff = "2";
				} if(!touch.getBonus9().equals(touchConfig.get("bonus9"))) {
					diff = "2";
				} if(!touch.getBonus10().equals(touchConfig.get("bonus10"))) {
					diff = "2";
				} 
				
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String inputDate = sdf.format(new Date());	// 오늘날짜
				touch.setInputDate(inputDate);
//				System.out.println(touch.toString());
				// 설정값 변경
				if(diff.equals("1")) {
					System.out.println("설정값 변경 ");
					mapper.insertChargerConfig(touch);
				} else if(diff.equals("2")) { // 보너스 변경
					System.out.println("보너스 변경 ");
					mapper.updateChargerBonus(touch);
				}*/
				
			} else { // 연결 실패
				touchConfig.replace("state", "0"); // 맵 수정
			}
			
		} // 장비 갯수 리스트 반복 끝
//		System.out.println("반환값은 뭘까 ");
//		Iterator<String> keys = tempConfig.keySet().iterator();
//        while( keys.hasNext() ){
//            String key = keys.next();
//            Object value = (Object) tempConfig.get(key);
//            System.out.println("키 : "+key+", 값 : "+value);
//        }
		return tempConfig;
	}
	
	
	/*
	 * 충전 기록 가져오기 - 30초에 1번씩 
	 1. 충전기 DB접속 
	 2. state = 0 인 레코드 검색하여 DC로 가져옴
	 3. 가져온것은 DC DC에 저장
	 4. 충전기 레코드 state = 1로 교체 
	 */
	@Override
	public String getChargerState() {
		String result = "0";
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "SELECT * FROM card WHERE state = 0";
			
		List<TouchListVO> vo = mapper.getTouchList(glsConfig.getTouch()); // 장비 리스트
		System.out.println(vo.toString());
		
		for (TouchListVO t : vo) { 
			// 연결 성공 시 터치충전기 DB접속 
			if (getConnect(t.getIp())) {
				try {
					Class.forName(driver);
					conn = DriverManager.getConnection(url, uid, upw);
					pstmt = conn.prepareStatement(sql);
					rs = pstmt.executeQuery();
					
					List<TouchChargeStateVO> stateList = new ArrayList<>(); // 충전기록 담을 리스트
					
					while(rs.next()) {
						TouchChargeStateVO state = new TouchChargeStateVO();
						state.setDeviceNo(t.getNo());
						state.setKind(rs.getString("kind"));
						state.setCardNum(rs.getString("card_num"));
						state.setChargeType("0");
						state.setSalesType("0");
						state.setCreditNo("0");
						state.setCurrentCreditMoney("0");
						state.setExhaustMoney(rs.getString("card_price"));
						state.setTotalMoney(rs.getString("total_mny"));
						state.setCurrentMoney(rs.getString("current_mny"));
						state.setCurrentBonus(rs.getString("current_bonus"));
						state.setCurrentCharge(rs.getString("charge_money"));
						state.setInputDate(rs.getString("datetime"));
						System.out.println("디버그 : " + state.toString());
						stateList.add(state);
					}
					
					System.out.println("전체 리스트 : " + Arrays.asList(stateList));
					for (TouchChargeStateVO s : stateList) {
						s.setExhaustMoney(String.format("%04d", (Integer.parseInt(s.getExhaustMoney()) / 100)));
						s.setCurrentMoney(String.format("%04d", (Integer.parseInt(s.getCurrentMoney()) / 100)));
						s.setCurrentBonus(String.format("%04d", (Integer.parseInt(s.getCurrentBonus()) / 100)));
						s.setCurrentCharge(String.format("%05d", (Integer.parseInt(s.getCurrentCharge()) / 100)));
						s.setTotalMoney(String.format("%05d", (Integer.parseInt(s.getTotalMoney()) / 100)));

						System.out.println("데 이 터 수 집 장 치   값  넣기 : " + s.toString());
						mapper.insertChargerState(s); 			// DC 저장
						
//						ChargerStateVO dataCollect = new ChargerStateVO(); // 데이터 수집장치 충전기록 모델 객체 생성
//						dataCollect.setDevice_no(s.getDeviceNo());
//						dataCollect.setKind(s.getKind());
//						dataCollect.setExhaust_money(String.format("%04d", (Integer.parseInt(s.getExhaustMoney()) / 100)));
//						dataCollect.setCurrent_money(String.format("%04d", (Integer.parseInt(s.getCurrentMoney()) / 100)));
//						dataCollect.setCurrent_bonus(String.format("%04d", (Integer.parseInt(s.getCurrentBonus()) / 100)));
//						dataCollect.setCurrent_charge(String.format("%05d", (Integer.parseInt(s.getCurrentCharge()) / 100)));
//						dataCollect.setTotal_money(String.format("%05d", (Integer.parseInt(s.getTotalMoney()) / 100)));
//						dataCollect.setCard_num(s.getCardNum());
//						
//						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//						dataCollect.setInput_date(sdf.format(new Date()));
//						System.out.println("데 이 터 수 집 장 치   값  넣기 : " + dataCollect.toString());
//						deviceMapper.insertChargerState(dataCollect); 		// DC 저장
					}
					String updateSql = "UPDATE card SET state = 1 WHERE state = 0";
					pstmt.close();
					
					pstmt = conn.prepareStatement(updateSql);
					pstmt.executeUpdate();
					
					result = "1";
					
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try { 
						rs.close(); 
						pstmt.close();
						conn.close();
					} catch(Exception e) {}
				}
			} // 커넥션 끝 
		} // 장비 리스트 끝
		return result;
	}
	
	// 스레드 중지
	public void stopChargeState() {
		STATE_TOUCH_THREAD = false;
	}

	/*
	 * 토탕 충전기록 가져오기 - 600초에 1번씩 
	 1. 충전기 DB접속 
	 2. state = 0 인 레코드 검색하여 DC로 가져옴
	 3. 가져온것은 DC DC에 저장
	 4. 충전기 레코드 state = 1로 교체 
	 */
	@Override
	public void getChargerTotal() {
		
	}
	
	@Override
	public boolean getConnect(String ip) {
//		https://madnix.tistory.com/entry/Java%EC%97%90%EC%84%9C-Linux-Shell-%EB%AA%85%EB%A0%B9%EC%96%B4-%EC%8B%A4%ED%96%89%ED%95%98%EA%B8%B0
//		// 리눅스 쉘 명령 실행 
//		String command = "timeout 1 ping -c 1 " + ip;
//		
//		try {
//			Runtime runtime = Runtime.getRuntime(); 
//			Process process = runtime.exec(command);
//	        InputStream is = process.getInputStream();
//	        InputStreamReader isr = new InputStreamReader(is);
//	        BufferedReader br = new BufferedReader(isr);
//	        
//	        String line;
//	        while((line = br.readLine()) != null) {
//	        	 System.out.println(line);
//	        }
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		
		boolean isAlive = false;
		
		try {
			InetAddress pingCheck = InetAddress.getByName(ip);
			isAlive = pingCheck.isReachable(1000);
			System.out.println("연결 상태 : " + isAlive);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return isAlive; 
	}
	
	// 1000, 10000 -> 010, 100 
	@Override
	public String moneyFormatting(int money) {
		String strMoney = "";
		
		if (money==0) {
			strMoney = "0";
		} else if (money > 0 && money < 10000) {
			strMoney = String.format("0%d", (money / 100));
		} else {
			strMoney = String.format("%d", (money / 100));
		}
		return strMoney;
	}
	
	// 1000, 10000 -> 0010, 0100
	@Override
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
//			System.out.println(card);
		}
		return strMoney;
	}
	
}

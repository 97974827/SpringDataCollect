package kr.gls.myapp.pos.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.gls.myapp.common.GlsConfigVO;
import kr.gls.myapp.device.model.SelfStateVO;
import kr.gls.myapp.pos.model.BlackCardVO;
import kr.gls.myapp.pos.model.CardVO;
import kr.gls.myapp.pos.model.MemberCardVO;
import kr.gls.myapp.pos.model.SalesListViewVO;
import kr.gls.myapp.pos.model.SetChargeVO;
import kr.gls.myapp.pos.model.TodayChargerStateVO;
import kr.gls.myapp.pos.repository.IPosMapper;

@Service
public class PosCardService implements IPosCardService {
	
	@Autowired
	private IPosMapper posCardMapper; // 카드관련 DB mapper
	
	@Autowired
	private IPosMapper posSalesMapper; // 매출관련 DB mapper
	
	@Autowired
	private IPosMapper posConfigMapper; // 설정관련 DB mapper
	
	@Autowired
	private GlsConfigVO glsConfig; // 디폴트 설정 관련 
	
	private Logger logger = LoggerFactory.getLogger(getClass()); 
	
	// 카드 등록 하기
	@Override
	public String setCard(String card_num) {
		String result = "0";
		posCardMapper.setCard(card_num, currentClock());
		result = "1";
		return result;
	}
	
	/* 카드 읽기
	 포스로부터 카드번호를 인자로 넘겨받아
	  해당 카드의 소지자, 마지막 사용일자, 카드 검증 값(사용/ 사용불가)를 반환
	 * 참고 : 카드 검증은 gl_card 테이블에 등록되지 않은 카드이거나 정지카드에 
	                 등록된 경우 사용 불가 카드로 처리한다.*/
	@Override
	public List<Map<String, Object>> readCard(String card_num) {
		List<Map<String, Object>> result = new ArrayList<>();
		Map<String, Object> resultMap = new LinkedHashMap<>();
		resultMap.put("name", "0");
		resultMap.put("verification", "0");
		resultMap.put("last_date", "0");
		
		String name = posCardMapper.selectCardName(card_num); // DB에서 카드번호로 이름 가져옴
		if (name == null) {
			resultMap.replace("name", "");	
		} else {
			resultMap.replace("name", name);
		}
		
		String dbCardNum = posCardMapper.selectCardNum(card_num);
		String res = "";
		if (dbCardNum == null) { // 카드가 존재하지 않을떄 
			res = "0";
			resultMap.replace("verification", "0");
		} else {
			res = "1";
		}
		
		Integer auth = posCardMapper.countVerification(card_num); // 사용검증 - 등록카드 검사 
		if (auth == 0 && res.equals("1")) { // 정지카드가 아니고 카드 리스트에 없으면 사용가능 처리
			resultMap.replace("verification", "1"); // 사용 가능
		} else {
			resultMap.replace("verification", "0"); // 사용 불가
		}
		
		String date = posCardMapper.selectLastDate(card_num);
		if (date == null) {
			resultMap.replace("last_date", "-");
		} else {
			resultMap.replace("last_date", date);
		}
		
		result.add(resultMap);
		return result;
	}
	
	/*
	 카드 이력 조회
         전체 카드의 카드번호, 발급일자, 누적충전금액, 현재 잔액, 최근사용일자, 회원코드를
          반환함. 필드에 값이 존재하지 않을 경우 '0'으로 처리
	 */
	@Override
	public List<Map<String, Object>> getCardHistory() {
		List<Map<String, Object>> result = new ArrayList<>();
		List<CardVO> cardList = posCardMapper.getCardHistory(); // 카드이력 불러오기	
		for(CardVO card : cardList) {
			Map<String, Object> resultMap = new LinkedHashMap<>();
			resultMap.put("card_num", card.getCard_num());
			resultMap.put("input_date", card.getInput_date());
			resultMap.put("mb_no", card.getMb_no());
			result.add(resultMap);
		}
		return result;
	}
	
	/*
	 * 카드 상세 조회
	    포스로부터 카드번호를 넘겨 받아 해당 카드의 이력을 반환한다.
	    사용일자 / 장비주소 / 장비명 / 충전금액 / 보너스 금액 / 사용금액 / 카드잔액
	    세차장비의 경우 충전금액과 보너스는 '0'으로 처리되며
	    충전장비의 경우 사용금액은 '0'으로 처리됨 
	 */
	@Override
	public List<Map<String, Object>> getCardHistoryDetail(String card_num) {
		List<Map<String, Object>> result = new ArrayList<>();
		List<SalesListViewVO> cardDetailList = posCardMapper.selectCardHistoryDeTail(card_num); // 카드이력 불러오기
		
		/* SalesListViewVO 
		 * [no=219, device_name=리더기, device_type=9, device_addr=01, 
		 * card_num=null, time=null, cash=0.0, card=4000.0, remain_card=0.0, 
		 * master_card=null, start_time=null, end_time=2020-01-14 16:06:01, current_money=null, credit_money=null]
		 */
		for(SalesListViewVO card : cardDetailList) {
			Map<String, Object> resultMap = new LinkedHashMap<>();
			resultMap.put("time", "0");
			resultMap.put("device_addr", "0");
			resultMap.put("device_name", "0");
			resultMap.put("charge", "0");
			resultMap.put("bonus", "0");
			resultMap.put("use", 0);
			resultMap.put("remain_card", 0);
			resultMap.put("enable_type", "0");
			
			String endTime = card.getEnd_time();
			String deviceAddr = card.getDevice_addr();
			String deviceName = card.getDevice_name();
			String charge = "0";
			String bonus = "0";
			Integer use = 0;
			Integer remainCard = Integer.parseInt(fmt((long) stringToDouble(card.getRemain_card())));
			String enableType = " - ";
			
			// 충전 장비 일떄
			if (card.getDevice_type().equals("3") || card.getDevice_type().equals("6") || card.getDevice_type().equals("7") || card.getDevice_type().equals("8")) {
				charge = fmt((long) stringToDouble(card.getCash()));
				bonus = fmt((long) stringToDouble(card.getCard()));
			} else {
				use = Integer.parseInt(fmt((long) stringToDouble(card.getMaster_card())));
				
				if (Integer.parseInt(card.getDevice_type())==glsConfig.getSelf()) {
					enableType = getSelfDetail(card.getNo(), "self");
				} else if (Integer.parseInt(card.getDevice_type())==glsConfig.getAir()) {
					enableType = "진공";
				} else if (Integer.parseInt(card.getDevice_type())==glsConfig.getReader()) {
					enableType = "매트";
				} else if (Integer.parseInt(card.getDevice_type())==glsConfig.getReader()) {
					enableType = "리더기";
				}
			}
			
			resultMap.replace("time", endTime);
			resultMap.replace("device_addr", deviceAddr);
			resultMap.replace("device_name", deviceName);
			resultMap.replace("charge", charge);
			resultMap.replace("bonus", bonus);
			resultMap.replace("use", use);
			resultMap.replace("remain_card", remainCard);
			resultMap.replace("enable_type", enableType);
			
			result.add(resultMap);
		}
		return result;
	}

	/*
	 * 정지 카드 조회
        정지 카드의 리스트를 반환
    * 참고 : index 번호도 같이 반환함으로써 추후 수정 및 삭제의 키 값으로 사용
	 */
	@Override
	public List<Map<String, Object>> getBlackCard() {
		List<Map<String, Object>> result = new ArrayList<>();
		List<BlackCardVO> blackCardList = posCardMapper.selectBlackCard(); // 카드이력 불러오기
		for(BlackCardVO card : blackCardList) {
			Map<String, Object> resultMap = new LinkedHashMap<String, Object>();
			resultMap.put("no", card.getNo());
			resultMap.put("card_num", card.getCard_num());
			resultMap.put("content", card.getContent());
			resultMap.put("input_date", card.getInput_date());
			result.add(resultMap);
		}
		return result;
	}

	// 정지 카드등록 
	@Override
	public String setBlackCard(String card_num, String content) {
		String result = "0";
		posCardMapper.insertBlackCard(card_num, content, currentClock());
		result = "1";
		return result;
	}
	
	// 정지카드 해제
	@Override
	public String deleteBlackCard(String no) {
		String result = "0";
		posCardMapper.deleteBlackCard(no);
		result = "1";
		return result;
	}
	/*
    * 카드 충전
        카드번호 / 충전금액 / 보너스 / 차감금액 / 사용처 / 매출여부 / 수입여부
        포스로부터 위와 같은 배열을 넘겨 받아 충전을 진행한다.
        현재는 충전 부분만 진행되어 있음.
        추후 가장먼저 차감 부분이 추가 될 예정이며,
         이후 비매출 / 비수입 / 충전 타입(현금/카드) 및 사용처(물품구매) 기능 추가 예정
	 */
	@Override
	public String setCharge(SetChargeVO params) {
		String result = "0";
//		SetChargeVO [charge=10000, minus=0, bonus=2000, remain=12000, use=, sales=0, income=0, card_num=43EA742B]
		
		try {
			String charge = params.getCharge();
			String minus = params.getMinus();
			String bonus = params.getBonus();
			String remain = params.getRemain();
			String sales = params.getSales();
			String cardNum = params.getCard_num();
			
			// 충전금액 
			String currentCharge =
					String.format("%04d", (Integer.parseInt(charge) + Integer.parseInt(bonus) - Integer.parseInt(minus)) / 100);
			// 투입금액 
			String currentMoney = 
				String.format("%04d", (Integer.parseInt(charge) - Integer.parseInt(minus)) / 100);
			
			// 보너스 
			bonus = String.format("%04d", Integer.parseInt(bonus) / 100);
			// 카드 잔액 
			remain = String.format("%04d", Integer.parseInt(remain) / 100);
			
			// 포스 : device_no 추출 
			Integer no = posConfigMapper.selectDeviceNo(glsConfig.getPos(), "01");
			
			// 현금 출전 / 차감 
			if (sales.equals("0")) {
				TodayChargerStateVO charger = new TodayChargerStateVO();
				charger.setDevice_no(no);
				charger.setMoney(currentMoney);
				charger.setCharge(currentCharge);
				charger.setBonus(bonus);
				charger.setRemain_card(remain);
				charger.setCard_num(cardNum);
				charger.setInput_date(currentClock());
				posSalesMapper.insertPosChargerState(charger);	
			} else if(sales.equals("1")){ // 신용카드 충전 / 차감 
				TodayChargerStateVO charger = new TodayChargerStateVO();
				charger.setDevice_no(no);
				charger.setMoney(currentMoney);
				charger.setCharge(currentCharge);
				charger.setBonus(bonus);
				charger.setRemain_card(remain);
				charger.setCard_num(cardNum);
				charger.setInput_date(currentClock());
				posSalesMapper.insertCreditChargerState(charger);	
			}
			
			result = "1";
		} catch (Exception e) {
			e.printStackTrace();
			result = "0";
		}
		
		return result;
	}
	
	/*
	 * 카드 검색
	    포스로부터 카드번호, 회원코드, 최근사용일자 3개의 인자를 받아 검색 후 반환.
	    포스에서 검색창은 한개로 구성되어 있기 때문에 3가지 인자값 중 한개의 인자만
	    값이 들어오며 나머지 두개의 인자는 공백으로 받음.
	    조건문에서 두개의 인자가 공백일 경우 나머지 인자가 값이 있다고 판단하여
	    해당 인자로 검색을 실시함.
	 */
	@Override
	public List<Map<String, Object>> searchCard(String card_num, String mb_no, String end_time) {
		List<Map<String, Object>> result = new ArrayList<>();
		
		try {
			String cardNum = card_num;	// 카드번호 
			String inputDate = "0"; 	// 발급일자
			String mbNo = mb_no;		// 회원코드 
			Integer totalCharge = 0;	// 누적 충전 금액 
			String remainCard = "0";	// 카드 잔액
			String endTime = end_time;  // 최근 사용 일자 
			
			// 카드 번호 검색 
			if (mb_no.equals("") && end_time.equals("")) {
				mbNo = posCardMapper.selectMemberNo(card_num); // 회원 코드검색
				if(mbNo == null) {
					mbNo = "0";
				}
				inputDate = posCardMapper.selectCardDate(card_num); // 발급일자 검색
				if(inputDate == null) {
					inputDate = "0";
				}
				totalCharge = posSalesMapper.selectTotalCharge(card_num); // 누적충전금액 검색
				if(totalCharge == null) {
					totalCharge = 0;
				}
				SalesListViewVO vo = posSalesMapper.selectRemainCardAndEndTime(card_num); // 카드잔액 및 최근 사용일자 검색
				if (vo != null) {
					remainCard = fmt((long) stringToDouble(vo.getRemain_card()));
					endTime = vo.getEnd_time();
				}
				
				Map<String, Object> resultMap = new LinkedHashMap<String, Object>();
				resultMap.put("card_num", cardNum);
				resultMap.put("input_date", inputDate);
				resultMap.put("mb_no", mbNo);
				resultMap.put("total_charge", totalCharge);
				resultMap.put("remain_card", remainCard);
				resultMap.put("end_time", endTime);
				result.add(resultMap);
				
			} else if (card_num.equals("") && end_time.equals("")) { // 회원코드 검색 
				MemberCardVO memberCard = posCardMapper.selectCardNumAndEndTime(mb_no);
				mbNo = mb_no;
				
				if (memberCard != null) {
					cardNum = memberCard.getNum();
					inputDate = memberCard.getInput_date();
				}
				totalCharge = posSalesMapper.selectTotalCharge(card_num); // 누적충전금액 검색
				if (totalCharge == null) {
					totalCharge = 0;
				}
				SalesListViewVO vo = posSalesMapper.selectRemainCardAndEndTime(card_num); // 카드잔액 및 최근 사용일자 검색
				if (vo != null) {
					remainCard = fmt((long) stringToDouble(vo.getRemain_card()));
					endTime = vo.getEnd_time();
				}
				
				Map<String, Object> resultMap = new LinkedHashMap<String, Object>();
				resultMap.put("card_num", cardNum);
				resultMap.put("input_date", inputDate);
				resultMap.put("mb_no", mbNo);
				resultMap.put("total_charge", totalCharge);
				resultMap.put("remain_card", remainCard);
				resultMap.put("end_time", endTime);
				result.add(resultMap);
				
			} else if (card_num.equals("") && mb_no.equals("")) { // 최근 사용일자 검색
				endTime = end_time;
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				cardNum = posSalesMapper.selectCardNumGroup(sdf.format(new Date()));
				if(cardNum == null) {
					cardNum = "-";
				}
				mbNo = posCardMapper.selectMemberNo(card_num); // 회원 코드검색
				if(mbNo == null) {
					mbNo = "0";
				}
				inputDate = posCardMapper.selectCardDate(card_num); // 발급일자 검색
				if(inputDate == null) {
					inputDate = "0";
				}
				totalCharge = posSalesMapper.selectTotalCharge(card_num); // 누적충전금액 검색
				if(totalCharge == null) {
					totalCharge = 0;
				}
				SalesListViewVO vo = posSalesMapper.selectRemainCardAndEndTime(card_num); // 카드잔액 및 최근 사용일자 검색
				if (vo != null) {
					remainCard = fmt((long) stringToDouble(vo.getRemain_card()));
				}
				
				Map<String, Object> resultMap = new LinkedHashMap<String, Object>();
				resultMap.put("card_num", cardNum);
				resultMap.put("input_date", inputDate);
				resultMap.put("mb_no", mbNo);
				resultMap.put("total_charge", totalCharge);
				resultMap.put("remain_card", remainCard);
				resultMap.put("end_time", endTime);
				result.add(resultMap);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	/*
	 * 카드 이력 초기화
	 * 포스로부터 카드번호를 넘겨받아 해당 카드의 사용 내역을 삭제한다.
          전달받은 카드번호가 '0'일 경우 전체 카드 내역을 삭제한다.
          셀프 / 매트 / 진공 / 리더(매트) /  Garage / 충전 테이블에서 내역을 삭제
	 */
	@Override
	public String resetCardHistory(String card_num) {
		String result = "0";
		try {
			if (card_num != "0") {
				posCardMapper.deleteSelfState(card_num);
				posCardMapper.deleteAirState(card_num);
				posCardMapper.deleteMateState(card_num);
				posCardMapper.deleteReaderState(card_num);
				posCardMapper.deleteChargerState(card_num);
			} else if (card_num == "0") {
				posCardMapper.deleteNotCardSelfState(card_num);
				posCardMapper.deleteNotCardAirState(card_num);
				posCardMapper.deleteNotCardMateState(card_num);
				posCardMapper.deleteNotCardReaderState(card_num);
				posCardMapper.deleteNotCardChargerState(card_num);
			}
			result = "1";
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	// 장비 전체이력 초기화
	@Override
	public String resetDeviceHistory() {
		String result = "0";
		try {
			posCardMapper.deleteSelf();
			posCardMapper.deleteAir();
			posCardMapper.deleteMate();
			posCardMapper.deleteReader();
			posCardMapper.deleteCharger();
			result = "1";
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
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
//				GarageStateVO self = posSalesMapper.getGarageDetail(no, type);
		}
		return useDetail;
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
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(new Date());
	}
}

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
import org.springframework.stereotype.Service;

import kr.gls.myapp.common.GlsConfigVO;
import kr.gls.myapp.pos.model.MemberBonusVO;
import kr.gls.myapp.pos.model.MemberCardVO;
import kr.gls.myapp.pos.model.MemberVO;
import kr.gls.myapp.pos.model.SalesListViewVO;
import kr.gls.myapp.pos.repository.IPosMapper;

@Service
public class PosMemberService implements IPosMemberService {
	@Autowired
	private IPosMapper posCardMapper; // 카드관련 DB mapper
	
	@Autowired
	private IPosMapper posSalesMapper; // 매출관련 DB mapper
	
	@Autowired
	private IPosMapper posConfigMapper; // 설정관련 DB mapper
	
	@Autowired
	private IPosMapper posMemberMapper; // 회원 관련 DB mapper
	
	@Autowired
	private GlsConfigVO glsConfig; // 디폴트 설정 관련
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	// 회원 목록 조회 
	@Override
	public List<Map<String, Object>> getMemberList() {
		List<Map<String, Object>> result = new ArrayList<>();
		List<MemberVO> memberList = posMemberMapper.selectMemberList();
		
		for (MemberVO member : memberList) {
			Map<String, Object> map = new LinkedHashMap<String, Object>();
			map.put("mb_no", member.getMb_no());
			map.put("name", member.getName());
			map.put("mobile", member.getMobile());
			map.put("car_num", member.getCar_num());
			map.put("card_num", member.getCard_num());
			result.add(map);
		}
		
		return result;
	}
	
	// 회원 레벨 조회
	@Override
	public List<Map<String, Object>> getMemberLevel() {
		List<Map<String, Object>> result = new ArrayList<>();
		List<MemberVO> memberLevel = posMemberMapper.selectMemberLevel();
		
		for (MemberVO member : memberLevel) {
			Map<String, Object> map = new LinkedHashMap<String, Object>();
			map.put("level", member.getLevel());
			map.put("level_name", member.getLevel_name());
			result.add(map);
		}
		
		return result;
	}

	/* 
	 * 회원 상세 조회 (non-Javadoc)
	  gl_member_card 를 검색하여 카드 단일 소지 / 다중 소지 / 미소지에 따라 
           작업을 실시하며 다중 카드 소지자의 경우 A카드^B카드^C카드 "^"를 구분 플래그로 
           사용하여 카드 값을 반환하고, 미소지자의 경우 공백을 반환
	 */
	@Override
	public Map<String, Object> getMemberDetail(String mb_no) {
		Map<String, Object> result = new LinkedHashMap<String, Object>();
		
		// 카드 갯수 추출
		Integer cardCount = posMemberMapper.countCardMember(mb_no);
	
		// 다중 카드 사용자 경우 
		if (cardCount > 1) {
			MemberVO member = posMemberMapper.selectMemberInfo(mb_no);
			List<String> cardList = posMemberMapper.selectMemberCard(mb_no);
			
			String cardNum = "";
			for(int i=0; i<cardList.size(); i++) {
				cardNum += cardList.get(i);
				if(i != cardList.size()-1) {
					cardNum += "^";
				}
			}
			
			result.put("mb_no", member.getMb_no());
			result.put("level", member.getLevel());
			result.put("name", member.getName());
			result.put("birth", member.getBirth());
			result.put("mobile", member.getMobile());
			result.put("car_num", member.getCar_num());
			result.put("addr", member.getAddr());
			result.put("group", member.getGroup());
			result.put("input_date", member.getInput_date());
			result.put("card_num", cardNum);
			result.put("vip_set", member.getVip_set());
			result.put("level_name", member.getLevel_name());
					
		} else if(cardCount == 0) { // 카드 미소지자
			MemberVO member = posMemberMapper.selectMemberInfo(mb_no);
			
			result.put("mb_no", member.getMb_no());
			result.put("level", member.getLevel());
			result.put("name", member.getName());
			result.put("birth", member.getBirth());
			result.put("mobile", member.getMobile());
			result.put("car_num", member.getCar_num());
			result.put("addr", member.getAddr());
			result.put("group", member.getGroup());
			result.put("input_date", member.getInput_date());
			result.put("card_num", "");
			result.put("vip_set", member.getVip_set());
			result.put("level_name", member.getLevel_name());
		} else { // 단일 카드 사용자
			MemberVO member = posMemberMapper.selectOneCardUser(mb_no);
			
			result.put("mb_no", member.getMb_no());
			result.put("level", member.getLevel());
			result.put("name", member.getName());
			result.put("birth", member.getBirth());
			result.put("mobile", member.getMobile());
			result.put("car_num", member.getCar_num());
			result.put("addr", member.getAddr());
			result.put("group", member.getGroup());
			result.put("input_date", member.getInput_date());
			result.put("card_num", member.getCard_num());
			result.put("vip_set", member.getVip_set());
			result.put("level_name", member.getLevel_name());
		}
		
		return result;
	}

	/*
	 * 회원 등록 및 수정
	    회원 코드가 '0'이면 회원 등록, 회원 코드가 존재하면 회원 수정으로 간주한다.
	    등록 및 수정 모두 단일 카드 소지 / 다중 카드 소지 / 카드 미소지를 구분하여
	    작업하며 회원 수정에서 다중 카드를 소지한 경우 기존의 소지한 카드를 일괄 삭제 후
	    재 등록함.
	    ( 'A카드', 'B카드'에서 'B카드', 'C카드', 'D카드'와 같이 수정할 경우 'B카드'에 
	      대한 원활한 처리를 위한 방법임.)
	 */
	@Override
	public String setMember(MemberVO params) {
		String result = "0";
		try {
			logger.info("{}", params.toString());
			/* MemberVO 
			 * [mb_no=null, no=2, name=이경진, mobile=01054113588, 
			 * car_num=0227, card_num=15FED842^16098CA2, group=0, 
			 * birth=1990-01-01, addr=대구, vip_set=0, input_date=null, level=1, level_name=null]
			 */
			String mbNo = params.getNo();
			Integer level = params.getLevel();
			String name = params.getName();
			String birth = params.getBirth();
			String mobile = params.getMobile();
			String carNum = params.getCar_num();
			String addr = params.getAddr();
			String group = params.getGroup();
			String vipSet = params.getVip_set();
			String cardNum = params.getCard_num();	
			Integer multiCard = 0; 						 // 다중 카드 수량 
			String[] multiCardData = cardNum.split("^"); // 다중 카드 정보 

			for (int i=0; i<cardNum.length(); i++) {
				if(cardNum.charAt(i) == '^') {
					multiCard++;
				}
			}
			
			// 회원 등록 
			if (mbNo.equals("0")) {
				params.setInput_date(currentClock());
				posMemberMapper.insertMember(params);
				
				// 카드 소지자 경우 
				if (cardNum != null) {
					String no = posMemberMapper.selectMbNo(params); // 회원 번호 추출 
					
					MemberCardVO card = new MemberCardVO();
					card.setMb_no(no);
					card.setInput_date(currentClock());
					
					// 단일 카드등록 
					if (multiCard == 0) {
						card.setNum(cardNum);
						posMemberMapper.insertMemberCard(card);
						
					} else { // 다중 카드등록  
						String[] cardData = cardNum.split("^");
						
						for(String s : cardData) {
							card.setNum(s);
						}
						posMemberMapper.insertMemberCard(card);
					}
				}
				
			} else { // 회원 수정 
				
//				card.setNum(cardNum);
//				card.setMb_no(mbNo);
//				card.setInput_date(currentClock());
//				posMemberMapper.updateMemberCard(card, mbNo);
				params.setInput_date(currentClock());
				posMemberMapper.updateMember(params, mbNo);
				
				MemberCardVO card = new MemberCardVO();
				if (cardNum == null) { // 등록된 카드를 지우는 경우 
					posMemberMapper.deleteCard(mbNo);
				} else { // 카드 소지자 경우
					Integer count = posMemberMapper.countCard(mbNo); // 회원 카드 갯수 추출 
					
					if (count != 0) { 
						// 회원 단일카드 정보 업데이트 
						if (multiCard == 0) {  
							card.setNum(cardNum);
							card.setMb_no(mbNo);
							card.setInput_date(currentClock());
							posMemberMapper.updateMemberCard(card, mbNo);
						} else { 
							// 회원 다중 카드 정보 업데이트 
							// 등록된 카드 삭제 
							String num = posMemberMapper.selectMemberCardNum(mbNo);
							posMemberMapper.deleteCard(num);
							
							MemberCardVO newCard = new MemberCardVO();
							card.setNum(num);
							card.setMb_no(mbNo);
							card.setInput_date(currentClock());
							posMemberMapper.insertMemberCard(newCard);							
						}
					} else {
						// 회원 카드 신규 등록 
								
						if (multiCard == 0) {  
							posMemberMapper.insertMemberCard(card);
						} else {
							posMemberMapper.insertMemberCard(card);
						}
					}
				}
			}
			result = "1";
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	// 회원 삭제 
	@Override
	public String deleteMember(String no) {
		String result = "0";
		try {
			posMemberMapper.deleteMember(no); // 회원 정보 삭제
			posMemberMapper.deleteCard(no); // 회원 카드 정보 삭제 
			result = "1";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/*
	 *  회원 이력 조회(non-Javadoc)
	 * @see kr.gls.myapp.pos.service.IPosMemberService#getMemberHistory()
	gl_member 테이블에서 기본 회원 정보를 저장한 이후
    gl_member_card 테이블에서 회원이 카드를 소지하였는지 검사하여 카드가 있을 경우
    gl_charger_state 에서 회원의 카드로 누적 충전금액을 추출하여 반환
	 */
	@Override
	public List<Map<String, Object>> getMemberHistory() {
		List<Map<String, Object>> result = new ArrayList<>();
		List<MemberVO> memberList = posMemberMapper.selectMemberList(); 
		
		for (MemberVO member : memberList) {
			Integer totalCharge = posMemberMapper.selectTotalCharge(member.getCard_num());
			if(totalCharge == null) {
				totalCharge = 0;
			}
			
			Map<String, Object> map = new LinkedHashMap<String, Object>();
			map.put("mb_no", member.getMb_no());
			map.put("name", member.getName());
			map.put("mobile", member.getMobile());
			map.put("car_num", member.getCar_num());
			map.put("card_num", member.getCard_num());
			map.put("input_date", member.getInput_date());
			map.put("total_charge", totalCharge);
			result.add(map);
		}
		
		return result;
	}
	
	/*
	 * 회원 상세 이력
	    포스로부터 회원코드(mb_no)를 받아 회원 카드를 검색하고 해당 카드의 상세 이력을 
	    조회하여 반환함.
	    세차장비의 경우 충전금액과 보너스는 '0'으로 처리되며
	    충전장비의 경우 사용금액은 '0'으로 처리됨
	 */
	@Override
	public List<Map<String, Object>> getMemberHistoryDetail(String mb_no) {
		List<Map<String, Object>> result = new ArrayList<>();
		
		String cardNum = posMemberMapper.selectMemberCardNum(mb_no); // 카드 정보  
		List<SalesListViewVO> memberList = posSalesMapper.selectMemberDetail(cardNum); // 
		
		
		for (SalesListViewVO member : memberList) {
			Map<String, Object> map = new LinkedHashMap<String, Object>();
			String cash = "0";
			Integer card = 0;
			String charge = "0";
			String bonus = "0";
			Integer remainCard = 0;
			
			remainCard = Integer.parseInt(fmt((long) stringToDouble(member.getRemain_card())));
			
			// 충전 장비 
			if (Integer.parseInt(member.getDevice_type())==glsConfig.getCharger() || Integer.parseInt(member.getDevice_type())==glsConfig.getTouch() 
					|| Integer.parseInt(member.getDevice_type())==glsConfig.getKiosk() || Integer.parseInt(member.getDevice_type())==glsConfig.getPos() ) {
				
				charge = fmt((long) stringToDouble(member.getCash())); 
				bonus = fmt((long) stringToDouble(member.getCard()));
	
				map.put("card", cardNum);
				map.put("time", member.getEnd_time());
				map.put("device_addr", member.getDevice_addr());
				map.put("device_name", member.getDevice_name());
				map.put("charge", charge);
				map.put("bonus", bonus);
				map.put("use", cash);
				map.put("remain_card", remainCard);
				
			} else { // 세차장비 
				cash = fmt((long) stringToDouble(member.getMaster_card())); 
				
				map.put("card", cardNum);
				map.put("time", member.getEnd_time());
				map.put("device_addr", member.getDevice_addr());
				map.put("device_name", member.getDevice_name());
				map.put("charge", charge);
				map.put("bonus", bonus);
				map.put("use", cash);
				map.put("remain_card", remainCard);
			}
			
			
			result.add(map);
		}
		
		return result;
	}
	
	/*
	 * 회원 검색
	    포스로부터 회원명, 연락처, 카드번호 3개의 인자를 받아 검색 후 반환.
	    포스에서 검색창은 한개로 구성되어 있기 때문에 3가지 인자값 중 한개의 인자만
	    값이 들어오며 나머지 두개의 인자는 공백으로 받음.
	    조건문에서 두개의 인자가 공백일 경우 나머지 인자가 값이 있다고 판단하여
	    해당 인자로 검색을 실시함.
	 */
	@Override
	public List<Map<String, Object>> searchMember(String name, String mobile, String car_num, String card_num) {
		List<Map<String, Object>> result = new ArrayList<>();
		
		try {
			Map<String, Object> map = new LinkedHashMap<String, Object>();
			
			Integer mbNo = 0;
			String inputDate = "";
			Integer totalCharge = 0;
			
			// name 검색
			if (mobile.equals("") && car_num.equals("") && card_num.equals("")) {
				MemberVO member = posMemberMapper.selectToName(name);
				
				mbNo = member.getMb_no();
				mobile = member.getMobile();
				car_num = member.getCar_num();
				card_num = member.getCard_num();
				inputDate = member.getInput_date();
				
			} else if (name.equals("") && car_num.equals("") && card_num.equals("")) { // mobile 검색 
				MemberVO member = posMemberMapper.selectToMobile(mobile);
				
				mbNo = member.getMb_no();
				name = member.getName();
				car_num = member.getCar_num();
				card_num = member.getCard_num();
				inputDate = member.getInput_date();
				
			} else if (name.equals("") && mobile.equals("") && card_num.equals("")) { // car 검색 
				MemberVO member = posMemberMapper.selectToCar(car_num);
				
				mbNo = member.getMb_no();
				name = member.getName();
				mobile = member.getMobile();
				card_num = member.getCard_num();
				inputDate = member.getInput_date();
				
			} else if (name.equals("") && mobile.equals("") && car_num.equals("")) { // card 검색 
				MemberVO member = posMemberMapper.selectToCard(card_num);
				
				mbNo = member.getMb_no();
				name = member.getName();
				mobile = member.getMobile();
				car_num = member.getCar_num();
				inputDate = member.getInput_date();
			} 
			
			
			totalCharge = posMemberMapper.selectTotalCharge(card_num);
			
			if(totalCharge == null) {
				totalCharge = 0;
			}
			
			map.put("mb_no", mbNo);
			map.put("name", name);
			map.put("mobile", mobile);
			map.put("car_num", car_num);
			map.put("card_num", card_num);
			map.put("input_date", inputDate);
			map.put("total_charge", totalCharge);
			result.add(map);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	// 회원 보너스 설정 불러오기
	@Override
	public List<Map<String, Object>> getMemberBonus() {
		List<Map<String, Object>> result = new ArrayList<>();
		List<MemberBonusVO> bonusList = posMemberMapper.selectMemberBonus(); 
		
		for (MemberBonusVO bonus : bonusList) {
			Map<String, Object> map = new LinkedHashMap<String, Object>();
			map.put("bonus1", bonus.getBonus1());
			map.put("bonus2", bonus.getBonus2());
			map.put("bonus3", bonus.getBonus3());
			map.put("bonus4", bonus.getBonus4());
			map.put("bonus5", bonus.getBonus5());
			map.put("bonus6", bonus.getBonus6());
			map.put("bonus7", bonus.getBonus7());
			map.put("bonus8", bonus.getBonus8());
			map.put("bonus9", bonus.getBonus9());
			map.put("bonus10", bonus.getBonus10());
			map.put("period", bonus.getPeriod());
			result.add(map);
		}
		
		return result;
	}
	
	// 회원 보너스 설정 : 미처리상태
	@Override
	public String setMemberBonus(Map<String, Object> params) {
		String result = "0";
		
		try {
			logger.info("{}", params.get("lv1_name"));
			logger.info("{}", params.get("lv1_money"));
			logger.info("{}", params.get("lv1_bonus1"));
			logger.info("{}", params.get("lv1_bonus2"));
			logger.info("{}", params.get("lv1_bonus3"));
			logger.info("{}", params.get("lv1_bonus4"));
			logger.info("{}", params.get("lv1_bonus5"));
			logger.info("{}", params.get("lv1_bonus6"));
			logger.info("{}", params.get("lv1_bonus7"));
			logger.info("{}", params.get("lv1_bonus8"));
			logger.info("{}", params.get("lv1_bonus9"));
			logger.info("{}", params.get("lv1_bonus10"));
			
			logger.info("{}", params.get("lv2_name"));
			logger.info("{}", params.get("lv2_money"));
			logger.info("{}", params.get("lv2_bonus1"));
			logger.info("{}", params.get("lv2_bonus2"));
			logger.info("{}", params.get("lv2_bonus3"));
			logger.info("{}", params.get("lv2_bonus4"));
			logger.info("{}", params.get("lv2_bonus5"));
			logger.info("{}", params.get("lv2_bonus6"));
			logger.info("{}", params.get("lv2_bonus7"));
			logger.info("{}", params.get("lv2_bonus8"));
			logger.info("{}", params.get("lv2_bonus9"));
			logger.info("{}", params.get("lv2_bonus10"));
			
			logger.info("{}", params.get("lv3_name"));
			logger.info("{}", params.get("lv3_money"));
			logger.info("{}", params.get("lv3_bonus1"));
			logger.info("{}", params.get("lv3_bonus2"));
			logger.info("{}", params.get("lv3_bonus3"));
			logger.info("{}", params.get("lv3_bonus4"));
			logger.info("{}", params.get("lv3_bonus5"));
			logger.info("{}", params.get("lv3_bonus6"));
			logger.info("{}", params.get("lv3_bonus7"));
			logger.info("{}", params.get("lv3_bonus8"));
			logger.info("{}", params.get("lv3_bonus9"));
			logger.info("{}", params.get("lv3_bonus10"));
			
			logger.info("{}", params.get("period"));
			
		} catch (Exception e) {
			result = "0";
			e.printStackTrace();
		}
		return result;
	}
	
	// 현재 시간 추출 
	public String currentClock() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(new Date());
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
	
}

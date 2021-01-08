package kr.gls.myapp.touch.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import kr.gls.myapp.common.ResponseDataSet;
import kr.gls.myapp.touch.model.TouchConfigSetVO;
import kr.gls.myapp.touch.service.ITouchChargerService;

@RestController
//@RequestMapping("/")
public class TouchChargerController {
	
	@Autowired
	private ITouchChargerService service; // 서비스 의존성주입
	
	// 터치충전기 설정 셋팅 요청
	@PostMapping(path="/set_touch_config", consumes={ MediaType.APPLICATION_FORM_URLENCODED_VALUE })
	public Map<String, Object> setTouchConfig(TouchConfigSetVO params) {
		System.out.println("/set_touch_config POST 요청  : " + params.toString());
		Map<String, Object> map = new HashMap<>();
		map.put("result", service.setTouchConfig(params));
		return map;
	}
	
	// 터치충전기 설정 불러오기 요청
	@PostMapping("/get_touch_config")	
	public ResponseDataSet getTouchConfig() {		
		System.out.println("/get_touch_config POST 요청  : " );
		List<Map<String, Object>> list = new ArrayList<>();
		list.add(service.getTouchConfig());
	    return new ResponseDataSet(list);
	}
	
}
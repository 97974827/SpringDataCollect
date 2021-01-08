package kr.gls.myapp.common;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude;

// JSON 데이터 응답 모델 클래스
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseDataSet {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private List<Map<String, Object>> result; // 클라이언트에 보낼 데이터 객체 포장
	
	public ResponseDataSet(List<Map<String, Object>> result) {
		logger.info("리턴 클래스 데이터 ");
		
		for(int i=0; i<result.size(); i++) {
			logger.info("{}\n", result.get(i));
		}
		
		this.result = result;
	}

	public List<Map<String, Object>> getResult() {
		return result;
	}

	
}

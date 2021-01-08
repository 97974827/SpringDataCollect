package kr.gls.myapp.common;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// 단일 맵 클래스화 
public class ResultDataSet {
	
	private Map<String, Object> result;

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public ResultDataSet(Map<String, Object> result) {
		this.result = result;
		
		logger.info("{}", result.get("result"));
	}
	
	public void setResult(Map<String, Object> result) {
		this.result = result;
	}
	
	public Map<String, Object> getResult() {
		return result;
	}
	
}

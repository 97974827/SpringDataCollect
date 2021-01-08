package kr.gls.myapp.common;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.PeriodicTrigger;
import org.springframework.stereotype.Component;

import kr.gls.myapp.device.service.IDeviceService;

@Component
public class ProgrammableScheduler {
	
	@Autowired
	IDeviceService service;
	
    private ThreadPoolTaskScheduler scheduler;
    
    public static boolean isThread = false; // 485 스레드 플래그 변수 : default OFF
	
	public boolean getIsThread() {
		return isThread;
	}
    
    public void stopScheduler() { // 스케줄러 중지
    	isThread = false; // OFF
        scheduler.shutdown();
    }
 
    public void startScheduler() { // 스케쥴러 초기화 이후 시작
    	isThread = true; // ON
        scheduler = new ThreadPoolTaskScheduler();
        scheduler.initialize();
        // 스케쥴러가 시작되는 부분 
        scheduler.schedule(getRunnable(), getTrigger());
    }
 
    private Runnable getRunnable(){
        return () -> {
        	// 작업 행위 
        	service.getDeviceState();
        };
    }
 
    private Trigger getTrigger() {
        // 작업 주기 설정 
        return new PeriodicTrigger(5, TimeUnit.SECONDS);
    }
}
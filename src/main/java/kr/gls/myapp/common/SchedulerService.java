//package kr.gls.myapp.common;
//
//import lombok.extern.slf4j.Slf4j;
//
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.TaskScheduler;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Service;
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.ScheduledFuture;
//import java.util.logging.Logger;
//
//@Service
//public class SchedulerService {
//	
//	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(SchedulerService.class);
//
//    private Map<String, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>(); // 스케쥴러 객체 
//    
//    public void setScheduledTasks(Map<String, ScheduledFuture<?>> scheduledTasks) {
//		this.scheduledTasks = scheduledTasks;
//	}
//    
//    public Map<String, ScheduledFuture<?>> getScheduledTasks() {
//		return scheduledTasks;
//	}
//    
//     
//    @Autowired
//    private TaskScheduler taskScheduler;
//
//    public void register() {
//        ScheduledFuture<?> task = taskScheduler.scheduleAtFixedRate(() ->
//        logger.info("hello DolphaGo. 저는 일하고 있어요....={}"),2000);
//
//        scheduledTasks.put("mySchedulerId", task);
//    }
//
//    public void remove() {
//    	logger.info("mySchedulerId"+"를 종료합니다.");
//        scheduledTasks.get("mySchedulerId").cancel(true);
//    }
//    
////    @Scheduled(fixedRateString = "${myscheduler.period}", initialDelay = 2000) 
////    private void scheduleTest() { 
////    	logger.error("fix"); 
////    }
//
//}
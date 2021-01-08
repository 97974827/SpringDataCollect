//package kr.gls.myapp.common;
//
//import java.util.Date;
//import java.util.concurrent.TimeUnit;
//
//import org.springframework.scheduling.Trigger;
//import org.springframework.scheduling.support.PeriodicTrigger;
//import org.springframework.stereotype.Component;
//
//@Component
//public class DynamicSchedule extends DynamicAbstractScheduler {
//    // 실행로직 
//    @Override
//    public void runner() {
//        System.out.println(new Date());
//    }
// 
//    // 실행주기 
//    @Override
//    public Trigger getTrigger() {
//        return new PeriodicTrigger(1, TimeUnit.SECONDS);
//    }
//}
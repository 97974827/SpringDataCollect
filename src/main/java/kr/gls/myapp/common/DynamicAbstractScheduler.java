//package kr.gls.myapp.common;
//
//import org.springframework.scheduling.Trigger;
//import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
//
//public abstract class DynamicAbstractScheduler {
//	
//    private ThreadPoolTaskScheduler scheduler;
// 
//    public void stopScheduler() {
//        scheduler.shutdown();
//    }
// 
//    public void startScheduler() {
//        scheduler = new ThreadPoolTaskScheduler();
//        scheduler.initialize();
//        scheduler.schedule(getRunnable(), getTrigger());
//    }
// 
//    private Runnable getRunnable(){
//        return new Runnable(){
//            @Override
//            public void run() {
//                runner();
//            }
//        };
//    }
// 
//    public abstract void runner();
// 
//    public abstract Trigger getTrigger();
//}
// 

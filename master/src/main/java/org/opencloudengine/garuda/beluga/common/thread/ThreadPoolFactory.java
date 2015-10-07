package org.opencloudengine.garuda.beluga.common.thread;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadPoolFactory {
	public static ThreadPoolExecutor newCachedThreadPool(String poolName, int max){
		return new ThreadPoolExecutor(0, max,
                60L, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>(), new DefaultThreadFactory(poolName, false), new ThreadPoolExecutor.AbortPolicy());
	}
	public static ThreadPoolExecutor newCachedDaemonThreadPool(String poolName, int max){
		return new ThreadPoolExecutor(0, max,
                60L, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>(), new DefaultThreadFactory(poolName, true), new ThreadPoolExecutor.AbortPolicy());
	}
	public static ThreadPoolExecutor newUnlimitedCachedThreadPool(String poolName){
		return new ThreadPoolExecutor(0, Integer.MAX_VALUE,
                60L, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>(), new DefaultThreadFactory(poolName, false));
	}
	public static ThreadPoolExecutor newUnlimitedCachedDaemonThreadPool(String poolName){
		return new ThreadPoolExecutor(0, Integer.MAX_VALUE,
                60L, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>(), new DefaultThreadFactory(poolName, true));
	}
	public static ScheduledThreadPoolExecutor newScheduledThreadPool(String poolName){
		return new ScheduledThreadPoolExecutor(0, new DefaultThreadFactory(poolName, false));
	}
	public static ScheduledExecutorService newScheduledDaemonThreadPool(String poolName){
		return new ScheduledThreadPoolExecutor(0, new DefaultThreadFactory(poolName, true));
	}
	
	
	static class DefaultThreadFactory implements ThreadFactory {
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;
        private boolean isDaemon;
        
        DefaultThreadFactory(String name, boolean isDaemon) {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() :
                                  Thread.currentThread().getThreadGroup();
            namePrefix = "pool-"+name+"-thread-";
            this.isDaemon = isDaemon;
        }

        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r,
                                  namePrefix + threadNumber.getAndIncrement(),
                                  0);
            t.setDaemon(isDaemon);
            if (t.getPriority() != Thread.NORM_PRIORITY)
                t.setPriority(Thread.NORM_PRIORITY);
            return t;
        }
    }
}

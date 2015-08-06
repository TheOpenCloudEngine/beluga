package org.opencloudengine.garuda.service;

import org.opencloudengine.garuda.action.*;
import org.opencloudengine.garuda.common.thread.ThreadPoolFactory;
import org.opencloudengine.garuda.env.Environment;
import org.opencloudengine.garuda.env.Settings;
import org.opencloudengine.garuda.exception.GarudaException;
import org.opencloudengine.garuda.service.common.ServiceManager;

import java.util.Map;
import java.util.concurrent.*;

/**
 * Created by swsong on 2015. 8. 4..
 */
public class ActionService extends AbstractService {

    private Map<ActionRequest, ActionStatus> actionStatusMap;

    private ThreadPoolExecutor executor;
    private BlockingQueue<RunnableAction> queue;
    private ActionConsumer consumer;

    public ActionService(Environment environment, Settings settings, ServiceManager serviceManager) {
        super(environment, settings, serviceManager);
    }

    @Override
    protected boolean doStart() throws GarudaException {
        actionStatusMap = new ConcurrentHashMap<>();
        executor = ThreadPoolFactory.newUnlimitedCachedDaemonThreadPool("actionService.executor");
        queue = new LinkedBlockingQueue<>();
        consumer = new ActionConsumer();
        consumer.start();
        return true;
    }

    @Override
    protected boolean doStop() throws GarudaException {
        consumer.interrupt();
        executor.shutdownNow();
        return true;
    }

    @Override
    protected boolean doClose() throws GarudaException {
        return false;
    }

    //같은작업이 동시에 들어올 수 있으므로 동기화 시킨다.
    public synchronized ActionStatus request(ActionRequest actionId) throws ActionException {

        //1. 존재하는가?
        ActionStatus status = actionStatusMap.get(actionId);

        //1.1 존재한다면 종료되었는가?
        if(status != null) {
            if(status.isDone()) {
                //1.1.1 종료되었다면 다시 할당한다.
            } else {
                //1.1.2 종료되지 않았다면 이미 실행중인 작업이라고 바로 리턴한다.
                throw new ActionException(String.format("Action %s is already running.", actionId));
            }
        } else {
            //2. 존재하지 않는다면 할당한다.
        }

        RunnableAction action = actionId.createAction();
        status = action.getStatus();
        actionStatusMap.put(actionId, status);
        queue.offer(action);
        status.setInQueue();
        return status;
    }

    public ActionStatus getStatus(ActionRequest actionId){
        return actionStatusMap.get(actionId);
    }

    class ActionConsumer extends Thread {

        public ActionConsumer() {
            super("ActionConsumerThread");
        }

        public void run() {
            while (!Thread.interrupted()) {
                RunnableAction action = null;
                try {
                    action = queue.take();
                    executor.execute(action);
                } catch (RejectedExecutionException e) {
                    // executor rejecthandler가 abortpolicy의 경우
                    // RejectedExecutionException을 던지게 되어있다.
                    ActionStatus status = actionStatusMap.get(action.getActionRequest());
                    status.setError("처리허용량을 초과하여 작업이 거부되었습니다.", e);
                } catch (Throwable e) {
                    ActionStatus status = actionStatusMap.get(action.getActionRequest());
                    status.setError(e);
                }
            }

        }
    }
}

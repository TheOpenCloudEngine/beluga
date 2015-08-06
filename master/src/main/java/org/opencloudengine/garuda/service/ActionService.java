package org.opencloudengine.garuda.service;

import org.opencloudengine.garuda.action.ActionException;
import org.opencloudengine.garuda.action.ActionRequest;
import org.opencloudengine.garuda.action.ActionStatus;
import org.opencloudengine.garuda.action.RunnableAction;
import org.opencloudengine.garuda.common.thread.ThreadPoolFactory;
import org.opencloudengine.garuda.env.Environment;
import org.opencloudengine.garuda.env.Settings;
import org.opencloudengine.garuda.exception.GarudaException;
import org.opencloudengine.garuda.service.common.ServiceManager;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.*;

/**
 * Created by swsong on 2015. 8. 4..
 */
public class ActionService extends AbstractService {

    //TODO 주기적으로 두 map 을 지워준다. 현재는 완료액션까지 계속 쌓이게 된다.시간이 5분이상 지난것을 먼저지우는등의 기법.
    //동일한 request가 여러번 들어오는 것을 막아주는 용도.
    private Map<ActionRequest, ActionStatus> actionRequestStatusMap;
    //id를 통해 상태를 찾을 수 있는 용도.
    private Map<String, ActionStatus> actionIdStatusMap;

    private ThreadPoolExecutor executor;
    private BlockingQueue<RunnableAction> queue;
    private ActionConsumer consumer;
    private Random actionIdGenerator = new Random(System.nanoTime());

    public ActionService(Environment environment, Settings settings, ServiceManager serviceManager) {
        super(environment, settings, serviceManager);
    }

    @Override
    protected boolean doStart() throws GarudaException {
        actionRequestStatusMap = new ConcurrentHashMap<>();
        actionIdStatusMap = new ConcurrentHashMap<>();
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
    public synchronized ActionStatus request(ActionRequest actionRequest) throws ActionException {

        //1. 존재하는가?
        ActionStatus status = actionRequestStatusMap.get(actionRequest);

        //1.1 존재한다면 종료되었는가?
        if(status != null) {
            if(status.checkDone()) {
                //1.1.1 종료되었다면 다시 할당한다.
            } else {
                //1.1.2 종료되지 않았다면 이미 실행중인 작업이라고 바로 리턴한다.
                throw new ActionException(String.format("Action %s is already running.", actionRequest));
            }
        } else {
            //2. 존재하지 않는다면 할당한다.
        }

        ///
        // 중요!!
        // 랜덤한 Action ID를 생성한다.
        //
        final String actionId = createActionId();
        actionRequest.setActionId(actionId);
        RunnableAction action = actionRequest.createAction();
        status = action.getStatus();
        actionRequestStatusMap.put(actionRequest, status);
        actionIdStatusMap.put(actionId, status);
        queue.offer(action);
        status.setInQueue();
        return status;
    }

    // 8자리 숫자를 만들어준다.
    private String createActionId() {
        return String.valueOf(actionIdGenerator.nextInt(90000000) + 10000000);
    }

    public ActionStatus getStatus(String actionId){
        return actionIdStatusMap.get(actionId);
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
                    logger.info("{}", action.getStatus());
                    executor.execute(action);
                } catch (RejectedExecutionException e) {
                    // executor rejecthandler가 abortpolicy의 경우
                    // RejectedExecutionException을 던지게 되어있다.
                    action.getStatus().setError("처리허용량을 초과하여 작업이 거부되었습니다.", e);
                } catch (Throwable e) {
                    action.getStatus().setError(e);
                }
            }

        }
    }
}

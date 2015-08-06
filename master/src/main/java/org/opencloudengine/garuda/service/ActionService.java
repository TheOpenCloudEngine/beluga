package org.opencloudengine.garuda.service;

import org.opencloudengine.garuda.action.*;
import org.opencloudengine.garuda.common.thread.ThreadPoolFactory;
import org.opencloudengine.garuda.env.Environment;
import org.opencloudengine.garuda.env.Settings;
import org.opencloudengine.garuda.exception.GarudaException;
import org.opencloudengine.garuda.service.common.ServiceManager;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by swsong on 2015. 8. 4..
 */
public class ActionService extends AbstractService {

    private Map<ActionId, ActionStatus> actionStatusMap;
//    private Map<ActionId, ActionResult> actionResultMap;

    private ThreadPoolExecutor executor;
    private BlockingQueue<RequestAction> queue;
    private ActionConsumer consumer;

    public ActionService(Environment environment, Settings settings, ServiceManager serviceManager) {
        super(environment, settings, serviceManager);


    }

    @Override
    protected boolean doStart() throws GarudaException {
        actionStatusMap = new ConcurrentHashMap<>();
//        actionResultMap = new ConcurrentHashMap<>();

        executor = ThreadPoolFactory.newUnlimitedCachedDaemonThreadPool("actionService.executor");
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

    private String nextAnctionId() {
        return null;
    }

    public ActionStatus request(ActionId actionId) throws ActionException {

        //검증.

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

        status = new ActionStatus();

        RequestAction action = actionId.createRequestAction();

//        long myJobId = jobIdIncrement.getAndIncrement();
////		logger.debug("### OFFER Job-{} : {}", myJobId, job.getClass().getSimpleName());
//
//        ResultFuture resultFuture = new ResultFuture(myJobId, resultFutureMap);
//        resultFutureMap.put(myJobId, resultFuture);
//        job.setId(myJobId);
        status = offerAction(action);

        return status;
    }


    private ActionStatus offerAction(RequestAction action){
        ActionStatus status = new ActionStatus();
        actionStatusMap.put(action.getActionId(), status);
        queue.offer(action);
        return status;
    }

    public ActionStatus getStatus(ActionId actionId){
        //TODO
        return null;
    }

    class ActionConsumer extends Thread {

        public ActionConsumer() {
            super("ActionConsumerThread");
        }

        public void run() {
            while (!Thread.interrupted()) {
                RequestAction action = null;
                try {
                    action = queue.take();
                    runningJobList.put(job.getId(), job);
                    executor.execute(action);
                } catch (InterruptedException e) {
                    logger.debug(this.getClass().getName() + " is interrupted.");
                } catch (RejectedExecutionException e) {
                    // executor rejecthandler가 abortpolicy의 경우
                    // RejectedExecutionException을 던지게 되어있다.
                    logger.error("처리허용량을 초과하여 작업이 거부되었습니다. max.pool = {}, job={}", executorMaxPoolSize, job);
                    result(job, new ExecutorMaxCapacityExceedException("처리허용량을 초과하여 작업이 거부되었습니다. max.pool =" + executorMaxPoolSize), false);

                } catch (Throwable e) {
                    logger.error("", e);
                }
            }

        }
    }
}

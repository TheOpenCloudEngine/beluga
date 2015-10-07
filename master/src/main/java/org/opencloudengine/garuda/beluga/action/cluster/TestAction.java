package org.opencloudengine.garuda.beluga.action.cluster;

import org.opencloudengine.garuda.beluga.action.ActionRequest;
import org.opencloudengine.garuda.beluga.action.RunnableAction;

/**
 * Created by swsong on 2015. 8. 6..
 */
public class TestAction extends RunnableAction {


    public TestAction(ActionRequest actionRequest) {
        super(actionRequest);
        status.registerStep("step1");
        status.registerStep("step2");
        status.registerStep("step3");
    }

    @Override
    protected void doAction() throws Exception {

        doSomething(10, "init");
        logger.info("TestAction Start!");

        status.walkStep();
        doSomething(5, "step1");

        status.walkStep();
        doSomething(5, "step2");

        status.walkStep();
        doSomething(5, "step3");

        setResult(new Boolean(true));
        logger.info("TestAction Finish!");
        doSomething(5, "finalize");
    }

    private void doSomething(int seconds, String msg) {
        logger.info("Doing {}...", msg);

        try {
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException e) {
        }
    }
}

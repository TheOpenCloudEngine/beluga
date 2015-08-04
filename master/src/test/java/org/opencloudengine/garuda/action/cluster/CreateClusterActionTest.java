package org.opencloudengine.garuda.action.cluster;

import org.junit.Test;
import org.opencloudengine.garuda.action.ActionResult;
import org.opencloudengine.garuda.action.ActionStatus;

/**
 * Created by swsong on 2015. 8. 4..
 */
public class CreateClusterActionTest {

    @Test
    public void create() {
        CreateClusterAction action = new CreateClusterAction();
        ActionStatus status = action.request("", "");
        while(!status.isDone()) {
            System.out.println(String.format("%d / %d" + status.getStep(), status.getTotalStep()));
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Done.");
        ActionResult result = action.getResult();
        System.out.println(result.getResult());
    }
}

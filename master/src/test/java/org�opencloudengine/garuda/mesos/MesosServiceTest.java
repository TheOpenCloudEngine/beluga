package org.opencloudengine.garuda.mesos;

import org.junit.Test;
import org.opencloudengine.garuda.action.BaseActionTest;

/**
 * Created by swsong on 2015. 8. 9..
 */
public class MesosServiceTest extends BaseActionTest {

    @Test
    public void getMarathonLeader(){
        String clusterId = "test-cluster";
        MesosService mesosService = serviceManager.getService(MesosService.class);
        String marathonEndPoint = mesosService.chooseMarathonEndPoint(clusterId);
        System.out.println(marathonEndPoint);
    }
}

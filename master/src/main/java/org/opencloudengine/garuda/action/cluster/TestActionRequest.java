package org.opencloudengine.garuda.action.cluster;

import org.opencloudengine.garuda.action.ActionRequest;
import org.opencloudengine.garuda.action.RunnableAction;

/**
 * Created by swsong on 2015. 8. 6..
 */
public class TestActionRequest extends ActionRequest {

    private String id;

    public TestActionRequest(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public boolean compareUnique(ActionRequest other) {
        return id.equals(((TestActionRequest)other).id);
    }

    @Override
    public RunnableAction createAction() {
        return new TestAction(this);
    }
}

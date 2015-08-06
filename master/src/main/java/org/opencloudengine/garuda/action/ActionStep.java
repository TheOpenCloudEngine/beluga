package org.opencloudengine.garuda.action;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by swsong on 2015. 8. 6..
 */
public class ActionStep {
    private List<String> stepMessages;
    private int currentStep;

    public ActionStep() {
        stepMessages = new ArrayList<>();
    }
    public void walkStep() {
        currentStep++;
    }

    public void setCurrentStep(int currentStep) {
        this.currentStep = currentStep;
    }

    public int getCurrentStep() {
        return currentStep;
    }

    public int getTotalStep() {
        return stepMessages.size();
    }

    public List<String> getStepMessages() {
        return stepMessages;
    }
}

package org.opencloudengine.garuda.beluga.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by swsong on 2015. 8. 6..
 */
public class ActionStep {
    private static Logger logger = LoggerFactory.getLogger(ActionStep.class);

    private List<String> stepMessages;
    private int currentStep;

    public ActionStep() {
        stepMessages = new ArrayList<>();
    }
    public void walkStep() {
        currentStep++;
        logWalkStepMessage();
    }
    private void logWalkStepMessage() {
        if(currentStep <= stepMessages.size()) {
            String message = stepMessages.get(currentStep - 1);
            logger.debug("Walk step [{}] {}", currentStep, message);
        } else {
            logger.debug("Walk step [{}] <No message is available.>", currentStep);
        }
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

    @Override
    public String toString() {
        return String.format("[%s] currentStep[%d] totalStep[%d]", getClass().getSimpleName(), currentStep, getTotalStep());
    }
}

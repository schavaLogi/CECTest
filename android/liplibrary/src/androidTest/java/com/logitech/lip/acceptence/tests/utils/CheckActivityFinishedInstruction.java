package com.logitech.lip.acceptence.tests.utils;

import android.support.test.rule.ActivityTestRule;
import android.util.Log;

public class CheckActivityFinishedInstruction extends Instruction {

    private ActivityTestRule activityTestRule;

    public CheckActivityFinishedInstruction(ActivityTestRule activityTestRule) {
        this.activityTestRule = activityTestRule;
    }

    @Override
    public String getDescription() {
        return "Activity not destroyed";
    }

    @Override
    public boolean checkCondition() {

        Log.e("CheckNoActivity", "Activity finishing =" + activityTestRule.getActivity().isFinishing());
        return (activityTestRule != null &&
                activityTestRule.getActivity() != null &&
                activityTestRule.getActivity().isFinishing());
    }
}

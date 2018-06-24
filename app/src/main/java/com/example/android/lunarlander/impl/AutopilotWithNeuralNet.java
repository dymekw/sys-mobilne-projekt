package com.example.android.lunarlander.impl;

import com.example.android.lunarlander.Autopilot;
import com.example.android.lunarlander.dto.Action;
import com.example.android.lunarlander.dto.InputDTO;
import com.example.android.lunarlander.dto.OutputDTO;
import com.example.android.lunarlander.net.ActionNet;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static com.example.android.lunarlander.dto.Action.*;

public class AutopilotWithNeuralNet implements Autopilot {

    private Random random = new Random();

    private Map<Integer, Action> actionMap = new HashMap<>();
    {
        actionMap.put(LEFT.ordinal(), LEFT);
        actionMap.put(STRAIGHT.ordinal(), STRAIGHT);
        actionMap.put(RIGHT.ordinal(), RIGHT);
        actionMap.put(LEFT_ACCEL.ordinal(), LEFT_ACCEL);
        actionMap.put(STRAIGHT_ACCEL.ordinal(), STRAIGHT_ACCEL);
        actionMap.put(RIGHT_ACCEL.ordinal(), RIGHT_ACCEL);
    }

    private ActionNet actionNet;

    @Override
    public OutputDTO getActions(InputDTO input) {

        int index;
        if(random.nextDouble() <= actionNet.getEpsilon()){
            index = random.nextInt(actionMap.values().size());
        }else{
            float[][] actionsRewards = actionNet.predict(Arrays.asList(input));
            index = getBestAction(actionsRewards[0]);
        }

        OutputDTO outputDTO = new OutputDTO();
        outputDTO.actions.add(actionMap.get(index));
        return outputDTO;
    }

    private int getBestAction(float[] rewards){
        float max = -Float.MIN_VALUE;
        int index = 0;

        for (int i = 0; i < rewards.length; i++) {
            if(max <= rewards[i]){
                max = rewards[i];
                index = i;
            }
        }
        return index;
    }

    @Override
    public int getPeriod() {
        return 50;
    }

    public void setActionNet(ActionNet actionNet) {
        this.actionNet = actionNet;
    }

}

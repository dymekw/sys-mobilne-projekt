package com.example.android.lunarlander.impl;

import com.example.android.lunarlander.Autopilot;
import com.example.android.lunarlander.dto.Action;
import com.example.android.lunarlander.dto.InputDTO;
import com.example.android.lunarlander.dto.OutputDTO;


public class AutopilotImpl2 implements Autopilot {

    private static final int dAngle = 12;
    private static final int dVelocity = 30;

    @Override
    public OutputDTO getActions(InputDTO input) {
        OutputDTO outputDTO = new OutputDTO();

        //ACCEL
        if (input.velocityX < -dVelocity && input.velocityX > dVelocity) {
            outputDTO.actions.add(Action.ACCEL);
        } else if (input.y > 240.0/1280.0) {
            if (input.velocityY < -70) {
                outputDTO.actions.add(Action.ACCEL);
            }
        } else if (input.y > 120.0/1280.0) {
            if (input.velocityY < -40) {
                outputDTO.actions.add(Action.ACCEL);
            }
        } else {
            if (input.velocityY < -10) {
                outputDTO.actions.add(Action.ACCEL);
            }
        }

        //ROTATE
        if (input.velocityX < -dVelocity) {
            if (input.headerAngle < dAngle || input.headerAngle > 180)
                outputDTO.actions.add(Action.RIGHT);
        } else if (input.velocityX > dVelocity) {
            if (input.headerAngle > 360-dAngle || input.headerAngle < 180)
                outputDTO.actions.add(Action.LEFT);
        } else if (input.x < input.goalX+input.goalWidth/3.0) {
            if (input.velocityX < 5)
                if (input.headerAngle < dAngle || input.headerAngle > 180)
                    outputDTO.actions.add(Action.RIGHT);
        } else if (input.x > input.goalX+2*input.goalWidth/3.0) {
            if (input.velocityX > -5)
                if (input.headerAngle > 360-dAngle || input.headerAngle < 180)
                    outputDTO.actions.add(Action.LEFT);
        } else if (input.velocityX > 2) {
            if (input.headerAngle > 360-dAngle || input.headerAngle < 180)
                outputDTO.actions.add(Action.LEFT);
        } else if (input.velocityX < -2) {
            if (input.headerAngle < dAngle || input.headerAngle > 180)
                outputDTO.actions.add(Action.RIGHT);
        }

        return outputDTO;
    }


    @Override
    public int getPeriod() {
        return 50;
    }
}

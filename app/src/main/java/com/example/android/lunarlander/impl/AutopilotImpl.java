package com.example.android.lunarlander.impl;

import android.util.Log;

import com.example.android.lunarlander.Autopilot;
import com.example.android.lunarlander.dto.Action;
import com.example.android.lunarlander.dto.InputDTO;
import com.example.android.lunarlander.dto.OutputDTO;

import java.util.Random;


public class AutopilotImpl implements Autopilot {

    private static final int dAngle = 6;

    @Override
    public OutputDTO getActions(InputDTO input) {
        System.out.println(input);
        OutputDTO outputDTO = new OutputDTO();

        //ACCEL
        if (input.y > 240) {
            if (input.velocityY < -60 && new Random().nextBoolean()) {
                outputDTO.actions.add(Action.ACCEL);
            }
        } else if (input.y > 120) {
            if (input.velocityY < -50) {
                outputDTO.actions.add(Action.ACCEL);
            }
        } else {
            if (input.velocityY < -20) {
                outputDTO.actions.add(Action.ACCEL);
            }
        }

        //ROTATE
        if (input.x < input.goalX+input.goalWidth/3.0) {
            if (input.velocityX < 1)
                if (input.headerAngle < dAngle || input.headerAngle > 180)
                    outputDTO.actions.add(Action.RIGHT);
        } else if (input.x > input.goalX+2*input.goalWidth/3.0) {
            if (input.velocityX > -1)
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

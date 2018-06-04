package com.example.android.lunarlander;

import com.example.android.lunarlander.dto.Action;
import com.example.android.lunarlander.dto.InputDTO;
import com.example.android.lunarlander.dto.OutputDTO;
import com.example.android.lunarlander.impl.AutopilotImpl2;
import com.example.android.lunarlander.impl.AutopilotWithNeuralNet;
import com.example.android.lunarlander.net.ActionNet;
import com.example.android.lunarlander.net.LearnEntry;

import java.io.IOException;
import java.util.EnumSet;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.android.lunarlander.dto.Action.*;

public class TensorMain {

    private static InputDTO state;
    private static OutputDTO outputDTO;

    private static double reward = 0.0;

    public static void main(String[] args) throws Exception {

        LunarLanderFerry lunarLander = new LunarLanderFerryImpl();



        Timer t = startAutopilot(lunarLander);
        for (int i=0; i<1000000000; i++) {
            boolean ended = LunarLanderPhysics.updatePhysics(lunarLander);
            if(ended) {
                System.out.println(reward);
                reward = 0;
            }
            Thread.sleep(50);
        }
        t.cancel();

    }

    private static Timer startAutopilot(LunarLanderFerry lunarLander) throws IOException {
        ActionNet actionNet = new ActionNet();
        actionNet.init();

        LunarLanderPhysics.isTraining = true;
        AutopilotWithNeuralNet autopilot = new AutopilotWithNeuralNet();
        autopilot.setActionNet(actionNet);
        Timer t = new Timer();
        t.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                InputDTO prevState = state;
                state = getFerryState(lunarLander);
                if (prevState != null) {
                    actionNet.remember(LearnEntry.create(prevState, outputDTO, calculateReward(lunarLander), state));
                    reward += calculateReward(lunarLander);
                    actionNet.fit();
                }
                outputDTO = autopilot.getActions(state);
                Action action = outputDTO.actions.get(0);

                changeStateBasedOnAction(lunarLander, action);
            }
        }, 10, autopilot.getPeriod());
        return t;
    }

    private static float calculateReward(LunarLanderFerry ferry) {

        float reward = 0f;
        double distanceDifference = ferry.getGoalX() - ferry.getX();
        boolean onGoal = (ferry.getGoalX() <= ferry.getX() - ferry.getLanderWidth() / 2 && ferry.getX()
                + ferry.getLanderWidth() / 2 <= ferry.getGoalX() + ferry.getGoalWidth());
        double speed = Math.sqrt(ferry.getDX() * ferry.getDX() + ferry.getDY() * ferry.getDY());
        if(speed <= ferry.getGoalSpeed()){
            reward += 1;
        }

        if((ferry.getHeading() <= ferry.getGoalAngle() || ferry.getHeading() >= 360 - ferry.getGoalAngle())){
            reward += 1;
        }

        if(onGoal){
            reward += 10;
        }

        return (float) (reward );//* Math.abs(1/(distanceDifference/1000)));
    }

    private static InputDTO getFerryState(LunarLanderFerry ferry) {

        return new InputDTO(
                ferry.getX() / ferry.getCanvasWidth(),
                ferry.getY() / ferry.getCanvasHeight(),
                ferry.getHeading(),
                ferry.getDX(),
                ferry.getDY(),
                ferry.getFuel(),
                ferry.getGoalX() / (double) ferry.getCanvasWidth(),
                ferry.getGoalWidth() / (double) ferry.getCanvasWidth());
    }

    private static void changeStateBasedOnAction(LunarLanderFerry ferry, Action action) {

        if (EnumSet.of(LEFT_ACCEL, STRAIGHT_ACCEL, RIGHT_ACCEL).contains(action)) {
            ferry.setEngineFiring(true);
        } else {
            ferry.setEngineFiring(false);
        }

        if (action == LEFT || action == LEFT_ACCEL) {
            ferry.setRotating(-1);
        } else if (action == RIGHT || action == RIGHT_ACCEL) {
            ferry.setRotating(1);
        } else if (action == STRAIGHT || action == STRAIGHT_ACCEL) {
            ferry.setRotating(0);
        }
    }
}

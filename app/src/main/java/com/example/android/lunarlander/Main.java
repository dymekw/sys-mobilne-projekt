package com.example.android.lunarlander;

import com.example.android.lunarlander.dto.Action;
import com.example.android.lunarlander.dto.InputDTO;
import com.example.android.lunarlander.dto.OutputDTO;
import com.example.android.lunarlander.impl.AutopilotImpl2;

import java.util.Timer;
import java.util.TimerTask;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        LunarLanderFerry lunarLander = new LunarLanderFerryImpl();
        Timer t = startAutopilot(lunarLander);
        for (int i=0; i<350; i++) {
            LunarLanderPhysics.updatePhysics(lunarLander);
            Thread.sleep(50);
        }
        t.cancel();
    }

    private static Timer startAutopilot(LunarLanderFerry ferry) {
        Autopilot autopilot = new AutopilotImpl2();
        Timer t = new Timer();
        t.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                InputDTO inputDTO = new InputDTO(
                        ferry.getX() / ferry.getCanvasWidth(),
                        ferry.getY() / ferry.getCanvasHeight(),
                        ferry.getHeading(),
                        ferry.getDX(),
                        ferry.getDY(),
                        ferry.getFuel(),
                        ferry.getGoalX() / (double)ferry.getCanvasWidth(),
                        ferry.getGoalWidth() / (double)ferry.getCanvasWidth());
                OutputDTO outputDTO = autopilot.getActions(inputDTO);

                ferry.setEngineFiring(outputDTO.actions.contains(Action.ACCEL));

                boolean rotate = false;
                if (outputDTO.actions.contains(Action.LEFT)) {
                    ferry.setRotating(-1);
                    rotate = true;
                }
                if (outputDTO.actions.contains(Action.RIGHT)) {
                    ferry.setRotating(1);
                    rotate = true;
                }
                if (!rotate) {
                    ferry.setRotating(0);
                }
            }
        }, 10, autopilot.getPeriod());
        return t;
    }
}

package com.example.android.lunarlander;

import android.content.res.Resources;
import android.util.Log;

import static com.example.android.lunarlander.LunarView.LunarThread.*;

public class LunarLanderPhysics {

    public static void updatePhysics(LunarLanderFerry ferry) {
        long now = System.currentTimeMillis();

        // Do nothing if mLastTime is in the future.
        // This allows the game-start to delay the start of the physics
        // by 100ms or whatever.
        if (ferry.getLastTime() > now) return;

        double elapsed = (now - ferry.getLastTime()) / 1000.0;

        // mRotating -- update heading
        if (ferry.getRotating() != 0) {
            ferry.setHeading(ferry.getHeading() + ferry.getRotating() * (PHYS_SLEW_SEC * elapsed));

            // Bring things back into the range 0..360
            if (ferry.getHeading() < 0)
                ferry.setHeading(ferry.getHeading() + 360);
            else if (ferry.getHeading() >= 360) ferry.setHeading(ferry.getHeading() - 360);
        }

        // Base accelerations -- 0 for x, gravity for y
        double ddx = 0.0;
        double ddy = -PHYS_DOWN_ACCEL_SEC * elapsed;

        if (ferry.isEngineFiring()) {
            // taking 0 as up, 90 as to the right
            // cos(deg) is ddy component, sin(deg) is ddx component
            double elapsedFiring = elapsed;
            double fuelUsed = elapsedFiring * PHYS_FUEL_SEC;

            // tricky case where we run out of fuel partway through the
            // elapsed
            if (fuelUsed > ferry.getFuel()) {
                elapsedFiring = ferry.getFuel()/ fuelUsed * elapsed;
                fuelUsed = ferry.getFuel();

                // Oddball case where we adjust the "control" from here
                ferry.setEngineFiring(false);
            }

            ferry.setFuel(ferry.getFuel() - fuelUsed);

            // have this much acceleration from the engine
            double accel = PHYS_FIRE_ACCEL_SEC * elapsedFiring;

            double radians = 2 * Math.PI * ferry.getHeading() / 360;
            ddx = Math.sin(radians) * accel;
            ddy += Math.cos(radians) * accel;
        }

        double dxOld = ferry.getDX();
        double dyOld = ferry.getDY();

        // figure speeds for the end of the period
        ferry.setDX(ferry.getDX() + ddx);
        ferry.setDY(ferry.getDY() + ddy);

        // figure position based on average speed during the period
        ferry.setX(ferry.getX() + elapsed * (ferry.getDX() + dxOld) / 2);
        ferry.setY(ferry.getY() + elapsed * (ferry.getDY() + dyOld) / 2);

        ferry.setLastTime(now);

        // Evaluate if we have landed ... stop the game
        double yLowerBound = TARGET_PAD_HEIGHT + ferry.getLanderHeight() / 2
                - TARGET_BOTTOM_PADDING;
        if (ferry.getY() <= yLowerBound) {
            ferry.setY(yLowerBound);

            int result = STATE_LOSE;
            CharSequence message = "";
            Resources res = ferry.getResource();
            double speed = Math.sqrt(ferry.getDX() * ferry.getDX() + ferry.getDY() * ferry.getDY());
            boolean onGoal = (ferry.getGoalX() <= ferry.getX() - ferry.getLanderWidth() / 2 && ferry.getX()
                    + ferry.getLanderWidth() / 2 <= ferry.getGoalX() + ferry.getGoalWidth());

            // "Hyperspace" win -- upside down, going fast,
            // puts you back at the top.
            if (onGoal && Math.abs(ferry.getHeading() - 180) < ferry.getGoalAngle()
                    && speed > PHYS_SPEED_HYPERSPACE) {
                result = STATE_WIN;
                ferry.incWinsInRow();
                doStart(ferry);

                return;
                // Oddball case: this case does a return, all other cases
                // fall through to setMode() below.
            } else if (!onGoal) {
                if (res!=null)
                    message = res.getText(R.string.message_off_pad);
                else
                    message = "off_pad";
            } else if (!(ferry.getHeading() <= ferry.getGoalAngle() || ferry.getHeading() >= 360 - ferry.getGoalAngle())) {
                if (res!=null)
                    message = res.getText(R.string.message_bad_angle);
                else
                    message = "bad_angle";
            } else if (speed > ferry.getGoalSpeed()) {
                if (res!=null)
                    message = res.getText(R.string.message_too_fast);
                else
                    message = "too_fast";
            } else {
                result = STATE_WIN;
                ferry.incWinsInRow();
            }

            ferry.setState(result, message);
        }
    }


    public static void doStart(LunarLanderFerry ferry) {
        // First set the game for Medium difficulty
        ferry.setFuel(PHYS_FUEL_INIT);
        ferry.setEngineFiring(false);
        ferry.setGoalWidth((int) (ferry.getLanderWidth() * TARGET_WIDTH));
        ferry.setGoalSpeed(TARGET_SPEED);
        ferry.setGoalAngle(TARGET_ANGLE);
        int speedInit = PHYS_SPEED_INIT;

        // Adjust difficulty params for EASY/HARD
        if (ferry.getDifficulty() == DIFFICULTY_EASY) {
            ferry.setFuel(ferry.getFuel() * 3 / 2);
            ferry.setGoalWidth(ferry.getGoalWidth() * 4 / 3);
            ferry.setGoalSpeed(ferry.getGoalSpeed() * 3 / 2);
            ferry.setGoalAngle(ferry.getGoalAngle() * 4 / 3);
            speedInit = speedInit * 3 / 4;
        } else if (ferry.getDifficulty()  == DIFFICULTY_HARD) {
            ferry.setFuel(ferry.getFuel() * 7 / 8);
            ferry.setGoalWidth(ferry.getGoalWidth() * 3 / 4);
            ferry.setGoalSpeed(ferry.getGoalSpeed() * 7 / 8);
            speedInit = speedInit * 4 / 3;
        }

        // pick a convenient initial location for the lander sprite
        ferry.setX(ferry.getCanvasWidth() / 2);
        ferry.setY(ferry.getCanvasHeight() - ferry.getLanderHeight() / 2);

        // start with a little random motion
        ferry.setDY(Math.random() * -speedInit);
        ferry.setDX(Math.random() * 2 * speedInit - speedInit);
        ferry.setHeading(0);

        // Figure initial spot for landing, not too near center
        while (true) {
            ferry.setGoalX((int) (Math.random() * (ferry.getCanvasWidth() - ferry.getGoalWidth())));
            if (Math.abs(ferry.getGoalX() - (ferry.getX() - ferry.getLanderWidth() / 2)) > ferry.getCanvasHeight() / 6)
                break;
        }

        ferry.setLastTime(System.currentTimeMillis() + 100);
        ferry.setState(STATE_RUNNING, null);
    }


}

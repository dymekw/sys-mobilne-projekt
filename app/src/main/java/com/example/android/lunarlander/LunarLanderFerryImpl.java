package com.example.android.lunarlander;

import android.content.res.Resources;

public class LunarLanderFerryImpl implements LunarLanderFerry {

    private long lastTime;
    private int rotating;
    private double heading;
    private boolean isEngineFiring;
    private double fuel;
    private double dx;
    private double dy;
    private double x;
    private double y;
    private int landerWidth;
    private int landerHeight;
    private int goalX;
    private int goalWidth;
    private int goalAngle;
    private int goalSpeed;
    private int winsInRow;

    public LunarLanderFerryImpl() {
        rotating=0;
        landerHeight=135;
        landerWidth=113;
        winsInRow=0;

        LunarLanderPhysics.doStart(this);
    }

    @Override
    public long getLastTime() {
        return lastTime;
    }

    @Override
    public void setLastTime(long time) {
        this.lastTime = time;
    }

    @Override
    public int getRotating() {
        return rotating;
    }

    @Override
    public void setRotating(int rotating) {
        this.rotating = rotating;
    }

    @Override
    public double getHeading() {
        return heading;
    }

    @Override
    public void setHeading(double heading) {
        this.heading = heading;
    }

    @Override
    public boolean isEngineFiring() {
        return isEngineFiring;
    }

    @Override
    public void setEngineFiring(boolean isEngineFiring) {
        this.isEngineFiring = isEngineFiring;
    }

    @Override
    public double getFuel() {
        return fuel;
    }

    @Override
    public void setFuel(double fuel) {
        this.fuel = fuel;
    }

    @Override
    public double getDX() {
        return dx;
    }

    @Override
    public double getDY() {
        return dy;
    }

    @Override
    public void setDX(double dx) {
        this.dx = dx;
    }

    @Override
    public void setDY(double dy) {
        this.dy = dy;
    }

    @Override
    public double getX() {
        return x;
    }

    @Override
    public double getY() {
        return y;
    }

    @Override
    public void setX(double dx) {
        this.x = dx;
    }

    @Override
    public void setY(double dy) {
        this.y = dy;
    }

    @Override
    public double getLanderWidth() {
        return landerWidth;
    }

    @Override
    public double getLanderHeight() {
        return landerHeight;
    }

    @Override
    public int getGoalX() {
        return goalX;
    }

    @Override
    public void setGoalX(int goalX) {
        this.goalX = goalX;
    }

    @Override
    public int getGoalWidth() {
        return goalWidth;
    }

    @Override
    public void setGoalWidth(int goalWidth) {
        this.goalWidth = goalWidth;
    }

    @Override
    public int getGoalAngle() {
        return goalAngle;
    }

    @Override
    public void setGoalAngle(int goalAngle) {
        this.goalAngle = goalAngle;
    }

    @Override
    public int getGoalSpeed() {
        return goalSpeed;
    }

    @Override
    public void setGoalSpeed(int goalSpeed) {
        this.goalSpeed = goalSpeed;
    }

    @Override
    public void incWinsInRow() {
        winsInRow++;
    }

    @Override
    public void setState(int result, CharSequence message) {
        if (message != null) {
            System.out.println(message);
        }
    }

    @Override
    public Resources getResource() {
        return null;
    }

    @Override
    public int getDifficulty() {
        return LunarView.LunarThread.DIFFICULTY_MEDIUM;
    }

    @Override
    public int getCanvasWidth() {
        return 480;
    }

    @Override
    public int getCanvasHeight() {
        return 768;
    }
}

package com.example.android.lunarlander;

import android.content.res.Resources;

public interface LunarLanderFerry {

    long getLastTime();
    void setLastTime(long time);

    int getRotating();
    void setRotating(int rotating);

    double getHeading();
    void setHeading(double heading);

    boolean isEngineFiring();
    void setEngineFiring(boolean isEngineFiring);

    double getFuel();
    void setFuel(double fuel);

    double getDX();
    double getDY();

    void setDX(double dx);
    void setDY(double dy);

    double getX();
    double getY();

    void setX(double dx);
    void setY(double dy);

    double getLanderWidth();
    double getLanderHeight();

    int getGoalX();
    void setGoalX(int goalX);
    int getGoalWidth();
    void setGoalWidth(int goalWidth);
    int getGoalAngle();
    void setGoalAngle(int goalAngle);
    int getGoalSpeed();
    void setGoalSpeed(int goalSpeed);

    void incWinsInRow();

    void setState(int result, CharSequence message);

    Resources getResource();

    int getDifficulty();

    int getCanvasWidth();
    int getCanvasHeight();
}

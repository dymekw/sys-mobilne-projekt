package com.example.android.lunarlander.dto;

/**
 * headerAngle - 0-360
 */
public class InputDTO {

    public double x;
    public double y;
    public double headerAngle;
    public double velocityX;
    public double velocityY;
    public double fuel;

    public double goalX;
    public double goalWidth;

    public InputDTO(double x, double y, double headerAngle, double velocityX, double velocityY, double fuel, double goalX, double goalWidth) {
        this.x = x;
        this.y = y;
        this.headerAngle = headerAngle;
        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.fuel = fuel;
        this.goalX = goalX;
        this.goalWidth = goalWidth;
    }

    @Override
    public String toString() {
        return "InputDTO{" +
                "x=" + x +
                ", y=" + y +
                ", headerAngle=" + headerAngle +
                ", velocityX=" + velocityX +
                ", velocityY=" + velocityY +
                ", fuel=" + fuel +
                ", goalX=" + goalX +
                ", goalWidth=" + goalWidth +
                '}';
    }
}

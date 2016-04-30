package com.sjyr.plantalarm.models;

/**
 * 아두이노에서 얻어오는 센서값
 */
public class SensorResult {
    private int moisture;

    public int getMoisture() {
        return moisture;
    }

    public void setMoisture(int moisture) {
        this.moisture = moisture;
    }
}

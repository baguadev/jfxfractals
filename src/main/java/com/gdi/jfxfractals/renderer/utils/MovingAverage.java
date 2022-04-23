package com.gdi.jfxfractals.renderer.utils;

public class MovingAverage {
    private final double[] data;

    public MovingAverage(int dataPoints) {
        data = new double[dataPoints];
    }

    public MovingAverage addValue(double val) {
        ArrayHelper.cycleArray(data, val);
        return this;
    }

    public double getAverage() {
        double avg = 0;
        for (int i = 0; i < data.length; i++) {
            avg += data[i];
        }
        return avg/data.length;
    }
}

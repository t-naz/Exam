package com.hellofresh.model;

public class StatisticsResponse {

    private double sumX;
    private double avgX;
    private long sumY;
    private long avgY;
    private long count;

    public double getSumX() {
        return sumX;
    }

    public void setSumX(double sumX) {
        this.sumX = sumX;
    }

    public double getAvgX() {
        return avgX;
    }

    public void setAvgX(double avgX) {
        this.avgX = avgX;
    }

    public long getSumY() {
        return sumY;
    }

    public void setSumY(long sumY) {
        this.sumY = sumY;
    }

    public long getAvgY() {
        return avgY;
    }

    public void setAvgY(long avgY) {
        this.avgY = avgY;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "StatisticsResponse{" +
                "sumX=" + sumX +
                ", avgX=" + avgX +
                "sumY=" + sumY +
                ", avgY=" + avgY +
                ", count=" + count +
                '}';
    }
}

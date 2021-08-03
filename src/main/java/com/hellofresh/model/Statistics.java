package com.hellofresh.model;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Statistics {
    private Lock lock = new ReentrantLock();
    private double sum = 0;
    private long total = 0;
    private double sumX = 0;
    private double avgX = 0;
    private long sumY = 0;
    private long avgY = 0;

    public Statistics() {
    }

    private Statistics(Statistics s) {
        this.sum = s.sum;
        this.total = s.total;
        this.avgX = s.avgX;
        this.sumX = s.sumX;
        this.avgY = s.avgY;
        this.sumY = s.sumY;
    }

    public void updateStatistics(double amount) {
        try{
            lock.lock();
            sum += amount;
            total++;
        }finally {
            lock.unlock();
        }
    }

    public void updateStatistics(double x,long y) {
        try{
            lock.lock();
            total++;
            sumX += x;
            sumY += y;
        }finally {
            lock.unlock();
        }
    }

    public Statistics getStatistics() {
        try{
            lock.lock();
            return new Statistics(this);
        }finally {
            lock.unlock();
        }
    }

    public double getSum() {
        return sum;
    }

    public double getSumX() {
        return sumX;
    }

    public long getSumY() {
        return sumY;
    }

    public double getAvgX() {
        return avgX;
    }

    public long getAvgY() {
        return avgY;
    }

    public long getTotal() {
        return total;
    }

}

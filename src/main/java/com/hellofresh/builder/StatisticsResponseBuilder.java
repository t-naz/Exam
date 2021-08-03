package com.hellofresh.builder;

import com.hellofresh.model.StatisticsResponse;

public class StatisticsResponseBuilder {

    private StatisticsResponse response;

    private StatisticsResponseBuilder() {
        response = new StatisticsResponse();
    }

    public static StatisticsResponseBuilder createStatisticsResponse(){
        return new StatisticsResponseBuilder();
    }

    public StatisticsResponseBuilder withSumX(double sumX){
        response.setSumX(sumX);
        return this;
    }

    public StatisticsResponseBuilder withAvgX(double avgX){
        response.setAvgX(avgX);
        return this;
    }

    public StatisticsResponseBuilder withSumY(long sumY){
        response.setSumY(sumY);
        return this;
    }

    public StatisticsResponseBuilder withAvgY(long avgY){
        response.setAvgY(avgY);
        return this;
    }

    public StatisticsResponseBuilder withTotal(long total){
        response.setTotal(total);
        return this;
    }

    public StatisticsResponse build(){
        return response;
    }
}

package com.hellofresh.builder;

import com.hellofresh.model.StatisticsRequest;

public class StatisticsRequestBuilder {

    private StatisticsRequest request;

    private StatisticsRequestBuilder() {
        request = new StatisticsRequest();
    }

    public static StatisticsRequestBuilder createStatisticsRequest(){
        return new StatisticsRequestBuilder();
    }

    public StatisticsRequestBuilder withX(double x){
        request.setX(x);
        return this;
    }

    public StatisticsRequestBuilder withY(long y){
        request.setY(y);
        return this;
    }

    public StatisticsRequestBuilder withTimestamp(long timestamp){
        request.setTimestamp(timestamp);
        return this;
    }

    public StatisticsRequest build(){
        return request;
    }
}

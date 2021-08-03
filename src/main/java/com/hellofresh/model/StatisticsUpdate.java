package com.hellofresh.model;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;

public class StatisticsUpdate {

    private HttpStatus responseEntity;
    private boolean added;

    public HttpStatus getResponseEntity() {
        return responseEntity;
    }

    public void setResponseEntity(HttpStatus responseEntity) {
        this.responseEntity = responseEntity;
    }

    public boolean isAdded() {
        return added;
    }

    public void setAdded(boolean added) {
        this.added = added;
    }
}

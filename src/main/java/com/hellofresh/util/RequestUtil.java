package com.hellofresh.util;

import com.hellofresh.model.StatisticsRequest;
import java.util.ArrayList;
import java.util.List;

public class RequestUtil {
    public static List<StatisticsRequest> convertRequest(String input){
        List<StatisticsRequest> request = new ArrayList<>();
        String[] requestList = input.split("[,\n]");
        for(int i=0;i<requestList.length;i=i+3){
            StatisticsRequest statisticsRequest = new StatisticsRequest();
            statisticsRequest.setTimestamp(Long.valueOf(requestList[i]));
            statisticsRequest.setX(Double.valueOf(requestList[i+1]));
            statisticsRequest.setY(Long.valueOf(requestList[i+2]));
            request.add(statisticsRequest);
        }
        return request;
    }
}

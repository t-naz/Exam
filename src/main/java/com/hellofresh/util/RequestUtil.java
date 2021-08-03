package com.hellofresh.util;

import com.hellofresh.model.StatisticsRequest;
import java.util.ArrayList;
import java.util.List;

public class RequestUtil {
    public static List<StatisticsRequest> convertRequest(String input){
        List<StatisticsRequest> request = new ArrayList<>();
        String[] namesList = input.split("[,\n]");
        for(int i=0;i<namesList.length;i=i+3){
            StatisticsRequest statisticsRequest = new StatisticsRequest();
            statisticsRequest.setTimestamp(Long.valueOf(namesList[i]));
            statisticsRequest.setX(Double.valueOf(namesList[i+1]));
            statisticsRequest.setY(Long.valueOf(namesList[i+2]));
            request.add(statisticsRequest);
        }
        return request;
    }
}

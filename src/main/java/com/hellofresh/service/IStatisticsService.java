package com.hellofresh.service;

import com.hellofresh.model.StatisticsRequest;
import com.hellofresh.model.StatisticsResponse;
import com.hellofresh.model.StatisticsUpdate;
import java.util.List;

public interface IStatisticsService {
    StatisticsUpdate addEvent(List<StatisticsRequest> request, long timestamp);
    String getStatistics(long timestamp);
    void clearCache();
}

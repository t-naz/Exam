package com.hellofresh.service.impl;

import com.hellofresh.builder.StatisticsResponseBuilder;
import com.hellofresh.cache.StatisticsCache;
import com.hellofresh.model.Statistics;
import com.hellofresh.model.StatisticsUpdate;
import com.hellofresh.util.Constants;
import com.hellofresh.model.StatisticsRequest;
import com.hellofresh.model.StatisticsResponse;
import com.hellofresh.service.IStatisticsService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StatisticsServiceImpl implements IStatisticsService{

    @Inject
    private final StatisticsCache<Long, Statistics> cache;

    @Inject
    public StatisticsServiceImpl(StatisticsCache<Long, Statistics> cache) {
        this.cache = cache;
    }

    public StatisticsUpdate addEvent(List<StatisticsRequest> requestList, long timestamp) {
        StatisticsUpdate statisticsUpdate = new StatisticsUpdate();
        boolean added = false;
        int count =0;
        for(StatisticsRequest request: requestList){
            long requestTime = request.getTimestamp();
            long delay = timestamp - requestTime;
            if (delay < 0 || delay >= Constants.ONE_MINUTE_IN_MS) {
                count++;
                continue;
            } else {
                added = true;
                Long key = getKeyFromTimestamp(requestTime);
                Statistics s = cache.get(key);
                if(s == null) {
                    synchronized (cache) {
                        s = cache.get(key);
                        if (s == null) {
                            s = new Statistics();
                            cache.put(key, s);
                        }
                    }
                }
                s.updateStatistics(request.getX(),request.getY());
            }
        }
        if(count>0 && added){
            statisticsUpdate.setResponseEntity(HttpStatus.PARTIAL_CONTENT);
            statisticsUpdate.setAdded(added);
            return statisticsUpdate;
        }
        else if(count>0){
            statisticsUpdate.setResponseEntity(HttpStatus.NO_CONTENT);
            return statisticsUpdate;
        }
        statisticsUpdate.setAdded(added);
        statisticsUpdate.setResponseEntity(HttpStatus.ACCEPTED);
        return statisticsUpdate;
    }

    public String getStatistics(long timestamp) {
        Map<Long, Statistics> copy = cache.entrySet().parallelStream().collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getStatistics()));
        return getStatisticsFromCacheCopy(copy, timestamp).toString();
    }

    private StatisticsResponse getStatisticsFromCacheCopy(Map<Long, Statistics> copy, long timestamp) {

        double sumX = 0;
        long sumY = 0;
        double avgX = 0;
        long avgY = 0;
        long total = 0;
        Long key = getKeyFromTimestamp(timestamp);

        for (Map.Entry<Long, Statistics> e : copy.entrySet()) {
            Long eKey = e.getKey();
            Long timeFrame = key - eKey;
            if(timeFrame >= 0 && timeFrame < cache.getCapacity()) {
                Statistics eValue = e.getValue();
                if(eValue.getTotal() > 0) {
                    total += eValue.getTotal();
                    sumX += eValue.getSumX();
                    sumY += eValue.getSumY();
                }
            }
        }
        if(total == 0) {
            avgX = 0;
            avgY = 0;
            sumX = 0;
            sumY = 0;
        } else {
            avgX = sumX / total;
            avgY = sumY / total;
        }

        return StatisticsResponseBuilder.createStatisticsResponse().withTotal(total).withSumX(sumX).withAvgX(avgX).withSumY(sumY).withAvgY(avgY).build();
    }

    private Long getKeyFromTimestamp(Long timestamp) {
        return (timestamp * cache.getCapacity()) / Constants.ONE_MINUTE_IN_MS;
    }

    public void clearCache() {
        cache.clear();
    }
}

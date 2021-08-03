package com.hellofresh.service;

import com.hellofresh.Application;
import com.hellofresh.builder.StatisticsRequestBuilder;
import com.hellofresh.model.StatisticsRequest;
import com.hellofresh.model.StatisticsResponse;
import com.hellofresh.model.StatisticsUpdate;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.time.Instant;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { Application.class})
public class StatisticsServiceTest {

    @Inject
    private IStatisticsService service;

    @Before
    public void init(){
        service.clearCache();
    }

    @Test
    public void testAddEvent_withValidStats_added(){
        long current = Instant.now().toEpochMilli();
        StatisticsRequest statisticsRequest = StatisticsRequestBuilder.createStatisticsRequest().withX(1.13454545).withY(1282509067).withTimestamp(current).build();
        List<StatisticsRequest> request = new ArrayList<StatisticsRequest>();
        request.add(statisticsRequest);
        StatisticsUpdate statisticsUpdate = service.addEvent(request, current);
        Assert.assertEquals(true, statisticsUpdate.isAdded());
    }

    @Test
    public void testAddEvent_withInPastTimestampMoreThanAMinute_notAdded(){
        long current = Instant.now().toEpochMilli();
        StatisticsRequest statisticsRequest = StatisticsRequestBuilder.createStatisticsRequest().withX(2.11123333).withY(1282509067).withTimestamp(current-60000).build();
        List<StatisticsRequest> request = new ArrayList<StatisticsRequest>();
        request.add(statisticsRequest);
        StatisticsUpdate statisticsUpdate = service.addEvent(request, current);
        Assert.assertEquals(false, statisticsUpdate.isAdded());
        Assert.assertEquals(HttpStatus.NO_CONTENT, statisticsUpdate.getResponseEntity());
    }

    @Test
    public void testAddEvent_withInPastTimestampWithinAMinute_created(){
        long current = Instant.now().toEpochMilli();
        StatisticsRequest statisticsRequest = StatisticsRequestBuilder.createStatisticsRequest().withX(2.11123333).withY(1282509067).withTimestamp(current-50000).build();
        List<StatisticsRequest> request = new ArrayList<StatisticsRequest>();
        request.add(statisticsRequest);
        StatisticsUpdate statisticsUpdate = service.addEvent(request, current);
        Assert.assertEquals(true, statisticsUpdate.isAdded());
        Assert.assertEquals(HttpStatus.ACCEPTED, statisticsUpdate.getResponseEntity());
    }

    @Test
    public void testGetStats_withAnyData_success() throws Exception{
        long timestamp = Instant.now().toEpochMilli();
        String response = service.getStatistics(timestamp);
        /** String[] requestList = response.split(",");
        Assert.assertEquals(requestList[0], "0");
       Assert.assertEquals("0", requestList[0]);
        Assert.assertEquals("0.0", requestList[1]);
        Assert.assertEquals("0.0", requestList[2]);
        Assert.assertEquals("0", requestList[3]);
        Assert.assertEquals("0", requestList[4]);**/
    }

    @Test
    public void testAddAndGetStats_withValidTimestampMultipleThread_success() throws Exception{
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        int n = 0;
        double x = 1.0;
        long y = 1;
        int count = 60000;
        long timestamp = Instant.now().toEpochMilli();
        long requestTime = timestamp;
        while(n<count) {
            // Time frame is managed from 0 to 59, for cache size 60.
            if(timestamp - requestTime >= 59000) {
                requestTime = timestamp;
            }
            StatisticsRequest statisticsRequest = StatisticsRequestBuilder.createStatisticsRequest().withX(x).withY(y).withTimestamp(requestTime).build();
            List<StatisticsRequest> request = new ArrayList<StatisticsRequest>();
            request.add(statisticsRequest);
            executorService.submit(() -> service.addEvent(request, timestamp));
            n++;
            x++;
            y++;
            requestTime -= 1;
        }

        executorService.shutdown();
        Thread.sleep(1000);
        String response = service.getStatistics(timestamp);
        /**String[] requestList = response.split(",");
        Assert.assertEquals(Integer.toString(count), requestList[0]);
        Assert.assertEquals(30000.5, requestList[1]);
        Assert.assertEquals(30000, requestList[2]);**/
    }
}

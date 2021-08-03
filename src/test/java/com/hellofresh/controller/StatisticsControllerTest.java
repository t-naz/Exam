package com.hellofresh.controller;

import com.hellofresh.Application;
import com.hellofresh.builder.StatisticsRequestBuilder;
import com.hellofresh.model.StatisticsRequest;
import com.hellofresh.model.StatisticsResponse;
import com.hellofresh.service.IStatisticsService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.time.Instant;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { Application.class})
public class StatisticsControllerTest {

    @Inject
    private StatisticsController controller;

    @Inject
    private IStatisticsService service;

    @Before
    public void init(){
        service.clearCache();
    }

    @Test
    public void testAddEvent_withValidStats_accepted(){
        StatisticsRequest request = StatisticsRequestBuilder.createStatisticsRequest().withTimestamp(Instant.now().toEpochMilli()).withX(0.0552672968).withY(1282509067).build();
        Instant.now().toEpochMilli();
        ResponseEntity responseEntity = controller.addEvent(request.toString());
        Assert.assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());
    }

     @Test
    public void testAddEvent_withOneNonValidTimeStamp_partialContent(){
        StatisticsRequest request1 = StatisticsRequestBuilder.createStatisticsRequest().withX(0.0552672968).withY(1282509067).withTimestamp(Instant.now().toEpochMilli()).build();
         StatisticsRequest request2 = StatisticsRequestBuilder.createStatisticsRequest().withX(0.0552672968).withY(1282509067).withTimestamp(Instant.now().toEpochMilli()-70000).build();
         ResponseEntity responseEntity = controller.addEvent(request1.toString()+"\n"+request2.toString());
        Assert.assertEquals(HttpStatus.PARTIAL_CONTENT, responseEntity.getStatusCode());
    }

    @Test
    public void testAddEvent_withInPastTimestampMoreThanAMinute_noContent(){
        StatisticsRequest request1 = StatisticsRequestBuilder.createStatisticsRequest().withX(0.0552672968).withY(1282509067).withTimestamp(Instant.now().toEpochMilli()-70000).build();
        StatisticsRequest request2 = StatisticsRequestBuilder.createStatisticsRequest().withX(0.0552672968).withY(1282509067).withTimestamp(Instant.now().toEpochMilli()-60000).build();
        ResponseEntity responseEntity = controller.addEvent(request1.toString()+"\n"+request2.toString());
        Assert.assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
    }

    @Test
    public void testAddEvent_withInPastTimestampWithinAMinute_accepted(){
        StatisticsRequest request = StatisticsRequestBuilder.createStatisticsRequest().withX(0.0552672968).withY(1282509067).withTimestamp(Instant.now().toEpochMilli()-50000).build();
        ResponseEntity responseEntity = controller.addEvent(request.toString());
        Assert.assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());
    }

    @Test
    public void testGetStrats_Avg_Sum(){
        StatisticsRequest request1 = StatisticsRequestBuilder.createStatisticsRequest().withX(0.0552672968).withY(128250906).withTimestamp(Instant.now().toEpochMilli()-50000).build();
        StatisticsRequest request2 = StatisticsRequestBuilder.createStatisticsRequest().withX(0.0552672968).withY(128250906).withTimestamp(Instant.now().toEpochMilli()).build();
        ResponseEntity responseEntity = controller.addEvent(request1.toString()+"\n"+request2.toString());
        ResponseEntity response = controller.getStatistics();
        Assert.assertEquals(0.0552672968, ((StatisticsResponse)response.getBody()).getAvgX(),0);
        Assert.assertEquals(128250906, ((StatisticsResponse)response.getBody()).getAvgY());
        Assert.assertEquals(0.1105345936, ((StatisticsResponse)response.getBody()).getSumX(),0);
        Assert.assertEquals(256501812, ((StatisticsResponse)response.getBody()).getSumY());
        Assert.assertEquals(2, ((StatisticsResponse)response.getBody()).getTotal());
    }

    @Test
    public void testAddAndGetStats_withInValidTimestampWithinAMinuteWithSameTime_success() throws Exception{
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        int n = 0;
        int count = 50000;
        double x = 1.0;
        while(n<count) {
            StatisticsRequest request = StatisticsRequestBuilder.createStatisticsRequest().withX(x).withY(1282509067).withTimestamp(Instant.now().toEpochMilli()).build();
            executorService.submit(() -> controller.addEvent(request.toString()));
            n++;
            x += 1;
        }

        executorService.shutdown();
        Thread.sleep(1000);
        ResponseEntity response = controller.getStatistics();
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertEquals(count, ((StatisticsResponse)response.getBody()).getTotal());
    }

    @Test
    public void testAddAndGetStats_withInValidTimestampWithinAMinuteWithDifferentTime_success() throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        int n = 0;
        double x = 1.0678999;
        long y = 12233223;
        int count = 50000;
        long timestamp = Instant.now().toEpochMilli();
        while(n<count) {
            StatisticsRequest request = StatisticsRequestBuilder.createStatisticsRequest().withX(x).withY(y).withY(1282509067).withTimestamp(timestamp).build();
            executorService.submit(() -> controller.addEvent(request.toString()));
            n++;
            x += 1;
            y += 1;
            timestamp -= 1;
        }

        executorService.shutdown();
        Thread.sleep(1000);
        ResponseEntity response = controller.getStatistics();
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertEquals(count, ((StatisticsResponse)response.getBody()).getTotal());
    }

    @Test
    public void testAddAndGetStats_withInValidAndOutdatedTimestamp_success() throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        int n = 0;
        double x = 1.099866778;
        long y = 12233223;
        int count = 500;
        long timestamp = Instant.now().toEpochMilli();
        while(n<count) {
            StatisticsRequest request = StatisticsRequestBuilder.createStatisticsRequest().withX(x).withY(y).withTimestamp(timestamp).build();
            executorService.submit(() -> controller.addEvent(request.toString()));
            n++;
            x += 1;
            y += 1;
            timestamp -= 1;
        }

        Thread.sleep(1000);
        timestamp -= 60000;
        n = 0;
        while(n<count) {
            StatisticsRequest request = StatisticsRequestBuilder.createStatisticsRequest().withX(x).withTimestamp(timestamp).build();
            executorService.submit(() -> controller.addEvent(request.toString()));
            n++;
            x += 1;
            y += 1;
            timestamp -= 60000;
        }

        executorService.shutdown();
        Thread.sleep(1000);
        ResponseEntity response = controller.getStatistics();
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertEquals(count, ((StatisticsResponse)response.getBody()).getTotal());
    }

}

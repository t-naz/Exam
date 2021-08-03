package com.hellofresh.controller;

import com.hellofresh.model.StatisticsRequest;
import com.hellofresh.model.StatisticsUpdate;
import com.hellofresh.service.IStatisticsService;
import com.hellofresh.util.RequestUtil;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.inject.Inject;
import java.time.Instant;

@Controller
public class StatisticsController {

    @Inject
    private IStatisticsService statisticsService;

    @RequestMapping(value = "/stats", method = RequestMethod.GET)
    public ResponseEntity getStatistics(){
        long current = Instant.now().toEpochMilli();
        return new ResponseEntity<>(statisticsService.getStatistics(current), HttpStatus.OK);
    }

    @RequestMapping(value = "/event", method = RequestMethod.POST)
    public ResponseEntity addEvent(@RequestBody String input){
        long current = Instant.now().toEpochMilli();
        List<StatisticsRequest> request = RequestUtil.convertRequest(input);
        StatisticsUpdate statisticsUpdate = statisticsService.addEvent(request, current);
        if(statisticsUpdate.isAdded()) {
            return new ResponseEntity(statisticsUpdate.getResponseEntity());
        } else {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }
    }
}

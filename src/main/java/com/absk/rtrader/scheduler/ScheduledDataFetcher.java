package com.absk.rtrader.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.absk.rtrader.exchange.upstox.Util;
import com.absk.rtrader.utils.TickerUtil;


public class ScheduledDataFetcher {
	
	//@Autowired
	//TickerUtil tickerUtil;
	
	//@Autowired
	//Util util;
	
	@Autowired
    private SimpMessagingTemplate template;

	
	//private static final Logger log = LoggerFactory.getLogger(ScheduledDataFetcher.class);
    //@Scheduled(fixedRate = 10000)
    public void reportCurrentTime() {
        //log.info("Scheduled datafetcher called");
        //Ticker tick = util.getFeed();
        //log.info("The tick is now {}", tick);
        //template.convertAndSend("/topic/greetings", tick);
    	
    }

}

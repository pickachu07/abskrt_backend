package com.absk.rtrader.scheduler.tasks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import com.absk.rtrader.exchange.upstox.Util;
import com.absk.rtrader.model.Ticker;

@Configurable
public class MainTask implements Runnable{


	@Autowired
	Util util;
	
	@Autowired
    private SimpMessagingTemplate template;
	
	private static final Logger log = LoggerFactory.getLogger(MainTask.class);
	
	int brickSize;
	String tickerName;
	
	public MainTask(int brickSize, String tickerName){
		this.brickSize = brickSize;
		this.tickerName = tickerName;
	}
	public MainTask(){
		this.brickSize = 0;
		this.tickerName = "";
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		//Util util = new Util();
		//SimpMessagingTemplate template = new Sim
		log.info("Main Task: ticker:"+this.tickerName+": brick size:"+this.brickSize);
		Ticker tick = util.getFeed();
	    log.info("The tick is now {}", tick);
	    template.convertAndSend("/topic/ticker_stream", tick);
		
	}

}

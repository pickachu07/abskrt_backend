package com.absk.rtrader.core.scheduler.tasks;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.absk.rtrader.core.indicators.Renko;
import com.absk.rtrader.core.models.Ticker;
import com.absk.rtrader.exchange.upstox.Util;

@Configurable
public class MainTask implements Runnable{


	@Autowired
	Util util;
	
	@Autowired
    private SimpMessagingTemplate template;
	
	@Autowired
	private Renko r;
	
	private static final Logger log = LoggerFactory.getLogger(MainTask.class);
	
	double brickSize;
	String tickerName;
	
	public MainTask(int brickSize, String tickerName){
		this.brickSize = brickSize;
		this.tickerName = tickerName;
	}
	public MainTask(){
		this.brickSize = 0;
		this.tickerName = "";
	}
	public void run() {
		log.info("Main Task: ticker:"+this.tickerName+": brick size:"+this.brickSize);
		
		Ticker tick = util.getFeed();
		
		//calculate renko
		Renko rInstance = r.getInstance();
		ArrayList<Ticker> tickArr = rInstance.drawRenko(tick,this.brickSize);
		
		//send ohlc to ohlc_stream
		template.convertAndSend("/topic/ohlc_stream", tick);
		
		//send renko brick to /topic/renko_stream
		for(int i=0;i<tickArr.size();i++){
			log.info("The tick is now {}", tick);
		    template.convertAndSend("/topic/ticker_stream", tickArr.get(i));
		}
	    
		
	}

}

package com.absk.rtrader.core.controller;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;

import com.absk.rtrader.core.indicators.NRenko;
import com.absk.rtrader.core.models.HistoricalStreamingSettings;
import com.absk.rtrader.core.models.Notification;
import com.absk.rtrader.core.models.OHLC;
import com.absk.rtrader.core.models.Ticker;
import com.absk.rtrader.core.models.TickerData;
import com.absk.rtrader.core.utils.TickerUtil;
import com.absk.rtrader.exchange.upstox.Util;

@Controller
public class HistoricalWebsocketController {
	
	@Autowired
	private NRenko renko;
	
	@Autowired
	private TickerUtil tickerUtil;
	
	@Autowired
    private SimpMessagingTemplate template;
	
	@Autowired
	Util upstoxUtil;
	
	private static final Logger log = LoggerFactory.getLogger(HistoricalWebsocketController.class);

	
	private boolean isPaused = false;
	private boolean isStopped = true;
    //@MessageMapping("/hello")
    @SendTo("/topic/historical_data_stream")
    public OHLC pushToSocket(Ticker tick) throws Exception {
        //Thread.sleep(1000); // simulated delay
        TickerData data = tick.getData();
        log.info("data is:"+data.toString());
    	return new OHLC(data.getOpen(),data.getHigh(),data.getLow(),data.getClose(),data.getVolume(),data.getTimestamp());
    }
    
    @SendTo("/topic/historical_ohlc_stream")
    public OHLC pushTickToSocket(Ticker tick) throws Exception {
        //Thread.sleep(1000); // simulated delay
        TickerData data = tick.getData();
        log.info("data is:"+data.toString());
    	return new OHLC(data.getOpen(),data.getHigh(),data.getLow(),data.getClose(),data.getVolume(),data.getTimestamp());
    }
    
    
    
    @SendTo("/topic/notification_stream")
    public Notification pushNotify(Notification notification) throws Exception {
      
    	return notification;
    	}
    
    
    //toggle isPaused
    @MessageMapping("/PauseHistoricalDataStream")
    public void pauseStreaming() {
    	this.isPaused = (this.isPaused = true)?false:true;
    }
    
    @MessageMapping("/StopHistoricalDataStream")
    public void stopStreaming() {
    	log.info("Streaming Stopped");
    	this.isStopped = true;
    }
    
    
    
    
    @MessageMapping("/StartHistoricalDataStream")
    public void StartStreaming(HistoricalStreamingSettings hSS,@PathVariable String queryDate) throws InterruptedException {
    	log.info("message received from historical pane:"+hSS.getBrick_size()+"::"+hSS.getTicker_name()+":::"+hSS.getDate());
    	ArrayList<Ticker> prevOut  = null;
    	int delta = -1;
    	OHLC[] data = upstoxUtil.getHistoricalOHLC(hSS.getDate());
    	renko.reset();
    	renko.setBrickSize(hSS.getBrick_size());
    	
    	if(data.length < 10) {
    		log.error("Insufficient Historical data received from exchange.");
        	return;	
    	}
    	else {
    		this.isStopped = false;
    		//convert OHLC to Ticker[]
    		ArrayList<Ticker> sourceData = tickerUtil.toTickerArray(data);
    		int tickCount = 0;
    		ArrayList<Double> listOfOpens = tickerUtil.getPriceArrayByPriceType(sourceData, "open");
    		
    		for(double open : listOfOpens) {
    			
    			if(this.isStopped) 
    			{
    				template.convertAndSend("/topic/notification_stream",new Notification("WARN","Bar Replay stopped!") );
    				return;
    			}
    			template.convertAndSend("/topic/historical_ohlc_stream", sourceData.get(tickCount++) );
    			long numberOfBricks = renko.doNext(open);
    			ArrayList<Double> renkoPrices = renko.getRenkoPrices();
    			
    			ArrayList<Ticker> out = tickerUtil.renkoPricesToTickerArray(renkoPrices, "NSE INDEX", "NIFTYBANK");
    		
    			
    			if(out!= null && out.size()>0) {
    				template.convertAndSend("/topic/historical_data_stream",out.subList(prevOut == null ? 0 : prevOut.size(), out.size()) );
    				//Thread.sleep(100);
    			}
    			prevOut = out;
    			Thread.sleep(5000);
    		}
    		
    			
    		}
    	}
    	
    	
    	
    	
    }
    


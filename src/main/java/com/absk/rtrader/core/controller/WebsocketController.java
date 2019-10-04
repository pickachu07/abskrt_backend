package com.absk.rtrader.core.controller;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import com.absk.rtrader.core.models.OHLC;
import com.absk.rtrader.core.models.RealtimeStreamingSettings;
import com.absk.rtrader.core.models.Ticker;
import com.absk.rtrader.core.models.TickerData;
import com.absk.rtrader.core.scheduler.tasks.MainTask;
import com.absk.rtrader.core.schedulers.CentralScheduler;

@Controller
public class WebsocketController {
	
	@Autowired
	private ApplicationContext applicationContext;
	
	private static final Logger log = LoggerFactory.getLogger(WebsocketController.class);

    //@MessageMapping("/hello")
    @SendTo("/topic/ticker_stream")
    public OHLC pushToSocket(Ticker tick) throws Exception {
        TickerData data = tick.getData();
        //log.debug("data is:"+data.toString());
    	return new OHLC(data.getOpen(),data.getHigh(),data.getLow(),data.getClose(),data.getVolume(),data.getTimestamp());
    }
    
    @MessageMapping("/start_streaming")
    public void StartStreaming(RealtimeStreamingSettings rSS) {
    	//log.info("message received:");
    	
    	Runnable task = new MainTask(rSS.getBrick_size(),rSS.getTicker_name());
    	applicationContext.getAutowireCapableBeanFactory().autowireBean(task);
		try {
			CentralScheduler.getInstance().start(task,"* * * ? * *");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    }
    @MessageMapping("/stop_streaming")
    public void StopStreaming(){
    	log.info("stop Streaming command received:");
    	CentralScheduler.getInstance().stopAll();
    }

}

package com.absk.rtrader.controller;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.absk.rtrader.core.TradingSession;
import com.absk.rtrader.exchange.upstox.Util;
import com.absk.rtrader.indicators.Renko;
import com.absk.rtrader.model.HistoricalStreamingSettings;
import com.absk.rtrader.model.OHLC;
import com.absk.rtrader.model.Ticker;
import com.absk.rtrader.model.TickerData;
import com.absk.rtrader.utils.TickerUtil;

@Controller
public class HistoricalWebsocketController {
	
	@Autowired
	private Renko r;
	
	@Autowired
	private TickerUtil tickerUtil;
	
	@Autowired
    private SimpMessagingTemplate template;
	
	private static final Logger log = LoggerFactory.getLogger(HistoricalWebsocketController.class);

    //@MessageMapping("/hello")
    @SendTo("/topic/historical_data_stream")
    public OHLC pushToSocket(Ticker tick) throws Exception {
        //Thread.sleep(1000); // simulated delay
        TickerData data = tick.getData();
        log.info("data is:"+data.toString());
    	return new OHLC(data.getOpen(),data.getHigh(),data.getLow(),data.getClose(),data.getVolume(),data.getTimestamp());
    }
    
    
    @MessageMapping("/StartHistoricalDataStream")
    public void StartStreaming(HistoricalStreamingSettings hSS) throws InterruptedException {
    	log.info("message received from historical pane:");
    	Util upstoxUtil = new Util();
    	OHLC[] data = upstoxUtil.getHistoricalOHLC();
    	TradingSession ts = new TradingSession();
		Renko rInstance = r.getInstance();
		double brickSize = 2;
		ArrayList<Ticker> tickArr = null;
    	for(int i=0;i<data.length;i++) {
    		Thread.sleep(1000);
    		//System.out.println("Historical data:"+data[i].toString());
    		tickArr = rInstance.drawRenko(tickerUtil.convertToTicker(data[i]),brickSize);
    		ts.processData(tickArr);
    		template.convertAndSend("/topic/historical_data_stream", tickArr);
    		template.convertAndSend("/topic/historical_trans_stream", ts.getTransactions());
    		template.convertAndSend("/topic/historical_calculated_data_stream", ts.calculateProfit());
    		tickArr = null;
    		
    	}
    }
    
}

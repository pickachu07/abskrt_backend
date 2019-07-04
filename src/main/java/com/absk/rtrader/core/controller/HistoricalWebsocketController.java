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

import com.absk.rtrader.core.services.TradingSession;
import com.absk.rtrader.core.indicators.Renko;
import com.absk.rtrader.core.models.HistoricalStreamingSettings;
import com.absk.rtrader.core.models.OHLC;
import com.absk.rtrader.core.models.Ticker;
import com.absk.rtrader.core.models.TickerData;
import com.absk.rtrader.exchange.upstox.Util;
import com.absk.rtrader.core.utils.TickerUtil;

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
    
    
    @MessageMapping("/StartHistoricalDataStream/{queryDate}")
    public void StartStreaming(HistoricalStreamingSettings hSS,@PathVariable String queryDate) throws InterruptedException {
    	log.info("message received from historical pane:");
    	Util upstoxUtil = new Util();
    	OHLC[] data = upstoxUtil.getHistoricalOHLC(queryDate);
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

package com.absk.rtrader.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import com.absk.rtrader.datafetcher.ScheduledDataFetcher;
import com.absk.rtrader.model.OHLC;
import com.absk.rtrader.model.Ticker;
import com.absk.rtrader.model.TickerData;

@Controller
public class WebsocketController {
	private static final Logger log = LoggerFactory.getLogger(WebsocketController.class);

    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    public OHLC pushToSocket(Ticker tick) throws Exception {
        //Thread.sleep(1000); // simulated delay
        TickerData data = tick.getData();
        log.info("data is:"+data.toString());
    	return new OHLC(data.getOpen(),data.getHigh(),data.getLow(),data.getClose(),data.getVolume(),data.getTimestamp());
    }

}

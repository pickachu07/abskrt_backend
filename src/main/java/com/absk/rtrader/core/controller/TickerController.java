package com.absk.rtrader.core.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.absk.rtrader.core.models.Ticker;
import com.absk.rtrader.core.repositories.TickerRepository;
import com.absk.rtrader.core.schedulers.CentralScheduler;
import com.absk.rtrader.exchange.upstox.constants.FeedTypeConstants;
import com.absk.rtrader.exchange.upstox.constants.UpstoxTicker;
import com.absk.rtrader.exchange.upstox.services.UpstoxFeedServiceImpl;
import com.absk.rtrader.exchange.upstox.services.UpstoxWebSocketService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class TickerController {
	
	@Autowired
	private TickerRepository tickerRepository;
	

	@Autowired
    private UpstoxWebSocketService upstoxWebSocketService;
	
	@Autowired
	private UpstoxFeedServiceImpl upstoxFeedService;
	
	@GetMapping("/tickers")
    public Page<Ticker> getTickers(Pageable pageable) {
        return tickerRepository.findAll(pageable);
    }
	
	@GetMapping("/tickers/{dateInString}")
    public List<Ticker> getTickersByDate(@PathVariable String dateInString) {//format yyyy-mm-dd
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");
		Date date;
		try {
			date = sdf.parse(dateInString);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			date = new Date();
			e.printStackTrace();
		}
		
        return tickerRepository.findByTimestampGreaterThan(date);
    }
	
	
	@GetMapping("/get")
    public String getOHLC() {
		return "Site is up!";
    }
	
	
	
	@GetMapping("/subscribe")
	public boolean subscribe() {
		
		return upstoxFeedService.subscribeToTicker(UpstoxTicker.BANK_NIFTY, FeedTypeConstants.FEEDTYPE_FULL);
	}
	
	@GetMapping("/stop")
	public void stopScheduler() {
		CentralScheduler.getInstance().stopAll();
	}
	
	@GetMapping(value = "/connect")
    public ModelAndView wsConnect() {
        log.info("Triggered websocket connect request");
        try {
			upstoxWebSocketService.connect();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return new ModelAndView("redirect:http://localhost:3000");
    }
	
	 @GetMapping(value = "/disconnect")
	    public ModelAndView wsDisconnect() {
	        log.info("Triggered websocket disconnect request");
	        upstoxWebSocketService.disconnect();
	        return new ModelAndView("redirect:http://localhost:3000");
	    }
	
}

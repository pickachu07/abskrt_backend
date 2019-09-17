package com.absk.rtrader.core.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.absk.rtrader.core.constants.CoreConstants;
import com.absk.rtrader.core.models.Ticker;
import com.absk.rtrader.core.repositories.TickerRepositoryCustom;
import com.absk.rtrader.core.services.TradingSession;
import com.absk.rtrader.exchange.upstox.constants.UpstoxFeedTypeConstants;
import com.absk.rtrader.exchange.upstox.constants.UpstoxSymbolNames;
import com.absk.rtrader.exchange.upstox.services.UpstoxFeedServiceImpl;
import com.absk.rtrader.exchange.upstox.services.UpstoxWebSocketService;

import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
public class TickerController {

	@Autowired
	private TradingSession tradingSession;
	
	@Autowired
	private TickerRepositoryCustom tickerRepository;

	@Autowired
	private UpstoxWebSocketService upstoxWebSocketService;

	@Autowired
	private UpstoxFeedServiceImpl upstoxFeedService;
	

	@CrossOrigin(origins = CoreConstants.FRONTEND_BASE_URI)
	@GetMapping("/tickers")
	public List<Ticker> getTickers() {
		return tickerRepository.findAll();
	}

	@CrossOrigin(origins = CoreConstants.FRONTEND_BASE_URI)
	@GetMapping("/tickers/{dateInString}")
	public List<Ticker> getTickersByDate(@PathVariable String dateInString) {// format yyyy-mm-dd
		return tickerRepository.findByTimestamp(dateInString);
	}
	
	@CrossOrigin(origins = CoreConstants.FRONTEND_BASE_URI)
	@GetMapping("/tickers/{dateInString}/{ticker}/{startTime}/{endTime}")
	public List<Ticker> getTickersByDateTime(@PathVariable String dateInString, @PathVariable String ticker, @PathVariable String startTime,@PathVariable String endTime) {// format yyyy-mm-dd
		return tickerRepository.findByDateTimeAndTicker(dateInString, ticker, startTime, endTime);
	}
	
	
	@CrossOrigin(origins = CoreConstants.FRONTEND_BASE_URI)
	@GetMapping("/tickers/{dateInString}/{ticker}")
	public List<Ticker> getTickersByDate(@PathVariable String dateInString, @PathVariable String ticker) {// format yyyy-mm-dd
		return tickerRepository.findByTimestampAndTicker(dateInString,ticker);
	}

	@CrossOrigin(origins = CoreConstants.FRONTEND_BASE_URI)
	@GetMapping("/get")
	public String getOHLC() {
		return "Site is up!";
	}
	

	@CrossOrigin(origins = CoreConstants.FRONTEND_BASE_URI)
	@GetMapping("/subscribe/{tickerName}/{brickSize}")
	public boolean subscribe(@PathVariable String tickerName, @PathVariable String brickSize) {
		
		log.debug("Subscribed with Ticker name:"+tickerName+" and brickSize: "+brickSize);
		//validate brickSize to be int
		int bs = Integer.parseInt(brickSize); 
		if (bs>50) return false;
		//TODO: validate ticker Name
		
		instantiateTradingSession(tickerName,bs);
		return upstoxFeedService.subscribeToTicker(tickerName, UpstoxFeedTypeConstants.FEEDTYPE_FULL);
	}

	@CrossOrigin(origins = CoreConstants.FRONTEND_BASE_URI)
	@GetMapping("/unsubscribe")
	public boolean unSubscribe() {
		return upstoxFeedService.unSubscribeToTicker(UpstoxSymbolNames.BANK_NIFTY, UpstoxFeedTypeConstants.FEEDTYPE_FULL);
	}

	@CrossOrigin(origins = CoreConstants.FRONTEND_BASE_URI)
	@GetMapping(value = "/connect")
	public ModelAndView wsConnect() {
		log.info("Triggered websocket connect request");
		try {
			//TODO check if auth token present/valid
			upstoxWebSocketService.connect();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ModelAndView("redirect:" + CoreConstants.FRONTEND_BASE_URI);
	}

	@CrossOrigin(origins = CoreConstants.FRONTEND_BASE_URI)
	@GetMapping(value = "/disconnect")
	public ModelAndView wsDisconnect() {
		log.info("Triggered websocket disconnect request");
		upstoxWebSocketService.disconnect();
		return new ModelAndView("redirect:" + CoreConstants.FRONTEND_BASE_URI);
	}

	 private void instantiateTradingSession(String tickerName,float brickSize) {
		log.debug("Instantiated trading sessions with TickerName:"+tickerName+" BrickSize: "+brickSize);
		tradingSession.setBrickSize(brickSize);
		tradingSession.setSessionType(1);
		tradingSession.setTickerName(tickerName);
		tradingSession.instantiateSLAgents();
		tradingSession.setRenkoBrickSize(brickSize);
	}
	
	
}

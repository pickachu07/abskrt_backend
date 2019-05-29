package com.absk.rtrader.core.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.absk.rtrader.core.constants.CoreConstants;
import com.absk.rtrader.core.models.Ticker;
import com.absk.rtrader.core.repositories.TickerRepository;
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

	@CrossOrigin(origins = CoreConstants.FRONTEND_BASE_URI)
	@GetMapping("/tickers")
	public Page<Ticker> getTickers(Pageable pageable) {
		return tickerRepository.findAll(pageable);
	}

	@CrossOrigin(origins = CoreConstants.FRONTEND_BASE_URI)
	@GetMapping("/tickers/{dateInString}")
	public List<Ticker> getTickersByDate(@PathVariable String dateInString) {// format yyyy-mm-dd
		return tickerRepository.findByTimestamp(dateInString);
	}

	@CrossOrigin(origins = CoreConstants.FRONTEND_BASE_URI)
	@GetMapping("/get")
	public String getOHLC() {
		return "Site is up!";
	}

	@CrossOrigin(origins = CoreConstants.FRONTEND_BASE_URI)
	@GetMapping("/subscribe")
	public boolean subscribe() {
		// TODO: add path mappings and ticker name validation method
		return upstoxFeedService.subscribeToTicker(UpstoxTicker.BANK_NIFTY, FeedTypeConstants.FEEDTYPE_FULL);
	}

	@CrossOrigin(origins = CoreConstants.FRONTEND_BASE_URI)
	@GetMapping("/unsubscribe")
	public boolean unSubscribe() {
		// TODO: add path mappings and ticker name validation method
		return upstoxFeedService.unSubscribeToTicker(UpstoxTicker.BANK_NIFTY, FeedTypeConstants.FEEDTYPE_FULL);
	}

	@CrossOrigin(origins = CoreConstants.FRONTEND_BASE_URI)
	@GetMapping(value = "/connect")
	public ModelAndView wsConnect() {
		log.info("Triggered websocket connect request");
		try {
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

}

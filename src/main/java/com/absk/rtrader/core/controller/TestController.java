package com.absk.rtrader.core.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.absk.rtrader.core.constants.CoreConstants;
import com.absk.rtrader.core.services.TradingSession;
import com.absk.rtrader.exchange.upstox.services.UpstoxSLService;


@RestController
public class TestController {

	@Autowired
	TradingSession tSession;
	
	@Autowired
	UpstoxSLService slService;
	
	@CrossOrigin(origins = CoreConstants.FRONTEND_BASE_URI)
	@GetMapping("/testbuy")
	public String testBuy() {
		tSession.testMarketBuy();
		
		return "Testing buy Service";
	}
	
	@CrossOrigin(origins = CoreConstants.FRONTEND_BASE_URI)
	@GetMapping("/init-sl")
	public String initAgents() {
		
		slService.instantiateAgents();
		
		return "Testing buy Service";
	}
}

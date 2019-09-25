package com.absk.rtrader.core.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.absk.rtrader.core.constants.CoreConstants;
import com.absk.rtrader.core.services.TradingSession;
import com.absk.rtrader.core.utils.TickerUtil;
import com.absk.rtrader.exchange.upstox.constants.UpstoxExchangeTypeConstants;
import com.absk.rtrader.exchange.upstox.constants.UpstoxFeedTypeConstants;
import com.absk.rtrader.exchange.upstox.constants.UpstoxOrderTypeConstants;
import com.absk.rtrader.exchange.upstox.constants.UpstoxProductTypeConstants;
import com.absk.rtrader.exchange.upstox.services.UpstoxBuyService;
import com.absk.rtrader.exchange.upstox.services.UpstoxSLService;
import com.absk.rtrader.exchange.upstox.services.UpstoxSellService;
import com.absk.rtrader.exchange.upstox.services.UpstoxUserServiceImpl;
import com.github.rishabh9.riko.upstox.users.models.Position;


@RestController
public class TestController {

	@Autowired
	TradingSession tSession;
	
	@Autowired
	UpstoxSLService slService;
	
	@Autowired
	UpstoxBuyService bService;
	
	@Autowired
	UpstoxSellService sService;
	
	@Autowired
	TickerUtil tickerUtil;
		
	@Autowired
	UpstoxUserServiceImpl userService;
	
	@CrossOrigin(origins = CoreConstants.FRONTEND_BASE_URI)
	@GetMapping("/testbuy/{strikeType}/{price}/{quantity}")
	public String testBuy(@PathVariable String price, @PathVariable String quantity,@PathVariable String strikeType) {
		
		String symbol = tickerUtil.getClosestStrikePrice(Integer.parseInt(price), 100, strikeType);
		
		bService.execute(20, symbol, UpstoxOrderTypeConstants.MARKET_ORDER, UpstoxExchangeTypeConstants.NSE_FUTURE_AND_OPTIONS, Integer.parseInt(quantity), UpstoxProductTypeConstants.DELIVERY, new BigDecimal(0));
		return "Testing buy Service.";
	}
	
	
	@CrossOrigin(origins = CoreConstants.FRONTEND_BASE_URI)
	@GetMapping("/testsell/{strikeType}/{price}/{quantity}")
	public String testSell(@PathVariable String price, @PathVariable String quantity,@PathVariable String strikeType) {
		String symbol = tickerUtil.getClosestStrikePrice(Integer.parseInt(price), 100,strikeType);
		
		sService.execute(symbol, UpstoxOrderTypeConstants.MARKET_ORDER, UpstoxExchangeTypeConstants.NSE_FUTURE_AND_OPTIONS, Integer.parseInt(quantity), UpstoxProductTypeConstants.DELIVERY, new BigDecimal(0));
		
		
		return "Testing sell Service";
	}
	
	
	@CrossOrigin(origins = CoreConstants.FRONTEND_BASE_URI)
	@GetMapping("/getpositions")
	public String testGetPositions() {
		List<Position> positions = userService.getPositions();
		
		return "Testing Get Positions functionality";
	}
	
	
	@CrossOrigin(origins = CoreConstants.FRONTEND_BASE_URI)
	@GetMapping("/init-sl")
	public String initAgents() {
		
		slService.instantiateAgents();
		
		return "Testing buy Service";
	}
	
	
	@GetMapping("/getStrike/{strikeType}/{price}")
	public String getClosestStrike(@PathVariable String strikeType, @PathVariable double price) {
		
		return tickerUtil.getClosestStrikePrice(price, 100,strikeType);
	}
	
	@GetMapping("/testsl/{strikeType}/{tickerName}/{price}")
	public Boolean testSLAgents(@PathVariable String tickerName,@PathVariable String strikeType,  @PathVariable double price) {
		
		String strikeName = tickerUtil.getClosestStrikePrice(price, 100,strikeType);
		return slService.startAgent(strikeName, UpstoxExchangeTypeConstants.NSE_FUTURE_AND_OPTIONS,new BigDecimal(price), 10);
	}
	
	@GetMapping("/testsubscribe/{tickerName}")
	public String testSubscribe(@PathVariable String tickerName) {
	
		return slService.subscribeToTickerGetDetail(tickerName, UpstoxExchangeTypeConstants.NSE_FUTURE_AND_OPTIONS, UpstoxFeedTypeConstants.FEEDTYPE_FULL);
	}
	
	@GetMapping("/testunsubscribe/{tickerName}")
	public String testUnSubscribe(@PathVariable String tickerName) {
	
		return slService.subscribeToTickerGetDetail(tickerName,  UpstoxExchangeTypeConstants.NSE_FUTURE_AND_OPTIONS, UpstoxFeedTypeConstants.FEEDTYPE_FULL);
	}
	
	
	
	
	
	
}

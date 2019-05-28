package com.absk.rtrader.core.controller;

import java.util.ArrayList;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.absk.rtrader.core.TradingSession;
import com.absk.rtrader.exchange.upstox.Util;
import com.absk.rtrader.indicators.Renko;
import com.absk.rtrader.model.OHLC;
import com.absk.rtrader.model.Ticker;
import com.absk.rtrader.utils.TickerUtil;
import com.google.common.collect.Table.Cell;

@RestController
@RequestMapping(path = "/historical")
public class UpstoxHistoricalDataController {

	@Autowired
	Util upstoxUtil;
	
	@Autowired
	Renko r;
	
	@Autowired
	TickerUtil util;
	
	@Autowired
	TradingSession ts;
	
	
	
	@CrossOrigin(origins = "http://localhost:3000")
	@GetMapping("/")
	public ArrayList<Ticker> getData(){
		
		OHLC[] data = upstoxUtil.getHistoricalOHLC();
		//TradingSession ts = new TradingSession();
		ts.processAllData(data,4);//TODO: add brick size and ticker as params
		return ts.getRenkoBricks();
	}
	
	@CrossOrigin(origins = "http://localhost:3000")
	@GetMapping("/trans")
	public  Set<Cell<String, Integer, Double>> getTransactions(){
		//OHLC[] data = upstoxUtil.getHistoricalOHLC();
		//TradingSession ts = new TradingSession();
		//ts.processAllData(data);
		return ts.getTransactions();
	}
}

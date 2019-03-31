package com.absk.rtrader.controller;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.absk.rtrader.exchange.upstox.Util;
import com.absk.rtrader.indicators.Renko;
import com.absk.rtrader.model.OHLC;
import com.absk.rtrader.model.Ticker;
import com.absk.rtrader.utils.TickerUtil;

@RestController
@RequestMapping(path = "/historical")
public class UpstoxHistoricalDataController {

	@Autowired
	Util upstoxUtil;
	
	@Autowired
	Renko r;
	
	@Autowired
	TickerUtil util;
	
	@CrossOrigin(origins = "http://localhost:3001")
	@GetMapping("/")
	public ArrayList<Ticker> getData(){
		
		OHLC[] data = upstoxUtil.getHistoricalOHLC();
		//calculate renko
		Renko rInstance = r.getInstance();
		

		
		ArrayList<Ticker> tickArr = new ArrayList<Ticker>();
		ArrayList<OHLC[]> output = new ArrayList<OHLC[]>();
		for(int i=0;i<data.length;i++) {
			ArrayList<Ticker> tempTickArr = rInstance.drawRenko(util.convertToTicker(data[i]),1);
			tickArr.addAll(tempTickArr);
		}
		
		return tickArr;
	}
}

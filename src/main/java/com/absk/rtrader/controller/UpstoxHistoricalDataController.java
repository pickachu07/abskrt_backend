package com.absk.rtrader.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.absk.rtrader.exchange.upstox.Util;
import com.absk.rtrader.model.OHLC;

@RestController
@RequestMapping(path = "/historical")
public class UpstoxHistoricalDataController {

	@Autowired
	Util upstoxUtil;
	
	@CrossOrigin(origins = "http://localhost:3001")
	@GetMapping("/")
	public OHLC[] getData(){
		
		return upstoxUtil.getHistoricalOHLC();
		
	}
}

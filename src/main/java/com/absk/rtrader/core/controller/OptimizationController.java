package com.absk.rtrader.core.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.absk.rtrader.core.Optimizer;
import com.absk.rtrader.exchange.upstox.Util;
import com.absk.rtrader.indicators.Renko;
import com.absk.rtrader.utils.TickerUtil;

@RestController
@RequestMapping(path = "/optimize")
public class OptimizationController {

	@Autowired
	Util upstoxUtil;
	
	@Autowired
	Renko r;
	
	@Autowired
	TickerUtil util;
	
	@Autowired
	Optimizer optimizer;
	
	@CrossOrigin(origins = "http://localhost:3000")
	@GetMapping("/")
	public Double getData(){	
		optimizer.optimize(upstoxUtil.getHistoricalOHLC());
		return 0.0;
	}
}

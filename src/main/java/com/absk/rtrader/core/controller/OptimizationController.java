package com.absk.rtrader.core.controller;


import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.absk.rtrader.core.constants.CoreConstants;
import com.absk.rtrader.core.indicators.Renko;
import com.absk.rtrader.core.models.OHLC;
import com.absk.rtrader.core.models.Ticker;
import com.absk.rtrader.core.services.OptimizationService;
import com.absk.rtrader.core.services.TimeframeTransformationService;
import com.absk.rtrader.core.utils.TickerUtil;
import com.absk.rtrader.exchange.upstox.Util;

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
	OptimizationService os;
	
	@CrossOrigin(origins = CoreConstants.FRONTEND_BASE_URI)
	@GetMapping("/{date}")
	public Double getData(@PathVariable String date){	
		
		ArrayList<Ticker> tickArr = new ArrayList<Ticker>();
		OHLC[] data = upstoxUtil.getHistoricalOHLC();
		TimeframeTransformationService tss = new TimeframeTransformationService();
		tss.setSourceTimeframe(60);
		tss.setDestinationTimeframe(600);
		for(int i=0;i<data.length;i++){
			Ticker tick = tss.transform(util.convertToTicker(data[i]));
			if(tick != null) {
				tickArr.add(tick);
				tick= null;
			}
		}		
			os.optimize(tickArr);	
		return 0.0;
	}
}

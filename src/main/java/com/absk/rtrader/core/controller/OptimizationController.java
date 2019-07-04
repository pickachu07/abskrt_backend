package com.absk.rtrader.core.controller;


import java.util.ArrayList;
import java.util.List;

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
import com.absk.rtrader.core.repositories.TickerRepositoryCustom;
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
	
	@Autowired
	private TickerRepositoryCustom tickerRepository;
	
	@CrossOrigin(origins = CoreConstants.FRONTEND_BASE_URI)
	@GetMapping("/exchange/{queryDate}")
	public Double getDatafromExchange(@PathVariable String queryDate){	
		
		List<Ticker> tickArr = new ArrayList<Ticker>();
		
		OHLC[] data = upstoxUtil.getHistoricalOHLC(queryDate);
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
	
	@CrossOrigin(origins = CoreConstants.FRONTEND_BASE_URI)
	@GetMapping("/db/{date}/{ticker}/{startTimeframe}/{endTimeframe}/{startBrickSize}/{endBrickSize}")
	public Double getDatafromDBAndOptimize(@PathVariable String date,@PathVariable String ticker,@PathVariable String startTimeframe,@PathVariable String endTimeframe,@PathVariable String startBrickSize,@PathVariable String endBrickSize){	
		
		long sTf = Long.parseLong(startTimeframe);
		long eTf = Long.parseLong(endTimeframe);
		long sBS = Long.parseLong(startBrickSize);
		long eBS = Long.parseLong(endBrickSize);
		
		//ArrayList<Ticker> tickArr = new ArrayList<Ticker>();
		List<Ticker> data = tickerRepository.findByTimestampAndTicker(date,ticker);
		
		if(data.size() <= 20) {//TODO:replace with a constant
			System.out.println("Data insufficient! Select any other date");//TODO: replace with an exception and error log
		}
		OptimizationService os = new OptimizationService(ticker,sBS,eBS,sTf,eTf);
		
		os.optimize(data);
		
		/*TimeframeTransformationService tss = new TimeframeTransformationService();
		tss.setSourceTimeframe(startTimeframe);
		tss.setDestinationTimeframe(endTimeframe);
		for(int i=0;i<data.size();i++){
			Ticker tick = tss.transform(data.get(i));
			if(tick != null) {
				tickArr.add(tick);
				tick= null;
			}
		}		
			os.optimize(tickArr);
			*/	
		return 0.0;
	}
	
}

package com.absk.rtrader.core.controller;

import java.util.ArrayList;
import java.util.Set;

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
import com.absk.rtrader.core.services.TimeframeTransformationService;
import com.absk.rtrader.core.services.TradingSession;
import com.absk.rtrader.core.utils.TickerUtil;
import com.absk.rtrader.exchange.upstox.Util;
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
	
	@Autowired
	TimeframeTransformationService tss;
	
	@CrossOrigin(origins = CoreConstants.FRONTEND_BASE_URI)
	@GetMapping("/{queryDate}/{bs}")
	public ArrayList<Ticker> getOrigData(@PathVariable String queryDate,@PathVariable String bs){
		
		ArrayList<Ticker> tickArr = new ArrayList<Ticker>();
		OHLC[] data = upstoxUtil.getHistoricalOHLC(queryDate);
		ts.processAllData(data,Double.parseDouble(bs));//TODO: add brick size and ticker as params
		for(int i=0;i<data.length;i++){
			Ticker tick = util.convertToTicker(data[i]);
			if(tick != null) {
				tickArr.add(tick);
				tick= null;
			}
		}
		ArrayList<Ticker> renkoBricks =ts.getRenkoBricks();
		ts.reset();
		return renkoBricks;
	}
	
	
	@CrossOrigin(origins = CoreConstants.FRONTEND_BASE_URI)
	@GetMapping("/transform/{queryDate}")
	public ArrayList<Ticker> getData(@PathVariable String queryDate){
		
		ArrayList<Ticker> tickArr = new ArrayList<Ticker>();
		OHLC[] data = upstoxUtil.getHistoricalOHLC(queryDate);
		//TradingSession ts = new TradingSession();
		//ts.processAllData(data,4);//TODO: add brick size and ticker as params
		//TimeframeTransformationService tss = new TimeframeTransformationService();
		tss.setSourceTimeframe(60);
		tss.setDestinationTimeframe(600);
		for(int i=0;i<data.length;i++){
			Ticker tick = tss.transform(util.convertToTicker(data[i]));
			if(tick != null) {
				tickArr.add(tick);
				tick= null;
			}
		}
		//return ts.getRenkoBricks();
		return tickArr;
	}
	
	@CrossOrigin(origins = CoreConstants.FRONTEND_BASE_URI)
	@GetMapping("/trans")
	public  Set<Cell<String, Integer, Double>> getTransactions(){
		//OHLC[] data = upstoxUtil.getHistoricalOHLC();
		//ts.processAllData(data);
		return ts.getTransactions();
	}
}

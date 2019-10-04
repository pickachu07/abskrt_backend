package com.absk.rtrader.core.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.absk.rtrader.core.indicators.Renko;
import com.absk.rtrader.core.models.Ticker;
import com.absk.rtrader.core.utils.TickerUtil;
import com.absk.rtrader.exchange.upstox.Util;

@Component
public class OptimizationService {

	@Autowired
	Util upstoxUtil;
	
	@Autowired
	TickerUtil tickerUtil;
	
	
	private String tickerName;
	private long brick_size_start;
	private long brick_size_end;
	private int brick_size_incriment;
	private long timeframeStart;
	private long timeframeEnd;
	private int timeframeIncrement;
	
	public OptimizationService(String tickerName, long brick_size_start, long brick_size_end, long timeframeStart, long timeframeEnd) {
		super();
		this.tickerName = tickerName;
		this.brick_size_start = brick_size_start;
		this.brick_size_end = brick_size_end;
		this.brick_size_incriment = 1;
		this.timeframeStart=timeframeStart;
		this.timeframeEnd=timeframeEnd;
		this.timeframeIncrement=10;
	}
	
	public OptimizationService() {
		this.tickerName = "BANKNIFTY";
		this.brick_size_start = 1;
		this.brick_size_end = 14;
		this.brick_size_incriment =  1;
		this.timeframeStart=60;
		this.timeframeEnd=600;
		this.timeframeIncrement=60;
	}
	
	public long optimize(List<Ticker> data) {
		
		HashMap<Long,Double> profitTable = new HashMap<Long,Double>();
		HashMap<Long,HashMap<Long,Double>> timeframeProfitTable = new HashMap<Long,HashMap<Long,Double>>();
		//Renko rInstance = renko.getInstance();
		TradingSession session = null;
		for(long currentTimeframe=this.timeframeStart;currentTimeframe<=this.timeframeEnd;currentTimeframe=(currentTimeframe + this.timeframeIncrement)) {
			
			TimeframeTransformationService tss = new TimeframeTransformationService(this.timeframeStart);
			List<Ticker> transformedData = tss.transform(data);
			
			for(long currentBrickSize = this.brick_size_start; currentBrickSize < brick_size_end;currentBrickSize+=this.brick_size_incriment) {
				
				session = new TradingSession(this.tickerName,1,currentBrickSize);
	
				ArrayList<Ticker> tickArr = null;
				Renko renko = new Renko();
				renko.setBrickSize(currentBrickSize);
				for(int i=0;i<transformedData.size();i++) {
					if(transformedData.get(i)!= null) {
						tickArr = renko.drawRenko(transformedData.get(i),currentBrickSize);
			    		session.processData(tickArr);
			    		session.calculateProfit();
			    		tickArr = null;
					}	
				}
				profitTable.put(currentBrickSize, session.getProfit());
				
				System.out.println("timeframe: "+currentTimeframe+" brickSize: "+currentBrickSize+" profit: "+session.getProfit());
				session.resetTempProfit(); 
				session = null;
				renko = null;
			}
			timeframeProfitTable.put(currentTimeframe, profitTable);
			profitTable.clear();
		}
		for(Map.Entry<Long,HashMap<Long,Double>> tfentry : timeframeProfitTable.entrySet()){
			for(Map.Entry<Long,Double> entry : tfentry.getValue().entrySet()){
	            System.out.println("TimeFrame(in Seconds): "+tfentry.getKey()+" Brick Size = " + entry.getKey() + ", Profit(in pips) = " + entry.getValue()); 
			}}
		
		return 0;
	}
	
	
	

	public long getTimeframeStart() {
		return timeframeStart;
	}

	public void setTimeframeStart(long timeframeStart) {
		this.timeframeStart = timeframeStart;
	}

	public long getTimeframeEnd() {
		return timeframeEnd;
	}

	public void setTimeframeEnd(long timeframeEnd) {
		this.timeframeEnd = timeframeEnd;
	}

	public long getTimeframeIncrement() {
		return timeframeIncrement;
	}

	public void setTimeframeIncrement(int timeframeIncrement) {
		this.timeframeIncrement = timeframeIncrement;
	}

	public String getTickerName() {
		return tickerName;
	}

	public void setTickerName(String tickerName) {
		this.tickerName = tickerName;
	}

	public long getBrick_size_start() {
		return brick_size_start;
	}

	public void setBrick_size_start(long brick_size_start) {
		this.brick_size_start = brick_size_start;
	}

	public long getBrick_size_end() {
		return brick_size_end;
	}

	public void setBrick_size_end(long brick_size_end) {
		this.brick_size_end = brick_size_end;
	}

	public int getBrick_size_incriment() {
		return brick_size_incriment;
	}

	public void setBrick_size_incriment(int brick_size_incriment) {
		this.brick_size_incriment = brick_size_incriment;
	}
}

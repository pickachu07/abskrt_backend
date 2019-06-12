package com.absk.rtrader.core.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.absk.rtrader.core.indicators.Renko;
import com.absk.rtrader.core.models.OHLC;
import com.absk.rtrader.core.models.Ticker;
import com.absk.rtrader.core.utils.TickerUtil;
import com.absk.rtrader.exchange.upstox.Util;

@Service
public class OptimizationService {

	@Autowired
	Util upstoxUtil;
	
	@Autowired
	TickerUtil tickerUtil;
	
	@Autowired
	Renko renko;
	
	@Autowired
	TimeframeTransformationService tss;
	
	private String tickerName;
	private long brick_size_start;
	private long brick_size_end;
	private int brick_size_incriment;
	
	public OptimizationService(String tickerName, long brick_size_start, long brick_size_end, int brick_size_incriment) {
		super();
		this.tickerName = tickerName;
		this.brick_size_start = brick_size_start;
		this.brick_size_end = brick_size_end;
		this.brick_size_incriment = brick_size_incriment;
	}
	
	public OptimizationService() {
		this.tickerName = "BANKNIFTY";
		this.brick_size_start = 1;
		this.brick_size_end = 14;
		this.brick_size_incriment =  1;
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

	public long optimize(OHLC[] ohlc) {
		HashMap<Long,Double> profitTable = new HashMap<Long,Double>();
		Renko rInstance = renko.getInstance();
		TradingSession session = null;
		for(long currentBrickSize = this.brick_size_start; currentBrickSize < brick_size_end;currentBrickSize+=this.brick_size_incriment) {
			session = new TradingSession(this.tickerName,1,this.brick_size_start);
			ArrayList<Ticker> tickArr = null;
			for(int i=0;i<ohlc.length;i++) {
				tickArr = rInstance.drawRenko(tickerUtil.convertToTicker(ohlc[i]),1);
	    		session.processData(tickArr);
	    		session.calculateProfit();
	    		tickArr = null;
			}
			profitTable.put(currentBrickSize, session.getProfit());
			
			System.out.println("brickSize:"+this.brick_size_start+" profit:"+session.getProfit());
			session = null;
		}
		for(Map.Entry<Long,Double> entry : profitTable.entrySet()){
            System.out.println("Brick Size = " + entry.getKey() + ", Profit(in pips) = " + entry.getValue()); 
		}
		return 0;
	}
}

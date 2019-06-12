package com.absk.rtrader.core.services;

import java.util.Date;

import org.springframework.stereotype.Service;

import com.absk.rtrader.core.models.Ticker;
import com.absk.rtrader.core.models.TickerData;
import com.absk.rtrader.exchange.upstox.constants.UpstoxTicker;

@Service
public class TimeframeTransformationService {

	private int sourceTimeframe;
	private double destinationTimeframe;
	private String tickerName;
	
	private double open;
	private double close;
	private double high;
	private double low;
	private long openTimestamp;
	private long closeTimestamp;
	private long tickCount;
	
	public TimeframeTransformationService(int sourceTimeframe, double destinationTimeframe, String tickerName) {
		super();
		this.sourceTimeframe = sourceTimeframe;
		this.destinationTimeframe = destinationTimeframe;
		this.tickerName = tickerName;
	}
	
	public TimeframeTransformationService() {
		super();
		this.sourceTimeframe = 1;//in seconds
		this.destinationTimeframe = 10;
		this.tickerName= UpstoxTicker.BANK_NIFTY;
	}
	
	public Ticker transform(Ticker tick) {
		
		if(tickCount==0) {
			this.open = tick.getData().getOpen();
			this.openTimestamp = tick.getData().getTimestamp();
			this.high = tick.getData().getHigh();
			this.low = tick.getData().getLow();
		}
		if(tick.getData().getLow() < this.low) {
			this.low = tick.getData().getLow();
		}
		if(tick.getData().getHigh() > this.high){
			this.high = tick.getData().getHigh();
		}
		if(tickCount>=(this.destinationTimeframe/this.sourceTimeframe)) {
			this.close = tick.getData().getClose();
			this.closeTimestamp = tick.getData().getTimestamp();
			incrementTickCount();
			return generateNewTickAndResetParams(open,high,low,close,this.closeTimestamp);
		}
		incrementTickCount();
		return null;
	}
	
	private void incrementTickCount() {
		this.tickCount++;
	}
	
	private Ticker generateNewTickAndResetParams(double o,double h,double l,double c,long t) {
		TickerData data = new TickerData(o,h,l,c,0.0,t,tickerName,"", 0.0,0.0);
		Ticker tick = new Ticker("Transformed time ticker",data,new Date(this.closeTimestamp));
		resetWorkingVars();
		
		return tick;
		
	}
	
	private void resetWorkingVars(){
		this.open=this.close;
		this.close=0F;
		this.high=0F;
		this.low=0F;
		this.openTimestamp = 0L;
		this.closeTimestamp=0L;
	}

	public int getSourceTimeframe() {
		return sourceTimeframe;
	}

	public void setSourceTimeframe(int sourceTimeframe) {
		this.sourceTimeframe = sourceTimeframe;
	}

	public double getDestinationTimeframe() {
		return destinationTimeframe;
	}

	public void setDestinationTimeframe(double destinationTimeframe) {
		this.destinationTimeframe = destinationTimeframe;
	}

	public String getTickerName() {
		return tickerName;
	}

	public void setTickerName(String tickerName) {
		this.tickerName = tickerName;
	}
	
	
	
	
	
}

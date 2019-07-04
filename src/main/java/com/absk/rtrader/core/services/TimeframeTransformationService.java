package com.absk.rtrader.core.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import com.absk.rtrader.core.models.Ticker;
import com.absk.rtrader.core.models.TickerData;
import com.absk.rtrader.exchange.upstox.constants.UpstoxTicker;

@Service
public class TimeframeTransformationService {

	private long sourceTimeframe;
	private long destinationTimeframe;
	private String tickerName;
	
	private double open;
	private double close;
	private double high;
	private double low;
	private long openTimestamp;
	private long closeTimestamp;
	private long tickCount;
	
	public TimeframeTransformationService(long sourceTimeframe, long destinationTimeframe, String tickerName) {
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
	
	public List<Ticker> transform(List<Ticker> sourceTickerList){
		
		List<Ticker> transformedTickerList = new ArrayList<Ticker>();
		for(int i=0;i<sourceTickerList.size();i++) {
			Ticker transformedTick = transform(sourceTickerList.get(i));
			if(transformedTick!=null) {
				transformedTickerList.add(transformedTick);
			}
		}
		return transformedTickerList;
	}
	
	public Ticker transform(Ticker tick) {
		
		if(tickCount==0) {
			this.open = tick.getData().getOpen();
			this.high = tick.getData().getHigh();
			this.low = tick.getData().getLow();
		}
		if(tick.getData().getLow() < this.low) {
			this.low = tick.getData().getLow();
		}
		if(tick.getData().getHigh() > this.high){
			this.high = tick.getData().getHigh();
		}
		if(tickCount>=(this.destinationTimeframe/this.sourceTimeframe)-1) {
			this.close = tick.getData().getClose();
			incrementTickCount();
			return generateNewTickAndResetParams(open,high,low,close,tick.getData().getTimestamp());
		}
		incrementTickCount();
		return null;
	}
	
	private void incrementTickCount() {
		this.tickCount++;
	}
	
	private Ticker generateNewTickAndResetParams(double o,double h,double l,double c,long t) {
		TickerData data = new TickerData(o,h,l,c,0.0,t,tickerName,"", 0.0,0.0);
		Ticker tick = new Ticker("Transformed time ticker",data,new Date(t));
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
		this.tickCount = 0L;
	}

	public long getSourceTimeframe() {
		return sourceTimeframe;
	}

	public void setSourceTimeframe(long sourceTimeframe) {
		this.sourceTimeframe = sourceTimeframe;
	}

	public double getDestinationTimeframe() {
		return destinationTimeframe;
	}

	public void setDestinationTimeframe(long destinationTimeframe) {
		this.destinationTimeframe = destinationTimeframe;
	}

	public String getTickerName() {
		return tickerName;
	}

	public void setTickerName(String tickerName) {
		this.tickerName = tickerName;
	}
	
	
	
	
	
}

package com.absk.rtrader.core.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.absk.rtrader.core.models.Ticker;
import com.absk.rtrader.core.models.TickerData;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class TimeframeTransformationService {

	private long destinationTimeframe;
	
	private double open;
	private double close;
	private double high;
	private double low;
	private long tickCount;
	
	public TimeframeTransformationService(long destinationTimeframe) {
		super();
	
		this.destinationTimeframe = destinationTimeframe;
		
	}
	
	public TimeframeTransformationService() {
		super();
		this.tickCount=1;
		this.destinationTimeframe = 1;
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
		
		if(tickCount==1) {
			this.open = tick.getData().getOpen();
			this.high = tick.getData().getHigh();
			this.low = tick.getData().getLow();
		}
		if(tickCount >= this.destinationTimeframe) {
			this.close = tick.getData().getClose();
			incrementTickCount();
			return generateNewTickAndResetParams(open,high,low,close,tick.getData().getTimestamp(),tick.getData().getSymbol());
		}
		if(tick.getData().getLow() < this.low) {
			this.low = tick.getData().getLow();
		}
		if(tick.getData().getHigh() > this.high){
			this.high = tick.getData().getHigh();
		}
		
		incrementTickCount();
		return null;
	}
	
	private void incrementTickCount() {
		this.tickCount++;
	}
	
	private Ticker generateNewTickAndResetParams(double o,double h,double l,double c,long t,String tickerName) {
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
		this.tickCount = 1L;
	}



	public double getDestinationTimeframe() {
		return destinationTimeframe;
	}

	public void setDestinationTimeframe(long destinationTimeframe) {
		this.destinationTimeframe = destinationTimeframe;
	}
	
}

package com.absk.rtrader.exchange.upstox.services;

import java.math.BigDecimal;
import java.util.UUID;

import com.absk.rtrader.core.interfaces.TickerDataListner;
import com.absk.rtrader.core.models.Ticker;
import com.absk.rtrader.exchange.upstox.utils.UpstoxTickerUtils;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class UpstoxSLAgent implements TickerDataListner {
	String tickerName;
	BigDecimal initialPrice;
	int stopLoss;
	boolean isActive;
	String id;
	
	UpstoxSLService SLServiceSupervisor;
	
	public UpstoxSLService getSLServiceSupervisor() {
		return SLServiceSupervisor;
	}

	public void setSLServiceSupervisor(UpstoxSLService sLServiceSupervisor) {
		SLServiceSupervisor = sLServiceSupervisor;
	}

	
	
	public UpstoxSLAgent(){
	//assign random id
		this.id = UUID.randomUUID().toString();
		System.out.println("SL Agent id: "+this.id);
		isActive = false;
	 }
	
	public String getTicker() {
		return tickerName;
	}



	public String setParams(String ticker, BigDecimal initialPrice, int stopLossPercent) {
		this.tickerName = ticker;
		this.initialPrice = initialPrice;
		this.stopLoss = stopLossPercent;
		return getId();
	}



	public void setTicker(String ticker) {
		this.tickerName = ticker;
	}



	public BigDecimal getInitialPrice() {
		return initialPrice;
	}



	public void setInitialPrice(BigDecimal initialPrice) {
		this.initialPrice = initialPrice;
	}



	public int getStopLoss() {
		return stopLoss;
	}



	public void setStopLoss(int stopLoss) {
		this.stopLoss = stopLoss;
	}



	public boolean isActive() {
		return isActive;
	}



	public String getId() {
		return id;
	}



	//onNext(data){} --> Add stoploss logic --> make it configurable
	@Override
	public void onNext(String data) {
		
		UpstoxTickerUtils util = new UpstoxTickerUtils();
		
		Ticker tick = util.filterTickerBySymbol(data, this.tickerName);
		if(tick == null) {
			System.out.println("Error: Data from exchange does not contain ticker of name:"+this.tickerName);
		}
		//sl business logic
		log.info("Agent: "+getId()+tick.toString());
	}
	
	//start()
	public boolean start() {
		boolean isSuccessful = subscribe();
		if(isSuccessful) {
			isActive = true;
			return true;
		}
		return false;
	}
	
	
	
	// ->subscribe to ticker
	public boolean subscribe() {
		//getHold of UpstoxSLService and call subscribe
		return this.SLServiceSupervisor.subscribeAgentToTickerStream(this.id);
	}
	
	public String resetAgent() {
		this.tickerName = "";
		this.initialPrice = null;
		this.stopLoss = 0;
		this.isActive = false;
		return this.id;
	}

}

package com.absk.rtrader.exchange.upstox.services;

import java.math.BigDecimal;
import java.util.UUID;

import com.absk.rtrader.core.interfaces.TickerDataListner;
import com.absk.rtrader.core.models.Ticker;
import com.absk.rtrader.exchange.upstox.constants.UpstoxFeedTypeConstants;
import com.absk.rtrader.exchange.upstox.utils.UpstoxTickerUtils;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class UpstoxSLAgent implements TickerDataListner {
	String tickerName;
	String exchange;
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

	
	
	public UpstoxSLAgent(UpstoxSLService slService){
	//assign random id
		this.id = UUID.randomUUID().toString();
		System.out.println("SL Agent id: "+this.id);
		isActive = false;
		setSLServiceSupervisor(slService);
	 }
	
	public String getTicker() {
		return tickerName;
	}



	public String setParams(String ticker,String exchange, BigDecimal initialPrice, int stopLossPercent) {
		this.tickerName = ticker;
		this.initialPrice = initialPrice;
		this.stopLoss = stopLossPercent;
		this.exchange = exchange;
		return getId();
	}



	public String getExchange() {
		return exchange;
	}

	public void setExchange(String exchange) {
		this.exchange = exchange;
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
	@SuppressWarnings("unused")
	@Override
	public void onNext(String data) {
		
		UpstoxTickerUtils util = new UpstoxTickerUtils();
		
		Ticker tick = util.filterTickerBySymbol(data, this.tickerName);
		
		if(tick == null) {
			log.error("Error: Data from exchange does not contain ticker of name:"+this.tickerName);
			return;
		}
		log.info("SL Agent: "+getId()+"Ticker received: "+tick.toString());
		//sl business logic
		int buyPrice = initialPrice.intValue();
		int currPrice = (int)tick.getData().getClose();
		if(currPrice <= buyPrice) {
			//sell instantly
			log.info("SL Agent: Sell trigerred on stoploss at price:"+tick.getData().getClose());
			//stop subscribing the symbol
			this.SLServiceSupervisor.subscribeToTicker(this.tickerName, this.exchange, UpstoxFeedTypeConstants.FEEDTYPE_FULL);
		}
		if(currPrice > buyPrice) {
			//increment stoploss
			this.stopLoss += (currPrice-buyPrice);
			log.info("SL AGENT:: SL incremented by"+(currPrice-buyPrice)+" Symbol "+this.tickerName);
		}
		
		log.info("SL Agent: "+getId()+"Ticker received: "+tick.toString());
	}
	
	//start()
	public boolean start() {
		subscribe();
		isActive = true;
		return true;	
	}
	
	// ->subscribe to ticker
	public void subscribe() {
		//getHold of UpstoxSLService and call subscribe
		this.SLServiceSupervisor.subscribeAgentToTickerStream(this.id);
	}
	
	public String resetAgent() {
		this.tickerName = "";
		this.initialPrice = null;
		this.stopLoss = 0;
		this.isActive = false;
		this.exchange = "";
		return this.id;
	}

}

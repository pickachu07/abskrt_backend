package com.absk.rtrader.exchange.upstox.services;

import java.math.BigDecimal;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.absk.rtrader.core.interfaces.TickerDataListner;
import com.absk.rtrader.core.models.Ticker;
import com.absk.rtrader.exchange.upstox.constants.UpstoxFeedTypeConstants;
import com.absk.rtrader.exchange.upstox.utils.UpstoxTickerUtils;


public class UpstoxSLAgent implements TickerDataListner {
	private String tickerName;
	String exchange;
	BigDecimal initialPrice;
	double stopLossPrice;
	int stopLossPercent;
	boolean isActive;
	String id;
	
	UpstoxSLService SLServiceSupervisor;
	
	private static final Logger log = LoggerFactory.getLogger(UpstoxSLAgent.class);

	
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
		this.stopLossPercent = stopLossPercent;
		this.stopLossPrice = Math.ceil(initialPrice.doubleValue() - (initialPrice.doubleValue()*stopLossPercent));
		this.exchange = exchange;
		return getId();
	}



	public String getExchange() {
		return this.exchange;
	}

	public void setExchange(String exchange) {
		this.exchange = exchange;
	}

	public void setTicker(String ticker) {
		this.tickerName = ticker;
	}



	public BigDecimal getInitialPrice() {
		return this.initialPrice;
	}



	public void setInitialPrice(BigDecimal initialPrice) {
		this.initialPrice = initialPrice;
	}



	public int getStopLoss() {
		return this.stopLossPercent;
	}



	public void setStopLoss(int stopLoss) {
		this.stopLossPercent = stopLoss;
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
		double currPrice = tick.getData().getClose();
		if(currPrice <= this.stopLossPrice) {
			//sell instantly
			log.info("SL Agent: Sell trigerred on stoploss at price:"+tick.getData().getClose());
			//stop subscribing the symbol
			this.SLServiceSupervisor.unsubscribeToTicker(this.tickerName, this.exchange, UpstoxFeedTypeConstants.FEEDTYPE_FULL);
			this.resetAgent();
		}
		if(currPrice > initialPrice.doubleValue()) {
			//increment stoploss
			this.stopLossPrice += (currPrice-initialPrice.doubleValue());
			log.info("SL AGENT:: SL incremented by"+ (currPrice-initialPrice.doubleValue())+" Symbol "+this.tickerName);
		}
		
		log.info("SL Agent: "+getId()+"Ticker received: "+tick.toString());//change to debug log
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
		log.info("SL Agent: "+getId()+"Resetting.");//change to debug log
		this.tickerName = "";
		this.initialPrice = null;
		this.stopLossPercent = 0;
		this.stopLossPrice = 0D;
		this.isActive = false;
		this.exchange = "";
		
		return this.id;
	}

}

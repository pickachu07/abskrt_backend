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
	private String agentType;
	private String exchange;
	private BigDecimal initialPrice;
	private double stopLossPrice;
	private int stopLossPercent;
	private boolean isActive;
	private String id;
	private double bufferPrice;
	
	UpstoxSLService SLServiceSupervisor;
	
	private static final Logger log = LoggerFactory.getLogger(UpstoxSLAgent.class);

	
	public UpstoxSLService getSLServiceSupervisor() {
		return SLServiceSupervisor;
	}

	public void setSLServiceSupervisor(UpstoxSLService sLServiceSupervisor) {
		SLServiceSupervisor = sLServiceSupervisor;
	}

	
	
	public UpstoxSLAgent(UpstoxSLService slService,String type){
	//assign random id
		this.id = UUID.randomUUID().toString();
		System.out.println("SL Agent created. Id: "+this.id+", Agent Type: "+type);
		isActive = false;
		setSLServiceSupervisor(slService);
		this.agentType = type;
		this.bufferPrice = 0D;
	 }
	
	public String getTicker() {
		return tickerName;
	}



	public String setParams(String ticker,String exchange, BigDecimal initialPrice, int stopLoss) {
		this.tickerName = ticker;
		this.initialPrice = initialPrice;
		this.stopLossPercent = stopLoss;
		this.stopLossPrice = initialPrice.doubleValue() - stopLossPercent;
		this.exchange = exchange;
		this.bufferPrice = this.initialPrice.doubleValue();//check overflow
		return getId();
	}

	public String setParams(String ticker,String exchange, BigDecimal initialPrice, int stopLoss,String agentType) {
		this.tickerName = ticker;
		this.initialPrice = initialPrice;
		this.stopLossPercent = stopLoss;
		this.stopLossPrice = initialPrice.doubleValue() - stopLossPercent;
		this.exchange = exchange;
		this.agentType = agentType;
		this.bufferPrice = this.initialPrice.doubleValue();//check overflow
		return getId();
	}

	


	public String getAgentType() {
		return agentType;
	}

	public void setAgentType(String agentType) {
		this.agentType = agentType;
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
		this.bufferPrice = this.initialPrice.doubleValue();//check overflow
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
			log.info("SL Agent:"+getId()+" Sell trigerred on stoploss at price:"+tick.getData().getClose()+" for symbol: "+this.tickerName);
			//stop subscribing the symbol
			this.SLServiceSupervisor.unsubscribeAgentFromTickerStream(getId());
			this.SLServiceSupervisor.unsubscribeToTicker(this.tickerName, this.exchange, UpstoxFeedTypeConstants.FEEDTYPE_FULL);
			this.resetAgent();
		}
		if(initialPrice != null && currPrice > this.bufferPrice) {
			//increment stoploss
			this.stopLossPrice += (currPrice-this.bufferPrice);
			this.bufferPrice = currPrice;
			log.info("SL AGENT:: SL incremented by"+ (currPrice-this.bufferPrice)+" Symbol "+this.tickerName);
		}
		
		log.info("SL Agent: "+getId()+"Ticker received: "+tick.toString());//change to debug log
	}
	
	//start()
	public boolean start() {
		subscribe();
		isActive = true;
		log.info("SL Agent: "+getId()+"Starting for symbol: "+this.tickerName+" at price: "+this.initialPrice);
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

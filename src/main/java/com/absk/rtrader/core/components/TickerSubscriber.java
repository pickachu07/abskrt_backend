package com.absk.rtrader.core.components;

import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;

import org.springframework.beans.factory.annotation.Autowired;

import com.absk.rtrader.core.models.Ticker;
import com.absk.rtrader.core.services.TradingSession;
import com.absk.rtrader.exchange.upstox.constants.UpstoxSymbolNames;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TickerSubscriber implements Subscriber<Ticker>{

	private Subscription tickerSubscription;
	
	@Autowired
	private TradingSession tradingSession;
	
	private boolean isNotifiedAlready = false;
	
	@Override
	public void onSubscribe(Subscription subscription) {
		log.info("TickerSubscriber Subscribed! Ready to receive tickers!");
		this.tickerSubscription = subscription;
		tickerSubscription.request(1);//Check effects of increasing size;
		instantiateTradingSession(UpstoxSymbolNames.BANK_NIFTY, 4.0F);//Default
	}

	@Override
	public void onNext(Ticker item) {
		log.info("TickerSubscriber received ticker: "+item.getData().toString());
		tickerSubscription.request(1);
	}

	@Override
	public void onError(Throwable throwable) {
		log.error("Error occured at TickerSubscriber. Message: "+throwable.getMessage());
		
	}

	@Override
	public void onComplete() {
		log.info("TickerSubscribe has received Complete event!");
		
	}
	
	public void notifyOnce() {
		if(!isNotifiedAlready) {
			this.tickerSubscription.request(1);
			this.isNotifiedAlready = true;
		}
	}
	
	public void setParams(String tickerName,float brickSize){
		instantiateTradingSession(tickerName,brickSize);
	}
	
	private void instantiateTradingSession(String tickerName,float brickSize) {
		log.debug("Instantiated trading sessions with TickerName:"+tickerName+" BrickSize: "+brickSize);
		tradingSession.setBrickSize(brickSize);
		tradingSession.setSessionType(1);
		tradingSession.setTickerName(tickerName);
	}

}

package com.absk.rtrader.exchange.upstox.services;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.absk.rtrader.exchange.upstox.constants.UpstoxFeedTypeConstants;
import com.github.rishabh9.riko.upstox.common.models.UpstoxResponse;
import com.github.rishabh9.riko.upstox.feed.FeedService;
import com.github.rishabh9.riko.upstox.feed.models.Subscription;
import com.github.rishabh9.riko.upstox.feed.models.SubscriptionResponse;
import com.github.rishabh9.riko.upstox.feed.models.SymbolSubscribed;

@Service
public class UpstoxFeedServiceImpl {

	@Autowired
	private FeedService feedService;
	
	private static final Logger log = LoggerFactory.getLogger(UpstoxFeedServiceImpl.class);


	public boolean subscribeToTicker(String tickerName,String exchange, String feedType) {

		// TODO: create utils to fetch feedtype and exchange type
		CompletableFuture<UpstoxResponse<SubscriptionResponse>> future = feedService
				.subscribe(feedType, exchange, tickerName);
		try {
			log.info("Subscribing to Ticker:data "+future.get().getData().toString());
			return future.get().getData().isSuccess();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	
	public SubscriptionResponse subscribeToTickerGetDetail(String tickerName,String exchange, String feedType) {

		// TODO: create utils to fetch feedtype and exchange type
		CompletableFuture<UpstoxResponse<SubscriptionResponse>> future = feedService
				.subscribe(feedType, exchange, tickerName);
		try {
			log.info("getting detail of Subscribing to Ticker:data "+future.get().getData().toString());
			return future.get().getData();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public SubscriptionResponse unSubscribeToTickerGetDetail(String tickerName,String exchange, String feedType) {

		// TODO: create utils to fetch feedtype and exchange type
		CompletableFuture<UpstoxResponse<SubscriptionResponse>> future = feedService
				.unsubscribe(feedType, exchange, tickerName);
		try {
			log.info("Getting detail of unSubscribing to Ticker:data "+future.get().getData().toString());
			return future.get().getData();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	

	public boolean unSubscribeToTicker(String tickerName,String exchange, String feedType) {

		// TODO: create utils to fetch feedtype and exchange type
		CompletableFuture<UpstoxResponse<SubscriptionResponse>> future = feedService
				.unsubscribe(feedType, exchange, tickerName);
		try {
			return future.get().getData().isSuccess();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public void fetchSubscribedSymbols(String feedType) {

		// TODO: create utils to fetch feedtype and exchange type
		CompletableFuture<UpstoxResponse<Subscription>> future = feedService
				.symbolsSubscribed(feedType);
		try {
			if(feedType.equalsIgnoreCase(UpstoxFeedTypeConstants.FEEDTYPE_FULL)) {
				 List<SymbolSubscribed> symbolList = future.get().getData().getFull();
				 log.info("Subscribed List Full symbols : "+symbolList.toString());
			}else {
				 List<SymbolSubscribed> symbolList = future.get().getData().getLtp();
				 log.info("Subscribed List LTP symbols : "+symbolList.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
}

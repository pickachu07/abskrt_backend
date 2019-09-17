package com.absk.rtrader.exchange.upstox.services;

import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.absk.rtrader.exchange.upstox.constants.UpstoxExchangeTypeConstants;
import com.absk.rtrader.exchange.upstox.constants.UpstoxFeedTypeConstants;
import com.absk.rtrader.exchange.upstox.constants.UpstoxSymbolNames;
import com.github.rishabh9.riko.upstox.common.models.UpstoxResponse;
import com.github.rishabh9.riko.upstox.feed.FeedService;
import com.github.rishabh9.riko.upstox.feed.models.SubscriptionResponse;

@Service
public class UpstoxFeedServiceImpl {

	@Autowired
	private FeedService feedService;

	public boolean subscribeToTicker(String tickerName, String feedType) {

		// TODO: create utils to fetch feedtype and exchange type
		CompletableFuture<UpstoxResponse<SubscriptionResponse>> future = feedService
				.subscribe(feedType, UpstoxExchangeTypeConstants.NSE_INDEX, tickerName);
		try {
			return future.get().getData().isSuccess();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean unSubscribeToTicker(String tickerName, String feedType) {

		// TODO: create utils to fetch feedtype and exchange type
		CompletableFuture<UpstoxResponse<SubscriptionResponse>> future = feedService
				.unsubscribe(UpstoxFeedTypeConstants.FEEDTYPE_FULL, UpstoxExchangeTypeConstants.NSE_INDEX, UpstoxSymbolNames.BANK_NIFTY);
		try {
			return future.get().getData().isSuccess();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}

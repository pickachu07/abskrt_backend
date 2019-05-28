package com.absk.rtrader.exchange.upstox.services;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.absk.rtrader.exchange.upstox.constants.FeedTypeConstants;
import com.absk.rtrader.exchange.upstox.constants.ExchangeTypes;
import com.absk.rtrader.exchange.upstox.constants.UpstoxTicker;
import com.github.rishabh9.riko.upstox.common.models.UpstoxResponse;
import com.github.rishabh9.riko.upstox.feed.FeedService;
import com.github.rishabh9.riko.upstox.feed.models.SubscriptionResponse;
import com.github.rishabh9.riko.upstox.users.UserService;

@Service
public class UpstoxFeedServiceImpl {

	@Autowired
    private UserService userService;
	
	@Autowired
	private FeedService feedService;

	public boolean subscribeToTicker(String tickerName, String feedType) {
		
		//TODO: create utils to fetch feedtype and exchange type
		CompletableFuture<UpstoxResponse<SubscriptionResponse>> future = feedService.subscribe(FeedTypeConstants.FEEDTYPE_FULL, ExchangeTypes.NSE_INDEX,UpstoxTicker.BANK_NIFTY);
		try {
			return future.get().getData().isSuccess();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
}

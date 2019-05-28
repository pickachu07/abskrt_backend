package com.absk.rtrader.exchange.upstox.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.rishabh9.riko.upstox.feed.FeedService;
import com.github.rishabh9.riko.upstox.users.UserService;
import com.absk.rtrader.core.constants.FeedTypeConstants;

@Service
public class UpstoxFeedServiceImpl {

	@Autowired
    private UserService userService;
	
	@Autowired
	private FeedService feedService;

	public void subscribeToTicker(String tickerName, FeedTypeConstants feedTypeConstants) {
		
		//TODO: create utils to fetch type and 
		feedService.subscribe(feedTypeConstants, exchange, symbolsCsv)
	}
}

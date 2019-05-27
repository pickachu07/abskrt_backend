package com.absk.rtrader.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.github.rishabh9.riko.upstox.common.RikoRetryPolicyFactory;
import com.github.rishabh9.riko.upstox.common.UpstoxAuthService;
import com.github.rishabh9.riko.upstox.feed.FeedService;
import com.github.rishabh9.riko.upstox.historical.HistoricalService;
import com.github.rishabh9.riko.upstox.login.LoginService;
import com.github.rishabh9.riko.upstox.orders.OrderService;
import com.github.rishabh9.riko.upstox.users.UserService;
import com.github.rishabh9.riko.upstox.websockets.WebSocketService;

@Configuration
public class RicoConfiguration implements WebMvcConfigurer {

	 	@Autowired
	    private UpstoxAuthService upstoxAuthService;

	    @Bean
	    public LoginService loginService() {
	        return new LoginService(upstoxAuthService, new RikoRetryPolicyFactory());
	    }

	    @Bean
	    public FeedService feedService() {
	        return new FeedService(upstoxAuthService, new RikoRetryPolicyFactory());
	    }

	    @Bean
	    public HistoricalService historicalService() {
	        return new HistoricalService(upstoxAuthService, new RikoRetryPolicyFactory());
	    }

	    @Bean
	    public UserService userService() {
	        return new UserService(upstoxAuthService, new RikoRetryPolicyFactory());
	    }

	    @Bean
	    public WebSocketService webSocketService() {
	        return new WebSocketService(upstoxAuthService, new RikoRetryPolicyFactory());
	    }

	    @Bean
	    public OrderService orderService() {
	        return new OrderService(upstoxAuthService, new RikoRetryPolicyFactory());
	    }
}
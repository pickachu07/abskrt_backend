package com.absk.rtrader.exchange.upstox.services;

import static com.absk.rtrader.exchange.upstox.constants.RikoConstants.TOKEN;
import static com.absk.rtrader.exchange.upstox.constants.RikoConstants.TOKEN_EXPIRY;
import static com.absk.rtrader.exchange.upstox.constants.RikoConstants.TOKEN_TYPE;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.absk.rtrader.core.models.UpstoxAccessToken;
import com.absk.rtrader.core.repositories.UpstoxAccessTokenRepository;
import com.absk.rtrader.exchange.upstox.utils.Cache;
import com.github.rishabh9.riko.upstox.login.models.AccessToken;

@Service
public class UpstoxAccessTokenManagementService {

	@Autowired
	UpstoxAccessTokenRepository tokenRepo; 
	
	@Autowired
	Cache cache;
	
	private static final Logger log = LoggerFactory.getLogger(UpstoxAccessTokenManagementService.class);

	
	UpstoxAccessTokenManagementService(){}
	
	
	
	public boolean isAuthenticated() {
		//cache hit
		if(cache.getAccessToken().isPresent()) {
			String token = cache.getAccessToken().get().getToken();
			if(!token.equalsIgnoreCase(TOKEN))return true;
		}
		return false;
	}
	
	
	public String getValidAccessToken() {
		
		//cache hit
		if(cache.getAccessToken().isPresent()) {
			String token = cache.getAccessToken().get().getToken();
			if(!token.equalsIgnoreCase(TOKEN))return token;
		}
		
		//cache miss fetch from db
		List<UpstoxAccessToken> tokenList = tokenRepo.findAccessToken(System.currentTimeMillis());
		
		if(tokenList !=null && tokenList.size() > 0) {
			
			//add entry to cache
			final AccessToken accessToken = new AccessToken();
			final String token = tokenList.get(0).getCode();
            accessToken.setType(TOKEN_TYPE);
            accessToken.setExpiresIn(TOKEN_EXPIRY);
            accessToken.setToken(token);
			return token;
		}
		
		return null;
	}
	
	
	public boolean storeAccessToken(AccessToken token) {
		UpstoxAccessToken utoken = new UpstoxAccessToken(token.getExpiresIn(),token.getType(),token.getToken());
		UpstoxAccessToken savedToken = tokenRepo.save(utoken);
		return (savedToken == null ? false : true);
		
	}
	
	public boolean storeAccessToken(String code,String type) {
		UpstoxAccessToken token = new UpstoxAccessToken(calculateTokenExpiringTime(), type, code);
		UpstoxAccessToken savedToken = tokenRepo.save(token);
		return (savedToken == null ? false : true);
	}
	
	
	private Long calculateTokenExpiringTime() {
		Calendar cal = Calendar.getInstance();
		int currentHour = cal.get(Calendar.HOUR_OF_DAY);
		int todaysDate = cal.get(Calendar.DAY_OF_MONTH);
		if(currentHour >=7 ) {
			cal.add(Calendar.DAY_OF_MONTH, todaysDate + 1); //add a day
			cal.set(Calendar.HOUR_OF_DAY, 6); //set hour to last hour
			cal.set(Calendar.MINUTE, 59); //set minutes to last minute
			cal.set(Calendar.SECOND, 59); //set seconds to last second
		}
		log.info("current time: "+new Date()+". Calculated expiring time: "+cal.getTime());
		return cal.getTimeInMillis();
	}
	
	
}

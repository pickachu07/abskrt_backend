package com.absk.rtrader.exchange.upstox;


import org.apache.tomcat.util.codec.binary.Base64;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import com.absk.rtrader.core.models.OHLC;
import com.absk.rtrader.core.models.Ticker;
import com.absk.rtrader.core.utils.ConfigUtil;
import com.absk.rtrader.core.utils.DateUtils;
import com.absk.rtrader.core.utils.TickerUtil;
import com.absk.rtrader.exchange.upstox.constants.UpstoxExchangeTypeConstants;
import com.absk.rtrader.exchange.upstox.constants.UpstoxSymbolNames;
import com.absk.rtrader.exchange.upstox.models.HistoricalAPIResponse;
import com.absk.rtrader.exchange.upstox.utils.Cache;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@Component
public class Util {

	/*@Autowired
	AccessTokenRepository atr;
	*/
	@Autowired
	Cache cache;
	
	@Autowired
	ConfigUtil config;
	
	@Autowired
	TickerUtil tickerUtil;
	
	@Autowired
	RestTemplateBuilder restTemplateBuilder;
	
	private static final Logger logger = LoggerFactory.getLogger(Util.class);
	
	
	/*blic AccessToken saveAuthCode(String code) {
		
		System.out.println("Api code:"+code);
		String token =getAccessToken(code);
		System.out.println("Acess token: "+token);
		AccessToken accessToken = new AccessToken(dateFormat.format(new Date()),token);
		atr.deleteAll();
		atr.save(accessToken);
		return accessToken; 
	}*/
	
	/*public boolean isAccessTokenValid() {
		if((getCurrentAccessToken().length()>0 )&&  (atr.getByDate(dateFormat.format(new Date())).size() > 0))return true;
		cache.getAccessToken()cache.;
		return false;
	}*/
	
	@SuppressWarnings("unused")
	private String getAccessToken(String code) {
		RestTemplate restTemplate = new RestTemplate();
		
		String plainCreds = "l8Tuqu26Uk7I2PA9IaAaD9zXrMzRxadS9oF0o3cQ:hc8g215w1c";
		byte[] plainCredsBytes = plainCreds.getBytes();
		byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
		String base64Creds = new String(base64CredsBytes);

		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Basic " + base64Creds);
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("x-api-key", config.getApiKey());
		
		JSONObject request = new JSONObject();
		try {
			request.put("code", code);
			request.put("grant_type", "authorization_code");
			request.put("redirect_uri", config.getRedirectUrl());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		HttpEntity<String> entity = new HttpEntity<String>(request.toString(),headers);
	
		ResponseEntity<String> response = restTemplate.postForEntity("https://api.upstox.com/index/oauth/token",entity, String.class);
		//result.toString();
		Gson gson = new Gson();
		JsonElement element = gson.fromJson (response.getBody(), JsonElement.class);
		JsonObject jsonObj = element.getAsJsonObject();
		System.out.println("Acess token response: "+jsonObj.get("access_token"));
        return jsonObj.get("access_token").getAsString();
	}
	 
	
	public String getCurrentAccessToken(){
		
		return cache.getAccessToken().get().getToken();
		
	}
	public ModelAndView initAuthentication(){
		String apiKey = config.getApiKey();
		String redirectUrl = config.getRedirectUrl();
		final String authURL = "https://api.upstox.com/index/dialog/authorize?apiKey="+apiKey+"&redirect_uri="+redirectUrl+"&response_type=code";
		return new ModelAndView("redirect:" + authURL);
			
	}
	
	public Ticker getFeed()
	{
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		String token = getCurrentAccessToken();
		headers.set("x-api-key", config.getApiKey());
		headers.setBearerAuth(token);
		HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
        ResponseEntity<Ticker> response = restTemplate.exchange("https://api.upstox.com/live/feed/now/nse_index/NIFTY_BANK/full",HttpMethod.GET,entity, Ticker.class);//https://api.upstox.com/live/feed/now/nse_eq/SBIN/fullhttp://localhost:3000
        Ticker ticker = (Ticker) response.getBody();
        System.out.println("Current Close::::::"+ticker.getData().getClose());
        logger.info(ticker.toString());
        tickerUtil.saveTicker(ticker);
        return ticker;
        
        
	}
	public OHLC[] getHistoricalOHLC(String exchange, String ticker, String inDate) {
		RestTemplate restTemplate = new RestTemplate();
		
		HttpHeaders headers = new HttpHeaders();
		String token = "";
		headers.set("x-api-key", config.getApiKey());
		//if(!isAccessTokenValid()) {return null;}
		//else {
			token = getCurrentAccessToken();
		//}
			
		headers.setBearerAuth(token);
		HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
        ResponseEntity<HistoricalAPIResponse> response = restTemplate.exchange("https://api.upstox.com/historical/"+exchange+"/"+ticker+"/1?start_date="+inDate+"&end_date="+inDate,HttpMethod.GET,entity, HistoricalAPIResponse.class);//https://api.upstox.com/live/feed/now/nse_eq/SBIN/fullhttp://localhost:3000
        HistoricalAPIResponse apiResponse = (HistoricalAPIResponse) response.getBody();
		
		return apiResponse.getData();
	}
	
	public OHLC[] getHistoricalOHLC(String inDate) {
		
		return getHistoricalOHLC(UpstoxExchangeTypeConstants.NSE_INDEX,UpstoxSymbolNames.BANK_NIFTY,DateUtils.convertDateFormat("yyyy-MM-dd", "dd-MM-yyyy", inDate));
	}
	
	
}

package com.absk.rtrader.exchange.upstox;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.tomcat.util.codec.binary.Base64;
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

import com.absk.rtrader.model.AccessToken;
import com.absk.rtrader.model.HistoricalAPIResponse;
import com.absk.rtrader.model.OHLC;
import com.absk.rtrader.model.Ticker;
import com.absk.rtrader.repository.AccessTokenRepository;
import com.absk.rtrader.utils.ConfigUtil;
import com.absk.rtrader.utils.TickerUtil;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
@Component
public class Util {

	@Autowired
	AccessTokenRepository atr;
	
	@Autowired
	ConfigUtil config;
	
	@Autowired
	TickerUtil tickerUtil;
	
	@Autowired
	RestTemplateBuilder restTemplateBuilder;
	
	private static final Logger logger = LoggerFactory.getLogger(Util.class);
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
	
	public AccessToken saveAuthCode(String code) {
		
		System.out.println("Api code:"+code);
		String token =getAccessToken(code);
		System.out.println("Acess token: "+token);
		AccessToken accessToken = new AccessToken(dateFormat.format(new Date()),token);
		atr.deleteAll();
		atr.save(accessToken);
		return accessToken; 
	}
	
	public boolean isAccessTokenValid() {
		if((getCurrentAccessToken().length()>0 )&& (atr.getByDate(dateFormat.format(new Date())).size() > 0))return true;
		return false;
	}
	
	private String getAccessToken(String code) {
		RestTemplate restTemplate = new RestTemplate();
		
		String plainCreds = "2DgWnzxnRk1TGBQZgdLH37lRcCtCLWE72oWsD9Tn:7025xno292";
		byte[] plainCredsBytes = plainCreds.getBytes();
		byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
		String base64Creds = new String(base64CredsBytes);

		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Basic " + base64Creds);
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("x-api-key", "2DgWnzxnRk1TGBQZgdLH37lRcCtCLWE72oWsD9Tn");
		
		JSONObject request = new JSONObject();
		request.put("code", code);
		request.put("grant_type", "authorization_code");
		request.put("redirect_uri", "http://localhost:8080/auth/");
		
		
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
		List<AccessToken> tokenList = atr.getByDate(dateFormat.format(new Date()));
		if(tokenList.size() ==  0)return "";
		String token = tokenList.get(0).getCode();
		return token;
		
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
		headers.set("x-api-key", "2DgWnzxnRk1TGBQZgdLH37lRcCtCLWE72oWsD9Tn");
		headers.setBearerAuth("bbc535e2b2542cc332d175e021c035b193c58314");
		HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
        ResponseEntity<Ticker> response = restTemplate.exchange("http://localhost:3000/data",HttpMethod.GET,entity, Ticker.class);//https://api.upstox.com/live/feed/now/nse_eq/SBIN/fullhttp://localhost:3000
        Ticker ticker = (Ticker) response.getBody();
        logger.info(ticker.toString());
      //tickerUtil.saveTicker(ticker);
        return ticker;
        
        
	}
	public OHLC[] getHistoricalOHLC() {
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		String token = "";
		headers.set("x-api-key", config.getApiKey());
		if(!isAccessTokenValid()) {return null;}
		else {
			token = getCurrentAccessToken();
		}
		headers.setBearerAuth(token);
		HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
        ResponseEntity<HistoricalAPIResponse> response = restTemplate.exchange("https://api.upstox.com/historical/nse_index/NIFTY_BANK/1?start_date=14-05-2019&end_date=14-05-2019",HttpMethod.GET,entity, HistoricalAPIResponse.class);//https://api.upstox.com/live/feed/now/nse_eq/SBIN/fullhttp://localhost:3000
        HistoricalAPIResponse apiResponse = (HistoricalAPIResponse) response.getBody();
		
		return apiResponse.getData();
	}
	
	
}

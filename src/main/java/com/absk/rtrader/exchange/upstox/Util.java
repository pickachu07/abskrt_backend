package com.absk.rtrader.exchange.upstox;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import com.absk.rtrader.model.ApiCode;
import com.absk.rtrader.model.HistoricalAPIResponse;
import com.absk.rtrader.model.OHLC;
import com.absk.rtrader.model.Ticker;
import com.absk.rtrader.repository.ApiCodeRepository;
import com.absk.rtrader.utils.ConfigUtil;
import com.absk.rtrader.utils.TickerUtil;
@Component
public class Util {

	@Autowired
	ApiCodeRepository acr;
	
	@Autowired
	ConfigUtil config;
	
	@Autowired
	TickerUtil tickerUtil;
	
	private static final Logger logger = LoggerFactory.getLogger(Util.class);
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
	
	public ApiCode saveAuthCode(String code) {
		String todaysDate = dateFormat.format(new Date());
		acr.deleteAll();
		return acr.save(new ApiCode(todaysDate, code));
		
	}
	
	public String getCurrentApiCode(){
		
		String apiCode = acr.getByDate(dateFormat.format(new Date())).get(0).getCode();
		
		return apiCode;
		
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
		headers.setBearerAuth("061e35025bdeb27bd9cc0afc8e3de060d1798372");
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
		headers.set("x-api-key", "2DgWnzxnRk1TGBQZgdLH37lRcCtCLWE72oWsD9Tn");
		headers.setBearerAuth("061e35025bdeb27bd9cc0afc8e3de060d1798372");
		HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
        ResponseEntity<HistoricalAPIResponse> response = restTemplate.exchange("https://api.upstox.com/historical/nse_eq/AXISBANK/1",HttpMethod.GET,entity, HistoricalAPIResponse.class);//https://api.upstox.com/live/feed/now/nse_eq/SBIN/fullhttp://localhost:3000
        HistoricalAPIResponse apiResponse = (HistoricalAPIResponse) response.getBody();
		
		return apiResponse.getData();
	}
	
	
}

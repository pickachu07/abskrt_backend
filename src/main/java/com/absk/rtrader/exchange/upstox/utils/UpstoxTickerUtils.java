package com.absk.rtrader.exchange.upstox.utils;

import java.util.Date;

import com.absk.rtrader.core.models.Ticker;
import com.absk.rtrader.core.models.TickerData;

public class UpstoxTickerUtils {

	
	public UpstoxTickerUtils() {
		
	}
	
	
	 public Ticker parseTicker(String tickerAsString){
	    	String[] tickerItems =  tickerAsString.split(",");
	        Long timestamp = Long.parseLong(tickerItems[0]);
	        String exchange = tickerItems[1];
	        String symbol = tickerItems[2];
	        double ltp = Double.parseDouble(tickerItems[3]);
	       // double open = Double.parseDouble(tickerItems[4]);
	        //double high = Double.parseDouble(tickerItems[5]);
	        //double low = Double.parseDouble(tickerItems[6]);
	        //double close = Double.parseDouble(tickerItems[7]);
	        double yHigh = Double.parseDouble(tickerItems[8]);
	        double yLow = Double.parseDouble(tickerItems[9]);
	        
	        TickerData td = new TickerData(ltp,ltp,ltp,ltp,0.0,timestamp,exchange,symbol,yHigh,yLow);
	        return new Ticker("Realtime Feed",td,new Date(timestamp));
	              
	    }
	 
	 public Ticker filterTickerBySymbol(String dataString, String symbol) {
		 
		 String[] tickerItems =  dataString.split(";");
		 for(String tickerItem :  tickerItems) {
			 Ticker tick = parseTicker(tickerItem);
			 if (tick.getData().getSymbol().equalsIgnoreCase(symbol))return tick;
		 }
		 return null;
		 
		 
	 }
	
}

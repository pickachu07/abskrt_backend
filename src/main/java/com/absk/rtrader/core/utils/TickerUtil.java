package com.absk.rtrader.core.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.absk.rtrader.core.models.OHLC;
import com.absk.rtrader.core.models.Ticker;
import com.absk.rtrader.core.models.TickerData;
import com.absk.rtrader.core.repositories.TickerRepository;
import com.absk.rtrader.exchange.upstox.constants.UpstoxStrikeTypeConstants;

@Component
public class TickerUtil {

	@Autowired
	private TickerRepository tickerRepo;
	
	@Autowired
	ConfigUtil configUtil;
	
	Random rand = new Random();

	private static final Logger log = LoggerFactory.getLogger(TickerUtil.class);

    //private static final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
    //private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
	
	
	public void createAndSaveMockTicker() {
		/*Ticker currentTicker = new Ticker("SBIN",rand.nextInt(10000),rand.nextInt(10000), rand.nextInt(10000), rand.nextInt(10000), rand.nextInt(1000), dateFormat.format(new Date()), timeFormat.format(new Date()));
        saveTicker(currentTicker);
        log.debug("Saved Mock ticker: ", currentTicker.toString());*/
	}

	public void saveTicker(Ticker ticker) {
		tickerRepo.save(ticker);
		log.debug("Saved Ticker: ", ticker.toString());
		
	}
	
	public Ticker getTickerFromOpenClose(double open,double close,String symbol,String message) {
		TickerData data = null;
		if(open < close) {
			 data = new TickerData(open,close,open,close,0.0,new Date().getTime(),"",symbol,0.0,0.0);
		}else {
			//open >= close
			data = new TickerData(open,open,close,close,0.0,new Date().getTime(),"",symbol,0.0,0.0);
		}
		return new Ticker(message,data,new Date());
		
	}
	
	
	public OHLC convertToOHLC(Ticker tick) {
		return new OHLC(tick.getData().getOpen(),tick.getData().getHigh(),tick.getData().getLow(),tick.getData().getClose(),tick.getData().getVolume(), tick.getData().getTimestamp());
	}
	
	public Ticker convertToTicker(OHLC candle) {	
		TickerData data= new TickerData(candle.getOpen(), candle.getHigh(), candle.getLow(), candle.getClose(), candle.getVolume(), candle.getTimestamp(), null, null, 0, 0);
		Ticker tick = new Ticker("Converted from OHLC", data, new Date(candle.getTimestamp()));
		return tick;
	}
	
	public ArrayList<Ticker> toTickerArray(OHLC[] ohlc){
		ArrayList<Ticker> tickArr = new ArrayList<Ticker>();
		for(int count=0;count<ohlc.length;count++) {
			tickArr.add(convertToTicker(ohlc[count]));
		}
		return tickArr;
	}
	public ArrayList<Double> getPriceArrayByPriceType(ArrayList<Ticker> tickerArray, String priceType) {
		
		if(tickerArray.size()<1)return null;
		ArrayList<Double> priceArray = new ArrayList<Double>();
		double priceValue;
		for(int count = 0; count < tickerArray.size() ; count++) {
			if(priceType.equalsIgnoreCase("close")) {
				priceValue = tickerArray.get(count).getData().getClose();
			}
			else if(priceType.equalsIgnoreCase("open")) {
				priceValue = tickerArray.get(count).getData().getOpen();
			}
			else {
				priceValue = tickerArray.get(count).getData().getOpen();
			}
			priceArray.add(priceValue);
		}
		return priceArray;
	}
	
	
	
	public ArrayList<Ticker> renkoPricesToTickerArray(ArrayList<Double> renkoPrices,String exchange,String symbol) {
		ArrayList<Ticker> tickerArray = new ArrayList<Ticker>();
		
		if(renkoPrices!= null && renkoPrices.size() > 1)
		{
			for(int count=0;count<renkoPrices.size()-1;count++) {
				double open = renkoPrices.get(count);
				double close = renkoPrices.get(count+1);
				double high;
				double low;
				if(close >open ) {
					high = close;
					low = open;
				}else {
					low = close;
					high = open;
				}
				TickerData tickData = new TickerData(open,high,low,close,0F,new Date().getTime(),exchange,symbol,0F,0F);
				tickerArray.add(new Ticker("Historical Renko",tickData, new Date()));
			}
			return tickerArray;
			
		}
		return null;
	}
	
	/*
	 * Calculate Call/Put Strike from current price
	 * In  : Current Price of index  : Double
	 * In  : Consecutive Diff between two Strike : int
	 * In  : call/put : String
	 * Out : Closest Strike : String
	 */
	public String getClosestStrikePrice(double currentPrice,int sDiff ,String strikeType) {
		
		String prefix = configUtil.getBNPrefix();
		
		int q = (int)currentPrice/sDiff;
		int r = (int)currentPrice % sDiff;
		
		if(r <= (sDiff/2)) {
			return (strikeType.equalsIgnoreCase(UpstoxStrikeTypeConstants.CALL) ? prefix + (q*sDiff) + "CE": prefix + (q*sDiff) + "PE");
		}else {
			return (strikeType.equalsIgnoreCase(UpstoxStrikeTypeConstants.CALL) ? prefix + ((q+1)*sDiff) + "CE": prefix + ((q+1)*sDiff) + "PE");
		}
	}
	
	
	
}

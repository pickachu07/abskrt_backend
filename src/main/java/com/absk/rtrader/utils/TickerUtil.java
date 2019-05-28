package com.absk.rtrader.utils;

import java.util.Date;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.absk.rtrader.model.OHLC;
import com.absk.rtrader.model.Ticker;
import com.absk.rtrader.model.TickerData;
import com.absk.rtrader.repository.TickerRepository;

@Component
public class TickerUtil {

	@Autowired
	private TickerRepository tickerRepo;
	
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
	
	public OHLC convertToOHLC(Ticker tick) {
		return new OHLC(tick.getData().getOpen(),tick.getData().getHigh(),tick.getData().getLow(),tick.getData().getClose(),tick.getData().getVolume(), tick.getData().getTimestamp());
	}
	
	public Ticker convertToTicker(OHLC candle) {	
		TickerData data= new TickerData(candle.getOpen(), candle.getHigh(), candle.getLow(), candle.getClose(), candle.getVolume(), candle.getTimestamp(), null, null, 0, 0);
		Ticker tick = new Ticker("Converted from OHLC", data, new Date(candle.getTimestamp()));
		return tick;
	}
	
	
}

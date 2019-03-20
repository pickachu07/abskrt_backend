package com.absk.rtrader.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.absk.rtrader.model.Ticker;
import com.absk.rtrader.repository.TickerRepository;

@Component
public class TickerUtil {

	@Autowired
	private TickerRepository tickerRepo;
	
	Random rand = new Random();

	private static final Logger log = LoggerFactory.getLogger(TickerUtil.class);

    private static final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
	
	
	public void createAndSaveMockTicker() {
		/*Ticker currentTicker = new Ticker("SBIN",rand.nextInt(10000),rand.nextInt(10000), rand.nextInt(10000), rand.nextInt(10000), rand.nextInt(1000), dateFormat.format(new Date()), timeFormat.format(new Date()));
        saveTicker(currentTicker);
        log.debug("Saved Mock ticker: ", currentTicker.toString());*/
	}

	public void saveTicker(Ticker ticker) {
		tickerRepo.save(ticker);
		log.debug("Saved Ticker: ", ticker.toString());
		
	}
	
	
}

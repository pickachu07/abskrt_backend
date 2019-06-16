package com.absk.rtrader.core.repositories;

import java.util.List;

import com.absk.rtrader.core.models.Ticker;

public interface TickerRepositoryCustom {

	List<Ticker> findByTimestamp(String date);

	List<Ticker> findByTimestampAndTicker(String date, String ticker);
	
	List<Ticker> findByDateTimeAndTicker(String date, String ticker, String startTime, String endTime);
	
	List<Ticker> findAll();
}

package com.absk.rtrader.core.repositories;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.absk.rtrader.core.models.Ticker;

@Repository
public interface TickerRepository extends MongoRepository<Ticker, Long>, TickerRepositoryCustom{

	//List<Ticker> findByTimestampGreaterThan(Date timestamp);
	
	List<Ticker> findByTimestamp(String timestamp);
	
	List<Ticker> findByTimestampAndTicker(String timestamp, String ticker);
	
	//List<Ticker> findByDateAndTickerAndTimeperiod(String date, String ticker,String timeStart,String timeEnd);
	
	List<Ticker> findAll();
	
	@SuppressWarnings("unchecked")
	Ticker save(Ticker tick);
	
	
	
}

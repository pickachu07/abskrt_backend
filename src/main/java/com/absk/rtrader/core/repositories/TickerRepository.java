package com.absk.rtrader.core.repositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.absk.rtrader.core.models.Ticker;

@Repository
public interface TickerRepository extends MongoRepository<Ticker, Long>{

	List<Ticker> findByTimestampGreaterThan(Date timestamp);
	
}

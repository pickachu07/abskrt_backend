package com.absk.rtrader.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.absk.rtrader.model.Ticker;

@Repository
public interface TickerRepository extends MongoRepository<Ticker, Long>{

	
}

package com.absk.rtrader.core.repositories;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.absk.rtrader.core.models.Ticker;

public class TickerRepositoryImpl implements TickerRepositoryCustom {

	private MongoTemplate mongoTemplate;
	
	@Autowired
	public TickerRepositoryImpl(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}
	
	public List<Ticker> findByDateTimeAndTicker(String date, String ticker, String startTime, String endTime){
		final Query query = new Query();
		final DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date startDate;
		Date endDate;
		
		try {
			startDate = formatter.parse(date+" "+startTime);
			endDate   = formatter.parse(date+" "+endTime);
		} catch (ParseException e) {
			startDate = new Date();
			endDate = new Date();
			e.printStackTrace();
		}
		
		if(date != null) {
			query.addCriteria(Criteria.where("timestamp").ne(null).andOperator(
	                Criteria.where("timestamp").gte(startDate),
	                Criteria.where("timestamp").lte(endDate),
	                Criteria.where("data.symbol").is(ticker)
	            ));
			
		}
		
		return mongoTemplate.find(query, Ticker.class);
	}
	
	
	@Override
	public List<Ticker> findByTimestamp(String date) {
		final Query query = new Query();
		final DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Long startDate;
		Long endDate;
		
		try {
			startDate = formatter.parse(date+" 00:00:00").getTime();
			endDate   = formatter.parse(date+" 23:00:00").getTime();
		} catch (ParseException e) {
			startDate = new Date().getTime();
			endDate = new Date().getTime();
			e.printStackTrace();
		}
		
		if(date != null) {
			query.addCriteria(Criteria.where("data.timestamp").ne(null).andOperator(
	                Criteria.where("data.timestamp").gte(startDate),
	                Criteria.where("data.timestamp").lte(endDate)
	            ));
		}
		
		return mongoTemplate.find(query, Ticker.class);
	}
	
	@Override
	public List<Ticker> findByTimestampAndTicker(String date,String ticker) {
		
		final Query query = new Query();
		final DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date startDate;
		Date endDate;
		
		try {
			startDate = formatter.parse(date+" 00:00:00");
			endDate   = formatter.parse(date+" 23:00:00");
		} catch (ParseException e) {
			startDate = new Date();
			endDate = new Date();
			e.printStackTrace();
		}
		
		if(date != null) {
			query.addCriteria(Criteria.where("timestamp").ne(null).andOperator(
	                Criteria.where("timestamp").gte(startDate),
	                Criteria.where("timestamp").lte(endDate),
	                Criteria.where("data.symbol").is(ticker)
	            ));
			
		}
		
		return mongoTemplate.find(query, Ticker.class);
	}

	@Override
	public List<Ticker> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

	

}

package com.absk.rtrader.core.repositories;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.absk.rtrader.core.models.Ticker;
import com.absk.rtrader.core.utils.DateUtils;

public class TickerRepositoryImpl implements TickerRepositoryCustom {

	private MongoTemplate mongoTemplate;
	
	@Autowired
	public TickerRepositoryImpl(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}
	
	@Override
	public List<Ticker> findByTimestamp(String date) {
		// TODO Auto-generated method stub
		final Query query = new Query();
		final DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date startDate;
		Date endDate;
		try {
			startDate = formatter.parse(date+" 00:00:00");
			endDate = formatter.parse(date+" 23:00:00");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			startDate = new Date();
			endDate = new Date();
			e.printStackTrace();
		}
		if(date != null) {
			query.addCriteria(Criteria.where("timestamp").ne(null).andOperator(
	                Criteria.where("timestamp").gte(startDate),
	                Criteria.where("timestamp").lte(endDate)
	            ));
		}
		
		
		return mongoTemplate.find(query, Ticker.class);
	}

}

package com.absk.rtrader.core.repositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.absk.rtrader.core.models.RTOrder;

public interface RTOrderRepository extends MongoRepository<RTOrder, Long>, RTOrderCustomRepository{

	@SuppressWarnings("unchecked")
	RTOrder save(RTOrder order);
	
	List<RTOrder> findByOrderDate(Date date);
}

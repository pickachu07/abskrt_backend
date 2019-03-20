package com.absk.rtrader.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.absk.rtrader.model.ApiCode;

@Repository
public interface ApiCodeRepository extends MongoRepository<ApiCode, Long>{

	public List<ApiCode> getByDate(String date);
}

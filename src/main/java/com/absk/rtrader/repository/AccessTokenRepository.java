package com.absk.rtrader.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.absk.rtrader.model.AccessToken;

@Repository
public interface AccessTokenRepository extends MongoRepository<AccessToken, Long>{

	public List<AccessToken> getByDate(String string);
}

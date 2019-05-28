package com.absk.rtrader.core.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.github.rishabh9.riko.upstox.login.models.AccessToken;

@Repository
public interface AccessTokenRepository extends MongoRepository<AccessToken, Long>{

	//public List<AccessToken> getByDate(String string);

	@SuppressWarnings("unchecked")
	public AccessToken save(AccessToken accessToken);
}

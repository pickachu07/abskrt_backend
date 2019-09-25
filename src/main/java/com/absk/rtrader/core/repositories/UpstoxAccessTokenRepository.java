package com.absk.rtrader.core.repositories;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.absk.rtrader.core.models.UpstoxAccessToken;

@Repository
public interface UpstoxAccessTokenRepository extends MongoRepository<UpstoxAccessToken, Long>{

	//public List<AccessToken> getByDate(String string);

	@SuppressWarnings("unchecked")
	public UpstoxAccessToken save(UpstoxAccessToken accessToken);
	
	public UpstoxAccessToken findByExpiringtime(long expiringtime);
	
	public UpstoxAccessToken findByType(String type);
	
	public UpstoxAccessToken findByCode(String code);
	
	@Query("{'expiringtime' : { '$gt' : ?0 }}")
	public List<UpstoxAccessToken> findAccessToken(Long dt);
}

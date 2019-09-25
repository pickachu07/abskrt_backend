package com.absk.rtrader.exchange.upstox.services;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.rishabh9.riko.upstox.common.models.UpstoxResponse;
import com.github.rishabh9.riko.upstox.users.UserService;
import com.github.rishabh9.riko.upstox.users.models.Position;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class UpstoxUserServiceImpl {
	
	@Autowired
	UserService userService;

	public List<Position> getPositions() {

		// TODO: create utils to fetch feedtype and exchange type
		CompletableFuture<UpstoxResponse<List<Position>>> positions = userService.getPositions();
		try {
			log.info("Fetching positons for current user : "+positions.get().getData().toString());
			return positions.get().getData();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public boolean isSymbolInPosition() {
		return false;
	}
	
	
	//TODO: audit this
	public Long getQuantity(String symbol) {
		Long totalQuantity = 0L;
		List<Position> positionList = getPositions();
		for(Position p : positionList) {
			totalQuantity += p.getBuyQuantity();
		}
		return totalQuantity;
	}
	
	
	
	
}

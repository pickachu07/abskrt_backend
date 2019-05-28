package com.absk.rtrader.core.repositories;

import java.util.List;

import com.absk.rtrader.core.models.Ticker;

public interface TickerRepositoryCustom {

	List<Ticker> findByTimestamp(String date);
}

package com.absk.rtrader.core.services.interfaces;

import com.github.rishabh9.riko.upstox.orders.models.OrderRequest;

public interface SellServiceInterface {

	public void execute(OrderRequest order);
}


package com.absk.rtrader.exchange.upstox.services;

import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.absk.rtrader.core.services.interfaces.SellServiceInterface;
import com.github.rishabh9.riko.upstox.common.models.UpstoxResponse;
import com.github.rishabh9.riko.upstox.orders.OrderService;
import com.github.rishabh9.riko.upstox.orders.models.Order;
import com.github.rishabh9.riko.upstox.orders.models.OrderRequest;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class UpstoxSellService implements SellServiceInterface{

	@Autowired
	OrderService orderService;
	

	@Async("AsyncTaskExecuter")
	public void execute(OrderRequest order) {
		// TODO Auto-generated method stub
		CompletableFuture<UpstoxResponse<Order>> placedOrder = orderService.placeOrder(order);
		try {
			placedOrder.get().getData().getMessage();
			placedOrder.get().getData().getStatus();
			placedOrder.get().getData().getExchangeTime();
			
		}catch(Exception e) {
			log.error("Error occured while placing sell order with ID: "+order.getOrderId());
			e.printStackTrace();
		}
		
	}
	

}

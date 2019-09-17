package com.absk.rtrader.exchange.upstox.services;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.absk.rtrader.exchange.upstox.constants.UpstoxTransactionTypeConstants;
import com.github.rishabh9.riko.upstox.common.models.UpstoxResponse;
import com.github.rishabh9.riko.upstox.orders.OrderService;
import com.github.rishabh9.riko.upstox.orders.models.Order;
import com.github.rishabh9.riko.upstox.orders.models.OrderRequest;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class UpstoxBuyService {

	@Autowired
	UpstoxSLService sLService;
	
	@Autowired
	OrderService orderService;
	
		
	@Async("AsyncTaskExecuter")
	public void execute(int stopLossPercent, String tickerName, String orderType, String exchangeType, int quantity,String productType, BigDecimal price) {

		//TODO: add some validation here
		OrderRequest order = createOrderRequest(stopLossPercent, tickerName,orderType,exchangeType, quantity, productType, price);
		
		CompletableFuture<UpstoxResponse<Order>> orderCreated =  orderService.placeOrder(order);
		try {
			String orderId = orderCreated.get().getData().getOrderId();
			String orderRequestId = orderCreated.get().getData().getOrderRequestId();
			String exchangeOrderId = orderCreated.get().getData().getExchangeOrderId();
			//TODO: determine if Stoploss agent must be started or not.
			//TODO: save in database
			log.info("Order Placement Successful"+"Order ID | Req ID | exchange Order ID :"+orderId+" | "+orderRequestId+" | "+" | " + exchangeOrderId);
			
		}catch(Exception e){
			e.printStackTrace();
			log.error("Error placing order to Exchange. Details: "+toString());
		}
	}
	
	public OrderRequest createOrderRequest(int stopLossPercent, String tickerName, String orderType, String exchangeType, long quantity,String productType, BigDecimal price) {
		
		log.debug("Creating order request with following details: "+toString());
		OrderRequest order = new OrderRequest();
		order.setTransactionType(UpstoxTransactionTypeConstants.BUY);
		order.setExchange(exchangeType);
		order.setSymbol(tickerName);
		order.setOrderType(orderType);
		order.setQuantity(quantity);
		order.setProduct(productType);
		order.setPrice(price);
		return order;
		
	}
	
	public boolean initStopLoss(String tickerName,BigDecimal price, int stopLossPercent) {
		//get a free agent if available
		UpstoxSLAgent slAgent = sLService.getFreeAgent();
		if(slAgent != null) {
			//set stoploss agent params
			slAgent.setParams(tickerName, price, stopLossPercent);
			
			//start agent
			log.debug("Trying to start SL Agent! with following params:(Ticker Name | Price | Stop Loss Percentage): "+tickerName+" | "+ price.toPlainString()+" | "+stopLossPercent);
			return slAgent.start();
		}else {
			//log: agent not free
			log.info("No Free SL Agent found");
			return false;
		}
	}

	
}

package com.absk.rtrader.exchange.upstox.services;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.absk.rtrader.core.utils.ConfigUtil;
import com.absk.rtrader.exchange.upstox.constants.UpstoxFeedTypeConstants;
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
	UpstoxSLService slService;
	
	@Autowired
	OrderService orderService;
	
	@Autowired
	ConfigUtil config;
	
	
		
	@Async("AsyncTaskExecuter")
	public int execute(int stopLossPercent, String tickerName, String orderType, String exchangeType, int quantity,String productType, BigDecimal price) {

		if(!slService.isAnyAgentFree())return 0;
		
		//TODO: add some validation here
		OrderRequest order = createBuyOrderRequest(tickerName,orderType,exchangeType, quantity, productType, price);
		
		if(config.isTradingEnabled()) {
		CompletableFuture<UpstoxResponse<Order>> orderCreated =  orderService.placeOrder(order);
		try {
			String orderId = orderCreated.get().getData().getOrderId();
			String orderRequestId = orderCreated.get().getData().getOrderRequestId();
			String exchangeOrderId = orderCreated.get().getData().getExchangeOrderId();
			//TODO: determine if Stoploss agent must be started or not.
			//TODO: save in database
			log.info("Order Placement Successful"+"Order ID | Req ID | exchange Order ID :"+orderId+" | "+orderRequestId+" | "+" | " + exchangeOrderId);
			return orderCreated.get().getCode();
		}catch(Exception e){
			e.printStackTrace();
			log.error("Error placing order to Exchange. Details: "+toString());
		}//end of actual trade
	}else{//paper trade
		log.info("*PAPER TRADE* Buying Agent: Placing Buy order: Details: "+order.toString());
			}
		return 200;
		
	}
	
	public OrderRequest createBuyOrderRequest(String tickerName, String orderType, String exchangeType, long quantity,String productType, BigDecimal price) {
		
		
		OrderRequest order = new OrderRequest();
		order.setTransactionType(UpstoxTransactionTypeConstants.BUY);
		order.setExchange(exchangeType);
		order.setSymbol(tickerName);
		order.setOrderType(orderType);
		order.setQuantity(quantity);
		order.setProduct(productType);
		order.setPrice(price);
		log.debug("Creating order request with following details: "+order.toString());
		return order;
		
	}
	
	public boolean initStopLossAgent(String tickerName,String exchange,BigDecimal price, int stopLossPercent) {
		//get a free agent if available
		UpstoxSLAgent slAgent = slService.getFreeAgent();
		if(slAgent != null) {
			//set stoploss agent params
			slAgent.setParams(tickerName,exchange, price, stopLossPercent);
			
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

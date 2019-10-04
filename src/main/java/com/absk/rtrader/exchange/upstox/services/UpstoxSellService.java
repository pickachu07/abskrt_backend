package com.absk.rtrader.exchange.upstox.services;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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


@Service
public class UpstoxSellService {

	@Autowired
	OrderService orderService;
	
	@Autowired
	UpstoxFeedServiceImpl feedService;
	
	@Autowired
	ConfigUtil config;
	
	private static final Logger log = LoggerFactory.getLogger(UpstoxSellService.class);
	

	@Async("AsyncTaskExecuter")
	public void execute(String tickerName, String orderType, String exchangeType, int quantity,String productType, BigDecimal price) {
		
		
		OrderRequest order = createSellOrderRequest(tickerName,orderType,exchangeType, quantity, productType, price);
		
		
		if(config.isTradingEnabled()) {
			CompletableFuture<UpstoxResponse<Order>> placedOrder = orderService.placeOrder(order);
			try {
				placedOrder.get().getData().getMessage();
				placedOrder.get().getData().getStatus();
				placedOrder.get().getData().getExchangeTime();
				
			
			log.info("Selling Agent: Placing Sell order: Details: "+placedOrder.toString());
		
			//unsubscribe ticker
			feedService.unSubscribeToTicker(order.getSymbol(), order.getExchange(), UpstoxFeedTypeConstants.FEEDTYPE_FULL);
		}catch(Exception e) {
			log.error("Error occured while placing sell order with ID: "+order.getOrderId());
			e.printStackTrace();
		}
	}else {//paper trading
		log.info("*PAPER TRADE* Selling Agent: Placing Sell order: Details: "+order.toString());
		feedService.unSubscribeToTicker(order.getSymbol(), order.getExchange(), UpstoxFeedTypeConstants.FEEDTYPE_FULL);
	}
		
	}
	
	
public OrderRequest createSellOrderRequest(String tickerName, String orderType, String exchangeType, long quantity,String productType, BigDecimal price) {
		
		log.debug("Creating sell order request with following details: "+toString());
		OrderRequest order = new OrderRequest();
		order.setTransactionType(UpstoxTransactionTypeConstants.SELL);
		order.setExchange(exchangeType);//FnO
		order.setSymbol(tickerName);//BANKNIFTY...
		order.setOrderType(orderType);//M
		order.setQuantity(quantity);
		order.setProduct(productType);//oco?
		if(price.intValue()>0)order.setPrice(price);
		return order;
		
	}
	

}

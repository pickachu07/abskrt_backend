package com.absk.rtrader.core.models;

import java.time.Instant;
import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import com.github.rishabh9.riko.upstox.orders.models.Order;



@Document(collection = "abskrt_orders")
public class RTOrder{

	@Id
	private String orderId;
	
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	
	private Date orderDate = new Date();
	
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	private Instant timestamp;
	
	private Order orderDetails;

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public Order getOrderDetails() {
		return orderDetails;
	}

	public void setOrderDetails(Order orderDetails) {
		this.orderDetails = orderDetails;
	}

	public Instant getTimeStamp() {
		return timestamp;
	}
	
	public Date getDate() {
		return orderDate;
	}

	public RTOrder(String orderId, Order orderDetails) {
		super();
		this.orderId = orderId;
		this.orderDetails = orderDetails;
	}
	
	public RTOrder() {
		
	}
	

}

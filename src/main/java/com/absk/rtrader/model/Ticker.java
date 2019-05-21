package com.absk.rtrader.model;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;



@Document(collection = "absk_ticker")
public class Ticker{

	@Id
	private String id;
	
	private String message;
	private TickerData data;
	private Date timestamp;
	
	public Ticker() {
	}
	
	public Ticker(String message, TickerData data, Date timestamp) {
		this.message = message;
		this.data = data;
		this.timestamp = timestamp;
	}
	public TickerData getData() {
		return data;
	}
	public Date gettimestamp() {
		return timestamp;
	}
	public String getMessage() {
		return message;
	}
	public void setData(TickerData data) {
		this.data = data;
	}
	public void settimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	@Override
	public String toString() {
		return "Ticker [id=" + id + ", type=" + message + ", data=" + data + ", timestamp=" + timestamp + "]";
	}
	

}

package com.absk.rtrader.model;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "historicalAPIData")
public class HistoricalAPIResponse {

	@Id
	private String id;
	
	private String message;
	private OHLC[] data;
	private Date timestamp;
	
	public HistoricalAPIResponse() {
	}
	
	public HistoricalAPIResponse(String message, OHLC[] data, Date timestamp) {
		this.message = message;
		this.data = data;
		this.timestamp = timestamp;
	}
	public OHLC[] getData() {
		return data;
	}
	public Date gettimestamp() {
		return timestamp;
	}
	public String getMessage() {
		return message;
	}
	public void setData(OHLC[] data) {
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

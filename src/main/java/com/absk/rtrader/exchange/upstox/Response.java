package com.absk.rtrader.exchange.upstox;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Response {

	private int responseCode;
	private String status;
	private String timeStamp;
	private String message;
	private Data data;
	public int getResponseCode() {
		return responseCode;
	}
	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getTimeStamp() {
		return timeStamp;
	}
	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Data getData() {
		return data;
	}
	public void setData(Data data) {
		this.data = data;
	}
	public Response() {
		
	}
	@Override
	public String toString() {
		return "Response [responseCode=" + responseCode + ", status=" + status + ", timeStamp=" + timeStamp
				+ ", message=" + message + ", data=" + data + "]";
	}
	
	
}

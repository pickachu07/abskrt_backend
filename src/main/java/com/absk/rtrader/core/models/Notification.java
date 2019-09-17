package com.absk.rtrader.core.models;


public class Notification {

	private String type;
	private String message;
	public Notification(String type, String message) {
		super();
		this.type = type;
		this.message = message;
	}
	
	public Notification() {
		this.type="Default";
		this.message = "Default Notification sent from Backend.";
	}
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	
}

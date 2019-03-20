package com.absk.rtrader.model;

public class ApiCode {

	private String date;
	private String code;
	@Override
	public String toString() {
		return "ApiCode [date=" + date + ", code=" + code + "]";
	}
	public ApiCode(String date, String code) {
		super();
		this.date = date;
		this.code = code;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
}

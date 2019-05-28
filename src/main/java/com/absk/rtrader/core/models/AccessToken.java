package com.absk.rtrader.core.models;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "abskrt_col_token")
public class AccessToken {

	private String date;
	private String code;
	@Override
	public String toString() {
		return "ApiCode [date=" + date + ", code=" + code + "]";
	}
	public AccessToken(String date, String code) {
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




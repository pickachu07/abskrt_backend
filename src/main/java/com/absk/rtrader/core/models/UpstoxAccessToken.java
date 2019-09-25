package com.absk.rtrader.core.models;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "abskrt_col_token")
public class UpstoxAccessToken {

	
	private Long expiringtime;
	
	private String type;
	
	private String code;

	public UpstoxAccessToken(Long expiringtime, String type, String code) {
		super();
		this.expiringtime = expiringtime;
		this.type = type;
		this.code = code;
	}
	
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Override
	public String toString() {
		return "UpstoxAccessToken [expiringtime=" + expiringtime + ", type=" + type + ", code=" + code + "]";
	}

	

	public Long getExpiringtime() {
		return expiringtime;
	}

	public void setExpiringtime(Long expiringtime) {
		this.expiringtime = expiringtime;
	}


	
}




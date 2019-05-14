package com.absk.rtrader.model;

public class AccessTokenResponse {

	private String access_token;
	private Long expires_in;
	private String token_type;
	public AccessTokenResponse(String access_token, Long expires_in, String token_type) {
		super();
		this.access_token = access_token;
		this.expires_in = expires_in;
		this.token_type = token_type;
	}
	public String getAccess_token() {
		return access_token;
	}
	public void setAccess_token(String access_token) {
		this.access_token = access_token;
	}
	public Long getExpires_in() {
		return expires_in;
	}
	public void setExpires_in(Long expires_in) {
		this.expires_in = expires_in;
	}
	@Override
	public String toString() {
		return "AccessTokenResponse [access_token=" + access_token + ", expires_in=" + expires_in + ", token_type="
				+ token_type + "]";
	}
	
	
	
}

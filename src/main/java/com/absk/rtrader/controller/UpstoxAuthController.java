package com.absk.rtrader.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.absk.rtrader.exchange.upstox.Util;
import com.absk.rtrader.model.AccessToken;
import com.absk.rtrader.repository.AccessTokenRepository;

@RestController
public class UpstoxAuthController {

	@Autowired
	AccessTokenRepository acr;
	
	@Autowired
	Util upstoxUtil;
	
	
	@GetMapping("/auth")
    public String saveApiCode(@RequestParam("code") String code) {
		AccessToken act = upstoxUtil.saveAuthCode(code);
		return act.toString();
    }
	
	@GetMapping("/get-token")
	public String getValidToken(){
		return upstoxUtil.getCurrentAccessToken();
		
	}
}

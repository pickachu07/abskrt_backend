package com.absk.rtrader.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.absk.rtrader.exchange.upstox.Util;
import com.absk.rtrader.model.AccessToken;
import com.absk.rtrader.repository.AccessTokenRepository;
import com.absk.rtrader.utils.ConfigUtil;

@RestController
public class UpstoxAuthController {

	@Autowired
	AccessTokenRepository acr;
	
	@Autowired
	Util upstoxUtil;
	
	@Autowired
	ConfigUtil configUtil;
	
	@GetMapping("/auth")
    public ModelAndView saveApiCode(@RequestParam("code") String code) {
		AccessToken act = upstoxUtil.saveAuthCode(code);
		final String redirURL = "https://abskrt-webapp.azurewebsites.net/settings";
		
		return new ModelAndView("redirect:" + redirURL);
    }
	
	@GetMapping("/initauth")
    public ModelAndView initAuth() {
		
		return upstoxUtil.initAuthentication();
    }
	
	
	@CrossOrigin(origins = "https://abskrt-webapp.azurewebsites.net")
	@GetMapping("/get-auth-data")
	public Map<String, String> getValidToken(){
		HashMap<String, String> authData = new HashMap<>();
		authData.put("client_id", configUtil.getApiKey());
		authData.put("client_secret", configUtil.getApiSecret());
		authData.put("access_token", upstoxUtil.getCurrentAccessToken());
		String isValid = Boolean.toString(upstoxUtil.isAccessTokenValid());
		authData.put("is_token_valid", isValid);
		return authData;
		
	}
}

package com.absk.rtrader.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.absk.rtrader.exchange.upstox.Util;
import com.absk.rtrader.repository.ApiCodeRepository;

@RestController
public class UpstoxAuthController {

	@Autowired
	ApiCodeRepository acr;
	
	@Autowired
	Util upstoxUtil;
	
	
	@GetMapping("/")
    public String saveApiCode(@RequestParam("code") String code) {
		return upstoxUtil.saveAuthCode(code).toString();
    }
	
	@GetMapping("/authenticate")
	public ModelAndView initAuth(){
		return upstoxUtil.initAuthentication();
		
	}
}

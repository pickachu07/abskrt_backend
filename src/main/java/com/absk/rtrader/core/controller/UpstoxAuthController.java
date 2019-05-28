package com.absk.rtrader.core.controller;

import static com.absk.rtrader.core.constants.CoreConstants.FRONTEND_BASE_URI;
import static com.absk.rtrader.core.constants.CoreConstants.FRONTEND_SETTINGS_URI;
import static com.absk.rtrader.exchange.upstox.constants.RikoConstants.GRANT_TYPE;
import static com.absk.rtrader.exchange.upstox.constants.RikoConstants.REDIRECT_URI;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.absk.rtrader.exchange.upstox.Util;
import com.absk.rtrader.exchange.upstox.utils.Cache;
import com.absk.rtrader.utils.ConfigUtil;
import com.github.rishabh9.riko.upstox.login.LoginService;
import com.github.rishabh9.riko.upstox.login.models.AccessToken;
import com.github.rishabh9.riko.upstox.login.models.TokenRequest;

import lombok.extern.log4j.Log4j2;
@Log4j2
@RestController
public class UpstoxAuthController {

	@Autowired
	private Cache cache;
	
	
	/*@Autowired
	AccessTokenRepository atr;
	*/
	@Autowired
	Util upstoxUtil;
	
	@Autowired
	ConfigUtil configUtil;
	
	@Autowired
    private LoginService loginService;
	

	
	@GetMapping("/auth")
    public ModelAndView saveApiCode(@RequestParam("code") String code) {
		final String redirURL = configUtil.getFrontendUrl();
		
		return new ModelAndView("redirect:" + redirURL);
    }
	
	@CrossOrigin(origins = FRONTEND_BASE_URI)
	@GetMapping("/initauth")
    public ModelAndView initAuth() {
		
		return upstoxUtil.initAuthentication();
    }
	
	
	@CrossOrigin(origins = FRONTEND_BASE_URI)
	@GetMapping("/get-auth-data")
	public Map<String, String> getValidToken(){
		HashMap<String, String> authData = new HashMap<String, String>();
		authData.put("client_id", configUtil.getApiKey());
		authData.put("client_secret", configUtil.getApiSecret());
		authData.put("access_token", upstoxUtil.getCurrentAccessToken());
		//String isValid = Boolean.toString(upstoxUtil.isAccessTokenValid());
		//authData.put("is_token_valid", isValid);
		return authData;
		
	}
	
	 @GetMapping(value = "/callback")
	    public ModelAndView callback(@RequestParam(required = false) String code)
	            throws Exception {
	        log.info("Receiving code from Upstox - {}", code);

	        final TokenRequest tokenRequest = new TokenRequest(code, GRANT_TYPE, REDIRECT_URI);
	        try {
	            final AccessToken accessToken = loginService.getAccessToken(tokenRequest).get();
	            // Save 'accessToken' into a database or cache
	            cache.updateAccessToken(accessToken);
	            //atr.save(accessToken);
	        } catch (ExecutionException | InterruptedException e) {
	            log.fatal("Error obtaining access token", e);
	            throw e;
	        }
	        
	        return new ModelAndView("redirect:" + FRONTEND_SETTINGS_URI);
	     
	        
	    }
	
}

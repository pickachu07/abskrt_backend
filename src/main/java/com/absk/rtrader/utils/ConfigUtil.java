package com.absk.rtrader.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

@Configuration
@PropertySource(ignoreResourceNotFound = true, value = "classpath:config.properties")
public class ConfigUtil {

	@Autowired
    private Environment env;

    public String getApiKey() {
        return env.getProperty("api_key");
    }
    
    public String getApiSecret() {
        return env.getProperty("api_secret");
    }
    public String getRedirectUrl() {
        return env.getProperty("redirect_url");
    }
    
    public String getFrontendUrl() {
        return env.getProperty("frontend_url");
    }
    
}

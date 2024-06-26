package com.absk.rtrader.exchange.upstox.utils;


import static com.absk.rtrader.exchange.upstox.constants.RikoConstants.ACCESS_TOKEN;
import static com.absk.rtrader.exchange.upstox.constants.RikoConstants.API_CRED;
import static com.absk.rtrader.exchange.upstox.constants.RikoConstants.API_KEY;
import static com.absk.rtrader.exchange.upstox.constants.RikoConstants.API_SECRET;
import static com.absk.rtrader.exchange.upstox.constants.RikoConstants.TOKEN;
import static com.absk.rtrader.exchange.upstox.constants.RikoConstants.TOKEN_EXPIRY;
import static com.absk.rtrader.exchange.upstox.constants.RikoConstants.TOKEN_TYPE;

import java.util.Optional;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.github.rishabh9.riko.upstox.common.models.ApiCredentials;
import com.github.rishabh9.riko.upstox.login.models.AccessToken;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * Using this cache to represent storage of token into the database.
 * Storing tokens into a database is the correct thing to do.
 * This class is for demonstration of this starter project only.
 */
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class Cache {

    private final LoadingCache<String, Optional<?>> cache;

    public Cache() {
        final CacheLoader<String, Optional<?>> loader = new CacheLoader<>() {
            @Override
            public Optional<?> load(final String key) {
                // You shouldn't be using this class in the first place.
                // If you are still using this class, you need to put logic here for a headless login
                // and return the authentication details.
                // In doing so, the first time retrieval of auth details from cache will be the slowest,
                // because of the headless login.
                switch (key) {
                    case API_CRED:
                        return Optional.of(new ApiCredentials(API_KEY, API_SECRET));
                    case ACCESS_TOKEN:
                        final AccessToken accessToken = new AccessToken();
                        accessToken.setType(TOKEN_TYPE);
                        accessToken.setExpiresIn(TOKEN_EXPIRY);
                        accessToken.setToken(TOKEN);
                        return Optional.of(accessToken);
                    default:
                        return Optional.empty();
                }

            }
        };
        cache = CacheBuilder.newBuilder().build(loader);
    }

    @SuppressWarnings(value = "unchecked")
    public Optional<ApiCredentials> getApiCredentials() {
        return (Optional<ApiCredentials>) cache.getUnchecked(API_CRED);
    }

    public void updateApiCredentials(final ApiCredentials apiCredentials) {
        cache.put(API_CRED, Optional.ofNullable(apiCredentials));
    }

    @SuppressWarnings(value = "unchecked")
    public Optional<AccessToken> getAccessToken() {
        return (Optional<AccessToken>) cache.getUnchecked(ACCESS_TOKEN);
    }

    public void updateAccessToken(final AccessToken accessToken) {
        cache.put(ACCESS_TOKEN, Optional.ofNullable(accessToken));
    }
}

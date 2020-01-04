package com.ben.poc;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.OAuthTokenCredential;
import com.paypal.base.rest.PayPalRESTException;

@Configuration
public class PaypalConfig {
	
	@Value("${paypal.mode}")
	private String mode;
	@Value("${paypal.client.id}")
	private String clientId;
	@Value("${paypal.client.secret}")
	private String clientSecret;
	
	@Bean
	public Map<String, String> configPaypalSdkMode(){
		Map<String, String> configMode = new HashMap<>();
		configMode.put("mode", mode);
		return configMode;
		
	}
	
	@Bean
	public OAuthTokenCredential authTokenCredential() {
		return new OAuthTokenCredential(clientId, clientSecret, configPaypalSdkMode());
		
	}
	
	@Bean
	public APIContext apiContext() throws PayPalRESTException {
		APIContext context = new APIContext(authTokenCredential().getAccessToken());
		context.setConfigurationMap(configPaypalSdkMode());
		return context;
		
	}
	

}

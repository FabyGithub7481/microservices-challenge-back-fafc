package com.account_transactions_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiConfig {

  @Value("${api.client.url}")
  private String apiClientUrl;

  @Value("${api.client.cuenta.url}")
  private String apiClientCuentaUrl;

  public String getApiClientUrl() {
    return apiClientUrl;
  }

  public String getApiClientCuentaUrl() {
    return apiClientCuentaUrl;
  }
}


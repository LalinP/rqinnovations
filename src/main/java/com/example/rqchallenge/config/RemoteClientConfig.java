package com.example.rqchallenge.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Data
@Configuration
@ConfigurationProperties("rq.emp")
@Log4j2
public class RemoteClientConfig {
  private String baseUrl;

  @Bean
  public WebClient webClient() {
    final ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .enable(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT)
        .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
        .enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);

    final ExchangeStrategies exchangeStrategies = ExchangeStrategies.builder()
        .codecs(configurer -> configurer.defaultCodecs()
            .jackson2JsonDecoder(new Jackson2JsonDecoder(mapper)))
        .build();

    return WebClient.builder()
        .baseUrl(baseUrl)
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .exchangeStrategies(exchangeStrategies)
        .build();
  }
}

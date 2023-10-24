package com.example.rqchallenge;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.example.rqchallenge.RqChallengeApplicationTests.TestConfig;
import com.example.rqchallenge.config.RemoteClientConfig;
import com.example.rqchallenge.exception.AuthException;
import com.example.rqchallenge.exception.BadRequestException;
import com.example.rqchallenge.exception.NotFoundException;
import com.example.rqchallenge.exception.ServerException;
import com.example.rqchallenge.inbound.controller.EmployeeService;
import com.example.rqchallenge.model.Employee;
import com.example.rqchallenge.model.EmployeeResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Import({TestConfig.class, JacksonAutoConfiguration.class})
class RqChallengeApplicationTests {

  @Autowired
  private ObjectMapper mapper;
  @Autowired
  @Qualifier("getWebClient")
  private WebClient webClient;
  @Autowired
  @Qualifier("getMockWebServer")
  MockWebServer mockWebServer;

  private EmployeeService employeeService;

  @BeforeEach
  @SneakyThrows
  void beforeEach() throws IOException  {
    employeeService = new EmployeeService(webClient);
  }

  @AfterEach
  void shutDown() throws IOException {
    mockWebServer.shutdown();
  }

  @Test
  void getAllEmployeesSuccess() {
    mockWebServer.url("/");
    mockWebServer.enqueue(
        new MockResponse()
            .setResponseCode(200)
            .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .setBody(allEmployeeList()));

    var products = employeeService.getEmployeeList();
    var code = products.getStatusCode();
    assertEquals(200, code.value());
    assertEquals(2, products.getBody().size());
  }

  @Test
  void throwsBadRequestExceptionWhenHttp400() {
    mockWebServer.url("/");
    mockWebServer.enqueue(
        new MockResponse()
            .setResponseCode(400));
    assertThrows(BadRequestException.class, () -> employeeService.getEmployeeList());
  }

  @Test
  void throwsAuthExceptionWhenHttp403() {
    mockWebServer.url("/");
    mockWebServer.enqueue(
        new MockResponse()
            .setResponseCode(403));
    assertThrows(AuthException.class, () -> employeeService.getEmployeeList());
  }

  @Test
  void throwsNotFoundExceptionWhenHttp404() {
    mockWebServer.url("/");
    mockWebServer.enqueue(
        new MockResponse()
            .setResponseCode(404));
    assertThrows(NotFoundException.class, () -> employeeService.getEmployeeList());
  }

  @Test
  void throwsAuthExceptionWhenHttp401() {
    mockWebServer.url("/");
    mockWebServer.enqueue(
        new MockResponse()
            .setResponseCode(401));
    assertThrows(AuthException.class, () -> employeeService.getEmployeeList());
  }

  @Test
  void throwsServerExceptionWhenHttp500() {
    mockWebServer.url("/");
    mockWebServer.enqueue(
        new MockResponse()
            .setResponseCode(500));
    assertThrows(ServerException.class, () -> employeeService.getEmployeeList());
  }

  private String allEmployeeList() {
    var response = EmployeeResponse.builder()
        .status("success")
        .message("Here is he list of employees")
        .data(List.of(Employee.builder()
                .id("1")
                .employeeAge("61")
                .employeeName("Tiger Nixon")
                .employeeSalary("320800")
                .build(),
            Employee.builder()
                .id("2")
                .employeeAge("63")
                .employeeName("Garrett Winters")
                .employeeSalary("170750")
                .build()
        ))
        .build();
    try {
      return mapper.writeValueAsString(response);
    } catch (JsonProcessingException e) {
      throw new IllegalStateException(e);
    }
  }

  @TestConfiguration
 static class TestConfig {
    @Bean
    public MockWebServer getMockWebServer() {
      return new MockWebServer();
    }

    @Bean
    public WebClient getWebClient(MockWebServer webServer) {
      return WebClient
          .builder()
          .baseUrl(webServer.url("/").toString())
          .build();
    }
  }
}

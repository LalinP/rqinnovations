package com.example.rqchallenge;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.rqchallenge.RqChallengeApplicationTests.TestConfig;
import com.example.rqchallenge.exception.AuthException;
import com.example.rqchallenge.exception.BadRequestException;
import com.example.rqchallenge.exception.NotFoundException;
import com.example.rqchallenge.exception.ServerException;
import com.example.rqchallenge.inbound.controller.EmployeeService;
import com.example.rqchallenge.model.Employee;
import com.example.rqchallenge.model.EmployeeResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
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
  void beforeEach() {
    employeeService = new EmployeeService(webClient);
  }


  @Test
  void getAllEmployeesSuccess() {
    mockWebServer.url("/");
    mockWebServer.enqueue(
        new MockResponse()
            .setResponseCode(200)
            .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .setBody(employeeSubList()));

    var empDetails = employeeService.getEmployeeList();
    var code = empDetails.getStatusCode();
    assertEquals(200, code.value());
    assertEquals(2, empDetails.getBody().size());
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

  @Test
  void getEmployeeByIdSuccess() {
    mockWebServer.url("/");
    mockWebServer.enqueue(
        new MockResponse()
            .setResponseCode(200)
            .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .setBody(getEmployeeByIdResponse("1")));

    var empDetails = employeeService.getEmployeeById("1");
    var code = empDetails.getStatusCode();
    assertEquals(200, code.value());
  }

  @Test
  void getHighestSalaryOfEmployeesListSuccess() {

    mockWebServer.url("/");
    mockWebServer.enqueue(
        new MockResponse()
            .setResponseCode(200)
            .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .setBody(getHighestSalaryOfEmployeesListResponse()));

    var empDetails = employeeService.getHighestSalaryOfEmployees();
    var code = empDetails.getStatusCode();
    assertEquals(320800, empDetails.getBody());
    assertEquals(200, code.value());
  }

  @Test
  void deleteEmployeeByIdSuccess() {
    mockWebServer.url("/");
    mockWebServer.enqueue(
        new MockResponse()
            .setResponseCode(200)
            .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .setBody("{\"status\":\"success\",\"data\":\"2\",\"message\":\"Successfully! Record has been deleted\"}"));

    var products = employeeService.deleteEmployeeById("2");
    var code = products.getStatusCode();
    assertEquals("{\"status\":\"success\",\"message\":\"Successfully! Record has been deleted\"}", products.getBody());
    assertEquals(200, code.value());
  }


  @Test
  void createEmployeeSuccess() throws IOException {

    mockWebServer.url("/");
    mockWebServer.enqueue(
        new MockResponse()
            .setResponseCode(200)
            .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .setBody(createEmployeeResponse()));

    var empDetails = employeeService.createEmployee(newEmployee());
    var code = empDetails.getStatusCode();
    assertEquals("success", empDetails.getBody());
    assertEquals(200, code.value());
  }

  private String getHighestSalaryOfEmployeesListResponse() {
    return employeeSubList();
  }

  private Map<String, Object> newEmployee() {
    Map<String, Object> map = new HashMap<>();
    map.put("name", "Bilbo Baggins");
    map.put("age", "45");
    map.put("salary", 5000);
    return map;
  }
  private String createEmployeeResponse() {
    var response = EmployeeResponse.builder()
        .status("success")
        .message("Successfully! Record has been added.")
        .data(List.of(Employee.builder()
                .id("1262")
                .build()
        ))
        .build();
    try {
      return mapper.writeValueAsString(response);
    } catch (JsonProcessingException e) {
      throw new IllegalStateException(e);
    }
  }
  private String employeeSubList() {
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

  private String allEmployees() throws IOException {
    File file = new File("src/test/resources/all_employees.json");
    List<Employee> employeeList = mapper.readValue(file, new TypeReference<>() {
    });

    var response = EmployeeResponse.builder()
        .status("success")
        .message("Here is the list of employees")
        .data(employeeList)
        .build();
    try {
      return mapper.writeValueAsString(response);
    } catch (JsonProcessingException e) {
      throw new IllegalStateException(e);
    }
  }

  private String getEmployeeByIdResponse(String id) {
    var response = EmployeeResponse.builder()
        .status("success")
        .message("Here is the employee details")
        .data(List.of(Employee.builder()
            .id(id)
            .employeeAge("61")
            .employeeName("Tiger Nixon")
            .employeeSalary("320800")
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

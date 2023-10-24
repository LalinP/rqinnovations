package com.example.rqchallenge.inbound.controller;

import java.util.List;
import java.util.Map;

import com.example.rqchallenge.config.RemoteClientConfig;
import com.example.rqchallenge.exception.AuthException;
import com.example.rqchallenge.exception.BadRequestException;
import com.example.rqchallenge.exception.InformationNotFoundException;
import com.example.rqchallenge.exception.NotFoundException;
import com.example.rqchallenge.exception.ServerException;
import com.example.rqchallenge.model.Employee;
import com.example.rqchallenge.model.EmployeeResponse;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeService {

  public static final String ERR_MSG_COULD_NOT_FETCH_THE_DATA = "Could not fetch the data ";
  public static final String URI_EMPLOYEES = "/employees";
  private final WebClient webClient;

  public ResponseEntity<List<Employee>> getEmployeeList() {
    var response = webClient
        .get().uri(URI_EMPLOYEES)
        .accept(MediaType.APPLICATION_JSON)
        .retrieve()
        .onStatus(HttpStatus::isError,
        res -> switch (res.rawStatusCode()){
          case 400 -> Mono.error(new BadRequestException("bad request : check payload"));
          case 401, 403 -> Mono.error(new AuthException("Authentication Error "));
          case 404 -> Mono.error(new NotFoundException("You sure you're looking for the right thing? "));
          case 500 -> Mono.error(new ServerException("Server is messing up a bit"));
          default -> Mono.error(new Exception("something went wrong"));
        })
        .bodyToMono(EmployeeResponse.class)
        .block();

    if(response == null){
      throw new InformationNotFoundException(ERR_MSG_COULD_NOT_FETCH_THE_DATA);
    }

    List<Employee> employeeList = List.of();
    if(isNotEmpty(response)) {
      employeeList = response.getData();
    }
    return new ResponseEntity<>(employeeList, HttpStatus.OK);
  }

  private void handleExceptions(int rawStatusCode) {
    switch (rawStatusCode) {
      case 400 -> Mono.error(new BadRequestException("bad request : check payload"));
      case 401, 403 -> Mono.error(new AuthException("Authentication Error "));
      case 404 -> Mono.error(new NotFoundException("You sure you're looking for the right thing? "));
      case 500 -> Mono.error(new ServerException("Server is messing up a bit"));
      default -> Mono.error(new Exception("something went wrong"));

    }
  }
  public ResponseEntity<Employee> getEmployeeById(String id) {
    var response = webClient
        .get()
        .uri("/employee/{id}", id)
        .accept(MediaType.APPLICATION_JSON)
        .retrieve()
        .bodyToMono(EmployeeResponse.class)
        .block();

    if(response == null){
      throw new InformationNotFoundException(ERR_MSG_COULD_NOT_FETCH_THE_DATA);
    }

    return new ResponseEntity(response.getData(), HttpStatus.OK);
  }

  public ResponseEntity<Integer> getHighestSalaryOfEmployees() {
    var response = webClient
        .get().uri(URI_EMPLOYEES)
        .accept(MediaType.APPLICATION_JSON)
        .retrieve()
        .bodyToMono(EmployeeResponse.class)
        .block();

    if(response == null){
      throw new InformationNotFoundException(ERR_MSG_COULD_NOT_FETCH_THE_DATA);
    }

        var  highestSalaryOptional = response.getData().stream()
        .map(employee -> Integer.parseInt(employee.getEmployeeSalary()))
        .max(Integer::compare).orElseThrow(()-> new InformationNotFoundException("salary details are not available"));

    return new ResponseEntity(highestSalaryOptional, HttpStatus.OK);
  }

  public ResponseEntity<String> deleteEmployeeById(String id) {

    var response = webClient
        .delete()
        .uri("/delete/{id}", id)
        .accept(MediaType.APPLICATION_JSON)
        .retrieve()
        .bodyToMono(String.class)
        .block();

    if(response == null){
      throw new InformationNotFoundException(ERR_MSG_COULD_NOT_FETCH_THE_DATA);
    }

    JsonObject jsonObject = new JsonObject();
    if (isNotEmpty(response)) {
      jsonObject = new JsonParser().parse(response).getAsJsonObject();
      jsonObject.remove("data");
    }
    return new ResponseEntity<>(jsonObject.toString(), HttpStatus.OK);
  }

  private String formatResponse(String response) {
    JsonObject jsonObject = new JsonObject();
    if (isNotEmpty(response)) {
      jsonObject = new JsonParser().parse(response.toString()).getAsJsonObject();
      jsonObject.remove("data");
    }
    return jsonObject.toString();
  }

  public ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames(){

    var response = webClient
        .get().uri(URI_EMPLOYEES)
        .accept(MediaType.APPLICATION_JSON)
        .retrieve()
        .bodyToMono(EmployeeResponse.class)
        .block();

    if(response == null){
      throw new InformationNotFoundException(ERR_MSG_COULD_NOT_FETCH_THE_DATA);
    }

    List<String> employeeList = null;
    if (isNotEmpty(response) && response.getData() != null) {
        employeeList = response
            .getData()
            .stream()
            .sorted((emp1, emp2) -> Integer.compare(Integer.parseInt(emp2.employeeSalary), Integer.parseInt(emp1.employeeSalary)))
            .limit(10)
            .map(employee -> employee.employeeName)
            .collect(toList());
      }
    return new ResponseEntity<>(employeeList, HttpStatus.OK);
  }

  public ResponseEntity<String> createEmployee(Map<String, Object> employeeInput) {

    Employee body = Employee.builder()
        .employeeName(String.valueOf(employeeInput.get("name")))
        .employeeAge(String.valueOf(employeeInput.get("age")))
        .employeeSalary(String.valueOf(employeeInput.get("salary")))
        .build();

    var response = webClient
        .post()
        .uri("/create")
        .accept(MediaType.APPLICATION_JSON)
        .bodyValue(BodyInserters.fromValue(body))
        .retrieve()
        .bodyToMono(EmployeeResponse.class)
        .block();
    if(response == null){
      throw new InformationNotFoundException(ERR_MSG_COULD_NOT_FETCH_THE_DATA);
    }
    return new ResponseEntity<>(response.getStatus(), HttpStatus.OK);
  }
}

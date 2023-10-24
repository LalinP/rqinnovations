package com.example.rqchallenge.inbound.controller;

import java.util.List;
import java.util.Map;

import com.example.rqchallenge.employees.IEmployeeController;
import com.example.rqchallenge.model.Employee;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class EmployeeCrudController implements IEmployeeController{

private final EmployeeService employeeService;

@Override
  public ResponseEntity<List<Employee>> getAllEmployees()  {

    return employeeService.getEmployeeList();
  }


  public ResponseEntity<List<Employee>> getEmployeesByNameSearch(String searchString) {
    return null;
  }

@Override
  public ResponseEntity<Employee> getEmployeeById(String id) {
    return employeeService.getEmployeeById(id);
  }


  public ResponseEntity<Integer> getHighestSalaryOfEmployees() {
    return employeeService.getHighestSalaryOfEmployees();
  }


  public ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames() {
    return employeeService.getTopTenHighestEarningEmployeeNames();
  }


  public ResponseEntity<String> createEmployee(Map<String, Object> employeeInput) {
    return employeeService.createEmployee(employeeInput);
  }

  @SneakyThrows
  public ResponseEntity<String> deleteEmployeeById(String id) {
    return employeeService.deleteEmployeeById(id);
  }
}

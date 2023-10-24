package com.example.rqchallenge.employees;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.example.rqchallenge.model.Employee;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public interface IEmployeeController {

    @GetMapping("/employees")
    ResponseEntity<List<Employee>> getAllEmployees() throws IOException;

    @GetMapping("/search/{searchString}")
    ResponseEntity<List<Employee>> getEmployeesByNameSearch(@PathVariable String searchString);

    @GetMapping("/employee/{id}")
    ResponseEntity<Employee> getEmployeeById(@PathVariable String id);

    @GetMapping("/employee/highestSalary")
    ResponseEntity<Integer> getHighestSalaryOfEmployees();

    @GetMapping("/employee/topTenHighestEarningEmployeeNames")
    ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames();

    @PostMapping("/employee")
    ResponseEntity<String> createEmployee(@RequestBody Map<String, Object> employeeInput);

    @DeleteMapping("/delete/{id}")
    ResponseEntity<String> deleteEmployeeById(@PathVariable String id);

}

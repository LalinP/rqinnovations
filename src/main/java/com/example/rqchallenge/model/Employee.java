package com.example.rqchallenge.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class Employee {
  @JsonProperty("id")
  public String id;
  @JsonProperty("employee_name")
  public String employeeName;
  @JsonProperty("employee_salary")
  public String employeeSalary;
  @JsonProperty("employee_age")
  public String employeeAge;
  @JsonProperty("profile_image")
  public String profileImage;
}

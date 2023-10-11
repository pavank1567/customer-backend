package com.stalwart.customer.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerDTO {
    Integer id;
    String name;
    String email;
    Gender gender;
    int age;
    List<String> roles;
    String username;

}

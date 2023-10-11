package com.stalwart.customer.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerRegistrationRequest {
    private String name;
    private String email;
    private int age;
    private String password;
    private Gender gender;

}

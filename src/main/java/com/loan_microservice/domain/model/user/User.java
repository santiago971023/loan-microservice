package com.loan_microservice.domain.model.user;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public class User {

    private Long id;
    private String name;
    private String lastname;
    private String email;
    private BigDecimal salary;

    public User() {
    }

    public User(Long id, String name, String lastname, String email, BigDecimal salary) {
        this.id = id;
        this.name = name;
        this.lastname = lastname;
        this.email = email;
        this.salary = salary;
    }



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }
}

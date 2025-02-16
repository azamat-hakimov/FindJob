package com.portfolio.demo.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "worktables")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class WorkTable {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String workName;
    private String description;
    private String experience;
    private String location;
    private String salary;


    @OneToOne
    @JoinColumn(name = "employee_id")
    private User employee;


    public WorkTable() {
    }

    public Long getId() {
        return id;
    }


    public void setId(Long id) {
        this.id = id;
    }


    public String getWorkName() {
        return workName;
    }


    public void setWorkName(String workName) {
        this.workName = workName;
    }
    

    public String getDescription() {
        return description;
    }


    public void setDescription(String description) {
        this.description = description;
    }


    public String getExperience() {
        return experience;
    }


    public void setExperience(String experience) {
        this.experience = experience;
    }


    public String getLocation() {
        return location;
    }


    public void setLocation(String location) {
        this.location = location;
    }


    public String getSalary() {
        return salary;
    }


    public void setSalary(String salary) {
        this.salary = salary;
    }


    public User getEmployee() {
        return employee;
    }


    public void setEmployee(User employee) {
        this.employee = employee;
    }
    
}

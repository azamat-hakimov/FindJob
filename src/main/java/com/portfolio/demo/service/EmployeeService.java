package com.portfolio.demo.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.portfolio.demo.model.JobTable;
import com.portfolio.demo.model.Picture;
import com.portfolio.demo.model.SavedThing;
import com.portfolio.demo.model.User;
import com.portfolio.demo.model.WorkTable;
import com.portfolio.demo.model.myenums.ThingType;
import com.portfolio.demo.model.myenums.UserIs;
import com.portfolio.demo.repository.JobTableRepository;
import com.portfolio.demo.repository.PictureRepository;
import com.portfolio.demo.repository.SavedThingRepository;
import com.portfolio.demo.repository.UserRepository;
import com.portfolio.demo.repository.WorkTableRepository;

@Service
public class EmployeeService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WorkTableRepository workTableRepository;

    @Autowired
    private JobTableRepository jobTableRepository;

    @Autowired
    private SavedThingRepository savedThingRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PictureRepository pictureRepository;

    //creating an employee
    public void createEmployee(User employee) {
        if (employee != null){
            if (!usernameTaken(employee.getUsername())){
                employee.setPassword(passwordEncoder.encode(employee.getPassword()));
                userRepository.save(employee);
                System.out.println("Created employee: " + employee.getFirstName());
            }
        }
        System.out.println("Employee object is might be null while creating employee!");
    }

    public void updateEmployee(Long id, User updatedEmployee) {
        Optional<User> oldEmployeeOptional = userRepository.findById(id);
        if (oldEmployeeOptional.isPresent()) {
            User oldEmployee = oldEmployeeOptional.get();
            
            // Only update if the values are not null or empty
            if (updatedEmployee.getFirstName() != null && !updatedEmployee.getFirstName().isEmpty()) {
                oldEmployee.setFirstName(updatedEmployee.getFirstName());
            }
            if (updatedEmployee.getLastName() != null && !updatedEmployee.getLastName().isEmpty()) {
                oldEmployee.setLastName(updatedEmployee.getLastName());
            }
            if (updatedEmployee.getPhoneNumber() != null && !updatedEmployee.getPhoneNumber().isEmpty()) {
                oldEmployee.setPhoneNumber(updatedEmployee.getPhoneNumber());
            }
            if (updatedEmployee.getGender() != null) {
                oldEmployee.setGender(updatedEmployee.getGender());
            }
    
            // Save the updated employee
            userRepository.save(oldEmployee);
            System.out.println("Updated employee : " + oldEmployee.getFirstName());

        }
        System.out.println("Employee couldn't updated!");
    }
    

    //creating an work table for employee
    public void createWorkTableForEmployee(String username, WorkTable workTable){
        User employee = getEmployee(username);
        if (employee != null && workTable != null){
            employee.setWorkTable(workTable);
            workTable.setEmployee(employee);
            workTableRepository.save(workTable);
            System.out.println("Table created for employee.");
        }

        System.out.println("Couldn't created table for employee cause of not provided info!");
    }

    public void updateWorkTableOfEmployee(String username, WorkTable workTable){
        User employee = getEmployee(username);
        if (employee != null){
            WorkTable oldWorkTable = employee.getWorkTable();
            oldWorkTable.setWorkName(workTable.getWorkName());
            oldWorkTable.setDescription(workTable.getDescription());
            oldWorkTable.setExperience(workTable.getExperience());
            oldWorkTable.setLocation(workTable.getLocation());
            oldWorkTable.setSalary(workTable.getSalary());
            oldWorkTable.setEmployee(employee);
            workTableRepository.save(oldWorkTable);
            System.out.println("Employee's table updated.");
        }

        System.out.println("Given employee info might not be exist!");
    }

    public void deleteWorkTableOfEmployee(String username){
        User employee = getEmployee(username);
        if (employee != null && employee.getWorkTable() != null){
            WorkTable workTable = employee.getWorkTable();
            employee.setWorkTable(null);
            workTable.setEmployee(null);
            workTableRepository.delete(workTable);
            System.out.println("Employee's table was deleted.");
        }
        System.out.println("Not provided info to process the operation!");

    }

    public boolean saveOrUpdateProfilePicture(User employee, MultipartFile picture) throws IOException{
        if (employee != null && picture != null && !picture.isEmpty()){
            Picture newPicture = new Picture();
            if (employee.getPicture() == null){
                newPicture.setPicture(picture.getBytes());
                newPicture.setUser(employee);
                employee.setPicture(newPicture);

                pictureRepository.save(newPicture);
                return true;
            }else {
                // we are updating old pic with new one ofc old one will be deleted
                Picture oldPicture = employee.getPicture();

                employee.setPicture(null);
                oldPicture.setUser(null);
                pictureRepository.delete(oldPicture);

                // setting up new picture
                newPicture.setPicture(picture.getBytes());
                newPicture.setUser(employee);
                employee.setPicture(newPicture);
                pictureRepository.save(newPicture);
                return true;
            }
        }
        return false;

    }

    public boolean deleteProfilePicture(User employee){
        if (employee != null && employee.getPicture() != null){
            Picture picture = employee.getPicture();
            employee.setPicture(null);

            picture.setUser(null);

            pictureRepository.delete(picture);

            return true;
        }
        return false;
    }

    // border for helper methods down there

    public String encodeProfilePicture(byte[] pictureData) {
        return Optional.ofNullable(pictureData)
                .map(data -> Base64.getEncoder().encodeToString(data))  // Encode image as Base64
                .orElse(null);  // If no picture, return null
    }

    public User getEmployee(String username){
        User employee = userRepository.findByUsername(username);
        if (employee != null){
            return employee;
        }
        return null;
    }

    public boolean usernameTaken(String username){
        User user = getEmployee(username);

        if (user != null){
            return true;
        }else {
            return false;
        }
    }

    // find employer by job titles
    public List<JobTable> getJobsByJobTitle(String jobTitle, String location) {
        List<JobTable> availableJobs = jobTableRepository.findByJobNameContainingIgnoreCase(jobTitle);
        
        if (!location.equals("all")) {
            List<JobTable> filteredJobs = new ArrayList<>();
            
            // Filter jobs based on location
            for (JobTable job : availableJobs) {
                if (job.getLocation().toLowerCase().trim().equals(location.toLowerCase().trim())) {
                    filteredJobs.add(job);
                }
            }
            
            // Replace availableJobs with the filtered list
            availableJobs = filteredJobs;
        }
        
        return availableJobs;
    }

    // ADDING JOBS IN SAVED JOBS LIST --
    public void addThingInSavedThingsList(String username, Long thingId){
        User employee = getEmployee(username);

        Optional<JobTable> jobTable = jobTableRepository.findById(thingId);

        if (jobTable.isPresent()){
            if (employee != null && employee.getUserIs().equals(UserIs.EMPLOYEE)){
                SavedThing savedJob = new SavedThing();

                if (!jobSaved(employee, thingId)){
                    savedJob.setUser(employee);
                    savedJob.setThingId(thingId);
                    savedJob.setThingType(ThingType.JOB);
                    savedThingRepository.save(savedJob);
                }else {
                    System.out.println("Job Id: " + thingId);
                    System.out.println("Failed to save! this job saved before!");
                }
            }
        }
        
        System.out.println("Job not exist to be saved!");
    }

    public List<JobTable> getAllSavedJobs(String username){
        User employee = getEmployee(username);

        List<JobTable> jobs = new ArrayList<>();

        if (employee != null){
            for (SavedThing savedJob :  employee.getSavedThings()){
                Optional<JobTable> jobOptional = jobTableRepository.findById(savedJob.getThingId());
                jobOptional.ifPresent(jobs::add);
            }
            return jobs;
        }
        return null;

    }

    public boolean jobSaved(User employee, Long thingId){
        for (SavedThing savedThings: employee.getSavedThings()){
            if (savedThings.getThingId().equals(thingId)) {
                return true;
            }
        }
        return false;
    }

    public void deleteJobFromSavedThings(String username, Long thingId){
        User employee = getEmployee(username);
        if (employee != null){
            for (SavedThing thing: employee.getSavedThings()){
                if (thing.getThingId() == thingId){
                    thing.setUser(null);
                    savedThingRepository.delete(thing);
                }
            }
        }
        
        System.out.println("Not provided info to process the operation!");
    }

}

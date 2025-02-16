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
public class EmployerService {
    
    @Autowired
    private UserRepository userRepository;


    @Autowired
    private JobTableRepository jobTableRepository;

    @Autowired
    private WorkTableRepository workTableRepository;

    @Autowired
    private SavedThingRepository savedThingRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PictureRepository pictureRepository;


    public void createEmployer(User employer){
        if (employer != null){
            if (!usernameTaken(employer.getUsername())){
                employer.setPassword(passwordEncoder.encode(employer.getPassword()));
                userRepository.save(employer);
            }
        }

        System.out.println("Not provided info to process the operation while creating employer!");
    }

    public void updateEmployer(Long id, User updatedEmployer) {
        Optional<User> oldEmployerOptional = userRepository.findById(id);
        if (oldEmployerOptional.isPresent()) {
            User oldEmployer = oldEmployerOptional.get();
            
            // Only update if the values are not null or empty
            if (updatedEmployer.getFirstName() != null && !updatedEmployer.getFirstName().isEmpty()) {
                oldEmployer.setFirstName(updatedEmployer.getFirstName());
            }
            if (updatedEmployer.getLastName() != null && !updatedEmployer.getLastName().isEmpty()) {
                oldEmployer.setLastName(updatedEmployer.getLastName());
            }
            if (updatedEmployer.getPhoneNumber() != null && !updatedEmployer.getPhoneNumber().isEmpty()) {
                oldEmployer.setPhoneNumber(updatedEmployer.getPhoneNumber());
            }
            if (updatedEmployer.getGender() != null) {
                oldEmployer.setGender(updatedEmployer.getGender());
            }
    
            // Save the updated employer
            userRepository.save(oldEmployer);
            
            System.out.println("Updated Employer: " + oldEmployer.getFirstName());
        } else {
            System.out.println("Not provided info to process the operation while updating employer!");
        }
    }

    public void createJobTableForEmployer(String username, JobTable jobTable){
        User employer = getEmployer(username);
        if (employer.getUserIs().equals(UserIs.EMPLOYER)){
            if (employer != null && jobTable != null){
                if (employer.getJobTable() == null){
                    employer.setJobTable(jobTable);
                    jobTable.setEmployer(employer);
                    jobTableRepository.save(jobTable);
                    System.out.println("Table created for employer");
                }
            } 
        } 
        System.out.println("User who is trying to create table is not employer!");
    }

    public void updateJobTableOfEmployer(String username, JobTable updatedJobTable){
        User employer = getEmployer(username);
        if (employer != null){
            JobTable oldJobTable = employer.getJobTable();
            oldJobTable.setJobName(updatedJobTable.getJobName());
            oldJobTable.setCompanyName(updatedJobTable.getCompanyName());
            oldJobTable.setDescription(updatedJobTable.getDescription());
            oldJobTable.setSalary(updatedJobTable.getSalary());
            oldJobTable.setExperience(updatedJobTable.getExperience());
            oldJobTable.setGender(updatedJobTable.getGender());
            oldJobTable.setAge(updatedJobTable.getAge());
            oldJobTable.setEmployer(employer);
            jobTableRepository.save(oldJobTable);
            System.out.println("Updated employer's table.");
        }

        System.out.println("Not provided info to process the operation to update employer's table!");
    }


    public void deleteJobTableOfEmployer(String username){
        User employer = getEmployer(username);

        if (employer != null){
            JobTable jobTable  = employer.getJobTable();
            if (employer.getJobTable() != null){
                employer.setJobTable(null);
                jobTable.setEmployer(null);
                jobTableRepository.delete(jobTable);
                System.out.println("Employer's table was deleted.");
            }
        }
        System.out.println("Not provided info to process the operation to delete employer's table!");
    }

    
    public boolean saveOrUpdateProfilePicture(User employer, MultipartFile picture) throws IOException{
        if (employer != null && picture != null && !picture.isEmpty()){
            Picture newPicture = new Picture();
            if (employer.getPicture() == null){
                newPicture.setPicture(picture.getBytes());
                newPicture.setUser(employer);
                employer.setPicture(newPicture);

                pictureRepository.save(newPicture);
                return true;
            }else {
                // deleting old picture
                Picture oldPicture = new Picture();

                employer.setPicture(null);
                oldPicture.setUser(null);
                pictureRepository.delete(oldPicture);

                // update the picture with new one
                newPicture.setPicture(picture.getBytes());
                newPicture.setUser(employer);
                employer.setPicture(newPicture);

                pictureRepository.save(newPicture);
                return true;
            }
        }
        return false;

    }

    public boolean deleteProfilePicture(User employer){
        if (employer != null && employer.getPicture() != null){
            Picture picture = employer.getPicture();
            employer.setPicture(null);

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


    public List<WorkTable> getEmployeesByWorkTitle(String workTitle, String location) {
        List<WorkTable> availableEmployees = workTableRepository.findByWorkNameContainingIgnoreCase(workTitle);
        
        if (!location.equals("all")) {
            List<WorkTable> filteredEmployees = new ArrayList<>();
            
            // Filter employees based on location
            for (WorkTable employee : availableEmployees) {
                if (employee.getLocation().toLowerCase().trim().equals(location.toLowerCase().trim())) {
                    filteredEmployees.add(employee);
                }
            }
            
            // Replace availableEmployees with the filtered list
            availableEmployees = filteredEmployees;
        }
        
        return availableEmployees;
    }

    public List<WorkTable> getAllSavedEmployees(String username){
        User employer = getEmployer(username);

        List<WorkTable> employees = new ArrayList<>();

        if (employer != null){
            for (SavedThing savedEmloyee :  employer.getSavedThings()){
                Optional<WorkTable> employeeOptional = workTableRepository.findById(savedEmloyee.getThingId());
                employeeOptional.ifPresent(employees::add);
            }
            return employees;
        }
        return null;

    }

    public void addThingInSavedThingsList(String username, Long thingId){
        User employer = getEmployer(username);

        Optional<WorkTable> workTable = workTableRepository.findById(thingId);

        if (workTable.isPresent()){
            if (employer != null && employer.getUserIs().equals(UserIs.EMPLOYER)){
                SavedThing savedEmployee = new SavedThing();

                if (!thingSaved(employer, thingId)){
                    savedEmployee.setUser(employer);
                    savedEmployee.setThingId(thingId);
                    savedEmployee.setThingType(ThingType.WORK);
                    savedThingRepository.save(savedEmployee);
                }else {
                    System.out.println("Employee Id: " + thingId);
                    System.out.println("Failed to save! this Employee saved before!");
                }
            }
        }
        
        System.out.println("Not provided info to process the operation to add employees to saved list!");
    }

    public boolean thingSaved(User employer, Long thingId){
        for (SavedThing savedThings: employer.getSavedThings()){
            if (savedThings.getThingId().equals(thingId)) {
                return true;
            }
        }
        return false;

    }


    //get an employer
    public User getEmployer(String username){
        User employer = userRepository.findByUsername(username);
        if (employer != null){
            if (employer.getUserIs().equals(UserIs.EMPLOYER)){
                return employer;
            }
        }
        return null;
    }

    public boolean usernameTaken(String username){
        User user = getEmployer(username);

        if (user != null){
            return true;
        }else {
            return false;
        }
    }

    public void deleteEmployeeFromSavedThings(String username, Long thingId) {
        User employer = getEmployer(username);
        if (employer != null){
            for (SavedThing thing: employer.getSavedThings()){
                if (thing.getThingId() == thingId){
                    thing.setUser(null);
                    savedThingRepository.delete(thing);
                }
            }
        }

        System.out.println("Not provided info to pop employee from saved employees list!");
    }

}

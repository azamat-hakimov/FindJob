package com.portfolio.demo.controller.employee;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.portfolio.demo.model.JobTable;
import com.portfolio.demo.model.User;
import com.portfolio.demo.service.AuthenticationService;
import com.portfolio.demo.service.EmployeeService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/employee-dashboard")
public class savedJobsController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private AuthenticationService authenticationService;

    @GetMapping("/saved-jobs")
    public String getSavedJobs(HttpSession session, 
                                Model model,
                                @CookieValue(value = "token", required = false) String token){
        User employee = authenticationService.authenticateAndGetUser(token);
        List<JobTable> savedJobs = employeeService.getAllSavedJobs(employee.getUsername());

        if (savedJobs == null){
            savedJobs = new ArrayList<>();
        }
        
        model.addAttribute("savedJobs", savedJobs);
        return "employee/saved-jobs";
    }

    @PostMapping("/save-jobs")
    public String saveJobs(@RequestParam Long id,
                            @CookieValue(value = "token", required = false) String token){
        User employee = authenticationService.authenticateAndGetUser(token);
        employeeService.addThingInSavedThingsList(employee.getUsername(), id);
        return "redirect:/employee-dashboard/saved-jobs";
    }

    @PostMapping("/saved-jobs/delete")
    public String deleteJob(@RequestParam (required = false) Long id, 
                            @CookieValue(value = "token", required = false) String token){
        User employee = authenticationService.authenticateAndGetUser(token);
        if (employee != null){
            employeeService.deleteJobFromSavedThings(employee.getUsername(), id);
            return "redirect:/employee-dashboard/saved-jobs";
        }
        return "employee/saved-jobs";
    }
    
}

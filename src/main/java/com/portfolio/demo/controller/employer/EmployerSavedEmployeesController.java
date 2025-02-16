package com.portfolio.demo.controller.employer;

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

import com.portfolio.demo.model.User;
import com.portfolio.demo.model.WorkTable;
import com.portfolio.demo.service.AuthenticationService;
import com.portfolio.demo.service.EmployerService;

@Controller
@RequestMapping("/employer-dashboard")
public class EmployerSavedEmployeesController {
    
    @Autowired
    private EmployerService employerService;

    @Autowired
    private AuthenticationService authenticationService;

    @GetMapping("/saved-employees")
    public String getSavedJobs(Model model, 
                    @CookieValue(value = "token", required = false) String token){
        User employer = authenticationService.authenticateAndGetUser(token);
        List<WorkTable> employees = employerService.getAllSavedEmployees(employer.getUsername());

        if (employees == null){
            employees = new ArrayList<>();
        }
        
        model.addAttribute("savedEmployees", employees);
        return "employer/saved-employees";
    }

    @PostMapping("/save-employees")
    public String saveJobs(@RequestParam Long id, 
                            @CookieValue(value = "token", required = false) String token){
        User employer = authenticationService.authenticateAndGetUser(token);
        employerService.addThingInSavedThingsList(employer.getUsername(), id);
        return "redirect:/employer-dashboard/saved-employees";
    }

    @PostMapping("/saved-employees/delete")
    public String deleteEmployee(@RequestParam (required = false) Long id, 
                                    @CookieValue(value = "token", required = false) String token){
        User employer = authenticationService.authenticateAndGetUser(token);
        if (employer != null){
            employerService.deleteEmployeeFromSavedThings(employer.getUsername(), id);
            return "redirect:/employer-dashboard/saved-employees";
        }
        return "employer/saved-employees";
    }
}

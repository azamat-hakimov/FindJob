package com.portfolio.demo.controller.search;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.portfolio.demo.model.User;
import com.portfolio.demo.model.myenums.UserIs;
import com.portfolio.demo.service.EmployeeService;
import com.portfolio.demo.service.EmployerService;

@Controller
public class SearchController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private EmployerService employerService;


    // to get the employee by username 
    @GetMapping("/employees/{username}")
    public String getEmployee(@PathVariable("username") String username, Model model){
        String message = null;
        User employee =  employeeService.getEmployee(username);
        if (employee != null){
            if (employee.getUserIs().equals(UserIs.EMPLOYEE)){

                String encodedPicture = null;

                if (employee.getPicture() != null){
                    encodedPicture = employeeService.encodeProfilePicture(employee.getPicture().getPicture());
                }

                model.addAttribute("picture",encodedPicture);
                model.addAttribute("user", employee);

            }
        }else {
            message = "Employee not found with given username @" + username;
        }

        model.addAttribute("message", message);
        return "employee/user-employee";
    }

    // to get the employer by username 
    @GetMapping("/employers/{username}")
    public String getEmployer(@PathVariable("username") String username, Model model){
        String message = null;
        User employer =  employerService.getEmployer(username);
        if (employer != null){
            if (employer.getUserIs().equals(UserIs.EMPLOYER)){

                String encodedPicture = null;

                if (employer.getPicture() != null){
                    encodedPicture = employerService.encodeProfilePicture(employer.getPicture().getPicture());
                }

                model.addAttribute("picture",encodedPicture);
                model.addAttribute("user", employer);
            }
        }else {
            message = "Employer not found with given username @" + username;
        }

        model.addAttribute("message", message);
        return "employer/user-employer";
    }
    
}

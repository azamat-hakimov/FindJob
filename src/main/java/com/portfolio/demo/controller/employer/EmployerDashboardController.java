package com.portfolio.demo.controller.employer;

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
import com.portfolio.demo.model.myenums.UserIs;
import com.portfolio.demo.service.AuthenticationService;
import com.portfolio.demo.service.EmployerService;


@Controller
@RequestMapping("/employer-dashboard")
public class EmployerDashboardController {

    @Autowired
    private EmployerService employerService;

    @Autowired 
    private AuthenticationService authenticationService;

    @GetMapping()
    public String getDashboard(@CookieValue(value = "token", required = false) String token){
        User employer = authenticationService.authenticateAndGetUser(token);
        if (employer != null && employer.getUserIs().equals(UserIs.EMPLOYER)){
            return "employer/employer-dashboard";
        }
        return "redirect:/login?employer=null";
    }

    @PostMapping("/search")
    public String postSearchOperations(@RequestParam("workTitle") String workTitle, 
                                        @RequestParam(value = "location", required = false, defaultValue = "all") String location,
                                        Model model, 
                                        @CookieValue(value = "token", required = false) String token){
        User employer = authenticationService.authenticateAndGetUser(token);

        String message = null;
        if (employer != null && employer.getUserIs().equals(UserIs.EMPLOYER)){
            if (employer.getJobTable() != null){
                List<WorkTable> employees = employerService.getEmployeesByWorkTitle(workTitle,location);
                model.addAttribute("employees", employees);
                return "employer/employer-dashboard";
            }
            message = "Iltimos, xodimlar tavsiyalarini olish uchun ishingiz haqida ma'lumot bering!";
        }
        model.addAttribute("message", message);
        return "employer/employer-dashboard";
    }
}

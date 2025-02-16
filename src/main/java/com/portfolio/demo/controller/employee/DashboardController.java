package com.portfolio.demo.controller.employee;

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
import com.portfolio.demo.model.myenums.UserIs;
import com.portfolio.demo.service.AuthenticationService;
import com.portfolio.demo.service.EmployeeService;

@Controller
@RequestMapping("/employee-dashboard")
public class DashboardController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private AuthenticationService authenticationService;

    @GetMapping()
    public String getEmployee(@CookieValue(value = "token", required = false) String token) {
        User employee = authenticationService.authenticateAndGetUser(token);

        if (employee != null && employee.getUserIs() == UserIs.EMPLOYEE) {
            return "employee/employee-dashboard";
        }
        return "redirect:/login?employee=null";
    }

    @PostMapping("/search")
    public String postSearch(@RequestParam("jobTitle") String jobTitle,
                             @RequestParam(value = "location", required = false, defaultValue = "all") String location,
                             Model model,
                             @CookieValue(value = "token", required = false) String token) {
        User employee = authenticationService.authenticateAndGetUser(token);

        if (employee != null) {
            if (employee.getWorkTable() != null) {
                List<JobTable> jobs = employeeService.getJobsByJobTitle(jobTitle, location);
                model.addAttribute("jobs", jobs);
                return "employee/employee-dashboard";
            } else {
                model.addAttribute("message", "Ish tavsiyalarini olish uchun ish tajribangiz haqida ma'lumot bering");
                return "employee/employee-dashboard";
            }
        }
        return "redirect:/login";
    }
}

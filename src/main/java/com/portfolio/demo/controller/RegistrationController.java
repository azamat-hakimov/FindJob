package com.portfolio.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.portfolio.demo.model.User;
import com.portfolio.demo.model.myenums.UserIs;
import com.portfolio.demo.repository.UserRepository;
import com.portfolio.demo.security.JwtTokenUtil;
import com.portfolio.demo.service.AuthenticationService;
import com.portfolio.demo.service.EmployeeService;
import com.portfolio.demo.service.EmployerService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/")
public class RegistrationController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private EmployerService employerService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationService authenticationService;


    private final String EMPLOYEE_DASHBOARD = "redirect:/employee-dashboard";
    private final String EMPLOYER_DASHBOARD = "redirect:/employer-dashboard";


    @GetMapping()
    public String getDashboard(@CookieValue(value = "token", required = false) String token) {
        return dashboardDirecter(token, "registration/dashboard");
    }

    @GetMapping("/signup")
    public String getSignup(Model model, @CookieValue(value = "token", required = false) String token) {
        User user = authenticationService.authenticateAndGetUser(token);
        if (user != null) {
            if (user.getUserIs().equals(UserIs.EMPLOYEE)){
                return EMPLOYEE_DASHBOARD;
            }
            return EMPLOYER_DASHBOARD;
        }

        model.addAttribute("user", new User());
        return "registration/signup";
    }

    @GetMapping("/login")
    public String getLogin(@CookieValue(value = "token",required = false) String token) {
        return dashboardDirecter(token, "registration/login");
    }

    @PostMapping("/signup")
    public String postSignup(@ModelAttribute("user") User user, 
                             @RequestParam(value = "confirmPassword", required = false) String confirmPassword,
                             Model model) {

        if (user != null) {
            if (user.getPassword().equals(confirmPassword)) {
                if (user.getPassword().length() >= 7) {
                    // Move the encoding to the service layer
                    if (user.getUserIs().equals(UserIs.EMPLOYEE)) {
                        if (!employeeService.usernameTaken(user.getUsername())) {
                            if (user.getUsername().length() >= 5) {
                                employeeService.createEmployee(user);  // This should handle password encoding internally
                                return "redirect:/login";
                            }
                        }
                    }
                    if (user.getUserIs().equals(UserIs.EMPLOYER)) {
                        if (!employerService.usernameTaken(user.getUsername())) {
                            if (user.getUsername().length() >= 5) {
                                employerService.createEmployer(user);  // This should handle password encoding internally
                                return "redirect:/login";
                            }
                        }
                    }
                }
            }
        }
        return "signup";
    }

    @PostMapping("/login")
    public String postLogin(@RequestParam("username") String username,
                            @RequestParam("password") String password,
                            HttpServletResponse response,
                            Model model) {
        String message = null;
        if (username != null && password != null) {
            User user = userRepository.findByUsername(username);
            if (user != null && passwordEncoder.matches(password, user.getPassword())) {  // Use matches() to verify password
                // Generate JWT token
                String token = jwtTokenUtil.generateToken(username);
                
                // Create HttpOnly cookie for the token
                Cookie jwtCookie = new Cookie("token", token);
                jwtCookie.setHttpOnly(true);
                jwtCookie.setSecure(true); // Use HTTPS in production
                jwtCookie.setPath("/");
                jwtCookie.setMaxAge(60 * 60 * 24); // a day

                // Add the cookie to the response
                response.addCookie(jwtCookie);

                // Redirect based on user role
                if (user.getUserIs().equals(UserIs.EMPLOYEE)) {
                    return EMPLOYEE_DASHBOARD;
                } else if (user.getUserIs().equals(UserIs.EMPLOYER)) {
                    return EMPLOYER_DASHBOARD;
                }
            }
            message = "Invalid username or password";
            model.addAttribute("message", message);
        }
        return "redirect:/login?error";
    }

    @PostMapping("/logout")
    public String logout(HttpServletResponse response,
                        @CookieValue(value = "token", required = false) String token) {
        // Log the token value at the start
        if (token != null) {
            System.out.println("Token before deletion: " + token);
        } else {
            System.out.println("No token found in the request.");
        }
        
        // Redirect to the login page
        return "redirect:/login?logout=true";
    }

    //HELPER METHOD
    public String dashboardDirecter(String token, String currentUrl){
        User user = authenticationService.authenticateAndGetUser(token);
        if (user != null){
            if (user.getUserIs().equals(UserIs.EMPLOYEE)){
                return EMPLOYEE_DASHBOARD;
            }
            return EMPLOYER_DASHBOARD;
        }
        return currentUrl;
    }
}

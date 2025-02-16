package com.portfolio.demo.controller.employee;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.portfolio.demo.model.User;
import com.portfolio.demo.model.WorkTable;
import com.portfolio.demo.service.AuthenticationService;
import com.portfolio.demo.service.EmployeeService;



@Controller
@RequestMapping("/employee-dashboard")
public class ProfileController {
    
    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private AuthenticationService authenticationService;

    // to get the page of employee profile
    @GetMapping("/profile")
    public String getProfile(Model model,
                         @CookieValue(value = "token", required = false) String token,
                         @RequestParam(required = false) Boolean edit, 
                         @RequestParam(required = false) Boolean editAbout) {
        // Get the logged-in user from the session
        User employee = authenticationService.authenticateAndGetUser(token);
        WorkTable workTable = null;

        if (employee != null) {
            workTable = employee.getWorkTable();
        }

        // If no workTable exists, create a new one
        if (workTable == null && employee != null) {
            workTable = new WorkTable();
           
        }

        String encodedImage = null;

        if (employee != null && employee.getPicture() != null){
            encodedImage =  employeeService.encodeProfilePicture(employee.getPicture().getPicture());
        }

        // Add attributes to the model
        model.addAttribute("workTable", workTable);
        model.addAttribute("edit", edit != null && edit); // For editing work details
        model.addAttribute("editAbout", editAbout != null && editAbout); // For editing About section
        model.addAttribute("user", employee);
        model.addAttribute("encodedImage", encodedImage);

        return "employee/employee-profile";
    }

    @PostMapping("/profile/upload-picture")
    public String uploadProfilePicture(@RequestParam("picture") MultipartFile picture,
                                        @CookieValue(value = "token", required = false) String token){
        User employee = authenticationService.authenticateAndGetUser(token);

        if (employee != null){
            try {
                employeeService.saveOrUpdateProfilePicture(employee, picture);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "redirect:/employee-dashboard/profile";
        }
        return "redirect:/login";

    }

    @PostMapping("/delete-picture")
    public String deletePicture(@CookieValue(value = "token",required = false) String token) {
        User employee = authenticationService.authenticateAndGetUser(token);
        if (employee != null){
            employeeService.deleteProfilePicture(employee);
            return "redirect:/employee-dashboard/profile";
        }

        return "redirect:/login";
    }
    


    // to post the employee profile
    @PostMapping("/profile")
    public String postProfile(@ModelAttribute("workTable") WorkTable workTable, 
                                @CookieValue(value = "token", required = false) String token) {
        User employee = authenticationService.authenticateAndGetUser(token);
        // If there's no workTable, create it
        if (workTable != null && employee != null && employee.getWorkTable() == null) {
            employeeService.createWorkTableForEmployee(employee.getUsername(), workTable);

            return "redirect:/employee-dashboard/profile";
        }

        return "redirect:/employee-dashboard/profile?error";
    }



    // to edit the employee profile
    @PostMapping("/profile/edit")
    public String editWorkDetails(@ModelAttribute("workTable") WorkTable workTable,
                                    @CookieValue(value = "token", required = false) String token) {
        User employee = authenticationService.authenticateAndGetUser(token);
        if (workTable != null && employee != null){
            employeeService.updateWorkTableOfEmployee(employee.getUsername(), workTable);

            return "redirect:/employee-dashboard/profile";
        }
        return "redirect:/employee-dashboard/profile?error";
    }

    // to delete the employee profile
    @PostMapping("/profile/delete")
    public String deleteWorkDetails(@RequestParam (required = false) Long id, 
                                    @CookieValue(value = "token", required = false) String token) {
        User employee = authenticationService.authenticateAndGetUser(token);
        if (employee.getWorkTable() != null){
            employeeService.deleteWorkTableOfEmployee(employee.getUsername());
            
            return "redirect:/employee-dashboard/profile";
        }
        
        return "redirect:/employee-dashboard/profile";
    }

    @PostMapping("/update/about")
    public String updateAbout(@ModelAttribute("user") User updatedEmployee, 
                            @CookieValue(value = "token", required = false) String token){
        User employee = authenticationService.authenticateAndGetUser(token);

        employeeService.updateEmployee(employee.getId(), updatedEmployee);

        return "redirect:/employee-dashboard/profile";

    }
}

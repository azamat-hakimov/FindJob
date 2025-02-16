package com.portfolio.demo.controller.employer;

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

import com.portfolio.demo.model.JobTable;
import com.portfolio.demo.model.User;
import com.portfolio.demo.service.AuthenticationService;
import com.portfolio.demo.service.EmployerService;


@Controller
@RequestMapping("/employer-dashboard")
public class EmployerProfileController {
    
    @Autowired
    private EmployerService employerService;

    @Autowired
    private AuthenticationService authenticationService;

    @GetMapping("/profile")
    public String getProfile(Model model, 
                            @CookieValue(value = "token", required = false) String token, 
                            @RequestParam(required = false) Boolean edit, 
                            @RequestParam(required = false) Boolean editAbout) {
        // Get the logged-in user from the session
        User employer = authenticationService.authenticateAndGetUser(token);
        JobTable jobTable = null;

        if (employer != null) {
            jobTable = employer.getJobTable();
        }

        if (jobTable == null) {
            jobTable = new JobTable();
        }

        String encodedImage = null;

        if (employer != null && employer.getPicture() != null){
            encodedImage =  employerService.encodeProfilePicture(employer.getPicture().getPicture());
        }

        // Add attributes to the model
        model.addAttribute("jobTable", jobTable);
        model.addAttribute("edit", edit != null && edit); // For editing
        model.addAttribute("editAbout", editAbout != null && editAbout); // For editing About section
        model.addAttribute("user", employer);
        model.addAttribute("encodedImage", encodedImage);
        return "employer/employer-profile";
    }

    @PostMapping("/profile/upload-picture")
    public String uploadProfilePicture(@RequestParam("picture") MultipartFile picture,
                                        @CookieValue(value = "token", required = false) String token){
        User employer = authenticationService.authenticateAndGetUser(token);

        if (employer != null){
            try {
                employerService.saveOrUpdateProfilePicture(employer, picture);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "redirect:/employer-dashboard/profile";
        }
        return "redirect:/login";

    }

    @PostMapping("/delete-picture")
    public String deletePicture(@CookieValue(value = "token",required = false) String token) {
        User employer = authenticationService.authenticateAndGetUser(token);
        if (employer != null){
            employerService.deleteProfilePicture(employer);
            return "redirect:/employer-dashboard/profile";
        }

        return "redirect:/login";
    }
    


    @PostMapping("/profile")
    public String postProfile(@ModelAttribute("jobTable") JobTable jobTable, 
                                @CookieValue(value = "token", required = false) String token){
        User employer = authenticationService.authenticateAndGetUser(token);
        if (jobTable != null){
            employerService.createJobTableForEmployer(employer.getUsername(), jobTable);

            return "redirect:/employer-dashboard/profile";
        }
        return "redirect:/employer-dashboard/profile?error";
    }


    @PostMapping("/profile/edit")
    public String editJobDetails(@ModelAttribute("jobTable") JobTable jobTable, 
                                @CookieValue(value = "token", required = false) String token) {
        User employer = authenticationService.authenticateAndGetUser(token);
        if (jobTable != null && employer != null){
            employerService.updateJobTableOfEmployer(employer.getUsername(), jobTable);

            return "redirect:/employer-dashboard/profile";
        }
        return "redirect:/employer-dashboard/profile?error";
    }

    // to delete the employee profile
    @PostMapping("/profile/delete")
    public String deleteJobDetails(@RequestParam (required = false) Long id, 
                                    @CookieValue(value = "token", required = false) String token) {
        User employer = authenticationService.authenticateAndGetUser(token);
        if (employer.getJobTable() != null){
            employerService.deleteJobTableOfEmployer(employer.getUsername());

            return "redirect:/employer-dashboard/profile";
        }
        
        return "redirect:/employer-dashboard/profile";
    }

    @PostMapping("/update/about")
    public String updateAbout(@ModelAttribute("user") User updatedEmployee, 
                                @CookieValue(value = "token", required = false) String token){
        User employer = authenticationService.authenticateAndGetUser(token);

        employerService.updateEmployer(employer.getId(), updatedEmployee);

        return "redirect:/employer-dashboard/profile";

    }

}

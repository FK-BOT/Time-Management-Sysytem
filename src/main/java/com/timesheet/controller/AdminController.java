package com.timesheet.controller;

import com.timesheet.entity.TimeSheet;
import com.timesheet.entity.User;
import com.timesheet.service.TimeSheetService;
import com.timesheet.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminController {
    
    @Autowired
    private TimeSheetService timeSheetService;
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/dashboard")
    public String adminDashboard(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userService.findByUsername(auth.getName()).orElse(null);
        
        if (currentUser == null || currentUser.getRole() != User.Role.MANAGER) {
            return "redirect:/timesheet/dashboard";
        }
        
        // Redirect admin users directly to the timesheets page
        return "redirect:/admin/timesheets";
    }
    
    @GetMapping("/timesheets")
    public String viewAllTimeSheets(@RequestParam(required = false) Long employeeId,
                                   @RequestParam(required = false) String startDate,
                                   @RequestParam(required = false) String endDate,
                                   Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userService.findByUsername(auth.getName()).orElse(null);
        
        if (currentUser == null || currentUser.getRole() != User.Role.MANAGER) {
            return "redirect:/timesheet/dashboard";
        }
        
        List<TimeSheet> timeSheets;
        
        if (employeeId != null && startDate != null && endDate != null) {
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);
            timeSheets = timeSheetService.getTimeSheetsByUserIdAndDateRange(employeeId, start, end);
        } else if (startDate != null && endDate != null) {
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);
            timeSheets = timeSheetService.getAllTimeSheetsByDateRange(start, end);
        } else {
            // Default: show only submitted timesheets
            timeSheets = timeSheetService.getSubmittedTimeSheets();
        }
        
        // Group timesheets by user
        Map<User, List<TimeSheet>> userTimesheetGroups = timeSheets.stream()
                .collect(Collectors.groupingBy(TimeSheet::getUser));
        
        List<User> allUsers = userService.findAllUsers();
        
        model.addAttribute("userTimesheetGroups", userTimesheetGroups);
        model.addAttribute("users", allUsers);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("selectedEmployeeId", employeeId);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        
        return "admin/timesheets";
    }
    
    @GetMapping("/reports")
    public String generateReports(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userService.findByUsername(auth.getName()).orElse(null);
        
        if (currentUser == null || currentUser.getRole() != User.Role.MANAGER) {
            return "redirect:/timesheet/dashboard";
        }
        
        // Get current month timesheets
        LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1);
        LocalDate endOfMonth = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());
        List<TimeSheet> currentMonthSheets = timeSheetService.getAllTimeSheetsByDateRange(startOfMonth, endOfMonth);
        
        model.addAttribute("currentMonthSheets", currentMonthSheets);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("startOfMonth", startOfMonth);
        model.addAttribute("endOfMonth", endOfMonth);
        
        return "admin/reports";
    }
} 
package com.timesheet.controller;

import com.timesheet.dto.TimeSheetDto;
import com.timesheet.entity.TimeSheet;
import com.timesheet.entity.User;
import com.timesheet.service.TimeSheetService;
import com.timesheet.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/timesheet")
public class TimeSheetController {
    
    @Autowired
    private TimeSheetService timeSheetService;
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByUsername(auth.getName()).orElse(null);
        
        if (user == null) {
            return "redirect:/login";
        }
        
        // Initialize current week timesheets if they don't exist
        LocalDate startOfWeek = LocalDate.now().with(DayOfWeek.MONDAY);
        timeSheetService.initializeWeekTimeSheets(user, startOfWeek);
        
        List<TimeSheet> currentWeekSheets = timeSheetService.getCurrentWeekTimeSheets(user);
        List<TimeSheetDto> timeSheetDtos = currentWeekSheets.stream()
                .map(TimeSheetDto::new)
                .collect(Collectors.toList());
        
        model.addAttribute("timeSheets", timeSheetDtos);
        model.addAttribute("user", user);
        model.addAttribute("weekStart", startOfWeek);
        model.addAttribute("weekEnd", startOfWeek.plusDays(6));
        
        return "dashboard";
    }
    
    @PostMapping("/update/{id}")
    public String updateTimeSheet(@PathVariable Long id,
                                 @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime loginTime,
                                 @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime logoutTime,
                                 @RequestParam(required = false) TimeSheet.WorkType workType,
                                 @RequestParam(required = false) Boolean isLeave,
                                 @RequestParam(required = false) String remark,
                                 RedirectAttributes redirectAttributes) {

        try {
            timeSheetService.updateTimeSheet(id, loginTime, logoutTime, workType, isLeave != null && isLeave, remark);
            redirectAttributes.addFlashAttribute("success", "Timesheet updated successfully!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        
        return "redirect:/timesheet/dashboard";
    }
    
    @PostMapping("/submit/{id}")
    public String submitToAdmin(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            timeSheetService.submitToAdmin(id);
            redirectAttributes.addFlashAttribute("success", "Timesheet submitted to admin successfully");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        
        return "redirect:/timesheet/dashboard";
    }
    
    @PostMapping("/delete/{id}")
    public String deleteTimeSheet(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            timeSheetService.deleteTimeSheet(id);
            redirectAttributes.addFlashAttribute("success", "Timesheet deleted successfully");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        
        return "redirect:/timesheet/dashboard";
    }
} 
package com.timesheet.controller;

import com.timesheet.entity.TimeSheet;
import com.timesheet.entity.User;
import com.timesheet.service.TimeSheetService;
import com.timesheet.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/timesheets")
public class TimeSheetRestController {

    @Autowired
    private TimeSheetService timeSheetService;

    

    @GetMapping
    public ResponseEntity<List<TimeSheet>> getTimeSheets(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        if (user.getRole() == User.Role.MANAGER) {
            return ResponseEntity.ok(timeSheetService.getAllTimeSheetsByDateRange(java.time.LocalDate.of(1970,1,1), java.time.LocalDate.of(2100,1,1)));
        } else {
            // Ensure all 7 days are present for the current week
            java.time.LocalDate startOfWeek = java.time.LocalDate.now().with(java.time.DayOfWeek.MONDAY);
            timeSheetService.initializeWeekTimeSheets(user, startOfWeek);
            return ResponseEntity.ok(timeSheetService.getCurrentWeekTimeSheets(user));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<TimeSheet> getTimeSheetById(@PathVariable Long id, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return timeSheetService.findById(id)
                .filter(ts -> user.getRole() == User.Role.MANAGER || ts.getUser().getId().equals(user.getId()))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<TimeSheet> createTimeSheet(@RequestBody TimeSheet timeSheet, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        TimeSheet created = timeSheetService.createTimeSheet(user, timeSheet.getDate());
        created.setLoginTime(timeSheet.getLoginTime());
        created.setLogoutTime(timeSheet.getLogoutTime());
        created.setWorkType(timeSheet.getWorkType());
        created.setLeave(timeSheet.isLeave());
        created.setRemark(timeSheet.getRemark());
        return ResponseEntity.ok(timeSheetService.findById(created.getId()).orElse(created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TimeSheet> updateTimeSheet(@PathVariable Long id, @RequestBody TimeSheet updated, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return timeSheetService.findById(id)
                .filter(ts -> user.getRole() == User.Role.MANAGER || ts.getUser().getId().equals(user.getId()))
                .map(ts -> {
                    TimeSheet result = timeSheetService.updateTimeSheet(
                        id,
                        updated.getLoginTime(),
                        updated.getLogoutTime(),
                        updated.getWorkType(),
                        updated.isLeave(),
                        updated.getRemark()
                    );
                    return ResponseEntity.ok(result);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTimeSheet(@PathVariable Long id, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return timeSheetService.findById(id)
                .filter(ts -> user.getRole() == User.Role.MANAGER || ts.getUser().getId().equals(user.getId()))
                .map(ts -> {
                    timeSheetService.deleteTimeSheet(id);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/submit/{id}")
    public ResponseEntity<?> submitTimeSheet(@PathVariable Long id, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return timeSheetService.findById(id)
                .filter(ts -> user.getRole() == User.Role.MANAGER || ts.getUser().getId().equals(user.getId()))
                .map(ts -> {
                    timeSheetService.submitToAdmin(id);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/submitted")
    public ResponseEntity<List<TimeSheet>> getSubmittedTimeSheets(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        if (user.getRole() == User.Role.MANAGER) {
            return ResponseEntity.ok(timeSheetService.getSubmittedTimeSheets());
        } else {
            return ResponseEntity.status(403).build();
        }
    }
} 
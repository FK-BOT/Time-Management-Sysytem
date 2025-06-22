package com.timesheet.service;

import com.timesheet.entity.TimeSheet;
import com.timesheet.entity.User;
import com.timesheet.repository.TimeSheetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class TimeSheetService {
    
    @Autowired
    private TimeSheetRepository timeSheetRepository;
    
    public List<TimeSheet> getCurrentWeekTimeSheets(User user) {
        LocalDate startOfWeek = LocalDate.now().with(DayOfWeek.MONDAY);
        LocalDate endOfWeek = startOfWeek.plusDays(6);
        return timeSheetRepository.findByUserAndDateBetween(user, startOfWeek, endOfWeek);
    }
    
    public List<TimeSheet> getTimeSheetsByDateRange(User user, LocalDate startDate, LocalDate endDate) {
        return timeSheetRepository.findByUserAndDateBetween(user, startDate, endDate);
    }
    
    public List<TimeSheet> getAllTimeSheetsByDateRange(LocalDate startDate, LocalDate endDate) {
        return timeSheetRepository.findAllByDateRange(startDate, endDate);
    }
    
    public List<TimeSheet> getTimeSheetsByUserIdAndDateRange(Long userId, LocalDate startDate, LocalDate endDate) {
        return timeSheetRepository.findByUserIdAndDateRange(userId, startDate, endDate);
    }
    
    public TimeSheet createTimeSheet(User user, LocalDate date) {
        String dayName = date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        TimeSheet timeSheet = new TimeSheet(user, date, dayName);
        return timeSheetRepository.save(timeSheet);
    }
    
    public TimeSheet updateTimeSheet(Long id, LocalTime loginTime, LocalTime logoutTime, TimeSheet.WorkType workType, boolean isLeave, String remark) {
        Optional<TimeSheet> optionalTimeSheet = timeSheetRepository.findById(id);
        if (optionalTimeSheet.isPresent()) {
            TimeSheet timeSheet = optionalTimeSheet.get();
            
            if (timeSheet.isSubmittedToAdmin()) {
                throw new RuntimeException("Cannot update timesheet that has been submitted to admin");
            }
            
            timeSheet.setLoginTime(loginTime);
            timeSheet.setLogoutTime(logoutTime);
            timeSheet.setWorkType(workType);
            timeSheet.setLeave(isLeave);
            timeSheet.setRemark(remark);
            
            return timeSheetRepository.save(timeSheet);
        }
        throw new RuntimeException("TimeSheet not found");
    }
    
    public void deleteTimeSheet(Long id) {
        Optional<TimeSheet> optionalTimeSheet = timeSheetRepository.findById(id);
        if (optionalTimeSheet.isPresent()) {
            TimeSheet timeSheet = optionalTimeSheet.get();
            if (timeSheet.isSubmittedToAdmin()) {
                throw new RuntimeException("Cannot delete timesheet that has been submitted to admin");
            }
            timeSheetRepository.deleteById(id);
        } else {
            throw new RuntimeException("TimeSheet not found");
        }
    }
    
    public void submitToAdmin(Long id) {
        Optional<TimeSheet> optionalTimeSheet = timeSheetRepository.findById(id);
        if (optionalTimeSheet.isPresent()) {
            TimeSheet timeSheet = optionalTimeSheet.get();
            timeSheet.setSubmittedToAdmin(true);
            timeSheetRepository.save(timeSheet);
        } else {
            throw new RuntimeException("TimeSheet not found");
        }
    }
    
    public List<TimeSheet> getSubmittedTimeSheets() {
        return timeSheetRepository.findBySubmittedToAdmin(true);
    }
    
    public Optional<TimeSheet> findById(Long id) {
        return timeSheetRepository.findById(id);
    }
    
    public void initializeWeekTimeSheets(User user, LocalDate weekStart) {
        for (int i = 0; i < 7; i++) {
            LocalDate date = weekStart.plusDays(i);
            String dayName = date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
            
            // Check if timesheet already exists for this date
            List<TimeSheet> existingSheets = timeSheetRepository.findByUserAndDateBetween(user, date, date);
            if (existingSheets.isEmpty()) {
                TimeSheet timeSheet = new TimeSheet(user, date, dayName);
                timeSheetRepository.save(timeSheet);
            }
        }
    }
} 
package com.timesheet.service;

import com.timesheet.entity.TimeSheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ReportScheduler {
    
    @Autowired
    private TimeSheetService timeSheetService;
    
    @Autowired
    private EmailService emailService;
    
    // Run on the 5th of every month at 9:00 AM
    @Scheduled(cron = "0 0 9 5 * ?")
    public void generateAndSendMonthlyReport() {
        LocalDate now = LocalDate.now();
        LocalDate previousMonth = now.minusMonths(1);
        
        // Get the first and last day of the previous month
        LocalDate startOfMonth = previousMonth.withDayOfMonth(1);
        LocalDate endOfMonth = previousMonth.withDayOfMonth(previousMonth.lengthOfMonth());
        
        // Get all timesheets for the previous month
        List<TimeSheet> monthlyTimeSheets = timeSheetService.getAllTimeSheetsByDateRange(startOfMonth, endOfMonth);
        
        if (!monthlyTimeSheets.isEmpty()) {
            emailService.sendMonthlyReport(monthlyTimeSheets, previousMonth);
        }
    }
} 
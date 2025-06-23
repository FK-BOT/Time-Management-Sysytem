package com.timesheet.service;

import com.timesheet.entity.TimeSheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@Service
public class ReportScheduler {

    @Autowired
    private TimeSheetService timeSheetService;

    @Autowired
    private EmailService emailService;

    // Original: Run on the 5th of every month at 9:00 AM
    // @Scheduled(cron = "0 0 9 5 * ?")

    // Modified Cron: Run every Monday at 9:00 AM (for weekly report)
    // You can adjust this if you want it on a different day/time
    @Scheduled(cron = "0 0 9 * * MON")
    public void generateAndSendMonthlyReport() { // Consider renaming this method to generateAndSendWeeklyReport
        System.out.println("DEBUG: generateAndSendWeeklyReport called"); // Updated debug log

        LocalDate now = LocalDate.now();

        // Calculate the start and end dates of the LAST WEEK (Monday to Sunday)
        // Get the Monday of the current week, then subtract one week to get last Monday
        LocalDate lastMonday = now.with(DayOfWeek.MONDAY).minusWeeks(1);
        // Get the Sunday of the current week, then subtract one week to get last Sunday
        LocalDate lastSunday = now.with(DayOfWeek.SUNDAY).minusWeeks(1);

        // Ensure that lastSunday is indeed after lastMonday, just for logical safety
        // In most cases, these calculations will naturally yield correct start/end
        LocalDate startOfLastWeek = lastMonday;
        LocalDate endOfLastWeek = lastSunday;

        System.out.println("DEBUG: Fetching timesheets from " + startOfLastWeek + " to " + endOfLastWeek); // Added for clarity

        // Get all timesheets for the previous week
        List<TimeSheet> weeklyTimeSheets = timeSheetService.getAllTimeSheetsByDateRange(startOfLastWeek, endOfLastWeek);

        if (!weeklyTimeSheets.isEmpty()) {
            // Pass the start date of the week to EmailService for context in subject/body
            emailService.sendMonthlyReport(weeklyTimeSheets, startOfLastWeek);
            // Note: You might want to modify sendMonthlyReport in EmailService
            // to be more generic (e.g., sendReport(List<TimeSheet>, LocalDate periodStartDate))
            // and change its subject line formatting for weekly reports.
        } else {
            System.out.println("DEBUG: No timesheets found for last week (" + startOfLastWeek + " to " + endOfLastWeek + "). Email not sent.");
        }
    }

}
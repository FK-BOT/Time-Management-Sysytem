package com.timesheet.service;

import com.timesheet.entity.TimeSheet;
import com.timesheet.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class EmailService {
    
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Autowired
    private UserService userService;
    
    
    public void sendMonthlyReport(List<TimeSheet> timeSheets, LocalDate month) {
        System.out.println("DEBUG: sendEmailWithAttachment called ");
        List<User> managers = userService.findAllManagers();
        
        if (managers.isEmpty()) {
            return;
        }
        
        
        try {
            String csvContent = generateCSVReport(timeSheets);
            String subject = "Monthly Timesheet Report - " + month.format(DateTimeFormatter.ofPattern("MMMM yyyy"));
            String body = "Please find attached the monthly timesheet report for " + month.format(DateTimeFormatter.ofPattern("MMMM yyyy"));
            
            for (User manager : managers) {
                sendEmailWithAttachment(manager.getEmail(), subject, body, csvContent, 
                    "timesheet_report_" + month.format(DateTimeFormatter.ofPattern("yyyy_MM")) + ".csv");
                System.out.println("email sent");
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to send monthly report", e);
        }
    }
    
    private String generateCSVReport(List<TimeSheet> timeSheets) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(baos);
        
        // CSV Header
        writer.println("User ID,User Name,Date,Day,Login Time,Logout Time,Work Type,Leave,Remark,Submitted to Admin");
        
        // CSV Data
        for (TimeSheet timeSheet : timeSheets) {
            writer.printf("%d,%s,%s,%s,%s,%s,%s,%s,%s,%s%n",
                timeSheet.getUser().getId(),
                escapeCsvField(timeSheet.getUser().getFullName()),
                timeSheet.getDate(),
                timeSheet.getDay(),
                timeSheet.getLoginTime() != null ? timeSheet.getLoginTime() : "",
                timeSheet.getLogoutTime() != null ? timeSheet.getLogoutTime() : "",
                timeSheet.getWorkType() != null ? timeSheet.getWorkType() : "",
                timeSheet.isLeave(),
                escapeCsvField(timeSheet.getRemark() != null ? timeSheet.getRemark() : ""),
                timeSheet.isSubmittedToAdmin()
            );
        }
        
        writer.close();
        return baos.toString();
    }
    
    private String escapeCsvField(String field) {
        if (field == null) {
            return "";
        }
        if (field.contains(",") || field.contains("\"") || field.contains("\n")) {
            return "\"" + field.replace("\"", "\"\"") + "\"";
        }
        return field;
    }
    
    private void sendEmailWithAttachment(String to, String subject, String body, String csvContent, String filename) 
            throws MessagingException, IOException {
        
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(body);
        
        ByteArrayResource resource = new ByteArrayResource(csvContent.getBytes());
        helper.addAttachment(filename, resource);
        
        mailSender.send(message);
    }
    
    
} 
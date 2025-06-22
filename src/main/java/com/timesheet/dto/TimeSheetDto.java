package com.timesheet.dto;

import com.timesheet.entity.TimeSheet;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

public class TimeSheetDto {
    
    private Long id;
    
    @NotNull(message = "Date is required")
    private LocalDate date;
    
    @NotNull(message = "Day is required")
    private String day;
    
    private LocalTime loginTime;
    
    private LocalTime logoutTime;
    
    private TimeSheet.WorkType workType;
    
    private boolean isLeave;
    
    private String remark;
    
    private boolean submittedToAdmin;
    
    // Constructors
    public TimeSheetDto() {}
    
    public TimeSheetDto(TimeSheet timeSheet) {
        this.id = timeSheet.getId();
        this.date = timeSheet.getDate();
        this.day = timeSheet.getDay();
        this.loginTime = timeSheet.getLoginTime();
        this.logoutTime = timeSheet.getLogoutTime();
        this.workType = timeSheet.getWorkType();
        this.isLeave = timeSheet.isLeave();
        this.remark = timeSheet.getRemark();
        this.submittedToAdmin = timeSheet.isSubmittedToAdmin();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public LocalDate getDate() {
        return date;
    }
    
    public void setDate(LocalDate date) {
        this.date = date;
    }
    
    public String getDay() {
        return day;
    }
    
    public void setDay(String day) {
        this.day = day;
    }
    
    public LocalTime getLoginTime() {
        return loginTime;
    }
    
    public void setLoginTime(LocalTime loginTime) {
        this.loginTime = loginTime;
    }
    
    public LocalTime getLogoutTime() {
        return logoutTime;
    }
    
    public void setLogoutTime(LocalTime logoutTime) {
        this.logoutTime = logoutTime;
    }
    
    public TimeSheet.WorkType getWorkType() {
        return workType;
    }
    
    public void setWorkType(TimeSheet.WorkType workType) {
        this.workType = workType;
    }
    
    public boolean isLeave() {
        return isLeave;
    }
    
    public void setLeave(boolean leave) {
        isLeave = leave;
    }
    
    public String getRemark() {
        return remark;
    }
    
    public void setRemark(String remark) {
        this.remark = remark;
    }
    
    public boolean isSubmittedToAdmin() {
        return submittedToAdmin;
    }
    
    public void setSubmittedToAdmin(boolean submittedToAdmin) {
        this.submittedToAdmin = submittedToAdmin;
    }
} 
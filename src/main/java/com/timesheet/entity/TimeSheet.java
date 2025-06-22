package com.timesheet.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "timesheets")
public class TimeSheet {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @NotNull
    private LocalDate date;
    
    @NotNull
    private String day;
    
    private LocalTime loginTime;
    
    private LocalTime logoutTime;
    
    @Enumerated(EnumType.STRING)
    private WorkType workType;
    
    private boolean isLeave;
    
    @Column(length = 500)
    private String remark;
    
    private boolean submittedToAdmin;
    
    public enum WorkType {
        OFFICE, HOME
    }
    
    // Constructors
    public TimeSheet() {}
    
    public TimeSheet(User user, LocalDate date, String day) {
        this.user = user;
        this.date = date;
        this.day = day;
        this.submittedToAdmin = false;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
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
    
    public WorkType getWorkType() {
        return workType;
    }
    
    public void setWorkType(WorkType workType) {
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
package com.timesheet.repository;

import com.timesheet.entity.TimeSheet;
import com.timesheet.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TimeSheetRepository extends JpaRepository<TimeSheet, Long> {
    List<TimeSheet> findByUser(User user);
    List<TimeSheet> findByUserAndDateBetween(User user, LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT t FROM TimeSheet t WHERE t.user.id = :userId AND t.date BETWEEN :startDate AND :endDate")
    List<TimeSheet> findByUserIdAndDateRange(@Param("userId") Long userId, 
                                            @Param("startDate") LocalDate startDate, 
                                            @Param("endDate") LocalDate endDate);
    
    List<TimeSheet> findBySubmittedToAdmin(boolean submittedToAdmin);
    
    @Query("SELECT t FROM TimeSheet t WHERE t.date BETWEEN :startDate AND :endDate")
    List<TimeSheet> findAllByDateRange(@Param("startDate") LocalDate startDate, 
                                      @Param("endDate") LocalDate endDate);
} 
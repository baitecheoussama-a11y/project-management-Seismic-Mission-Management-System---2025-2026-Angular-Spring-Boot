// entity/analytics/DimTime.java
package com.pfe.webapp.entity.analytics;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "dim_time")
public class DimTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private LocalDate date;

    private Integer year;
    private Integer month;
    private Integer quarter;
    private Integer week;
    private Integer dayOfWeek;
    private String monthName;
    private String yearMonth; // e.g., "2026-06"
    private Boolean isWeekend;

    // Constructors
    public DimTime() {}

    public DimTime(LocalDate date) {
        this.date = date;
        this.year = date.getYear();
        this.month = date.getMonthValue();
        this.quarter = (date.getMonthValue() - 1) / 3 + 1;
        this.week = date.getDayOfWeek().getValue();
        this.dayOfWeek = date.getDayOfWeek().getValue();
        this.monthName = date.getMonth().name();
        this.yearMonth = date.getYear() + "-" + String.format("%02d", date.getMonthValue());
        this.isWeekend = date.getDayOfWeek().getValue() >= 6;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }

    public Integer getMonth() { return month; }
    public void setMonth(Integer month) { this.month = month; }

    public Integer getQuarter() { return quarter; }
    public void setQuarter(Integer quarter) { this.quarter = quarter; }

    public Integer getWeek() { return week; }
    public void setWeek(Integer week) { this.week = week; }

    public Integer getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(Integer dayOfWeek) { this.dayOfWeek = dayOfWeek; }

    public String getMonthName() { return monthName; }
    public void setMonthName(String monthName) { this.monthName = monthName; }

    public String getYearMonth() { return yearMonth; }
    public void setYearMonth(String yearMonth) { this.yearMonth = yearMonth; }

    public Boolean getIsWeekend() { return isWeekend; }
    public void setIsWeekend(Boolean isWeekend) { this.isWeekend = isWeekend; }
}
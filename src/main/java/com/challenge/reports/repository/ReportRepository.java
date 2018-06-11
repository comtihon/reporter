package com.challenge.reports.repository;

import com.challenge.reports.entity.Report;
import com.challenge.reports.entity.type.ReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Integer> {
    List<Report> findByReportData(LocalDate reportData);

    List<Report> findBySite(ReportEntity site);
}

package com.challenge.reports.repository;

import com.challenge.reports.entity.Report;
import com.challenge.reports.entity.type.ReportEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ReportRepositoryService {

    @Autowired
    private ReportRepository repository;

    public Report save(Report report) {
        return repository.save(report);
    }

    public Report saveIfNotSaved(Report report) {
        if (repository.existsById(report.getId())) {
            throw new AlreadySavedException("Report " + report.getId() + " was already saved");
        } else {
            return save(report);
        }
    }

    public Optional<Report> getById(Integer id) {
        return repository.findById(id);
    }

    public List<Report> getReportsByTime(Integer year, Integer month) {
        return repository.findByReportData(LocalDate.of(year, month, 1));
    }

    public List<Report> getReportBySite(ReportEntity site) {
        return repository.findBySite(site);
    }
}

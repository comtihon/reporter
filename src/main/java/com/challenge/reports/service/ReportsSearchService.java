package com.challenge.reports.service;

import com.challenge.reports.dto.ReportDto;
import com.challenge.reports.entity.Report;
import com.challenge.reports.entity.type.ReportEntity;
import com.challenge.reports.repository.ReportRepositoryService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

@Slf4j
@Service
public class ReportsSearchService {
    @Autowired
    private ReportRepositoryService service;
    @Autowired
    private ModelMapper modelMapper;

    @Async
    @Transactional
    public CompletableFuture<ReportDto> getReport(Integer year, Integer month, ReportEntity site) {
        log.debug("Get report {}-{} {}", year, month, site);
        Integer id = Report.composeId(year, month, site.ordinal());
        return service.getById(id)
                .map(report -> modelMapper.map(report, ReportDto.class))
                .map(CompletableFuture::completedFuture)
                .orElse(CompletableFuture.completedFuture(null));
    }

    @Async
    @Transactional
    public CompletableFuture<ReportDto> getReportAggregate(Integer year, Integer month) {
        log.debug("Get report aggregate {}-{} {}", year, month);
        LocalDate requested = YearMonth.of(year, month).atDay(1);
        List<Report> reports = service.getReportsByTime(year, month);
        ReportDto report = reports.stream()
                .map(r -> modelMapper.map(r, ReportDto.class))
                .collect(collectingAndThen(toList(), ReportDto::new));
        return CompletableFuture.completedFuture(report.withReportData(requested));
    }

    @Async
    @Transactional
    public CompletableFuture<ReportDto> getReportAggregate(ReportEntity site) {
        log.debug("Get report aggregate {}", site);
        List<Report> reports = service.getReportBySite(site);
        ReportDto report = reports.stream()
                .map(r -> modelMapper.map(r, ReportDto.class))
                .collect(collectingAndThen(toList(), ReportDto::new));
        return CompletableFuture.completedFuture(report.withSite(site));
    }
}

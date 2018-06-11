package com.challenge.reports.controller;

import com.challenge.reports.dto.ReportDto;
import com.challenge.reports.entity.type.ReportEntity;
import com.challenge.reports.service.ReportsSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Month;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@RestController
public class ReportsController {
    private final static String DEFAULT_YEAR = "2018";

    @Autowired
    private ReportsSearchService reportsSearchService;

    @RequestMapping(path = "/reports/{month}/{site}", method = RequestMethod.GET)
    public CompletableFuture<ResponseEntity<?>> getReportSiteStatic(@PathVariable String month,
                                                                    @PathVariable String site) throws Exception {
        return getReport(month, DEFAULT_YEAR, site).thenApply(dto -> new ResponseEntity<>(dto, HttpStatus.OK));
    }


    @RequestMapping(value ={"/reports/{month}", "/reports" }, method = RequestMethod.GET)
    public CompletableFuture<ResponseEntity<?>> getReportSiteParam(@PathVariable Optional<String> month,
                                                                   @RequestParam(value = "year", defaultValue = DEFAULT_YEAR) String year,
                                                                   @RequestParam(value = "site", required = false) String site) throws Exception {
        return getReport(month.orElse(null), year, site).thenApply(dto -> new ResponseEntity<>(dto, HttpStatus.OK));
    }


    private CompletableFuture<ReportDto> getReport(String monthStr, String yearStr, String siteStr) {
        Integer year;
        try {
            year = Integer.parseInt(yearStr);
        } catch (Exception e) {
            throw new RuntimeException("Incorrect year: " + yearStr);
        }
        Integer month = getMonthNum(monthStr);
        if (siteStr != null) {
            ReportEntity entity = ReportEntity.getByName(siteStr);
            if (month == null) { // aggregate request by entities for all months
                return reportsSearchService.getReportAggregate(entity);
            } else { // ordinary request for one entity
                return reportsSearchService.getReport(year, month, entity);
            }
        } else if (month != null) { // aggregate request by month for all sites
            return reportsSearchService.getReportAggregate(year, month);
        }
        throw new RuntimeException("Incorrect request.");
    }

    private Integer getMonthNum(String month) {
        if (month == null) {
            return null;
        }
        try {
            return Integer.parseInt(month);
        } catch (Exception ignored) {
            return getMonthByName(month);
        }
    }

    private Integer getMonthByName(String monthStr) {
        Locale locale = Locale.getDefault();
        for (Month month : Month.values()) {
            if (month.getDisplayName(TextStyle.SHORT, locale).equals(monthStr)) { // Jan, Feb ...
                return month.getValue();
            }
            if (month.getDisplayName(TextStyle.FULL, locale).toLowerCase().equals(monthStr.toLowerCase())) { // january, february
                return month.getValue();
            }
        }
        throw new RuntimeException("No such month: " + monthStr);
    }
}

package com.challenge.reports.service;

import com.challenge.reports.dto.ReportDto;
import com.challenge.reports.entity.Report;
import com.challenge.reports.repository.AlreadySavedException;
import com.challenge.reports.repository.ReportRepositoryService;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

@Slf4j
@Component
@Profile("!test")
public class ResourceInitializer {

    @Value("${resource.path}")
    private String resourcePath;
    @Autowired
    private ReportRepositoryService repositoryService;
    @Autowired
    private ModelMapper modelMapper;

    @EventListener
    public void onApplicationEvent(ApplicationStartedEvent event) throws IOException {
        log.info("Processing csv files");
        try (Stream<Path> paths = Files.walk(Paths.get(resourcePath))) {
            paths
                    .filter(Files::isRegularFile)
                    .map(Path::toString)
                    .filter(path -> path.endsWith(".csv"))
                    .forEach(this::loadObjectList);
        }
        log.info("All files were processed");
    }

    /**
     * Parses csv file and saves all entries to the database.
     *
     * @param filePath path to csv file
     */
    protected void loadObjectList(String filePath) {
        log.info("Process {}", filePath);
        try {
            LocalDate reportDate = getReportDate(filePath);
            CsvSchema bootstrapSchema = CsvSchema.emptySchema().withHeader();
            CsvMapper mapper = new CsvMapper();
            File file = new File(filePath);
            MappingIterator<ReportDto> readValues =
                    mapper.readerFor(ReportDto.class).with(bootstrapSchema).readValues(file);
            readValues.readAll().stream()
                    .map(report -> report.withReportData(reportDate))
                    .forEach(this::saveReport);
        } catch (AlreadySavedException ignored) {
            // this csv was already processed. No need to parse through it.
            // if this csv was not processed to an end before - data should be removed manually and it
            // should be reprocessed later.
        } catch (Exception e) {
            log.error("Error occurred while loading object list from file " + filePath, e);
        }
    }

    /**
     * Save report to the database if it was not saved.
     *
     * @param reportDto data from csv
     */
    private void saveReport(ReportDto reportDto) {
        Report entity = modelMapper.map(reportDto, Report.class);
        repositoryService.saveIfNotSaved(entity);
    }

    /**
     * Get LocalDate from path.
     *
     * @param filePath ex. /home/tihon/Projects/crealytics/crealytics_java_challenge/data/2018_02_report.csv
     * @return LocalDate of 2018-02
     */
    private LocalDate getReportDate(String filePath) {
        String[] splitted = filePath.split("/");
        String data = splitted[splitted.length - 1];
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy_MM'_report.csv'");
        return YearMonth.parse(data, formatter).atDay(1);
    }
}

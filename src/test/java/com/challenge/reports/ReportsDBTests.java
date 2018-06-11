package com.challenge.reports;

import com.challenge.reports.entity.Report;
import com.challenge.reports.entity.type.ReportEntity;
import com.challenge.reports.repository.ReportRepositoryService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class ReportsDBTests {
    @Autowired
    private ReportRepositoryService repositoryService;

    @Test
    public void checkReportsLoaded() throws InterruptedException {
        Optional<Report> report = repositoryService.getById(2018010);
        Assert.assertTrue(report.isPresent());
        Report report1 = report.get();
        Assert.assertEquals(ReportEntity.DESKTOP, report1.getSite());
        Assert.assertEquals(LocalDate.parse("2018-01-01"), report1.getReportData());
        Assert.assertEquals((long) 12483775, (long) report1.getRequests());
        Assert.assertEquals((long) 11866157, (long) report1.getImpressions());
        Assert.assertEquals((long) 30965, (long) report1.getClicks());
        Assert.assertEquals((long) 7608, (long) report1.getConversions());
        Assert.assertEquals(BigDecimal.valueOf(23555.46), report1.getRevenue());
        Assert.assertEquals(BigDecimal.valueOf(0.26), report1.getCtr());
        Assert.assertEquals(BigDecimal.valueOf(0.06), report1.getCr());
        Assert.assertEquals(BigDecimal.valueOf(95.05), report1.getFillRate());
        Assert.assertEquals(BigDecimal.valueOf(1.99), report1.getEcpm());

        Assert.assertEquals(4, repositoryService.getReportsByTime(2018, 1).size());
        Assert.assertEquals(4, repositoryService.getReportsByTime(2018, 2).size());
    }
}

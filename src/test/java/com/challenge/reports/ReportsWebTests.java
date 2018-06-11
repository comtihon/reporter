package com.challenge.reports;

import com.challenge.reports.dto.ReportDto;
import com.challenge.reports.entity.type.ReportEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class ReportsWebTests {
    @LocalServerPort
    protected int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void checkReportFetched() {
        //desktop web, 12483775, 11866157, 30965, 7608, 23555.46
        ReportDto report1 =
                this.restTemplate.getForObject(
                        "http://localhost:" + port + "/reports/1/desktop_web", ReportDto.class);
        Assert.assertNotNull(report1);
        Assert.assertEquals(ReportEntity.DESKTOP, report1.getSite());
        Assert.assertEquals(LocalDate.parse("2018-01-01"), report1.getReportData());
        Assert.assertEquals(12483775L, (long) report1.getRequests());
        Assert.assertEquals(11866157L, (long) report1.getImpressions());
        Assert.assertEquals(30965L, (long) report1.getClicks());
        Assert.assertEquals(7608L, (long) report1.getConversions());
        Assert.assertEquals(BigDecimal.valueOf(23555.46), report1.getRevenue());
        Assert.assertEquals(BigDecimal.valueOf(0.26), report1.getCtr());
        Assert.assertEquals(BigDecimal.valueOf(0.06), report1.getCr());
        Assert.assertEquals(BigDecimal.valueOf(95.05), report1.getFillRate());
        Assert.assertEquals(BigDecimal.valueOf(1.99), report1.getEcpm());

        ReportDto report2 =
                this.restTemplate.getForObject(
                        "http://localhost:" + port + "/reports/Jan/desktop_web", ReportDto.class);
        Assert.assertEquals(report1, report2);

        ReportDto report3 =
                this.restTemplate.getForObject(
                        "http://localhost:" + port + "/reports/January/desktop_web", ReportDto.class);
        Assert.assertEquals(report1, report3);

        ReportDto report4 =
                this.restTemplate.getForObject(
                        "http://localhost:" + port + "/reports/january/desktop_web", ReportDto.class);
        Assert.assertEquals(report1, report4);

        ReportDto report5 =
                this.restTemplate.getForObject(
                        "http://localhost:" + port + "/reports/january?site=desktop_web", ReportDto.class);
        Assert.assertEquals(report1, report5);
    }

    @Test
    public void checkAggregateByMonthFetch() {
        ReportDto report1 =
                this.restTemplate.getForObject(
                        "http://localhost:" + port + "/reports/january", ReportDto.class);
        Assert.assertNull(report1.getSite());
        Assert.assertEquals(LocalDate.parse("2018-01-01"), report1.getReportData());
        Assert.assertEquals(34853988L, (long) report1.getRequests());
        Assert.assertEquals(33100001L, (long) report1.getImpressions());
        Assert.assertEquals(86982L, (long) report1.getClicks());
        Assert.assertEquals(21406L, (long) report1.getConversions());
        Assert.assertEquals(BigDecimal.valueOf(65411.76), report1.getRevenue());
        Assert.assertEquals(BigDecimal.valueOf(1.05), report1.getCtr());
        Assert.assertEquals(BigDecimal.valueOf(0.25), report1.getCr());
        Assert.assertEquals(BigDecimal.valueOf(379.79), report1.getFillRate());
        Assert.assertEquals(BigDecimal.valueOf(7.88), report1.getEcpm());
    }

    @Test
    public void checkAggregateBySiteFetch() {
        ReportDto report1 =
                this.restTemplate.getForObject(
                        "http://localhost:" + port + "/reports?site=desktop_web", ReportDto.class);
        System.out.println(report1);
        Assert.assertNull(report1.getReportData());
        Assert.assertEquals(ReportEntity.DESKTOP, report1.getSite());
        Assert.assertEquals(23727650L, (long) report1.getRequests());
        Assert.assertEquals(22232512L, (long) report1.getImpressions());
        Assert.assertEquals(71421L, (long) report1.getClicks());
        Assert.assertEquals(9064L, (long) report1.getConversions());
        Assert.assertEquals(BigDecimal.valueOf(39300.78), report1.getRevenue());
        Assert.assertEquals(BigDecimal.valueOf(0.65), report1.getCtr());
        Assert.assertEquals(BigDecimal.valueOf(0.07), report1.getCr());
        Assert.assertEquals(BigDecimal.valueOf(187.25), report1.getFillRate());
        Assert.assertEquals(BigDecimal.valueOf(3.51), report1.getEcpm());

    }

    @Test
    public void checkIncorrectInput() {
        ResponseEntity<Map> response = restTemplate.exchange(
                "http://localhost:" + port + "/reports/",
                HttpMethod.GET,
                new HttpEntity<>(new HttpHeaders()),
                Map.class);
        Assert.assertEquals(500, response.getStatusCodeValue());
        Assert.assertEquals(response.getBody().get("message"), "Incorrect request.");

        response = restTemplate.exchange(
                "http://localhost:" + port + "/reports/13",
                HttpMethod.GET,
                new HttpEntity<>(new HttpHeaders()),
                Map.class);
        Assert.assertEquals(500, response.getStatusCodeValue());
        Assert.assertEquals(response.getBody().get("message"),
                "java.time.DateTimeException: Invalid value for MonthOfYear (valid values 1 - 12): 13");

        response = restTemplate.exchange(
                "http://localhost:" + port + "/reports/Juni",
                HttpMethod.GET,
                new HttpEntity<>(new HttpHeaders()),
                Map.class);
        Assert.assertEquals(500, response.getStatusCodeValue());
        Assert.assertEquals(response.getBody().get("message"), "No such month: Juni");

        response = restTemplate.exchange(
                "http://localhost:" + port + "/reports?site=desktop",
                HttpMethod.GET,
                new HttpEntity<>(new HttpHeaders()),
                Map.class);
        Assert.assertEquals(500, response.getStatusCodeValue());
        Assert.assertEquals(response.getBody().get("message"), "No such report type: desktop");
    }
}

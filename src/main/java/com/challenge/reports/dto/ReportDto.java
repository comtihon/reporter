package com.challenge.reports.dto;

import com.challenge.reports.entity.type.ReportEntity;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.Wither;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class ReportDto {
    @Wither
    private ReportEntity site;
    private Long requests = 0L;
    private Long impressions = 0L;
    private Long clicks = 0L;
    @Wither
    private LocalDate reportData;
    private Long conversions = 0L;
    @JsonProperty("revenue (USD)")
    private BigDecimal revenue;
    private BigDecimal ctr;
    private BigDecimal cr;
    private BigDecimal fillRate;
    private BigDecimal ecpm;

    public ReportDto(List<ReportDto> aggregates) {
        revenue = BigDecimal.ZERO;
        ctr = BigDecimal.ZERO;
        cr = BigDecimal.ZERO;
        fillRate = BigDecimal.ZERO;
        ecpm = BigDecimal.ZERO;
        for (ReportDto report : aggregates) {
            requests += report.requests;
            impressions += report.impressions;
            clicks += report.clicks;
            conversions += report.conversions;
            revenue = revenue.add(report.revenue);
            ctr = ctr.add(report.ctr);
            cr = cr.add(report.cr);
            fillRate = fillRate.add(report.fillRate);
            ecpm = ecpm.add(report.ecpm);
        }
    }

    @JsonProperty("month")
    public String getMonth() {
        if (reportData == null) {
            return null;
        }
        return reportData.getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault());
    }
}

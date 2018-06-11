package com.challenge.reports.entity;

import com.challenge.reports.entity.type.ReportEntity;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@Entity(name = "reports")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class Report {
    private Integer id;
    @Enumerated(EnumType.STRING)
    private ReportEntity site;
    private LocalDate reportData;
    private Long requests;
    private Long impressions;
    private Long clicks;
    private Long conversions;
    private BigDecimal revenue;
    private BigDecimal ctr;
    private BigDecimal cr;
    private BigDecimal fillRate;
    private BigDecimal ecpm;

    /**
     * Set id as date + entity type.
     * Ex: 2018-02 with type MOBILE will turn into 2018021
     */
    @Id
    @Column(name = "id")
    public Integer getId() {
        if (id == null) {
            id = composeId(reportData.getYear(), reportData.getMonthValue(), site.ordinal());
        }
        return id;
    }

    /**
     * Clicks / impressions * 100
     */
    @Column(name = "ctr")
    public BigDecimal getCtr() {
        if (ctr == null && clicks != null && impressions != null) {
            ctr = BigDecimal.valueOf(clicks).setScale(4, RoundingMode.HALF_UP)
                    .divide(BigDecimal.valueOf(impressions), RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
        }
        return ctr;
    }

    /**
     * Conversions / impressions * 100
     */
    @Column(name = "cr")
    public BigDecimal getCr() {
        if (cr == null && conversions != null && impressions != null) {
            cr = BigDecimal.valueOf(conversions).setScale(4, RoundingMode.HALF_UP)
                    .divide(BigDecimal.valueOf(impressions), RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
        }
        return cr;
    }

    /**
     * Impressions / requests * 100
     */
    @Column(name = "fill_rate")
    public BigDecimal getFillRate() {
        if (fillRate == null && impressions != null && requests != null) {
            fillRate = BigDecimal.valueOf(impressions).setScale(4, RoundingMode.HALF_UP)
                    .divide(BigDecimal.valueOf(requests), RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
        }
        return fillRate;
    }

    /**
     * revenue * 1000 / impressions
     */
    @Column(name = "ecpm")
    public BigDecimal getEcpm() {
        if (ecpm == null && impressions != null && revenue != null) {
            ecpm = revenue.multiply(BigDecimal.valueOf(1000)).setScale(4, RoundingMode.HALF_UP)
                    .divide(BigDecimal.valueOf(impressions), RoundingMode.HALF_UP);
        }
        return ecpm;
    }

    public static Integer composeId(int year, int month, int site) {
        return (year * 100 + month) * 10 + site;
    }
}

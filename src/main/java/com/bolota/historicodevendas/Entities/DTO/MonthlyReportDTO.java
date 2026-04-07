package com.bolota.historicodevendas.Entities.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyReportDTO {
    private String monthName;
    private Integer year;
    private String subtitle;
    private String generatedAtFormatted;
    private String periodLabel;
    private String customMessage;
    private Double totalGross;
    private Double totalProfit;
    private Double averageTicket;
    private Integer totalServices;
    private ArrayList<ServiceReportDTO> services;
}
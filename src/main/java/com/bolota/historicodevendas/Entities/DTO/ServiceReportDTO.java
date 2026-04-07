package com.bolota.historicodevendas.Entities.DTO;

import com.bolota.historicodevendas.Entities.PersistentEntities.ServiceEntityPersistent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ServiceReportDTO {
    private String name;
    private String description;
    private String serviceDateFormatted;
    private String serviceType;
    private String category;
    private double averageServiceDurationMinutes;
    private Double salePrice;
    private Double suggestedPrice;
    private Double serviceExpenses;
    private Double finalProfit;
    public ServiceReportDTO(ServiceEntityPersistent sep) {
        this.name = sep.getName();
        this.description = sep.getDescription();
        this.serviceDateFormatted = sep.getServiceDate().toString();
        this.serviceType = sep.getServiceType();
        this.category = sep.getCategory();
        this.averageServiceDurationMinutes = sep.getAverageServiceDurationMinutes();
        this.salePrice = sep.getSalePrice();
        this.suggestedPrice = sep.getSugestedPrice();
        this.serviceExpenses = sep.getSalePrice()-sep.getLiquidProfit();
        this.finalProfit = sep.getLiquidProfit();
    }
}

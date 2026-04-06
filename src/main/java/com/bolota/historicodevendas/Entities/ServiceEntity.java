package com.bolota.historicodevendas.Entities;

import com.bolota.historicodevendas.Entities.DTO.ServiceEntityDTO;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;

@Slf4j
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class ServiceEntity {

    private String name;
    private String description;
    private String serviceType;
    private String category;
    private int averageServiceDurationMinutes;
    private int quantity;
    private double salePrice;
    private ArrayList<String> fixedSuppliesUsedUUID;
    private ArrayList<String> variableSuppliesUsedUUID;
    private HashMap<String, Double> suppliesQuantity;

    private double costPerMinute;

    private double suggestedPrice;

    private double serviceExpenses;

    private double finalProfit;
    public ServiceEntity(ServiceEntityDTO peDTO, double serviceExpenses, double profitMargin, double desiredAmountPerMonth, int daysWorking, double hoursWorking){
        double minutesWorking = hoursWorking * 60.0;
        double monthlyWorkingMinutes = daysWorking * 4.33 * minutesWorking;


        this.name = peDTO.getName();
        this.description = peDTO.getDescription();
        this.serviceType = peDTO.getServiceType();
        this.category = peDTO.getCategory();
        this.salePrice = peDTO.getSalePrice();
        this.quantity = peDTO.getQuantity();
        this.serviceExpenses = serviceExpenses;
        this.averageServiceDurationMinutes = peDTO.getAverageServiceDurationMinutes();
        this.variableSuppliesUsedUUID = peDTO.getVariableSuppliesUsedUUID();
        this.suppliesQuantity = peDTO.getVariableSuppliesQuantityUsed();
        this.costPerMinute = desiredAmountPerMonth/(daysWorking * 4.33 * minutesWorking);
        this.finalProfit = salePrice - (serviceExpenses + this.averageServiceDurationMinutes * this.costPerMinute);
        this.suggestedPrice =(this.averageServiceDurationMinutes * this.costPerMinute + serviceExpenses) + ((this.averageServiceDurationMinutes * this.costPerMinute + serviceExpenses) * (profitMargin)/100);
        fixedSuppliesUsedUUID = peDTO.getFixedSuppliesUsedUUID();
        variableSuppliesUsedUUID = peDTO.getVariableSuppliesUsedUUID();
    }
}
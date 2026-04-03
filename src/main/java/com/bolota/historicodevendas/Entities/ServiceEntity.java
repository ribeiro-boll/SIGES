package com.bolota.historicodevendas.Entities;

import com.bolota.historicodevendas.Entities.DTO.ProductEntityDTO;
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
    private ArrayList<String> variableSuppliesUsedUUID;
    private HashMap<String, Double> suppliesQuantity;

    private double profitMargin;
    private double desiredAmountPerMonth;
    private int daysWorking;
    private double hoursWorking;
    private double minutesWorking;

    private double costPerMinute;

    private double sugestedPrice;

    private double serviceExpenses;

    private double finalProfit;
    public ServiceEntity(ProductEntityDTO peDTO, double serviceExpenses, double profitMargin,double desiredAmountPerMonth,int daysWorking, double hoursWorking){

        this.profitMargin = profitMargin; // em porcentagem // double
        this.desiredAmountPerMonth = desiredAmountPerMonth;// double
        this.daysWorking = daysWorking; // int
        this.hoursWorking = hoursWorking;// double
        this.minutesWorking = this.hoursWorking * 60.0;// double


        this.name = peDTO.getName();
        this.description = peDTO.getDescription();
        this.serviceType = peDTO.getServiceType();
        this.category = peDTO.getCategory();
        this.salePrice = peDTO.getSalePrice();
        this.quantity = peDTO.getQuantity();
        this.serviceExpenses = serviceExpenses;
        this.costPerMinute = this.desiredAmountPerMonth/(this.daysWorking * this.minutesWorking);
        this.averageServiceDurationMinutes = peDTO.getAverageServiceDurationMinutes();
        this.variableSuppliesUsedUUID = peDTO.getVariableSuppliesUsedUUID();
        this.suppliesQuantity = peDTO.getVariableSuppliesQuantityUsed();
        this.finalProfit = salePrice - (serviceExpenses + this.averageServiceDurationMinutes * this.costPerMinute);
        this.sugestedPrice =(this.averageServiceDurationMinutes * this.costPerMinute + serviceExpenses) + ((this.averageServiceDurationMinutes * this.costPerMinute + serviceExpenses) * (profitMargin)/100);
    }
}
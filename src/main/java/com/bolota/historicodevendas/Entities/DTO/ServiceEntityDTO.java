package com.bolota.historicodevendas.Entities.DTO;

import lombok.*;

import java.util.ArrayList;
import java.util.HashMap;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class ServiceEntityDTO {
    private String name;
    private String description;
    private String serviceType;
    private String category;
    private int quantity;
    private int averageServiceDurationMinutes;
    private double salePrice;
    private ArrayList<String> variableSuppliesUsedUUID; // armazena nomes
    private HashMap<String,Double> variableSuppliesQuantityUsed;
    private ArrayList<String> fixedSuppliesUsedUUID;
    private String serviceNotes;

    public boolean checkIfNull() {
        return this.name == null || this.name.isBlank() || this.description == null || this.description.isBlank() || this.serviceType == null || this.serviceType.isBlank() || this.category == null || this.category.isBlank() || this.quantity <= 0 || this.averageServiceDurationMinutes <= 0 || this.salePrice <= 0 || this.variableSuppliesUsedUUID == null || this.fixedSuppliesUsedUUID == null || this.variableSuppliesQuantityUsed == null || (this.variableSuppliesUsedUUID.isEmpty() && this.fixedSuppliesUsedUUID.isEmpty());
    }
}

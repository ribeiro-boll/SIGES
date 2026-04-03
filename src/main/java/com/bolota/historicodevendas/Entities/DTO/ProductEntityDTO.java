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
public class ProductEntityDTO {
    private String name;
    private String description;
    private String serviceType;
    private String category;
    private int quantity;
    private int averageServiceDurationMinutes;
    private double salePrice;
    private ArrayList<String> variableSuppliesUsedUUID; // armazena nomes
    private HashMap<String,Double> variableSuppliesQuantityUsed;
    private String serviceNotes;

}

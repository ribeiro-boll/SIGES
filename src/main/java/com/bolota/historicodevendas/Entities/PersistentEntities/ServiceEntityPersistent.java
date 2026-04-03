package com.bolota.historicodevendas.Entities.PersistentEntities;

import com.bolota.historicodevendas.Entities.ServiceEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;

import static com.bolota.historicodevendas.Service.ProductService.genJSON;


@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ServiceEntityPersistent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "service_type", nullable = false)
    private String serviceType;

    @Column(name = "category", nullable = false)
    private String category;

    @Column(name = "averageServiceDurationMinutes", nullable = false)
    private int averageServiceDurationMinutes;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "salePrice", nullable = false)
    private double salePrice;

    @Column(name = "liquidProfit", nullable = false)
    private double liquidProfit;

    @Column(name = "sugestedPrice", nullable = false)
    private double sugestedPrice;

    @Column(name = "suppliesUsed", nullable = false)
    private ArrayList<String> suppliesUsed;

    @Column(name = "UUID", nullable = false)
    private String UUID;

    @Column(name = "suppliesQuantity", nullable = false)
    private String suppliesQuantity; // mapper to json

    public ServiceEntityPersistent(ServiceEntity pe, String UUID){
        this.name = pe.getName();
        this.description = pe.getDescription();
        this.serviceType = pe.getServiceType();
        this.category = pe.getCategory();
        this.UUID = UUID;
        this.salePrice = pe.getSalePrice();
        this.sugestedPrice = pe.getSugestedPrice();
        this.liquidProfit = pe.getFinalProfit();
        this.quantity = pe.getQuantity();
        this.averageServiceDurationMinutes = pe.getAverageServiceDurationMinutes();
        this.suppliesUsed = pe.getVariableSuppliesUsedUUID();
        this.suppliesQuantity = genJSON(pe.getSuppliesQuantity());
    }
}

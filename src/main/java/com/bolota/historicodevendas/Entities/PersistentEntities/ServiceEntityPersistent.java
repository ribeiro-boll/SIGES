package com.bolota.historicodevendas.Entities.PersistentEntities;

import com.bolota.historicodevendas.Entities.ServiceEntity;
import jakarta.persistence.*;
import lombok.*;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

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

    @Column(name = "description", nullable = true)
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

    @ElementCollection
    @CollectionTable(name = "variableSuppliesUsedUUIDService", joinColumns = @JoinColumn(name = "UUID"))
    private List<String> variableSuppliesUsedUUID;
    @ElementCollection
    @CollectionTable(name = "fixedSuppliesUsedUUIDService", joinColumns = @JoinColumn(name = "UUID"))
    private List<String> fixedSuppliesUsedUUID;

    @Column(name = "UUID", nullable = false)
    private String UUID;

    @Column(name = "serviceDate", nullable = false)
    private LocalDate serviceDate;

    @Column(name = "suppliesQuantity", nullable = true)
    private String suppliesQuantity; // mapper to json

    public ServiceEntityPersistent(ServiceEntity pe, String UUID){
        this.serviceDate = LocalDate.now(ZoneId.of("America/Sao_Paulo"));
        this.name = pe.getName();
        this.description = pe.getDescription();
        this.serviceType = pe.getServiceType();
        this.category = pe.getCategory();
        this.salePrice = pe.getSalePrice();
        this.liquidProfit = pe.getFinalProfit();
        this.sugestedPrice = pe.getSuggestedPrice();
        this.averageServiceDurationMinutes = pe.getAverageServiceDurationMinutes();
        this.variableSuppliesUsedUUID = pe.getVariableSuppliesUsedUUID();
        this.suppliesQuantity = genJSON(pe.getSuppliesQuantity());
        this.fixedSuppliesUsedUUID = pe.getFixedSuppliesUsedUUID();
        this.UUID = UUID;
    }
    public boolean checkIfNull(){
        return this.name == null || this.serviceType == null || this.category == null || this.variableSuppliesUsedUUID == null || this.fixedSuppliesUsedUUID == null || this.UUID == null || this.suppliesQuantity == null;
    }
}

package com.bolota.historicodevendas.Entities.PersistentEntities;

import com.bolota.historicodevendas.Entities.SuppliesEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SuppliesEntityPersistent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    private String name;
    private String description;
    private double productValue;
    private double measure;
    private double costPerMeasure;
    private String UUID;
    private int counterInUseByServices;
    public SuppliesEntityPersistent(SuppliesEntity se, String UUID){
        this.name = se.getName();
        this.description = se.getDescription();
        this.productValue = se.getProductValue();
        this.measure = se.getMeasure();
        this.counterInUseByServices = 0;
        this.costPerMeasure = this.getProductValue()/this.getMeasure();
        this.UUID = UUID;
    }
}

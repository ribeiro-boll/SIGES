package com.bolota.historicodevendas.Entities.PersistentEntities;

import com.bolota.historicodevendas.Entities.FixedSuppliesEntity;
import com.bolota.historicodevendas.Entities.SuppliesEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.ZoneId;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class FixedSuppliesEntityPersistent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    private String name;
    private String description;
    private double supplyTotalCost;
    private String UUID;
    private LocalDate fixedSupplyDate;
    private Double costPerMinute;
    private int counterInUseByServices;

    public FixedSuppliesEntityPersistent(FixedSuppliesEntity se, String UUID){
        this.name = se.getName();
        this.description = se.getDescription();
        this.supplyTotalCost = se.getSuppliesValue();
        this.counterInUseByServices = 0;
        this.fixedSupplyDate = LocalDate.now(ZoneId.of("America/Sao_Paulo"));
        this.costPerMinute = this.supplyTotalCost/(getDaysInMonth(this.fixedSupplyDate.getMonthValue()) * 24 * 60);
        this.UUID = UUID;
    }
    public int getDaysInMonth(int month) {
        return switch (month) {
            case 1, 3, 5, 7, 8, 10, 12 -> 31;
            case 4, 6, 9, 11 -> 30;
            case 2 -> 28;
            default -> throw new IllegalArgumentException("Mês inválido: " + month);
        };
    }
}
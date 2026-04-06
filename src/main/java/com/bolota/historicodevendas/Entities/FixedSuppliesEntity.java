package com.bolota.historicodevendas.Entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.ZoneId;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class FixedSuppliesEntity {
    private String name;
    private String description;
    private double suppliesValue;
}
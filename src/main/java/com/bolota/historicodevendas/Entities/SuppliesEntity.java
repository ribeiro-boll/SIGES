package com.bolota.historicodevendas.Entities;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SuppliesEntity {
    private String name;
    private String description;
    private double productValue;
    private double measure;
}

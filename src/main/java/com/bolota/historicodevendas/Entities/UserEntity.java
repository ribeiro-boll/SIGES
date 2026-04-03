package com.bolota.historicodevendas.Entities;


import com.bolota.historicodevendas.Entities.DTO.UserEntityDTO;
import jakarta.persistence.*;
import lombok.*;
import com.bolota.historicodevendas.Entities.DTO.UserEntityDTO;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.util.ArrayList;


@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String login;
    @Column(nullable = false,length=60)
    private String passwordHash;
    private double desiredMonthlyIncome;
    private int daysWorkingWeekly;
    private double hoursWorkingDaily;
    private double profitMargin;
    private ArrayList<String> servicesUUIDList;
    private ArrayList<String> variableSuppliesUsedUUID;
    public UserEntity(UserEntityDTO userEntityDTO, String passwordHash){
        this.login = userEntityDTO.getLogin();
        this.passwordHash = passwordHash;
        this.desiredMonthlyIncome = userEntityDTO.getDesiredMonthlyIncome();
        this.daysWorkingWeekly = userEntityDTO.getDaysWorkingWeekly();
        this.hoursWorkingDaily = userEntityDTO.getHoursWorkingDaily();
        this.profitMargin = userEntityDTO.getProfitMargin();
        this.servicesUUIDList = new ArrayList<>();
        this.variableSuppliesUsedUUID = new ArrayList<>();
    }
}
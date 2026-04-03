package com.bolota.historicodevendas.Entities.DTO;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserEntityDTO {
    private String login;
    private String passwordHash;
    private double desiredMonthlyIncome;
    private int daysWorkingWeekly;
    private double hoursWorkingDaily;
    private double profitMargin;

}

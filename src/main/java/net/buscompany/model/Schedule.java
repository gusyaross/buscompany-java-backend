package net.buscompany.model;

import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class Schedule {
    private LocalDate fromDate;
    private LocalDate toDate;

    private String period;
}

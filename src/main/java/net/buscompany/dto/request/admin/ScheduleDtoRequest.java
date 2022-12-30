package net.buscompany.dto.request.admin;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.buscompany.validator.Period;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ScheduleDtoRequest {
    @NotNull
    @FutureOrPresent
    private LocalDate fromDate;

    @NotNull
    @FutureOrPresent
    private LocalDate toDate;

    @NotNull
    @Period
    private String period;
}

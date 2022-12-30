package net.buscompany.dto.request.client;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class OrderDtoRequest {
    @NotNull
    @Min(1)
    private int idTrip;

    @NotNull
    @FutureOrPresent
    private LocalDate dateTrip;

    @NotNull
    @Size(min = 1)
    private List<PassengerDtoRequest> passengers;
}

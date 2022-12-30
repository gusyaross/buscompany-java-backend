package net.buscompany.dto.response.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.buscompany.dto.response.client.PassengerDtoResponse;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class GetOrderDtoResponse {
    private int idOrder;
    private int idTrip;

    private String fromStation;
    private String toStation;

    private String busName;

    private LocalDate dateTrip;

    private LocalTime startTime;

    private int durationInMinutes;

    private int price;
    private int totalPrice;

    private List<PassengerDtoResponse> passengers;
}

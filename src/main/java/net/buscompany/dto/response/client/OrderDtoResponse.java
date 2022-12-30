package net.buscompany.dto.response.client;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OrderDtoResponse {
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

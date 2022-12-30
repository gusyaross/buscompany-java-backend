package net.buscompany.model;

import lombok.*;

import java.time.LocalTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class Trip {
    private int id;

    private Bus bus;

    private String fromStation;
    private String toStation;

    private LocalTime startTime;

    private int durationInMinutes;

    private int price;

    private boolean approved;

    private Schedule schedule;

    private List<AssignedTrip> assignedTrips;
}

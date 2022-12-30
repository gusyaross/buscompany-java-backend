package net.buscompany.model;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class Place {
    private int placeNumber;

    private AssignedTrip assignedTrip;

    private Passenger passenger;
}

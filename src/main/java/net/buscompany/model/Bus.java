package net.buscompany.model;


import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class Bus {
    private int id;

    private String name;

    private int placeCount;
}

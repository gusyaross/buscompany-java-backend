package net.buscompany.dto.request.admin;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class AddBusDtoRequest {
    @NotNull
    private String name;

    @NotNull
    private int placeCount;
}

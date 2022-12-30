package net.buscompany.dto.response.info;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public abstract class GetInfoUserDtoResponse {
    private int id;
    private String firstName;
    private String lastName;
    private String patronymic;
    private String userType;
}

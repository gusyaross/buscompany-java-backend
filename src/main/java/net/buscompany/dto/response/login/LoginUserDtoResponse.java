package net.buscompany.dto.response.login;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public abstract class LoginUserDtoResponse {
    private int id;
    private String firstName;
    private String lastName;
    private String patronymic;
    private String userType;
}

package net.buscompany.dto.response.login;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginAdminDtoResponse extends LoginUserDtoResponse {
    private String position;

    public LoginAdminDtoResponse(int id, String firstName, String lastName, String patronymic, String userType, String position) {
        super(id, firstName, lastName, patronymic, userType);
        this.position = position;
    }
}

package net.buscompany.dto.request.register;

import lombok.*;

import net.buscompany.validator.Name;
import net.buscompany.validator.Password;

import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class RegisterAdminDtoRequest {

    @NotNull
    private String login;

    @Password
    private String password;

    @NotNull
    private String position;

    @NotNull
    @Name
    private String firstName;

    @NotNull
    @Name
    private String lastName;

    @Name
    private String patronymic;

}

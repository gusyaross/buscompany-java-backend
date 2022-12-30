package net.buscompany.dto.request.login;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class LoginUserDtoRequest {

    @NotNull
    private String login;
    @NotNull
    private String password;
}

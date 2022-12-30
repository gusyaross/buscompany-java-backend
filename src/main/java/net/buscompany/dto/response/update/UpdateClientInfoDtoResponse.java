package net.buscompany.dto.response.update;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class UpdateClientInfoDtoResponse {
    private String firstName;
    private String lastName;
    private String patronymic;
    private String userType;
    private String email;
    private String phoneNumber;

}

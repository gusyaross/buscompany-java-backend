package net.buscompany.dto.response.info;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class GetInfoClientDtoResponse extends GetInfoUserDtoResponse {
    private String email;
    private String phoneNumber;

    public GetInfoClientDtoResponse(int id, String firstName, String lastName, String patronymic, String userType, String email, String phoneNumber) {
        super(id, firstName, lastName, patronymic, userType);
        this.email = email;
        this.phoneNumber = phoneNumber;
    }
}

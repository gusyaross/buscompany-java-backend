package net.buscompany.dto.response.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ErrorDto {
    private String errorCode;
    private String field;
    private String message;
}

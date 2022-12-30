package net.buscompany.dto.response.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ErrorDtoResponse {
    private List<ErrorDto> errors;
}

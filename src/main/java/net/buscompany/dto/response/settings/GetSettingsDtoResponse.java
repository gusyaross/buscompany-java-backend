package net.buscompany.dto.response.settings;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class GetSettingsDtoResponse {
    private int maxNameLength;
    private int minPasswordLength;
}

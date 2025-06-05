package denys.mazurenko.dressupbot.dto.external;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ImgBBResponseDto(DataImgBBResponseDto data) {
}

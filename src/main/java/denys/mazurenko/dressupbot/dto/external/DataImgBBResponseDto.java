package denys.mazurenko.dressupbot.dto.external;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DataImgBBResponseDto(
        @JsonProperty("display_url")
        String url) {
}

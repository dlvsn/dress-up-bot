package denys.mazurenko.dressupbot.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@ToString
public class AdvertisementDto {
    private Long id;
    private List<String> imgUrls;
    private String name;
    private String description;
    private BigDecimal price;
}

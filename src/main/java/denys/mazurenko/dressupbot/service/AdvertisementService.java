package denys.mazurenko.dressupbot.service;

import denys.mazurenko.dressupbot.dto.AdvertisementDto;
import denys.mazurenko.dressupbot.dto.AdvertisementPriceNameDto;

import java.util.List;

public interface AdvertisementService {
    AdvertisementDto saveAdd(AdvertisementDto advertisementDto);

    List<AdvertisementDto> findAllAdds();

    List<AdvertisementPriceNameDto> findAdWithNameAndPrice();

    AdvertisementDto findById(Long id);

}

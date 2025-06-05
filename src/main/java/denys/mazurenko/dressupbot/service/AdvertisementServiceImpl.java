package denys.mazurenko.dressupbot.service;

import denys.mazurenko.dressupbot.dto.AdvertisementDto;
import denys.mazurenko.dressupbot.dto.AdvertisementPriceNameDto;
import denys.mazurenko.dressupbot.mapper.AdvertisementMapper;
import denys.mazurenko.dressupbot.model.Advertisement;
import denys.mazurenko.dressupbot.repository.AdRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdvertisementServiceImpl implements AdvertisementService {
    private final AdRepository adRepository;
    private final ImageService imageService;
    private final AdvertisementMapper advertisementMapper;

    @Override
    public AdvertisementDto saveAdd(AdvertisementDto advertisementDto) {
        Advertisement advertisement = advertisementMapper.toEntity(advertisementDto);
        return advertisementMapper.toDto(adRepository.save(advertisement));
    }

    @Override
    public List<AdvertisementDto> findAllAdds() {
        return adRepository.findAll()
                .stream()
                .map(advertisementMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<AdvertisementPriceNameDto> findAdWithNameAndPrice() {
        return adRepository.findAll().stream()
                .map(advertisementMapper::toDtoWithNameAndPrice)
                .toList();
    }

    @Override
    public AdvertisementDto findById(Long id) {
        Advertisement advertisement = adRepository.findById(id).orElseThrow(IllegalArgumentException::new);
        return advertisementMapper.toDto(advertisement);
    }

}
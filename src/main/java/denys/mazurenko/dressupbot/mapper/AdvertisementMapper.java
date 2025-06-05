package denys.mazurenko.dressupbot.mapper;

import denys.mazurenko.dressupbot.config.MapperConfig;
import denys.mazurenko.dressupbot.dto.AdvertisementDto;
import denys.mazurenko.dressupbot.dto.AdvertisementPriceNameDto;
import denys.mazurenko.dressupbot.model.Advertisement;
import denys.mazurenko.dressupbot.model.Image;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import java.util.List;

@Mapper(config = MapperConfig.class)
public interface AdvertisementMapper {

    @AfterMapping
    default void setImageUrls(@MappingTarget AdvertisementDto advertisementDto, Advertisement advertisement) {
        if (advertisement.getImages() != null) {
            List<String> imageIds = advertisement.getImages().stream().map(Image::getUrl).toList();
            advertisementDto.setImgUrls(imageIds);
        }
    }

    @Mapping(target = "id", ignore = true)
    Advertisement toEntity(AdvertisementDto advertisementDto);

    AdvertisementDto toDto(Advertisement advertisement);

    AdvertisementPriceNameDto toDtoWithNameAndPrice(Advertisement advertisement);

    @AfterMapping
    default void setImages(@MappingTarget Advertisement advertisement, AdvertisementDto advertisementDto) {
        List<Image> images = advertisementDto.getImgUrls()
                .stream()
                .map(url -> {
                    Image image = new Image(url);
                    image.setAdvertisements(advertisement);
                    return image;
                }).toList();
        advertisement.setImages(images);
    }
}

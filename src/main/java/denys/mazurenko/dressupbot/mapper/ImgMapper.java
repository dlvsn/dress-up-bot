package denys.mazurenko.dressupbot.mapper;

import denys.mazurenko.dressupbot.config.MapperConfig;
import denys.mazurenko.dressupbot.dto.ImageResponseDto;
import denys.mazurenko.dressupbot.dto.external.ImgBBResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface ImgMapper {
    @Mapping(target = "url", source = "data.url")
    ImageResponseDto fromExternalToInternalDto(ImgBBResponseDto externalDto);
}

package denys.mazurenko.dressupbot.mapper;

import denys.mazurenko.dressupbot.config.MapperConfig;
import denys.mazurenko.dressupbot.dto.CategoryRequestDto;
import denys.mazurenko.dressupbot.dto.CategoryResponseDto;
import denys.mazurenko.dressupbot.model.Category;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface CategoryMapper {
    CategoryResponseDto toDto(Category category);

    Category toEntity(CategoryRequestDto categoryRequestDto);
}

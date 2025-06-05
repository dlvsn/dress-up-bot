package denys.mazurenko.dressupbot.service;

import denys.mazurenko.dressupbot.dto.CategoryRequestDto;
import denys.mazurenko.dressupbot.dto.CategoryResponseDto;

public interface CategoryService {
    CategoryResponseDto saveCategory(CategoryRequestDto requestDto);
}

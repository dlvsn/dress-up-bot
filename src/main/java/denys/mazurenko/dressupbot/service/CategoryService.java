package denys.mazurenko.dressupbot.service;

import denys.mazurenko.dressupbot.dto.CategoryRequestDto;
import denys.mazurenko.dressupbot.dto.CategoryResponseDto;

import java.util.List;

public interface CategoryService {
    CategoryResponseDto saveCategory(CategoryRequestDto requestDto);

    boolean isCategoryExists();

    List<CategoryResponseDto> getAllCategories();
}

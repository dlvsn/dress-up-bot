package denys.mazurenko.dressupbot.service;

import denys.mazurenko.dressupbot.dto.CategoryRequestDto;
import denys.mazurenko.dressupbot.dto.CategoryResponseDto;
import denys.mazurenko.dressupbot.mapper.CategoryMapper;
import denys.mazurenko.dressupbot.model.Category;
import denys.mazurenko.dressupbot.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public CategoryResponseDto saveCategory(CategoryRequestDto requestDto) {
        Category entity = categoryMapper.toEntity(requestDto);
        return categoryMapper.toDto(categoryRepository.save(entity));
    }

    @Override
    public boolean isCategoryExists() {
        return categoryRepository.existsBy();
    }

    @Override
    public List<CategoryResponseDto> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(categoryMapper::toDto)
                .toList();
    }
}

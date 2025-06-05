package denys.mazurenko.dressupbot.service;

import denys.mazurenko.dressupbot.dto.CategoryRequestDto;
import denys.mazurenko.dressupbot.dto.CategoryResponseDto;
import denys.mazurenko.dressupbot.mapper.CategoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryMapper categoryMapper;
    @Override
    public CategoryResponseDto saveCategory(CategoryRequestDto requestDto) {
        return null;
    }
}

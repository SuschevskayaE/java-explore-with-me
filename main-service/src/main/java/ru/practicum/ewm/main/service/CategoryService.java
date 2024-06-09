package ru.practicum.ewm.main.service;

import ru.practicum.ewm.main.dto.CategoryDto;
import ru.practicum.ewm.main.dto.NewCategoryDto;

import java.util.List;


public interface CategoryService {
    CategoryDto addCategory(NewCategoryDto categoryDto);

    void deleteCategory(long catId);

    CategoryDto updateCategory(long catId, CategoryDto categoryDto);

    List<CategoryDto> getCategories(int from, int size);

    CategoryDto getCategory(long catId);

}

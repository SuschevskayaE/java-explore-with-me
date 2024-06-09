package ru.practicum.ewm.main.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.main.common.FromSizeRequest;
import ru.practicum.ewm.main.dto.CategoryDto;
import ru.practicum.ewm.main.dto.NewCategoryDto;
import ru.practicum.ewm.main.entity.CategoryEntity;
import ru.practicum.ewm.main.exeption.DataNotFoundException;
import ru.practicum.ewm.main.exeption.ValidationException;
import ru.practicum.ewm.main.mapper.CategoryMapper;
import ru.practicum.ewm.main.repository.CategoryRepository;
import ru.practicum.ewm.main.repository.EventRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final EventRepository eventRepository;

    @Override
    public CategoryDto addCategory(NewCategoryDto categoryDto) {
        categoryRepository.findByName(categoryDto.getName())
                .ifPresent(f -> {
                    throw new ValidationException(String.format("Имя категории %s уже существует", categoryDto.getName()));
                });
        CategoryEntity entity = categoryRepository.save(categoryMapper.toEntity(categoryDto));
        return categoryMapper.toCategoryDto(entity);
    }

    @Override
    public void deleteCategory(long catId) {
        eventRepository.findByCategoryId(catId).stream().findFirst()
                .ifPresent(f -> {
                    throw new ValidationException(String.format("У категории %s есть события", catId));
                });
        categoryRepository.deleteById(catId);
    }

    @Override
    public CategoryDto updateCategory(long catId, CategoryDto categoryDto) {
        CategoryEntity categoryEntity = categoryMapper.toEntity(categoryDto);
        categoryEntity.setId(catId);

        CategoryEntity entity = categoryRepository.findById(catId)
                .orElseThrow(() -> new DataNotFoundException(String.format("Категория с id %s не найдена", catId)));
        categoryRepository.findByName(categoryDto.getName()).filter(c -> c.getId() != catId)
                .ifPresent(f -> {
                    throw new ValidationException(String.format("Имя категории %s уже существует", categoryDto.getName()));
                });

        categoryMapper.updateEntity(categoryEntity, entity);
        return categoryMapper.toCategoryDto(categoryRepository.save(entity));
    }

    @Override
    public List<CategoryDto> getCategories(int from, int size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        final Pageable pageable = FromSizeRequest.of(from, size, sort);

        return categoryRepository.findAll(pageable)
                .stream()
                .map(categoryMapper::toCategoryDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDto getCategory(long catId) {
        return categoryRepository.findById(catId).map(categoryMapper::toCategoryDto)
                .orElseThrow(() -> new DataNotFoundException(String.format("Категория с id %s не найдена", catId)));
    }
}

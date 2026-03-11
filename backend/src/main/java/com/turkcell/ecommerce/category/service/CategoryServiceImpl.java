package com.turkcell.ecommerce.category.service;

import com.turkcell.ecommerce.category.dto.CategoryRequest;
import com.turkcell.ecommerce.category.dto.CategoryResponse;
import com.turkcell.ecommerce.category.entity.Category;
import com.turkcell.ecommerce.category.exception.CategoryNameAlreadyExistsException;
import com.turkcell.ecommerce.category.exception.CategoryNotFoundException;
import com.turkcell.ecommerce.category.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public CategoryResponse getCategoryById(UUID id) {
        return toResponse(findByIdOrThrow(id));
    }

    @Override
    public CategoryResponse createCategory(CategoryRequest request) {
        assertNameIsUnique(request.getName());
        return toResponse(categoryRepository.save(toEntity(request)));
    }

    @Override
    public CategoryResponse updateCategory(UUID id, CategoryRequest request) {
        Category category = findByIdOrThrow(id);
        assertNameIsUniqueForUpdate(request.getName(), id);
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        return toResponse(categoryRepository.save(category));
    }

    @Override
    public void deleteCategory(UUID id) {
        categoryRepository.delete(findByIdOrThrow(id));
    }

    private Category findByIdOrThrow(UUID id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));
    }

    private void assertNameIsUnique(String name) {
        if (categoryRepository.existsByName(name)) {
            throw new CategoryNameAlreadyExistsException(name);
        }
    }

    private void assertNameIsUniqueForUpdate(String name, UUID id) {
        if (categoryRepository.existsByNameAndIdNot(name, id)) {
            throw new CategoryNameAlreadyExistsException(name);
        }
    }

    private Category toEntity(CategoryRequest request) {
        return new Category(request.getName(), request.getDescription());
    }

    private CategoryResponse toResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();
    }
}

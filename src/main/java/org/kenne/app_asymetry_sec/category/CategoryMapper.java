package org.kenne.app_asymetry_sec.category;

import org.kenne.app_asymetry_sec.category.request.CategoryRequest;
import org.kenne.app_asymetry_sec.category.request.CategoryUpdateRequest;
import org.kenne.app_asymetry_sec.category.response.CategoryResponse;
import org.springframework.stereotype.Service;

@Service
public class CategoryMapper {

    public Category toCategory(CategoryRequest request) {
        return Category.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();
    }

    public void mergeCategory(Category categoryToUpdate, CategoryUpdateRequest request) {
        if (!request.getName().equals(categoryToUpdate.getName())) {
            categoryToUpdate.setName(request.getName());
        }

        if (!request.getDescription().equals(categoryToUpdate.getDescription())) {
            categoryToUpdate.setDescription(request.getDescription());
        }
    }

    public CategoryResponse toCategoryResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .todoCount(category.getTodos().size())
                .build();
    }
}

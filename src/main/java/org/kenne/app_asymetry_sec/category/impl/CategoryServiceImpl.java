package org.kenne.app_asymetry_sec.category.impl;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import jakarta.persistence.EntityNotFoundException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.kenne.app_asymetry_sec.category.Category;
import org.kenne.app_asymetry_sec.category.CategoryMapper;
import org.kenne.app_asymetry_sec.category.CategoryRepository;
import org.kenne.app_asymetry_sec.category.CategoryService;
import org.kenne.app_asymetry_sec.category.request.CategoryRequest;
import org.kenne.app_asymetry_sec.category.request.CategoryUpdateRequest;
import org.kenne.app_asymetry_sec.category.response.CategoryResponse;
import org.kenne.app_asymetry_sec.exception.BusinessException;
import org.kenne.app_asymetry_sec.exception.ErrorCode;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final LoadingCache<String, Category> categoryCache = Caffeine.newBuilder()
            .expireAfterWrite(1, TimeUnit.MINUTES)
            .maximumSize(1000)
            .build(this::findWithCache);

    private final CategoryRepository categoryRepository;

    private final CategoryMapper categoryMapper;

    @Override
    public @NonNull String createCategory(
            @NonNull CategoryRequest request,
            @NonNull String userId
    ) {
        final boolean alreadyExists = categoryRepository.existsByNameAndOwnerId(request.getName(), userId);

        if (alreadyExists) {
            throw new BusinessException(ErrorCode.CATEGORY_ALREADY_EXISTS_FOR_USER);
        }

        final var category = categoryMapper.toCategory(request);

        return categoryRepository.save(category).getId();
    }

    @Override
    public void updateCategory(
            @NonNull CategoryUpdateRequest request,
            @NonNull String categoryId,
            @NonNull String userId
    ) {
        final var existingCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Category with id '%s' not found.", categoryId)));

        checkCategoryUnicityForUser(request, userId);

        categoryMapper.mergeCategory(existingCategory, request);
        categoryRepository.save(existingCategory);

        categoryCache.invalidate(categoryId);
    }

    private void checkCategoryUnicityForUser(
            @NonNull CategoryUpdateRequest request,
            @NonNull String userId
    ) {
        final boolean alreadyExists = categoryRepository.existsByNameAndOwnerId(request.getName(), userId);

        if (alreadyExists) {
            throw new BusinessException(ErrorCode.CATEGORY_ALREADY_EXISTS_FOR_USER);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> findAllByOwnerId(@NonNull String userId) {
        return categoryRepository.findAllByUserId(userId)
                .stream()
                .map(categoryMapper::toCategoryResponse)
                .toList();
    }

    @Override
    public @Nullable Category findWithCache(@NonNull String categoryId) {
        return categoryRepository.findById(categoryId)
                .orElse(null);
    }

    @Override
    public CategoryResponse findById(@NonNull String categoryId) {
        return Optional.ofNullable(categoryCache.get(categoryId))
                .map(this.categoryMapper::toCategoryResponse)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Category with id '%s' not found.", categoryId)));
    }

    @Override
    public void deleteCategory(@NonNull String categoryId) {
        categoryCache.invalidate(categoryId);
        // todo:
        // mark the category as deleted instead of deleting it
        // the scheduler should pick up all the marked categories and delete them after a certain period
    }
}

package org.kenne.app_asymetry_sec.category.security;

import jakarta.persistence.EntityNotFoundException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kenne.app_asymetry_sec.category.CategoryService;
import org.kenne.app_asymetry_sec.security.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategorySecurityService {

    private final CategoryService categoryService;

    @Transactional(readOnly = true)
    public boolean isCategoryOwner(@NonNull String categoryId) {
        final String userId = SecurityUtils.getUserIdFromSecurityContext();

        var category = Optional.ofNullable(categoryService.findWithCache(categoryId))
                .orElseThrow(() -> new EntityNotFoundException(String.format("Category with id '%s' not found.", categoryId)));

        return category.getCreatedBy().equals(userId);
    }
}

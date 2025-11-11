package org.kenne.app_asymetry_sec.category;

import lombok.NonNull;
import org.kenne.app_asymetry_sec.category.request.CategoryRequest;
import org.kenne.app_asymetry_sec.category.request.CategoryUpdateRequest;
import org.kenne.app_asymetry_sec.category.response.CategoryResponse;
import org.springframework.lang.Nullable;

import java.util.List;

public interface CategoryService {

    String createCategory(@NonNull CategoryRequest request, @NonNull String userId);

    void updateCategory(@NonNull CategoryUpdateRequest request, @NonNull String categoryId, @NonNull String userId);

    List<CategoryResponse> findAllByOwnerId(@NonNull String userId);

    @Nullable
    Category findWithCache(@NonNull String categoryId);

    CategoryResponse findById(@NonNull String categoryId);

    void deleteCategory(@NonNull String categoryId);
}

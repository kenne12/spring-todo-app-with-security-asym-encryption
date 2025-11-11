package org.kenne.app_asymetry_sec.category;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.kenne.app_asymetry_sec.category.request.CategoryRequest;
import org.kenne.app_asymetry_sec.category.request.CategoryUpdateRequest;
import org.kenne.app_asymetry_sec.category.response.CategoryResponse;
import org.kenne.app_asymetry_sec.common.RestResponse;
import org.kenne.app_asymetry_sec.user.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@Tag(name = "Categories", description = "Categories API")
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<RestResponse> createCategory(
            @RequestBody @Valid final CategoryRequest request,
            final Authentication authentication
    ) {
        final String savedCategoryId = categoryService.createCategory(request, ((User) authentication.getPrincipal()).getId());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new RestResponse(savedCategoryId));
    }

    @PutMapping("/{categoryId}")
    @PreAuthorize("@categorySecurityService.isCategoryOwner(#categoryId)")
    public ResponseEntity<Void> updateCategory(
            @PathVariable("categoryId") final @NonNull String categoryId,
            @RequestBody @Valid final CategoryUpdateRequest request,
            final Authentication authentication
    ) {
        categoryService.updateCategory(request, categoryId, ((User) authentication.getPrincipal()).getId());

        return ResponseEntity.accepted().build();
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> findAllCategories(final Authentication authentication) {
        final var categories = categoryService.findAllByOwnerId(((User) authentication.getPrincipal()).getId());

        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{categoryId}")
    @PreAuthorize("@categorySecurityService.isCategoryOwner(#categoryId)")
    public ResponseEntity<CategoryResponse> findCategoryById(@PathVariable("categoryId") final @NonNull String categoryId) {
        final var category = categoryService.findById(categoryId);

        return ResponseEntity.ok(category);
    }

    @DeleteMapping("/{categoryId}")
    @PreAuthorize("@categorySecurityService.isCategoryOwner(#categoryId)")
    public ResponseEntity<Void> deleteCategoryById(@PathVariable("categoryId") final @NonNull String categoryId) {
        categoryService.deleteCategory(categoryId);

        return ResponseEntity.noContent().build();
    }
}

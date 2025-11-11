package org.kenne.app_asymetry_sec.category.impl;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kenne.app_asymetry_sec.category.Category;
import org.kenne.app_asymetry_sec.category.CategoryMapper;
import org.kenne.app_asymetry_sec.category.CategoryRepository;
import org.kenne.app_asymetry_sec.category.request.CategoryRequest;
import org.kenne.app_asymetry_sec.category.request.CategoryUpdateRequest;
import org.kenne.app_asymetry_sec.category.response.CategoryResponse;
import org.kenne.app_asymetry_sec.exception.BusinessException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CategoryServiceImpl Unit Test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private Category category;
    private CategoryRequest createRequest;
    private CategoryUpdateRequest updateRequest;
    private CategoryResponse response;

    @BeforeEach
    void setUp() {
        category = Category.builder()
                .id("cat-1")
                .name("Work")
                .description("Work related")
                .todos(Collections.emptyList())
                .build();

        createRequest = CategoryRequest.builder()
                .name("Work")
                .description("Work related")
                .build();

        updateRequest = CategoryUpdateRequest.builder()
                .name("Work Updated")
                .description("Updated desc")
                .build();

        response = CategoryResponse.builder()
                .id("cat-1")
                .name("Work")
                .description("Work related")
                .todoCount(0)
                .build();
    }

    @Nested
    @DisplayName("Create Category Tests")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class CreateCategoryTests {
        @Test
        @Order(1)
        @DisplayName("Should create category when unique for user")
        void shouldCreateCategoryWhenUnique() {
            String userId = "user-1";
            when(categoryRepository.existsByNameAndOwnerId(createRequest.getName(), userId))
                    .thenReturn(false);
            when(categoryMapper.toCategory(createRequest)).thenReturn(category);
            when(categoryRepository.save(category)).thenReturn(category);

            String id = categoryService.createCategory(createRequest, userId);

            assertEquals("cat-1", id);
            verify(categoryRepository, times(1)).existsByNameAndOwnerId("Work", userId);
            verify(categoryMapper, times(1)).toCategory(createRequest);
            verify(categoryRepository, times(1)).save(category);
        }

        @Test
        @Order(2)
        @DisplayName("Should throw BusinessException when category already exists for user")
        void shouldThrowWhenCategoryExistsForUser() {
            String userId = "user-1";
            when(categoryRepository.existsByNameAndOwnerId(createRequest.getName(), userId))
                    .thenReturn(true);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> categoryService.createCategory(createRequest, userId));

            assertEquals("Category already exists for this user", ex.getMessage());
            verify(categoryRepository, times(1)).existsByNameAndOwnerId("Work", userId);
            verifyNoInteractions(categoryMapper);
            verify(categoryRepository, never()).save(any(Category.class));
        }
    }

    @Nested
    @DisplayName("Update Category Tests")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class UpdateCategoryTests {
        @Test
        @Order(3)
        @DisplayName("Should throw EntityNotFoundException when category not found on update")
        void shouldThrowWhenCategoryNotFoundOnUpdate() {
            String userId = "user-1";
            String categoryId = "cat-404";
            when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

            EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                    () -> categoryService.updateCategory(updateRequest, categoryId, userId));

            assertEquals("Category with id 'cat-404' not found.", ex.getMessage());
            verify(categoryRepository, times(1)).findById(categoryId);
            verify(categoryRepository, never()).existsByNameAndOwnerId(anyString(), anyString());
            verify(categoryMapper, never()).mergeCategory(any(Category.class), any(CategoryUpdateRequest.class));
            verify(categoryRepository, never()).save(any(Category.class));
        }

        @Test
        @Order(4)
        @DisplayName("Should throw BusinessException when new name already exists for user")
        void shouldThrowWhenNewNameExists() {
            String userId = "user-1";
            String categoryId = "cat-1";
            when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
            when(categoryRepository.existsByNameAndOwnerId(updateRequest.getName(), userId)).thenReturn(true);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> categoryService.updateCategory(updateRequest, categoryId, userId));

            assertEquals("Category already exists for this user", ex.getMessage());
            verify(categoryRepository, times(1)).findById(categoryId);
            verify(categoryRepository, times(1)).existsByNameAndOwnerId(updateRequest.getName(), userId);
            verify(categoryMapper, never()).mergeCategory(any(Category.class), any(CategoryUpdateRequest.class));
            verify(categoryRepository, never()).save(any(Category.class));
        }

        @Test
        @Order(5)
        @DisplayName("Should update, save and invalidate cache; subsequent findById reloads from repository")
        void shouldUpdateAndInvalidateCache() {
            String userId = "user-1";
            String categoryId = "cat-1";

            // 1) First call to findById loads into cache
            when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
            when(categoryMapper.toCategoryResponse(category)).thenReturn(response);

            // load cache
            CategoryResponse first = categoryService.findById(categoryId);
            assertNotNull(first);

            // Verify repository was called once for the initial load
            verify(categoryRepository, times(1)).findById(categoryId);
            verify(categoryMapper, times(1)).toCategoryResponse(category);

            // 2) Update succeeds and invalidates cache
            reset(categoryRepository, categoryMapper);
            when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
            when(categoryRepository.existsByNameAndOwnerId(updateRequest.getName(), userId)).thenReturn(false);

            categoryService.updateCategory(updateRequest, categoryId, userId);

            verify(categoryRepository, times(1)).findById(categoryId);
            verify(categoryRepository, times(1)).existsByNameAndOwnerId(updateRequest.getName(), userId);
            verify(categoryMapper, times(1)).mergeCategory(category, updateRequest);
            verify(categoryRepository, times(1)).save(category);

            // 3) After invalidation, a new findById should hit repository again
            reset(categoryRepository, categoryMapper);
            when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
            when(categoryMapper.toCategoryResponse(category)).thenReturn(response);

            CategoryResponse afterUpdate = categoryService.findById(categoryId);
            assertNotNull(afterUpdate);
            verify(categoryRepository, times(1)).findById(categoryId);
            verify(categoryMapper, times(1)).toCategoryResponse(category);
        }
    }

    @Nested
    @DisplayName("Find and Cache Category Tests")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class FindAndCacheTests {
        @Test
        @Order(6)
        @DisplayName("findAllByOwnerId should map list and return responses")
        void shouldFindAllByOwnerId() {
            when(categoryRepository.findAllByUserId("user-1")).thenReturn(List.of(category));
            when(categoryMapper.toCategoryResponse(category)).thenReturn(response);

            var results = categoryService.findAllByOwnerId("user-1");
            assertEquals(1, results.size());
            verify(categoryRepository, times(1)).findAllByUserId("user-1");
            verify(categoryMapper, times(1)).toCategoryResponse(category);
        }

        @Test
        @Order(7)
        @DisplayName("findAllByOwnerId should return empty list and not call mapper when none")
        void shouldReturnEmptyListWhenNoCategories() {
            when(categoryRepository.findAllByUserId("user-1")).thenReturn(Collections.emptyList());

            var results = categoryService.findAllByOwnerId("user-1");
            assertNotNull(results);
            assertEquals(0, results.size());
            verify(categoryRepository, times(1)).findAllByUserId("user-1");
            verify(categoryMapper, never()).toCategoryResponse(any(Category.class));
        }

        @Test
        @Order(8)
        @DisplayName("findWithCache should return entity when present, null otherwise")
        void shouldFindWithCacheReturnNullable() {
            when(categoryRepository.findById("cat-1")).thenReturn(Optional.of(category));
            Category found = categoryService.findWithCache("cat-1");
            assertNotNull(found);

            when(categoryRepository.findById("cat-2")).thenReturn(Optional.empty());
            Category notFound = categoryService.findWithCache("cat-2");
            assertNull(notFound);
        }

        @Test
        @Order(9)
        @DisplayName("findById should use cache (repo called once, mapper called each time)")
        void shouldUseCacheOnFindById() {
            when(categoryRepository.findById("cat-1")).thenReturn(Optional.of(category));
            when(categoryMapper.toCategoryResponse(category)).thenReturn(response);

            CategoryResponse r1 = categoryService.findById("cat-1");
            CategoryResponse r2 = categoryService.findById("cat-1");

            assertNotNull(r1);
            assertNotNull(r2);
            // Repository only once due to cache
            verify(categoryRepository, times(1)).findById("cat-1");
            // Mapper called for each service call (mapping happens after cache fetch)
            verify(categoryMapper, times(2)).toCategoryResponse(category);
        }

        @Test
        @Order(10)
        @DisplayName("findById should throw when category not found")
        void shouldThrowWhenCategoryNotFoundOnFindById() {
            when(categoryRepository.findById("missing")).thenReturn(Optional.empty());

            EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                    () -> categoryService.findById("missing"));

            assertEquals("Category with id 'missing' not found.", ex.getMessage());
            verify(categoryRepository, times(1)).findById("missing");
            verify(categoryMapper, never()).toCategoryResponse(any(Category.class));
        }
    }

    @Nested
    @DisplayName("Delete Category Tests")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class DeleteCategoryTests {
        @Test
        @Order(11)
        @DisplayName("deleteCategory should invalidate cache (subsequent find reloads from repo)")
        void deleteShouldInvalidateCache() {
            String categoryId = "cat-1";
            // Prime the cache
            when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
            when(categoryMapper.toCategoryResponse(category)).thenReturn(response);
            var r1 = categoryService.findById(categoryId);
            assertNotNull(r1);
            verify(categoryRepository, times(1)).findById(categoryId);

            // Delete (invalidate) and then next find should hit repo again
            categoryService.deleteCategory(categoryId);

            reset(categoryRepository, categoryMapper);
            when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
            when(categoryMapper.toCategoryResponse(category)).thenReturn(response);

            var r2 = categoryService.findById(categoryId);
            assertNotNull(r2);
            verify(categoryRepository, times(1)).findById(categoryId);
            verify(categoryMapper, times(1)).toCategoryResponse(category);
        }
    }
}

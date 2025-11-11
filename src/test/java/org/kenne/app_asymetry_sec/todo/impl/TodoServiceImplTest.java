package org.kenne.app_asymetry_sec.todo.impl;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kenne.app_asymetry_sec.category.Category;
import org.kenne.app_asymetry_sec.category.CategoryRepository;
import org.kenne.app_asymetry_sec.todo.Todo;
import org.kenne.app_asymetry_sec.todo.TodoMapper;
import org.kenne.app_asymetry_sec.todo.TodoRepository;
import org.kenne.app_asymetry_sec.todo.request.TodoRequest;
import org.kenne.app_asymetry_sec.todo.request.TodoUpdateRequest;
import org.kenne.app_asymetry_sec.todo.response.TodoResponse;
import org.kenne.app_asymetry_sec.user.User;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("TodoServiceImpl Unit Test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TodoServiceImplTest {

    @Mock
    private TodoRepository todoRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private TodoMapper todoMapper;

    @InjectMocks
    private TodoServiceImpl todoService;

     Category testCategory;

     Todo testTodo;

     TodoRequest todoRequest;

     TodoUpdateRequest testTodoUpdateRequest;

     TodoResponse testTodoResponse;

    @BeforeEach
    void setUp() {
        this.testCategory = Category.builder()
                .id("cat123")
                .name("Work")
                .description("Work related tasks")
                .build();

        final User testUser = User.builder()
                .id("user123")
                .firstName("John")
                .lastName("Doe")
                .email("johndoe@mail.com")
                .build();

        this.testTodo = Todo.builder()
                .id("todo123")
                .title("Finish report")
                .description("Complete the annual report")
                .category(testCategory)
                .user(testUser)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(1))
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(17, 0))
                .done(false)
                .build();

        this.todoRequest = TodoRequest.builder()
                .title("Finish report")
                .description("Complete the annual report")
                .categoryId("cat123")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(1))
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(17, 0))
                .build();

        this.testTodoUpdateRequest = TodoUpdateRequest.builder()
                .title("Finish updated report")
                .description("Complete the updated annual report")
                .categoryId("cat123")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(2))
                .startTime(LocalTime.of(10, 0))
                .endTime(LocalTime.of(18, 0))
                .done(true)
                .build();

        this.testTodoResponse = TodoResponse.builder()
                .id("todo123")
                .title("Finish report")
                .description("Complete the annual report")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(1))
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(17, 0))
                .done(false)
                .build();

    }

    @Nested
    @DisplayName("Create Todo Tests")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class CreateTodoTests {

        @Test
        @DisplayName("Should create a new todo successfully when valid request is provided")
        @Order(1)
        void shouldCreateTodoSuccessfully() {
            // Given
            final String userId = "user123";
            when(categoryRepository.findByIdAndUserId(todoRequest.getCategoryId(), userId))
                    .thenReturn(Optional.of(testCategory));

            when(todoMapper.toTodo(todoRequest, testCategory))
                    .thenReturn(testTodo);

            when(todoRepository.save(testTodo))
                    .thenReturn(testTodo);

            // When
            final String result = todoService.createTodo(todoRequest, userId);

            // Then
            assertNotNull(result);
            assertEquals("todo123", result);

            verify(categoryRepository, times(1))
                    .findByIdAndUserId(todoRequest.getCategoryId(), userId);
        }

        @Test
        @DisplayName("Should throw EntityNotFoundException when category does not exist during todo creation")
        @Order(2)
        void shouldThrowExceptionWhenCategoryNotFoundDuringCreate() {
            // Given
            final String userId = "user123";
            when(categoryRepository.findByIdAndUserId(todoRequest.getCategoryId(), userId))
                    .thenReturn(Optional.empty());

            // when & then
            EntityNotFoundException entityNotFoundException = assertThrows(
                    EntityNotFoundException.class,
                    () -> todoService.createTodo(todoRequest, userId)
            );

            assertEquals(String.format("Category with id '%s' and user '%s' not found.", todoRequest.getCategoryId(), userId), entityNotFoundException.getMessage());

            verify(categoryRepository, times(1))
                    .findByIdAndUserId(todoRequest.getCategoryId(), userId);

            verify(todoRepository, never())
                    .save(any(Todo.class));
            verifyNoInteractions(TodoServiceImplTest.this.todoMapper);
            verifyNoInteractions(TodoServiceImplTest.this.todoRepository);
        }
    }

    @Nested
    @DisplayName("Update Todo Tests")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class UpdateTodoTests {

        @Test
        @DisplayName("Should successfully update an existing todo when valid request is provided")
        @Order(3)
        void shouldUpdateTodoSuccessfully() {
            // Given
            final String userId = "user123";
            final String todoId = "todo123";

            when(todoRepository.findById(todoId))
                    .thenReturn(Optional.of(testTodo));

            when(categoryRepository.findByIdAndUserId(testTodoUpdateRequest.getCategoryId(), userId))
                    .thenReturn(Optional.of(testCategory));

            when(todoRepository.save(any(Todo.class)))
                    .thenReturn(testTodo);

            // When
            todoService.updateTodo(testTodoUpdateRequest, todoId, userId);

            // Then
            verify(todoRepository, times(1))
                    .findById(todoId);
            verify(categoryRepository, times(1))
                    .findByIdAndUserId(testTodoUpdateRequest.getCategoryId(), userId);
            verify(todoMapper, times(1))
                    .mergeTodo(testTodo, testTodoUpdateRequest);
            verify(todoRepository, times(1))
                    .save(testTodo);
        }

        @Test
        @DisplayName("Should throw EntityNotFoundException when todo does not exist")
        @Order(4)
        void shouldThrowExceptionWhenTodoNotFoundDuringUpdate() {
            // Given
            final String userId = "user123";
            final String todoId = "todo123";
            when(todoRepository.findById(todoId))
                    .thenReturn(Optional.empty());

            // when & then
            EntityNotFoundException entityNotFoundException = assertThrows(
                    EntityNotFoundException.class,
                    () -> todoService.updateTodo(testTodoUpdateRequest, todoId, userId)
            );
            assertEquals(String.format("Todo not found with id '%s'.", todoId), entityNotFoundException.getMessage());

            verify(todoRepository, times(1))
                    .findById(todoId);
            verify(categoryRepository, never())
                    .findByIdAndUserId(any(String.class), any(String.class));
            verify(todoMapper, never())
                    .mergeTodo(any(Todo.class), any(TodoUpdateRequest.class));
            verify(todoRepository, never())
                    .save(any(Todo.class));
        }
    }

    @Nested
    @DisplayName("Find Todo By ID Tests")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class FindTodoByIdTests {

        @Test
        @DisplayName("Should successfully find a todo by ID")
        @Order(5)
        void shouldFindTodoByIdSuccessfully() {
            // Given
            final String todoId = "todo123";
            when(todoRepository.findById(todoId))
                    .thenReturn(Optional.of(testTodo));
            when(todoMapper.toTodoResponse(testTodo))
                    .thenReturn(testTodoResponse);

            // When
            final TodoResponse result = todoService.findTodoById(todoId);

            // Then
            assertNotNull(result);
            verify(todoRepository, times(1))
                    .findById(todoId);
            verify(todoMapper, times(1))
                    .toTodoResponse(testTodo);
        }

        @Test
        @DisplayName("Should throw EntityNotFoundException when todo does not exist")
        @Order(6)
        void shouldThrowExceptionWhenTodoNotFoundDuringFindById() {
            // Given
            final String todoId = "todo123";
            when(todoRepository.findById(todoId))
                    .thenReturn(Optional.empty());

            // when & then
            EntityNotFoundException entityNotFoundException = assertThrows(
                    EntityNotFoundException.class,
                    () -> todoService.findTodoById(todoId)
            );
            assertEquals(String.format("Todo not found with id '%s'", todoId), entityNotFoundException.getMessage());

            verify(todoRepository, times(1))
                    .findById(todoId);
            verify(todoMapper, never())
                    .toTodoResponse(any(Todo.class));
        }
    }

    @Nested
    @DisplayName("List and Delete Todo Tests")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class ListAndDeleteTodoTests {

        @Test
        @DisplayName("Should return all today's todos for a user and map them to responses")
        @Order(7)
        void shouldReturnAllTodosForToday() {
            // Given
            final String userId = "user123";
            when(todoRepository.findAllByUserId(userId))
                    .thenReturn(List.of(testTodo));
            when(todoMapper.toTodoResponse(testTodo))
                    .thenReturn(testTodoResponse);

            // When
            final var results = todoService.findAllTodosForToday(userId);

            // Then
            assertNotNull(results);
            assertEquals(1, results.size());
            verify(todoRepository, times(1))
                    .findAllByUserId(userId);
            verify(todoMapper, times(1))
                    .toTodoResponse(testTodo);
        }

        @Test
        @DisplayName("Should return empty list when user has no todos today")
        @Order(8)
        void shouldReturnEmptyListForToday() {
            // Given
            final String userId = "user123";
            when(todoRepository.findAllByUserId(userId))
                    .thenReturn(Collections.emptyList());

            // When
            final var results = todoService.findAllTodosForToday(userId);

            // Then
            assertNotNull(results);
            assertEquals(0, results.size());
            verify(todoRepository, times(1))
                    .findAllByUserId(userId);
            verify(todoMapper, never())
                    .toTodoResponse(any(Todo.class));
        }

        @Test
        @DisplayName("Should return todos by category for user and map them")
        @Order(9)
        void shouldReturnTodosByCategory() {
            // Given
            final String userId = "user123";
            final String categoryId = "cat123";
            when(todoRepository.findAllByUserIdAndCategoryId(userId, categoryId))
                    .thenReturn(List.of(testTodo));
            when(todoMapper.toTodoResponse(testTodo))
                    .thenReturn(testTodoResponse);

            // When
            final var results = todoService.findTodosByCategoryId(categoryId, userId);

            // Then
            assertNotNull(results);
            assertEquals(1, results.size());
            verify(todoRepository, times(1))
                    .findAllByUserIdAndCategoryId(userId, categoryId);
            verify(todoMapper, times(1))
                    .toTodoResponse(testTodo);
        }

        @Test
        @DisplayName("Should return all due todos for user and map them")
        @Order(10)
        void shouldReturnAllDueTodos() {
            // Given
            final String userId = "user123";
            when(todoRepository.findAllDueTodos(userId))
                    .thenReturn(List.of(testTodo));
            when(todoMapper.toTodoResponse(testTodo))
                    .thenReturn(testTodoResponse);

            // When
            final var results = todoService.findAllDueTodos(userId);

            // Then
            assertNotNull(results);
            assertEquals(1, results.size());
            verify(todoRepository, times(1))
                    .findAllDueTodos(userId);
            verify(todoMapper, times(1))
                    .toTodoResponse(testTodo);
        }

        @Test
        @DisplayName("Should delete todo by id")
        @Order(11)
        void shouldDeleteTodoById() {
            // When
            todoService.deleteTodoById("todo123");

            // Then
            verify(todoRepository, times(1))
                    .deleteById("todo123");
        }
    }
}
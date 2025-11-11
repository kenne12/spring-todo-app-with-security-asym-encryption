package org.kenne.app_asymetry_sec.todo.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kenne.app_asymetry_sec.category.Category;
import org.kenne.app_asymetry_sec.category.CategoryRepository;
import org.kenne.app_asymetry_sec.todo.Todo;
import org.kenne.app_asymetry_sec.todo.TodoMapper;
import org.kenne.app_asymetry_sec.todo.TodoRepository;
import org.kenne.app_asymetry_sec.todo.TodoService;
import org.kenne.app_asymetry_sec.todo.request.TodoRequest;
import org.kenne.app_asymetry_sec.todo.request.TodoUpdateRequest;
import org.kenne.app_asymetry_sec.todo.response.TodoResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TodoServiceImpl implements TodoService {

    private final TodoRepository todoRepository;

    private final CategoryRepository categoryRepository;

    private final TodoMapper todoMapper;

    @Override
    public String createTodo(
            @NonNull TodoRequest request,
            @NonNull String userId
    ) {
        final var category = checkAndReturnCategory(request.getCategoryId(), userId);
        Todo todo = todoMapper.toTodo(request, category);

        return todoRepository.save(todo).getId();
    }

    @Override
    public void updateTodo(
            @NonNull TodoUpdateRequest request,
            @NonNull String todoId,
            @NonNull String userId
    ) {
        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Todo not found with id '%s'.", todoId)));

        final Category category = checkAndReturnCategory(request.getCategoryId(), userId);

        this.todoMapper.mergeTodo(todo, request);
        todo.setCategory(category);
        todoRepository.save(todo);
    }

    @Override
    public TodoResponse findTodoById(@NonNull String todoId) {
        return todoRepository.findById(todoId)
                .map(todoMapper::toTodoResponse)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Todo not found with id '%s'", todoId)));
    }

    @Override
    public List<TodoResponse> findAllTodosForToday(@NonNull String userId) {
        return this.todoRepository.findAllByUserId(userId).stream()
                .map(todoMapper::toTodoResponse)
                .toList();
    }

    @Override
    public List<TodoResponse> findTodosByCategoryId(
            @NonNull String categoryId,
            @NonNull String userId
    ) {
        return this.todoRepository.findAllByUserIdAndCategoryId(userId, categoryId)
                .stream()
                .map(todoMapper::toTodoResponse)
                .toList();
    }

    @Override
    public List<TodoResponse> findAllDueTodos(@NonNull String userId) {
        return this.todoRepository.findAllDueTodos(userId).stream()
                .map(todoMapper::toTodoResponse)
                .toList();
    }

    @Override
    public void deleteTodoById(@NonNull String todoId) {
        todoRepository.deleteById(todoId);
    }

    private @NonNull Category checkAndReturnCategory(
            @NonNull String categoryId,
            @NonNull String userId
    ) {
        return categoryRepository.findByIdAndUserId(categoryId, userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Category with id '%s' and user '%s' not found.", categoryId, userId)));
    }
}

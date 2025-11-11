package org.kenne.app_asymetry_sec.todo;

import lombok.NonNull;
import org.kenne.app_asymetry_sec.todo.request.TodoRequest;
import org.kenne.app_asymetry_sec.todo.request.TodoUpdateRequest;
import org.kenne.app_asymetry_sec.todo.response.TodoResponse;

import java.util.List;

public interface TodoService {

    String createTodo(@NonNull TodoRequest request, @NonNull String userId);

    void updateTodo(@NonNull TodoUpdateRequest request, @NonNull String todoId, @NonNull String userId);

    TodoResponse findTodoById(@NonNull String todoId);

    List<TodoResponse> findAllTodosForToday(@NonNull String userId);

    List<TodoResponse> findTodosByCategoryId(@NonNull String categoryId, @NonNull String userId);

    List<TodoResponse> findAllDueTodos(@NonNull String userId);

    void deleteTodoById(@NonNull String todoId);
}

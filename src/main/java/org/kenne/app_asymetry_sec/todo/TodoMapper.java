package org.kenne.app_asymetry_sec.todo;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.kenne.app_asymetry_sec.category.Category;
import org.kenne.app_asymetry_sec.category.CategoryMapper;
import org.kenne.app_asymetry_sec.todo.request.TodoRequest;
import org.kenne.app_asymetry_sec.todo.request.TodoUpdateRequest;
import org.kenne.app_asymetry_sec.todo.response.TodoResponse;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TodoMapper {

    private final CategoryMapper categoryMapper;

    public Todo toTodo(TodoRequest request) {
        return fromRequest(request);
    }

    public Todo toTodo(TodoRequest request, Category category) {
        final var todo = fromRequest(request);
        todo.setCategory(category);

        return todo;
    }

    private @NonNull Todo fromRequest(TodoRequest request) {
        return Todo.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .done(false)
                .build();
    }

    public void mergeTodo(Todo todo, TodoUpdateRequest request) {
        if (StringUtils.isNotBlank(request.getTitle()) && !todo.getTitle().equals(request.getTitle())) {
            todo.setTitle(request.getTitle());
        }

        if (StringUtils.isNotBlank(request.getDescription()) && !todo.getDescription().equals(request.getDescription())) {
            todo.setDescription(request.getDescription());
        }
    }

    public @NonNull TodoResponse toTodoResponse(@NonNull Todo todo) {
        return TodoResponse.builder()
                .id(todo.getId())
                .title(todo.getTitle())
                .description(todo.getDescription())
                .startDate(todo.getStartDate())
                .endDate(todo.getEndDate())
                .startTime(todo.getStartTime())
                .endTime(todo.getEndTime())
                .done(todo.isDone())
                .category(categoryMapper.toCategoryResponse(todo.getCategory()))
                .build();
    }
}

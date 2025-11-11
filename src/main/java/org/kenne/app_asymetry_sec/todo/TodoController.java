package org.kenne.app_asymetry_sec.todo;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.kenne.app_asymetry_sec.common.RestResponse;
import org.kenne.app_asymetry_sec.todo.request.TodoRequest;
import org.kenne.app_asymetry_sec.todo.request.TodoUpdateRequest;
import org.kenne.app_asymetry_sec.todo.response.TodoResponse;
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
@RequestMapping("/api/v1/todos")
@RequiredArgsConstructor
@Tag(name = "Todos", description = "Todos API")
public class TodoController {

    private final TodoService todoService;

    @PostMapping
    @PreAuthorize("@categorySecurityService.isCategoryOwner(#request.categoryId)")
    public ResponseEntity<RestResponse> createTodo(
            @RequestBody @Valid TodoRequest request,
            final Authentication authentication
    ) {
        final var userId = ((User) authentication.getPrincipal()).getId();
        final var todoId = todoService.createTodo(request, userId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new RestResponse(todoId));
    }

    @PutMapping("/{todoId}")
    @PreAuthorize("@todoSecurityService.isTodoOwner(#todoId)")
    public ResponseEntity<Void> updateTodo(
            @PathVariable("todoId") @NonNull String todoId,
            @RequestBody @Valid TodoUpdateRequest request,
            final Authentication authentication
    ) {
        final var userId = ((User) authentication.getPrincipal()).getId();
        todoService.updateTodo(request, todoId, userId);

        return ResponseEntity.accepted().build();
    }

    @GetMapping("/{todoId}")
    @PreAuthorize("@todoSecurityService.isTodoOwner(#todoId)")
    public ResponseEntity<TodoResponse> findTodoById(@PathVariable("todoId") @NonNull String todoId) {
        return ResponseEntity.ok(todoService.findTodoById(todoId));
    }

    @GetMapping("/today")
    public ResponseEntity<List<TodoResponse>> findAllTodosForToday(
            final Authentication authentication
    ) {
        final var userId = ((User) authentication.getPrincipal()).getId();

        return ResponseEntity.ok(todoService.findAllTodosForToday(userId));
    }

    @GetMapping("/category/{categoryId}")
    @PreAuthorize("@categorySecurityService.isCategoryOwner(#categoryId)")
    public ResponseEntity<List<TodoResponse>> findAllTodosForToday(
            @PathVariable("categoryId") @NonNull String categoryId,
            final Authentication authentication
    ) {
        final var userId = ((User) authentication.getPrincipal()).getId();

        return ResponseEntity.ok(todoService.findTodosByCategoryId(categoryId, userId));
    }

    @GetMapping("/due")
    public ResponseEntity<List<TodoResponse>> findAllDueTodos(
            final Authentication authentication
    ) {
        final var userId = ((User) authentication.getPrincipal()).getId();

        return ResponseEntity.ok(todoService.findAllDueTodos(userId));
    }

    @DeleteMapping("/{todoId}")
    @PreAuthorize("@todoSecurityService.isTodoOwner(#todoId)")
    public ResponseEntity<Void> deleteTodoById(@PathVariable("todoId") @NonNull String todoId) {
        todoService.deleteTodoById(todoId);

        return ResponseEntity.ok().build();
    }
}

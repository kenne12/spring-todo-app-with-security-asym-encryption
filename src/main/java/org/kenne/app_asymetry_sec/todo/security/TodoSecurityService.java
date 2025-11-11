package org.kenne.app_asymetry_sec.todo.security;

import jakarta.persistence.EntityNotFoundException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kenne.app_asymetry_sec.security.SecurityUtils;
import org.kenne.app_asymetry_sec.todo.TodoRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TodoSecurityService {

    private final TodoRepository todoRepository;

    public boolean isTodoOwner(@NonNull String todoId) {

        var userId = SecurityUtils.getUserIdFromSecurityContext();

        final var todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Todo not found with id '%s'.", todoId)));

        return todo.getUser().getId().equals(userId);
    }
}

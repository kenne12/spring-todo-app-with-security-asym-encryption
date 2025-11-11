package org.kenne.app_asymetry_sec.todo;

import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TodoRepository extends JpaRepository<Todo, String> {

    @Query("SELECT t FROM Todo t WHERE t.user.id = :userId AND t.startDate = CURRENT_DATE")
    List<Todo> findAllByUserId(@NonNull String userId);

    List<Todo> findAllByUserIdAndCategoryId(@NonNull String userId, @NonNull String categoryId);

    @Query("SELECT t FROM Todo t WHERE t.user.id = :userId AND t.endDate >= CURRENT_TIME AND t.endTime >= CURRENT_TIME")
    List<Todo> findAllDueTodos(@NonNull String userId);
}

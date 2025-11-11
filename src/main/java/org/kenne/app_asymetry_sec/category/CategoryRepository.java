package org.kenne.app_asymetry_sec.category;

import lombok.NonNull;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends CrudRepository<Category, String> {

    @Query("""
                    SELECT COUNT(c) > 0 FROM Category c
                    WHERE LOWER(c.name) = LOWER(:name)
                                AND c.createdBy = :userId OR c.createdBy = 'SYSTEM'
            """)
    boolean existsByNameAndOwnerId(@NonNull String name, @NonNull String userId);

    @Query("""
                    SELECT c FROM Category c
                    WHERE c.createdBy = :userId OR c.createdBy = 'SYSTEM'
            """)
    List<Category> findAllByUserId(@NonNull String userId);

    @Query("""
                    SELECT c FROM Category c
                    WHERE c.id = :categoryId
                                AND (c.createdBy = :userId OR c.createdBy = 'SYSTEM')
            """)
    Optional<Category> findByIdAndUserId(@NonNull String categoryId, @NonNull String userId);
}

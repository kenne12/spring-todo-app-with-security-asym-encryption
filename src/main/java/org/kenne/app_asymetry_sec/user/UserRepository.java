package org.kenne.app_asymetry_sec.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,String> {

    boolean existsByEmailIgnoreCase(@NonNull String email);

    boolean existsByPhoneNumber(@NonNull String phoneNumber);

    Optional<User> findByEmailIgnoreCase(@NonNull String email);
}

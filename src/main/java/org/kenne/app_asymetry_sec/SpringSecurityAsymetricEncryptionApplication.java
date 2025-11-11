package org.kenne.app_asymetry_sec;

import org.kenne.app_asymetry_sec.role.Role;
import org.kenne.app_asymetry_sec.role.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SpringSecurityAsymetricEncryptionApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringSecurityAsymetricEncryptionApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(RoleRepository roleRepository) {
        return args -> {
            roleRepository.findByName("ROLE_USER")
                    .orElseGet(() -> roleRepository.saveAndFlush(
                            Role.builder()
                                    .name("ROLE_USER")
                                    .createdBy("SYSTEM")
                                    .build()
                    ));
        };
    }

}

package com.feedback.feedback.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.feedback.feedback.model.entity.RoleEntity;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import com.feedback.feedback.repository.RoleRepository;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner{

    private final RoleRepository roleRepository;


    @Override
    public void run(String... args) throws Exception {
            createRoleIfNotExist("ADMIN", "Admin role");
            createRoleIfNotExist("OWNER", "Owner role");
    }

    private void createRoleIfNotExist(String name, String description){
        if(!roleRepository.existsByName(name)){
            RoleEntity roleEntity = new RoleEntity();
            roleEntity.setName(name);
            roleEntity.setDescription(description);
            roleEntity.setActive(true);
            roleRepository.save(roleEntity);
            log.info("Role {} created", name);
        }
        log.info("Role {} already exists", name);

    }
}

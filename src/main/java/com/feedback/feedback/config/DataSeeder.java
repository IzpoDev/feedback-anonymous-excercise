package com.feedback.feedback.config;

import com.feedback.feedback.model.entity.PrivilegeEntity;
import com.feedback.feedback.model.entity.RolePrivilegeEntity;
import com.feedback.feedback.repository.PrivilegeRepository;
import com.feedback.feedback.repository.RolePrivilegeRepository;
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
    private final PrivilegeRepository privilegeRepository;
    private final RolePrivilegeRepository rolePrivilegeRepository;

    // Actualiza el método run:
    @Override
    public void run(String... args) throws Exception {
        createRoleIfNotExist("ADMIN", "Admin role");
        createRoleIfNotExist("OWNER", "Owner role");
        createPrivilegeIfNotExist("READ_FEEDBACK", "Permiso para leer feedbacks");
        createPrivilegeIfNotExist("UPDATE_FEEDBACK", "Permiso para actualizar feedbacks");
        createPrivilegeIfNotExist("DELETE_FEEDBACK", "Permiso para eliminar feedbacks");
        // Asigna privilegios al rol ADMIN
        RoleEntity admin = roleRepository.findByName("ADMIN").orElseThrow();
        PrivilegeEntity read = privilegeRepository.findByName("READ_FEEDBACK").orElseThrow();
        assignPrivilegeToRole(admin.getId(), read.getId());
        // Repite para otros privilegios si es necesario
    }

    // Agrega estos métodos:
    private void createPrivilegeIfNotExist(String name, String description) {
        if (!privilegeRepository.existByName(name)) {
            PrivilegeEntity privilege = new PrivilegeEntity();
            privilege.setName(name);
            privilege.setDescription(description);
            privilege.setActive(true);
            privilegeRepository.save(privilege);
            log.info("Privilege {} created", name);
        }
        log.info("Privilege {} already exists", name);
    }

    private void assignPrivilegeToRole(Long roleId, Long privilegeId) {
        RoleEntity role = roleRepository.findById(roleId).orElseThrow();
        PrivilegeEntity privilege = privilegeRepository.findById(privilegeId).orElseThrow();
        if (rolePrivilegeRepository.findByRoleIdAndPrivilegesId(roleId, privilegeId).isEmpty()) {
            RolePrivilegeEntity rp = new RolePrivilegeEntity();
            rp.setRole(role);
            rp.setPrivilege(privilege);
            rp.setActive(true);
            rolePrivilegeRepository.save(rp);
        }
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

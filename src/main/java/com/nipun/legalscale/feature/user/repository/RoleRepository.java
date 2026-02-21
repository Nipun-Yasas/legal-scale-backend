package com.nipun.legalscale.feature.user.repository;

import com.nipun.legalscale.feature.auth.enums.Role;
import com.nipun.legalscale.feature.user.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<RoleEntity, Long> {
    Optional<RoleEntity> findByRoleName(Role roleName);
}

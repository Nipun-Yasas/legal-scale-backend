package com.nipun.legalscale.feature.user.entity;

import com.nipun.legalscale.feature.auth.enums.Role;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private Role roleName;

    @Column(nullable = false)
    private boolean legalDepartmentMember;
}

package com.nipun.legalscale.feature.agreementapproval.entity;

import com.nipun.legalscale.feature.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "agreement_signatures")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgreementSignatureEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agreement_id", nullable = false)
    private AgreementEntity agreement;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "signed_by_id", nullable = false)
    private UserEntity signedBy;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String cryptographicKey;

    @Column(nullable = false)
    private LocalDateTime signedAt;
}

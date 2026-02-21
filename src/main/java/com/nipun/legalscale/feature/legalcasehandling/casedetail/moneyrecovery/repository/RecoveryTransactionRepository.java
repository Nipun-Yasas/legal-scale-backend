package com.nipun.legalscale.feature.legalcasehandling.casedetail.moneyrecovery.repository;

import com.nipun.legalscale.feature.legalcasehandling.casedetail.moneyrecovery.entity.RecoveryTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface RecoveryTransactionRepository extends JpaRepository<RecoveryTransaction, Long> {

    List<RecoveryTransaction> findByMoneyRecoveryDetailIdOrderByTransactionDateAsc(Long detailId);

    /**
     * Sum of all amounts recovered so far for a given MoneyRecoveryDetail.
     * Returns 0 if no transactions exist.
     */
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM RecoveryTransaction t " +
            "WHERE t.moneyRecoveryDetail.id = :detailId")
    BigDecimal sumAmountByDetailId(@Param("detailId") Long detailId);
}

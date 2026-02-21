package com.nipun.legalscale.feature.legalcasehandling.casedetail.criminal.enums;

public enum ChargeStatus {
    /** Charge filed, trial pending */
    PENDING,
    /** Accused found guilty of this charge */
    CONVICTED,
    /** Accused found not guilty */
    ACQUITTED,
    /** Charge withdrawn by prosecution */
    WITHDRAWN,
    /** Prosecution filed nolle prosequi (no further proceedings) */
    NOLLE_PROSEQUI
}

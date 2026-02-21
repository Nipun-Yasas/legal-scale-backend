package com.nipun.legalscale.feature.legalcasehandling.casedetail.land.enums;

public enum OwnershipType {
    /** Absolute ownership, no time limit */
    FREEHOLD,
    /** Ownership for a limited period under a lease agreement */
    LEASEHOLD,
    /** Held in trust for a beneficiary */
    TRUST,
    /** Land originally granted by the Crown / State */
    CROWN_GRANT,
    /** Ownership inherited through succession */
    INHERITANCE,
    OTHER
}

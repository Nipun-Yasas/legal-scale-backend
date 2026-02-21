package com.nipun.legalscale.feature.legalcasehandling.casedetail.inquiry.enums;

public enum DecisionStatus {
    /** Decision made, implementation not yet started */
    PENDING,
    /** Implementation is underway */
    IN_PROGRESS,
    /** Decision has been fully implemented */
    IMPLEMENTED,
    /** Implementation deferred to a later date */
    DEFERRED,
    /** Management decided not to implement */
    REJECTED
}

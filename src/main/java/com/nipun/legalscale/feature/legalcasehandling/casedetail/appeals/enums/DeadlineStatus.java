package com.nipun.legalscale.feature.legalcasehandling.casedetail.appeals.enums;

public enum DeadlineStatus {
    /** Deadline is upcoming and not yet acted upon */
    PENDING,
    /** Deadline was met on time */
    MET,
    /** Deadline was not met */
    MISSED,
    /** Deadline was formally extended by the court */
    EXTENDED
}

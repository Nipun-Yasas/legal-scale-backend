package com.nipun.legalscale.feature.legalcasehandling.casedetail.criminal.enums;

public enum HearingOutcome {
    /** Hearing held, case adjourned to next date */
    ADJOURNED,
    /** Judgment / verdict delivered */
    JUDGMENT_DELIVERED,
    /** Accused acquitted at this hearing */
    ACQUITTED,
    /** Accused convicted at this hearing */
    CONVICTED,
    /** Bail granted */
    BAIL_GRANTED,
    /** Bail refused */
    BAIL_REFUSED,
    /** Case withdrawn at this hearing */
    WITHDRAWN,
    /** Hearing was not held (e.g. party absent, postponed) */
    NOT_HELD,
    OTHER
}

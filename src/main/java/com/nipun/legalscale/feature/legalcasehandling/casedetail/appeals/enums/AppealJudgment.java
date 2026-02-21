package com.nipun.legalscale.feature.legalcasehandling.casedetail.appeals.enums;

public enum AppealJudgment {
    /** Appeal granted — original decision overturned in appellant's favour */
    UPHELD,
    /** Appeal rejected — original decision stands */
    DISMISSED,
    /** Appeal partially granted */
    PARTIALLY_UPHELD,
    /** Case sent back to lower court / authority for reconsideration */
    REMITTED,
    /** Appellant voluntarily withdrew the appeal */
    WITHDRAWN
}

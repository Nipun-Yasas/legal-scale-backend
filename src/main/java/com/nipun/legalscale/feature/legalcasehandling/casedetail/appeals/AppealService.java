package com.nipun.legalscale.feature.legalcasehandling.casedetail.appeals;

import com.nipun.legalscale.feature.legalcasehandling.casedetail.appeals.dto.*;

public interface AppealService {

    // ─── Feature 1 & Detail Management ───────────────────────────────────────────

    /**
     * Set or update the appeal header (original case link, grounds, filing date).
     * Creates on first call.
     */
    AppealDetailResponse setAppealDetails(Long caseId, AppealDetailRequest request);

    /** Get the full appeal summary. */
    AppealDetailResponse getAppealDetail(Long caseId);

    // ─── Feature 2: Deadline Tracking ────────────────────────────────────────────

    /** Add a new deadline to the appeal. */
    AppealDeadlineResponse addDeadline(Long caseId, AppealDeadlineRequest request);

    /** Update the status of a deadline (MET, MISSED, EXTENDED). */
    AppealDeadlineResponse updateDeadlineStatus(Long caseId, Long deadlineId, DeadlineStatusUpdateRequest request);

    /** Remove a deadline. */
    void deleteDeadline(Long caseId, Long deadlineId);

    // ─── Feature 3: Outcome Recording ────────────────────────────────────────────

    /** Record or update the appeal outcome / judgment. */
    AppealOutcomeResponse recordOutcome(Long caseId, AppealOutcomeRequest request);

    /** Remove a recorded outcome (e.g. entered in error). */
    void deleteOutcome(Long caseId);
}

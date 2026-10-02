package com.acos.sdk;

import com.acos.sdk.generated.ApiClient;
import com.acos.sdk.generated.api.LearningMilestonesApi;
import com.acos.sdk.generated.api.LearningPlansApi;
import com.acos.sdk.generated.api.LearningTopicsApi;
import com.acos.sdk.generated.model.ApiResponseLearningPlanPageResponse;
import com.acos.sdk.generated.model.ApiResponseLearningPlanResponse;
import com.acos.sdk.generated.model.LearningPlanApiResponse;
import com.acos.sdk.generated.model.LearningPlanRequest;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Domain wrapper for Learning Plans / Milestones / Topics generated APIs.
 */
public final class LearningApiService extends ServiceSupport {

  private final LearningPlansApi plans;
  private final LearningMilestonesApi milestones;
  private final LearningTopicsApi topics;

  public LearningApiService(ApiClient apiClient) {
    this(apiClient, RetryPolicy.defaults());
  }

  public LearningApiService(ApiClient apiClient, RetryPolicy retryPolicy) {
    super(retryPolicy, LearningApiService.class);
    Objects.requireNonNull(apiClient, "apiClient");
    this.plans = new LearningPlansApi(apiClient);
    this.milestones = new LearningMilestonesApi(apiClient);
    this.topics = new LearningTopicsApi(apiClient);
  }

  public LearningPlansApi plans() {
    return plans;
  }

  public LearningMilestonesApi milestones() {
    return milestones;
  }

  public LearningTopicsApi topics() {
    return topics;
  }

  public LearningPlanApiResponse createPlan(LearningPlanRequest request) {
    return execute("learning.createPlan", () -> plans.create5(request));
  }

  public ApiResponseLearningPlanResponse getPlan(UUID planId) {
    return execute("learning.getPlan", () -> plans.get5(planId));
  }

  public ApiResponseLearningPlanPageResponse listPlans(
      Integer page, Integer size, List<String> sort) {
    return execute("learning.listPlans", () -> plans.list5(page, size, sort));
  }
}

package com.acos.sdk;

import com.acos.sdk.generated.ApiClient;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * Facade that wires domain wrappers from a shared generated {@link ApiClient}.
 */
public final class AcosApiClient {

  private final ApiClient apiClient;
  private final KnowledgeApiService knowledge;
  private final LearningApiService learning;
  private final CareerApiService career;
  private final PortfolioApiService portfolio;

  public AcosApiClient(String basePath) {
    this(basePath, RetryPolicy.defaults());
  }

  public AcosApiClient(String basePath, RetryPolicy retryPolicy) {
    this(new ApiClient().setBasePath(Objects.requireNonNull(basePath, "basePath")), retryPolicy);
  }

  public AcosApiClient(ApiClient apiClient) {
    this(apiClient, RetryPolicy.defaults());
  }

  public AcosApiClient(ApiClient apiClient, RetryPolicy retryPolicy) {
    this.apiClient = Objects.requireNonNull(apiClient, "apiClient");
    RetryPolicy policy = Objects.requireNonNullElseGet(retryPolicy, RetryPolicy::defaults);
    this.knowledge = new KnowledgeApiService(this.apiClient, policy);
    this.learning = new LearningApiService(this.apiClient, policy);
    this.career = new CareerApiService(this.apiClient, policy);
    this.portfolio = new PortfolioApiService(this.apiClient, policy);
  }

  public AcosApiClient withBearerToken(String token) {
    apiClient.setBearerToken(token);
    return this;
  }

  public AcosApiClient withBearerToken(Supplier<String> tokenSupplier) {
    apiClient.setBearerToken(tokenSupplier);
    return this;
  }

  public ApiClient apiClient() {
    return apiClient;
  }

  public KnowledgeApiService knowledge() {
    return knowledge;
  }

  public LearningApiService learning() {
    return learning;
  }

  public CareerApiService career() {
    return career;
  }

  public PortfolioApiService portfolio() {
    return portfolio;
  }
}

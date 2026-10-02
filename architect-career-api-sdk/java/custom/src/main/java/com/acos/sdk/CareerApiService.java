package com.acos.sdk;

import com.acos.sdk.generated.ApiClient;
import com.acos.sdk.generated.api.CareerApplicationsApi;
import com.acos.sdk.generated.api.CareerCompaniesApi;
import com.acos.sdk.generated.api.CareerDashboardApi;
import com.acos.sdk.generated.api.CareerInterviewsApi;
import com.acos.sdk.generated.api.CareerOffersApi;
import com.acos.sdk.generated.api.CareerRecruitersApi;
import com.acos.sdk.generated.model.CareerDashboardApiResponse;
import java.util.Objects;

/**
 * Domain wrapper for Career generated APIs.
 */
public final class CareerApiService extends ServiceSupport {

  private final CareerApplicationsApi applications;
  private final CareerCompaniesApi companies;
  private final CareerRecruitersApi recruiters;
  private final CareerInterviewsApi interviews;
  private final CareerOffersApi offers;
  private final CareerDashboardApi dashboard;

  public CareerApiService(ApiClient apiClient) {
    this(apiClient, RetryPolicy.defaults());
  }

  public CareerApiService(ApiClient apiClient, RetryPolicy retryPolicy) {
    super(retryPolicy, CareerApiService.class);
    Objects.requireNonNull(apiClient, "apiClient");
    this.applications = new CareerApplicationsApi(apiClient);
    this.companies = new CareerCompaniesApi(apiClient);
    this.recruiters = new CareerRecruitersApi(apiClient);
    this.interviews = new CareerInterviewsApi(apiClient);
    this.offers = new CareerOffersApi(apiClient);
    this.dashboard = new CareerDashboardApi(apiClient);
  }

  public CareerApplicationsApi applications() {
    return applications;
  }

  public CareerCompaniesApi companies() {
    return companies;
  }

  public CareerRecruitersApi recruiters() {
    return recruiters;
  }

  public CareerInterviewsApi interviews() {
    return interviews;
  }

  public CareerOffersApi offers() {
    return offers;
  }

  public CareerDashboardApi dashboard() {
    return dashboard;
  }

  public CareerDashboardApiResponse getCareerDashboard() {
    return execute("career.getCareerDashboard", dashboard::getSummary);
  }
}

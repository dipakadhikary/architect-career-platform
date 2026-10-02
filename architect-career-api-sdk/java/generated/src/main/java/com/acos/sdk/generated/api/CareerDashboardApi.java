package com.acos.sdk.generated.api;

import com.acos.sdk.generated.ApiClient;

import com.acos.sdk.generated.model.CareerDashboardApiResponse;
import com.acos.sdk.generated.model.CareerErrorApiResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient.ResponseSpec;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen", comments = "Generator version: 7.12.0")
public class CareerDashboardApi {
    private ApiClient apiClient;

    public CareerDashboardApi() {
        this(new ApiClient());
    }

    @Autowired
    public CareerDashboardApi(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public ApiClient getApiClient() {
        return apiClient;
    }

    public void setApiClient(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    /**
     * Get career dashboard summary
     * Returns dynamically calculated career dashboard metrics for the authenticated user, including total applications, applications by status, scheduled interviews, offers received, rejected applications, acceptance ratio, and average interview rating.
     * <p><b>200</b> - Dashboard summary returned
     * <p><b>401</b> - JWT authentication required for career dashboard
     * @return CareerDashboardApiResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec getSummaryRequestCreation() throws RestClientResponseException {
        Object postBody = null;
        // create path and map variables
        final Map<String, Object> pathParams = new HashMap<>();

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, String> cookieParams = new LinkedMultiValueMap<>();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<>();

        final String[] localVarAccepts = { 
            "application/json"
        };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        String[] localVarAuthNames = new String[] { "bearer-jwt" };

        ParameterizedTypeReference<CareerDashboardApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return apiClient.invokeAPI("/api/v1/career/dashboard", HttpMethod.GET, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Get career dashboard summary
     * Returns dynamically calculated career dashboard metrics for the authenticated user, including total applications, applications by status, scheduled interviews, offers received, rejected applications, acceptance ratio, and average interview rating.
     * <p><b>200</b> - Dashboard summary returned
     * <p><b>401</b> - JWT authentication required for career dashboard
     * @return CareerDashboardApiResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public CareerDashboardApiResponse getSummary() throws RestClientResponseException {
        ParameterizedTypeReference<CareerDashboardApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return getSummaryRequestCreation().body(localVarReturnType);
    }

    /**
     * Get career dashboard summary
     * Returns dynamically calculated career dashboard metrics for the authenticated user, including total applications, applications by status, scheduled interviews, offers received, rejected applications, acceptance ratio, and average interview rating.
     * <p><b>200</b> - Dashboard summary returned
     * <p><b>401</b> - JWT authentication required for career dashboard
     * @return ResponseEntity&lt;CareerDashboardApiResponse&gt;
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<CareerDashboardApiResponse> getSummaryWithHttpInfo() throws RestClientResponseException {
        ParameterizedTypeReference<CareerDashboardApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return getSummaryRequestCreation().toEntity(localVarReturnType);
    }

    /**
     * Get career dashboard summary
     * Returns dynamically calculated career dashboard metrics for the authenticated user, including total applications, applications by status, scheduled interviews, offers received, rejected applications, acceptance ratio, and average interview rating.
     * <p><b>200</b> - Dashboard summary returned
     * <p><b>401</b> - JWT authentication required for career dashboard
     * @return ResponseSpec
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec getSummaryWithResponseSpec() throws RestClientResponseException {
        return getSummaryRequestCreation();
    }
}

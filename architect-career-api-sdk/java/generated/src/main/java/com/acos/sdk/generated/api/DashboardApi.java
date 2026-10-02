package com.acos.sdk.generated.api;

import com.acos.sdk.generated.ApiClient;

import com.acos.sdk.generated.model.DashboardApiResponse;
import com.acos.sdk.generated.model.DashboardErrorApiResponse;

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
public class DashboardApi {
    private ApiClient apiClient;

    public DashboardApi() {
        this(new ApiClient());
    }

    @Autowired
    public DashboardApi(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public ApiClient getApiClient() {
        return apiClient;
    }

    public void setApiClient(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    /**
     * Get dashboard
     * Returns a dashboard summary for the authenticated user. Metric values are placeholders until analytics aggregation is implemented. The caller identity is taken from the security context; userId must not be supplied as a request parameter.
     * <p><b>200</b> - Dashboard summary returned
     * <p><b>401</b> - Authentication required
     * @return DashboardApiResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec getDashboardRequestCreation() throws RestClientResponseException {
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

        ParameterizedTypeReference<DashboardApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return apiClient.invokeAPI("/api/v1/dashboard", HttpMethod.GET, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Get dashboard
     * Returns a dashboard summary for the authenticated user. Metric values are placeholders until analytics aggregation is implemented. The caller identity is taken from the security context; userId must not be supplied as a request parameter.
     * <p><b>200</b> - Dashboard summary returned
     * <p><b>401</b> - Authentication required
     * @return DashboardApiResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public DashboardApiResponse getDashboard() throws RestClientResponseException {
        ParameterizedTypeReference<DashboardApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return getDashboardRequestCreation().body(localVarReturnType);
    }

    /**
     * Get dashboard
     * Returns a dashboard summary for the authenticated user. Metric values are placeholders until analytics aggregation is implemented. The caller identity is taken from the security context; userId must not be supplied as a request parameter.
     * <p><b>200</b> - Dashboard summary returned
     * <p><b>401</b> - Authentication required
     * @return ResponseEntity&lt;DashboardApiResponse&gt;
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<DashboardApiResponse> getDashboardWithHttpInfo() throws RestClientResponseException {
        ParameterizedTypeReference<DashboardApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return getDashboardRequestCreation().toEntity(localVarReturnType);
    }

    /**
     * Get dashboard
     * Returns a dashboard summary for the authenticated user. Metric values are placeholders until analytics aggregation is implemented. The caller identity is taken from the security context; userId must not be supplied as a request parameter.
     * <p><b>200</b> - Dashboard summary returned
     * <p><b>401</b> - Authentication required
     * @return ResponseSpec
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec getDashboardWithResponseSpec() throws RestClientResponseException {
        return getDashboardRequestCreation();
    }
}

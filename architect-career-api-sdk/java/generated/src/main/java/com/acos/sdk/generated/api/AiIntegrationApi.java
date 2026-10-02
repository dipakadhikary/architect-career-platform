package com.acos.sdk.generated.api;

import com.acos.sdk.generated.ApiClient;

import com.acos.sdk.generated.model.AiPlatformHealthApiResponse;
import com.acos.sdk.generated.model.AiPlatformHealthErrorApiResponse;

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
public class AiIntegrationApi {
    private ApiClient apiClient;

    public AiIntegrationApi() {
        this(new ApiClient());
    }

    @Autowired
    public AiIntegrationApi(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public ApiClient getApiClient() {
        return apiClient;
    }

    public void setApiClient(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    /**
     * Get AI Platform health
     * Returns the availability of the AI Platform integration. Status values are AVAILABLE, UNAVAILABLE, or DEGRADED. When integration is disabled the status is UNAVAILABLE and capability gateways return mocked responses.
     * <p><b>200</b> - AI Platform health status returned
     * <p><b>401</b> - Authentication required
     * @return AiPlatformHealthApiResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec getHealthRequestCreation() throws RestClientResponseException {
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

        ParameterizedTypeReference<AiPlatformHealthApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return apiClient.invokeAPI("/api/v1/integration/ai/health", HttpMethod.GET, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Get AI Platform health
     * Returns the availability of the AI Platform integration. Status values are AVAILABLE, UNAVAILABLE, or DEGRADED. When integration is disabled the status is UNAVAILABLE and capability gateways return mocked responses.
     * <p><b>200</b> - AI Platform health status returned
     * <p><b>401</b> - Authentication required
     * @return AiPlatformHealthApiResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public AiPlatformHealthApiResponse getHealth() throws RestClientResponseException {
        ParameterizedTypeReference<AiPlatformHealthApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return getHealthRequestCreation().body(localVarReturnType);
    }

    /**
     * Get AI Platform health
     * Returns the availability of the AI Platform integration. Status values are AVAILABLE, UNAVAILABLE, or DEGRADED. When integration is disabled the status is UNAVAILABLE and capability gateways return mocked responses.
     * <p><b>200</b> - AI Platform health status returned
     * <p><b>401</b> - Authentication required
     * @return ResponseEntity&lt;AiPlatformHealthApiResponse&gt;
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<AiPlatformHealthApiResponse> getHealthWithHttpInfo() throws RestClientResponseException {
        ParameterizedTypeReference<AiPlatformHealthApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return getHealthRequestCreation().toEntity(localVarReturnType);
    }

    /**
     * Get AI Platform health
     * Returns the availability of the AI Platform integration. Status values are AVAILABLE, UNAVAILABLE, or DEGRADED. When integration is disabled the status is UNAVAILABLE and capability gateways return mocked responses.
     * <p><b>200</b> - AI Platform health status returned
     * <p><b>401</b> - Authentication required
     * @return ResponseSpec
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec getHealthWithResponseSpec() throws RestClientResponseException {
        return getHealthRequestCreation();
    }
}

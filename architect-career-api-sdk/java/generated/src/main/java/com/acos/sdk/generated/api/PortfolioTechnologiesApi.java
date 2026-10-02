package com.acos.sdk.generated.api;

import com.acos.sdk.generated.ApiClient;

import com.acos.sdk.generated.model.ApiResponseListTechnologyResponse;
import com.acos.sdk.generated.model.ApiResponseTechnologyResponse;
import com.acos.sdk.generated.model.ApiResponseVoid;
import com.acos.sdk.generated.model.TechnologyRequest;
import java.util.UUID;

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
public class PortfolioTechnologiesApi {
    private ApiClient apiClient;

    public PortfolioTechnologiesApi() {
        this(new ApiClient());
    }

    @Autowired
    public PortfolioTechnologiesApi(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public ApiClient getApiClient() {
        return apiClient;
    }

    public void setApiClient(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    /**
     * List technologies
     * 
     * <p><b>200</b> - OK
     * @return ApiResponseListTechnologyResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec callListRequestCreation() throws RestClientResponseException {
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

        ParameterizedTypeReference<ApiResponseListTechnologyResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return apiClient.invokeAPI("/api/v1/portfolio/technologies", HttpMethod.GET, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * List technologies
     * 
     * <p><b>200</b> - OK
     * @return ApiResponseListTechnologyResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ApiResponseListTechnologyResponse callList() throws RestClientResponseException {
        ParameterizedTypeReference<ApiResponseListTechnologyResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return callListRequestCreation().body(localVarReturnType);
    }

    /**
     * List technologies
     * 
     * <p><b>200</b> - OK
     * @return ResponseEntity&lt;ApiResponseListTechnologyResponse&gt;
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<ApiResponseListTechnologyResponse> callListWithHttpInfo() throws RestClientResponseException {
        ParameterizedTypeReference<ApiResponseListTechnologyResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return callListRequestCreation().toEntity(localVarReturnType);
    }

    /**
     * List technologies
     * 
     * <p><b>200</b> - OK
     * @return ResponseSpec
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec callListWithResponseSpec() throws RestClientResponseException {
        return callListRequestCreation();
    }
    /**
     * Create technology
     * 
     * <p><b>200</b> - OK
     * @param technologyRequest The technologyRequest parameter
     * @return ApiResponseTechnologyResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec createRequestCreation(TechnologyRequest technologyRequest) throws RestClientResponseException {
        Object postBody = technologyRequest;
        // verify the required parameter 'technologyRequest' is set
        if (technologyRequest == null) {
            throw new RestClientResponseException("Missing the required parameter 'technologyRequest' when calling create", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
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
        final String[] localVarContentTypes = { 
            "application/json"
        };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        String[] localVarAuthNames = new String[] { "bearer-jwt" };

        ParameterizedTypeReference<ApiResponseTechnologyResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return apiClient.invokeAPI("/api/v1/portfolio/technologies", HttpMethod.POST, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Create technology
     * 
     * <p><b>200</b> - OK
     * @param technologyRequest The technologyRequest parameter
     * @return ApiResponseTechnologyResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ApiResponseTechnologyResponse create(TechnologyRequest technologyRequest) throws RestClientResponseException {
        ParameterizedTypeReference<ApiResponseTechnologyResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return createRequestCreation(technologyRequest).body(localVarReturnType);
    }

    /**
     * Create technology
     * 
     * <p><b>200</b> - OK
     * @param technologyRequest The technologyRequest parameter
     * @return ResponseEntity&lt;ApiResponseTechnologyResponse&gt;
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<ApiResponseTechnologyResponse> createWithHttpInfo(TechnologyRequest technologyRequest) throws RestClientResponseException {
        ParameterizedTypeReference<ApiResponseTechnologyResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return createRequestCreation(technologyRequest).toEntity(localVarReturnType);
    }

    /**
     * Create technology
     * 
     * <p><b>200</b> - OK
     * @param technologyRequest The technologyRequest parameter
     * @return ResponseSpec
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec createWithResponseSpec(TechnologyRequest technologyRequest) throws RestClientResponseException {
        return createRequestCreation(technologyRequest);
    }
    /**
     * Delete technology
     * 
     * <p><b>200</b> - OK
     * @param technologyId Technology identifier
     * @return ApiResponseVoid
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec deleteRequestCreation(UUID technologyId) throws RestClientResponseException {
        Object postBody = null;
        // verify the required parameter 'technologyId' is set
        if (technologyId == null) {
            throw new RestClientResponseException("Missing the required parameter 'technologyId' when calling delete", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // create path and map variables
        final Map<String, Object> pathParams = new HashMap<>();

        pathParams.put("technologyId", technologyId);

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

        ParameterizedTypeReference<ApiResponseVoid> localVarReturnType = new ParameterizedTypeReference<>() {};
        return apiClient.invokeAPI("/api/v1/portfolio/technologies/{technologyId}", HttpMethod.DELETE, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Delete technology
     * 
     * <p><b>200</b> - OK
     * @param technologyId Technology identifier
     * @return ApiResponseVoid
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ApiResponseVoid delete(UUID technologyId) throws RestClientResponseException {
        ParameterizedTypeReference<ApiResponseVoid> localVarReturnType = new ParameterizedTypeReference<>() {};
        return deleteRequestCreation(technologyId).body(localVarReturnType);
    }

    /**
     * Delete technology
     * 
     * <p><b>200</b> - OK
     * @param technologyId Technology identifier
     * @return ResponseEntity&lt;ApiResponseVoid&gt;
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<ApiResponseVoid> deleteWithHttpInfo(UUID technologyId) throws RestClientResponseException {
        ParameterizedTypeReference<ApiResponseVoid> localVarReturnType = new ParameterizedTypeReference<>() {};
        return deleteRequestCreation(technologyId).toEntity(localVarReturnType);
    }

    /**
     * Delete technology
     * 
     * <p><b>200</b> - OK
     * @param technologyId Technology identifier
     * @return ResponseSpec
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec deleteWithResponseSpec(UUID technologyId) throws RestClientResponseException {
        return deleteRequestCreation(technologyId);
    }
    /**
     * Get technology
     * 
     * <p><b>200</b> - OK
     * @param technologyId Technology identifier
     * @return ApiResponseTechnologyResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec getRequestCreation(UUID technologyId) throws RestClientResponseException {
        Object postBody = null;
        // verify the required parameter 'technologyId' is set
        if (technologyId == null) {
            throw new RestClientResponseException("Missing the required parameter 'technologyId' when calling get", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // create path and map variables
        final Map<String, Object> pathParams = new HashMap<>();

        pathParams.put("technologyId", technologyId);

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

        ParameterizedTypeReference<ApiResponseTechnologyResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return apiClient.invokeAPI("/api/v1/portfolio/technologies/{technologyId}", HttpMethod.GET, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Get technology
     * 
     * <p><b>200</b> - OK
     * @param technologyId Technology identifier
     * @return ApiResponseTechnologyResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ApiResponseTechnologyResponse get(UUID technologyId) throws RestClientResponseException {
        ParameterizedTypeReference<ApiResponseTechnologyResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return getRequestCreation(technologyId).body(localVarReturnType);
    }

    /**
     * Get technology
     * 
     * <p><b>200</b> - OK
     * @param technologyId Technology identifier
     * @return ResponseEntity&lt;ApiResponseTechnologyResponse&gt;
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<ApiResponseTechnologyResponse> getWithHttpInfo(UUID technologyId) throws RestClientResponseException {
        ParameterizedTypeReference<ApiResponseTechnologyResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return getRequestCreation(technologyId).toEntity(localVarReturnType);
    }

    /**
     * Get technology
     * 
     * <p><b>200</b> - OK
     * @param technologyId Technology identifier
     * @return ResponseSpec
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec getWithResponseSpec(UUID technologyId) throws RestClientResponseException {
        return getRequestCreation(technologyId);
    }
    /**
     * Update technology
     * 
     * <p><b>200</b> - OK
     * @param technologyId Technology identifier
     * @param technologyRequest The technologyRequest parameter
     * @return ApiResponseTechnologyResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec updateRequestCreation(UUID technologyId, TechnologyRequest technologyRequest) throws RestClientResponseException {
        Object postBody = technologyRequest;
        // verify the required parameter 'technologyId' is set
        if (technologyId == null) {
            throw new RestClientResponseException("Missing the required parameter 'technologyId' when calling update", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // verify the required parameter 'technologyRequest' is set
        if (technologyRequest == null) {
            throw new RestClientResponseException("Missing the required parameter 'technologyRequest' when calling update", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // create path and map variables
        final Map<String, Object> pathParams = new HashMap<>();

        pathParams.put("technologyId", technologyId);

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, String> cookieParams = new LinkedMultiValueMap<>();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<>();

        final String[] localVarAccepts = { 
            "application/json"
        };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { 
            "application/json"
        };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        String[] localVarAuthNames = new String[] { "bearer-jwt" };

        ParameterizedTypeReference<ApiResponseTechnologyResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return apiClient.invokeAPI("/api/v1/portfolio/technologies/{technologyId}", HttpMethod.PUT, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Update technology
     * 
     * <p><b>200</b> - OK
     * @param technologyId Technology identifier
     * @param technologyRequest The technologyRequest parameter
     * @return ApiResponseTechnologyResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ApiResponseTechnologyResponse update(UUID technologyId, TechnologyRequest technologyRequest) throws RestClientResponseException {
        ParameterizedTypeReference<ApiResponseTechnologyResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return updateRequestCreation(technologyId, technologyRequest).body(localVarReturnType);
    }

    /**
     * Update technology
     * 
     * <p><b>200</b> - OK
     * @param technologyId Technology identifier
     * @param technologyRequest The technologyRequest parameter
     * @return ResponseEntity&lt;ApiResponseTechnologyResponse&gt;
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<ApiResponseTechnologyResponse> updateWithHttpInfo(UUID technologyId, TechnologyRequest technologyRequest) throws RestClientResponseException {
        ParameterizedTypeReference<ApiResponseTechnologyResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return updateRequestCreation(technologyId, technologyRequest).toEntity(localVarReturnType);
    }

    /**
     * Update technology
     * 
     * <p><b>200</b> - OK
     * @param technologyId Technology identifier
     * @param technologyRequest The technologyRequest parameter
     * @return ResponseSpec
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec updateWithResponseSpec(UUID technologyId, TechnologyRequest technologyRequest) throws RestClientResponseException {
        return updateRequestCreation(technologyId, technologyRequest);
    }
}

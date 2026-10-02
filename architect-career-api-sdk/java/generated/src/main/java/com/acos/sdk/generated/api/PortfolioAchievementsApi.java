package com.acos.sdk.generated.api;

import com.acos.sdk.generated.ApiClient;

import com.acos.sdk.generated.model.AchievementRequest;
import com.acos.sdk.generated.model.ApiResponseAchievementResponse;
import com.acos.sdk.generated.model.ApiResponseListAchievementResponse;
import com.acos.sdk.generated.model.ApiResponseVoid;
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
public class PortfolioAchievementsApi {
    private ApiClient apiClient;

    public PortfolioAchievementsApi() {
        this(new ApiClient());
    }

    @Autowired
    public PortfolioAchievementsApi(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public ApiClient getApiClient() {
        return apiClient;
    }

    public void setApiClient(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    /**
     * Create achievement
     * 
     * <p><b>200</b> - OK
     * @param achievementRequest The achievementRequest parameter
     * @return ApiResponseAchievementResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec create4RequestCreation(AchievementRequest achievementRequest) throws RestClientResponseException {
        Object postBody = achievementRequest;
        // verify the required parameter 'achievementRequest' is set
        if (achievementRequest == null) {
            throw new RestClientResponseException("Missing the required parameter 'achievementRequest' when calling create4", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
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

        ParameterizedTypeReference<ApiResponseAchievementResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return apiClient.invokeAPI("/api/v1/portfolio/achievements", HttpMethod.POST, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Create achievement
     * 
     * <p><b>200</b> - OK
     * @param achievementRequest The achievementRequest parameter
     * @return ApiResponseAchievementResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ApiResponseAchievementResponse create4(AchievementRequest achievementRequest) throws RestClientResponseException {
        ParameterizedTypeReference<ApiResponseAchievementResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return create4RequestCreation(achievementRequest).body(localVarReturnType);
    }

    /**
     * Create achievement
     * 
     * <p><b>200</b> - OK
     * @param achievementRequest The achievementRequest parameter
     * @return ResponseEntity&lt;ApiResponseAchievementResponse&gt;
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<ApiResponseAchievementResponse> create4WithHttpInfo(AchievementRequest achievementRequest) throws RestClientResponseException {
        ParameterizedTypeReference<ApiResponseAchievementResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return create4RequestCreation(achievementRequest).toEntity(localVarReturnType);
    }

    /**
     * Create achievement
     * 
     * <p><b>200</b> - OK
     * @param achievementRequest The achievementRequest parameter
     * @return ResponseSpec
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec create4WithResponseSpec(AchievementRequest achievementRequest) throws RestClientResponseException {
        return create4RequestCreation(achievementRequest);
    }
    /**
     * Delete achievement
     * 
     * <p><b>200</b> - OK
     * @param achievementId Achievement identifier
     * @return ApiResponseVoid
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec delete4RequestCreation(UUID achievementId) throws RestClientResponseException {
        Object postBody = null;
        // verify the required parameter 'achievementId' is set
        if (achievementId == null) {
            throw new RestClientResponseException("Missing the required parameter 'achievementId' when calling delete4", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // create path and map variables
        final Map<String, Object> pathParams = new HashMap<>();

        pathParams.put("achievementId", achievementId);

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
        return apiClient.invokeAPI("/api/v1/portfolio/achievements/{achievementId}", HttpMethod.DELETE, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Delete achievement
     * 
     * <p><b>200</b> - OK
     * @param achievementId Achievement identifier
     * @return ApiResponseVoid
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ApiResponseVoid delete4(UUID achievementId) throws RestClientResponseException {
        ParameterizedTypeReference<ApiResponseVoid> localVarReturnType = new ParameterizedTypeReference<>() {};
        return delete4RequestCreation(achievementId).body(localVarReturnType);
    }

    /**
     * Delete achievement
     * 
     * <p><b>200</b> - OK
     * @param achievementId Achievement identifier
     * @return ResponseEntity&lt;ApiResponseVoid&gt;
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<ApiResponseVoid> delete4WithHttpInfo(UUID achievementId) throws RestClientResponseException {
        ParameterizedTypeReference<ApiResponseVoid> localVarReturnType = new ParameterizedTypeReference<>() {};
        return delete4RequestCreation(achievementId).toEntity(localVarReturnType);
    }

    /**
     * Delete achievement
     * 
     * <p><b>200</b> - OK
     * @param achievementId Achievement identifier
     * @return ResponseSpec
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec delete4WithResponseSpec(UUID achievementId) throws RestClientResponseException {
        return delete4RequestCreation(achievementId);
    }
    /**
     * Get achievement
     * 
     * <p><b>200</b> - OK
     * @param achievementId Achievement identifier
     * @return ApiResponseAchievementResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec get4RequestCreation(UUID achievementId) throws RestClientResponseException {
        Object postBody = null;
        // verify the required parameter 'achievementId' is set
        if (achievementId == null) {
            throw new RestClientResponseException("Missing the required parameter 'achievementId' when calling get4", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // create path and map variables
        final Map<String, Object> pathParams = new HashMap<>();

        pathParams.put("achievementId", achievementId);

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

        ParameterizedTypeReference<ApiResponseAchievementResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return apiClient.invokeAPI("/api/v1/portfolio/achievements/{achievementId}", HttpMethod.GET, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Get achievement
     * 
     * <p><b>200</b> - OK
     * @param achievementId Achievement identifier
     * @return ApiResponseAchievementResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ApiResponseAchievementResponse get4(UUID achievementId) throws RestClientResponseException {
        ParameterizedTypeReference<ApiResponseAchievementResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return get4RequestCreation(achievementId).body(localVarReturnType);
    }

    /**
     * Get achievement
     * 
     * <p><b>200</b> - OK
     * @param achievementId Achievement identifier
     * @return ResponseEntity&lt;ApiResponseAchievementResponse&gt;
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<ApiResponseAchievementResponse> get4WithHttpInfo(UUID achievementId) throws RestClientResponseException {
        ParameterizedTypeReference<ApiResponseAchievementResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return get4RequestCreation(achievementId).toEntity(localVarReturnType);
    }

    /**
     * Get achievement
     * 
     * <p><b>200</b> - OK
     * @param achievementId Achievement identifier
     * @return ResponseSpec
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec get4WithResponseSpec(UUID achievementId) throws RestClientResponseException {
        return get4RequestCreation(achievementId);
    }
    /**
     * List achievements
     * 
     * <p><b>200</b> - OK
     * @return ApiResponseListAchievementResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec list4RequestCreation() throws RestClientResponseException {
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

        ParameterizedTypeReference<ApiResponseListAchievementResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return apiClient.invokeAPI("/api/v1/portfolio/achievements", HttpMethod.GET, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * List achievements
     * 
     * <p><b>200</b> - OK
     * @return ApiResponseListAchievementResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ApiResponseListAchievementResponse list4() throws RestClientResponseException {
        ParameterizedTypeReference<ApiResponseListAchievementResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return list4RequestCreation().body(localVarReturnType);
    }

    /**
     * List achievements
     * 
     * <p><b>200</b> - OK
     * @return ResponseEntity&lt;ApiResponseListAchievementResponse&gt;
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<ApiResponseListAchievementResponse> list4WithHttpInfo() throws RestClientResponseException {
        ParameterizedTypeReference<ApiResponseListAchievementResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return list4RequestCreation().toEntity(localVarReturnType);
    }

    /**
     * List achievements
     * 
     * <p><b>200</b> - OK
     * @return ResponseSpec
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec list4WithResponseSpec() throws RestClientResponseException {
        return list4RequestCreation();
    }
    /**
     * Update achievement
     * 
     * <p><b>200</b> - OK
     * @param achievementId Achievement identifier
     * @param achievementRequest The achievementRequest parameter
     * @return ApiResponseAchievementResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec update4RequestCreation(UUID achievementId, AchievementRequest achievementRequest) throws RestClientResponseException {
        Object postBody = achievementRequest;
        // verify the required parameter 'achievementId' is set
        if (achievementId == null) {
            throw new RestClientResponseException("Missing the required parameter 'achievementId' when calling update4", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // verify the required parameter 'achievementRequest' is set
        if (achievementRequest == null) {
            throw new RestClientResponseException("Missing the required parameter 'achievementRequest' when calling update4", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // create path and map variables
        final Map<String, Object> pathParams = new HashMap<>();

        pathParams.put("achievementId", achievementId);

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

        ParameterizedTypeReference<ApiResponseAchievementResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return apiClient.invokeAPI("/api/v1/portfolio/achievements/{achievementId}", HttpMethod.PUT, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Update achievement
     * 
     * <p><b>200</b> - OK
     * @param achievementId Achievement identifier
     * @param achievementRequest The achievementRequest parameter
     * @return ApiResponseAchievementResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ApiResponseAchievementResponse update4(UUID achievementId, AchievementRequest achievementRequest) throws RestClientResponseException {
        ParameterizedTypeReference<ApiResponseAchievementResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return update4RequestCreation(achievementId, achievementRequest).body(localVarReturnType);
    }

    /**
     * Update achievement
     * 
     * <p><b>200</b> - OK
     * @param achievementId Achievement identifier
     * @param achievementRequest The achievementRequest parameter
     * @return ResponseEntity&lt;ApiResponseAchievementResponse&gt;
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<ApiResponseAchievementResponse> update4WithHttpInfo(UUID achievementId, AchievementRequest achievementRequest) throws RestClientResponseException {
        ParameterizedTypeReference<ApiResponseAchievementResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return update4RequestCreation(achievementId, achievementRequest).toEntity(localVarReturnType);
    }

    /**
     * Update achievement
     * 
     * <p><b>200</b> - OK
     * @param achievementId Achievement identifier
     * @param achievementRequest The achievementRequest parameter
     * @return ResponseSpec
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec update4WithResponseSpec(UUID achievementId, AchievementRequest achievementRequest) throws RestClientResponseException {
        return update4RequestCreation(achievementId, achievementRequest);
    }
}

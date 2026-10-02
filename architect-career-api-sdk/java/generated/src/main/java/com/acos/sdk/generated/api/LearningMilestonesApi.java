package com.acos.sdk.generated.api;

import com.acos.sdk.generated.ApiClient;

import com.acos.sdk.generated.model.ApiResponseLearningMilestoneResponse;
import com.acos.sdk.generated.model.ApiResponseListLearningMilestoneResponse;
import com.acos.sdk.generated.model.ApiResponseVoid;
import com.acos.sdk.generated.model.LearningMilestoneRequest;
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
public class LearningMilestonesApi {
    private ApiClient apiClient;

    public LearningMilestonesApi() {
        this(new ApiClient());
    }

    @Autowired
    public LearningMilestonesApi(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public ApiClient getApiClient() {
        return apiClient;
    }

    public void setApiClient(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    /**
     * Create learning milestone
     * 
     * <p><b>200</b> - OK
     * @param planId Learning plan identifier
     * @param learningMilestoneRequest The learningMilestoneRequest parameter
     * @return ApiResponseLearningMilestoneResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec create6RequestCreation(UUID planId, LearningMilestoneRequest learningMilestoneRequest) throws RestClientResponseException {
        Object postBody = learningMilestoneRequest;
        // verify the required parameter 'planId' is set
        if (planId == null) {
            throw new RestClientResponseException("Missing the required parameter 'planId' when calling create6", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // verify the required parameter 'learningMilestoneRequest' is set
        if (learningMilestoneRequest == null) {
            throw new RestClientResponseException("Missing the required parameter 'learningMilestoneRequest' when calling create6", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // create path and map variables
        final Map<String, Object> pathParams = new HashMap<>();

        pathParams.put("planId", planId);

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

        ParameterizedTypeReference<ApiResponseLearningMilestoneResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return apiClient.invokeAPI("/api/v1/learning/plans/{planId}/milestones", HttpMethod.POST, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Create learning milestone
     * 
     * <p><b>200</b> - OK
     * @param planId Learning plan identifier
     * @param learningMilestoneRequest The learningMilestoneRequest parameter
     * @return ApiResponseLearningMilestoneResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ApiResponseLearningMilestoneResponse create6(UUID planId, LearningMilestoneRequest learningMilestoneRequest) throws RestClientResponseException {
        ParameterizedTypeReference<ApiResponseLearningMilestoneResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return create6RequestCreation(planId, learningMilestoneRequest).body(localVarReturnType);
    }

    /**
     * Create learning milestone
     * 
     * <p><b>200</b> - OK
     * @param planId Learning plan identifier
     * @param learningMilestoneRequest The learningMilestoneRequest parameter
     * @return ResponseEntity&lt;ApiResponseLearningMilestoneResponse&gt;
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<ApiResponseLearningMilestoneResponse> create6WithHttpInfo(UUID planId, LearningMilestoneRequest learningMilestoneRequest) throws RestClientResponseException {
        ParameterizedTypeReference<ApiResponseLearningMilestoneResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return create6RequestCreation(planId, learningMilestoneRequest).toEntity(localVarReturnType);
    }

    /**
     * Create learning milestone
     * 
     * <p><b>200</b> - OK
     * @param planId Learning plan identifier
     * @param learningMilestoneRequest The learningMilestoneRequest parameter
     * @return ResponseSpec
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec create6WithResponseSpec(UUID planId, LearningMilestoneRequest learningMilestoneRequest) throws RestClientResponseException {
        return create6RequestCreation(planId, learningMilestoneRequest);
    }
    /**
     * Delete learning milestone
     * 
     * <p><b>200</b> - OK
     * @param planId Learning plan identifier
     * @param milestoneId Learning milestone identifier
     * @return ApiResponseVoid
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec delete6RequestCreation(UUID planId, UUID milestoneId) throws RestClientResponseException {
        Object postBody = null;
        // verify the required parameter 'planId' is set
        if (planId == null) {
            throw new RestClientResponseException("Missing the required parameter 'planId' when calling delete6", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // verify the required parameter 'milestoneId' is set
        if (milestoneId == null) {
            throw new RestClientResponseException("Missing the required parameter 'milestoneId' when calling delete6", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // create path and map variables
        final Map<String, Object> pathParams = new HashMap<>();

        pathParams.put("planId", planId);
        pathParams.put("milestoneId", milestoneId);

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
        return apiClient.invokeAPI("/api/v1/learning/plans/{planId}/milestones/{milestoneId}", HttpMethod.DELETE, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Delete learning milestone
     * 
     * <p><b>200</b> - OK
     * @param planId Learning plan identifier
     * @param milestoneId Learning milestone identifier
     * @return ApiResponseVoid
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ApiResponseVoid delete6(UUID planId, UUID milestoneId) throws RestClientResponseException {
        ParameterizedTypeReference<ApiResponseVoid> localVarReturnType = new ParameterizedTypeReference<>() {};
        return delete6RequestCreation(planId, milestoneId).body(localVarReturnType);
    }

    /**
     * Delete learning milestone
     * 
     * <p><b>200</b> - OK
     * @param planId Learning plan identifier
     * @param milestoneId Learning milestone identifier
     * @return ResponseEntity&lt;ApiResponseVoid&gt;
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<ApiResponseVoid> delete6WithHttpInfo(UUID planId, UUID milestoneId) throws RestClientResponseException {
        ParameterizedTypeReference<ApiResponseVoid> localVarReturnType = new ParameterizedTypeReference<>() {};
        return delete6RequestCreation(planId, milestoneId).toEntity(localVarReturnType);
    }

    /**
     * Delete learning milestone
     * 
     * <p><b>200</b> - OK
     * @param planId Learning plan identifier
     * @param milestoneId Learning milestone identifier
     * @return ResponseSpec
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec delete6WithResponseSpec(UUID planId, UUID milestoneId) throws RestClientResponseException {
        return delete6RequestCreation(planId, milestoneId);
    }
    /**
     * Get learning milestone
     * 
     * <p><b>200</b> - OK
     * @param planId Learning plan identifier
     * @param milestoneId Learning milestone identifier
     * @return ApiResponseLearningMilestoneResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec get6RequestCreation(UUID planId, UUID milestoneId) throws RestClientResponseException {
        Object postBody = null;
        // verify the required parameter 'planId' is set
        if (planId == null) {
            throw new RestClientResponseException("Missing the required parameter 'planId' when calling get6", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // verify the required parameter 'milestoneId' is set
        if (milestoneId == null) {
            throw new RestClientResponseException("Missing the required parameter 'milestoneId' when calling get6", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // create path and map variables
        final Map<String, Object> pathParams = new HashMap<>();

        pathParams.put("planId", planId);
        pathParams.put("milestoneId", milestoneId);

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

        ParameterizedTypeReference<ApiResponseLearningMilestoneResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return apiClient.invokeAPI("/api/v1/learning/plans/{planId}/milestones/{milestoneId}", HttpMethod.GET, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Get learning milestone
     * 
     * <p><b>200</b> - OK
     * @param planId Learning plan identifier
     * @param milestoneId Learning milestone identifier
     * @return ApiResponseLearningMilestoneResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ApiResponseLearningMilestoneResponse get6(UUID planId, UUID milestoneId) throws RestClientResponseException {
        ParameterizedTypeReference<ApiResponseLearningMilestoneResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return get6RequestCreation(planId, milestoneId).body(localVarReturnType);
    }

    /**
     * Get learning milestone
     * 
     * <p><b>200</b> - OK
     * @param planId Learning plan identifier
     * @param milestoneId Learning milestone identifier
     * @return ResponseEntity&lt;ApiResponseLearningMilestoneResponse&gt;
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<ApiResponseLearningMilestoneResponse> get6WithHttpInfo(UUID planId, UUID milestoneId) throws RestClientResponseException {
        ParameterizedTypeReference<ApiResponseLearningMilestoneResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return get6RequestCreation(planId, milestoneId).toEntity(localVarReturnType);
    }

    /**
     * Get learning milestone
     * 
     * <p><b>200</b> - OK
     * @param planId Learning plan identifier
     * @param milestoneId Learning milestone identifier
     * @return ResponseSpec
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec get6WithResponseSpec(UUID planId, UUID milestoneId) throws RestClientResponseException {
        return get6RequestCreation(planId, milestoneId);
    }
    /**
     * List learning milestones
     * 
     * <p><b>200</b> - OK
     * @param planId Learning plan identifier
     * @return ApiResponseListLearningMilestoneResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec list6RequestCreation(UUID planId) throws RestClientResponseException {
        Object postBody = null;
        // verify the required parameter 'planId' is set
        if (planId == null) {
            throw new RestClientResponseException("Missing the required parameter 'planId' when calling list6", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // create path and map variables
        final Map<String, Object> pathParams = new HashMap<>();

        pathParams.put("planId", planId);

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

        ParameterizedTypeReference<ApiResponseListLearningMilestoneResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return apiClient.invokeAPI("/api/v1/learning/plans/{planId}/milestones", HttpMethod.GET, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * List learning milestones
     * 
     * <p><b>200</b> - OK
     * @param planId Learning plan identifier
     * @return ApiResponseListLearningMilestoneResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ApiResponseListLearningMilestoneResponse list6(UUID planId) throws RestClientResponseException {
        ParameterizedTypeReference<ApiResponseListLearningMilestoneResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return list6RequestCreation(planId).body(localVarReturnType);
    }

    /**
     * List learning milestones
     * 
     * <p><b>200</b> - OK
     * @param planId Learning plan identifier
     * @return ResponseEntity&lt;ApiResponseListLearningMilestoneResponse&gt;
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<ApiResponseListLearningMilestoneResponse> list6WithHttpInfo(UUID planId) throws RestClientResponseException {
        ParameterizedTypeReference<ApiResponseListLearningMilestoneResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return list6RequestCreation(planId).toEntity(localVarReturnType);
    }

    /**
     * List learning milestones
     * 
     * <p><b>200</b> - OK
     * @param planId Learning plan identifier
     * @return ResponseSpec
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec list6WithResponseSpec(UUID planId) throws RestClientResponseException {
        return list6RequestCreation(planId);
    }
    /**
     * Update learning milestone
     * 
     * <p><b>200</b> - OK
     * @param planId Learning plan identifier
     * @param milestoneId Learning milestone identifier
     * @param learningMilestoneRequest The learningMilestoneRequest parameter
     * @return ApiResponseLearningMilestoneResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec update6RequestCreation(UUID planId, UUID milestoneId, LearningMilestoneRequest learningMilestoneRequest) throws RestClientResponseException {
        Object postBody = learningMilestoneRequest;
        // verify the required parameter 'planId' is set
        if (planId == null) {
            throw new RestClientResponseException("Missing the required parameter 'planId' when calling update6", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // verify the required parameter 'milestoneId' is set
        if (milestoneId == null) {
            throw new RestClientResponseException("Missing the required parameter 'milestoneId' when calling update6", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // verify the required parameter 'learningMilestoneRequest' is set
        if (learningMilestoneRequest == null) {
            throw new RestClientResponseException("Missing the required parameter 'learningMilestoneRequest' when calling update6", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // create path and map variables
        final Map<String, Object> pathParams = new HashMap<>();

        pathParams.put("planId", planId);
        pathParams.put("milestoneId", milestoneId);

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

        ParameterizedTypeReference<ApiResponseLearningMilestoneResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return apiClient.invokeAPI("/api/v1/learning/plans/{planId}/milestones/{milestoneId}", HttpMethod.PUT, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Update learning milestone
     * 
     * <p><b>200</b> - OK
     * @param planId Learning plan identifier
     * @param milestoneId Learning milestone identifier
     * @param learningMilestoneRequest The learningMilestoneRequest parameter
     * @return ApiResponseLearningMilestoneResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ApiResponseLearningMilestoneResponse update6(UUID planId, UUID milestoneId, LearningMilestoneRequest learningMilestoneRequest) throws RestClientResponseException {
        ParameterizedTypeReference<ApiResponseLearningMilestoneResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return update6RequestCreation(planId, milestoneId, learningMilestoneRequest).body(localVarReturnType);
    }

    /**
     * Update learning milestone
     * 
     * <p><b>200</b> - OK
     * @param planId Learning plan identifier
     * @param milestoneId Learning milestone identifier
     * @param learningMilestoneRequest The learningMilestoneRequest parameter
     * @return ResponseEntity&lt;ApiResponseLearningMilestoneResponse&gt;
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<ApiResponseLearningMilestoneResponse> update6WithHttpInfo(UUID planId, UUID milestoneId, LearningMilestoneRequest learningMilestoneRequest) throws RestClientResponseException {
        ParameterizedTypeReference<ApiResponseLearningMilestoneResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return update6RequestCreation(planId, milestoneId, learningMilestoneRequest).toEntity(localVarReturnType);
    }

    /**
     * Update learning milestone
     * 
     * <p><b>200</b> - OK
     * @param planId Learning plan identifier
     * @param milestoneId Learning milestone identifier
     * @param learningMilestoneRequest The learningMilestoneRequest parameter
     * @return ResponseSpec
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec update6WithResponseSpec(UUID planId, UUID milestoneId, LearningMilestoneRequest learningMilestoneRequest) throws RestClientResponseException {
        return update6RequestCreation(planId, milestoneId, learningMilestoneRequest);
    }
}

package com.acos.sdk.generated.api;

import com.acos.sdk.generated.ApiClient;

import com.acos.sdk.generated.model.ApiResponseLearningTopicResponse;
import com.acos.sdk.generated.model.ApiResponseListLearningTopicResponse;
import com.acos.sdk.generated.model.ApiResponseVoid;
import com.acos.sdk.generated.model.LearningTopicRequest;
import com.acos.sdk.generated.model.TopicStatusUpdateRequest;
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
public class LearningTopicsApi {
    private ApiClient apiClient;

    public LearningTopicsApi() {
        this(new ApiClient());
    }

    @Autowired
    public LearningTopicsApi(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public ApiClient getApiClient() {
        return apiClient;
    }

    public void setApiClient(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    /**
     * Create learning topic
     * 
     * <p><b>200</b> - OK
     * @param planId Learning plan identifier
     * @param milestoneId Learning milestone identifier
     * @param learningTopicRequest The learningTopicRequest parameter
     * @return ApiResponseLearningTopicResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec create7RequestCreation(UUID planId, UUID milestoneId, LearningTopicRequest learningTopicRequest) throws RestClientResponseException {
        Object postBody = learningTopicRequest;
        // verify the required parameter 'planId' is set
        if (planId == null) {
            throw new RestClientResponseException("Missing the required parameter 'planId' when calling create7", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // verify the required parameter 'milestoneId' is set
        if (milestoneId == null) {
            throw new RestClientResponseException("Missing the required parameter 'milestoneId' when calling create7", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // verify the required parameter 'learningTopicRequest' is set
        if (learningTopicRequest == null) {
            throw new RestClientResponseException("Missing the required parameter 'learningTopicRequest' when calling create7", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
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

        ParameterizedTypeReference<ApiResponseLearningTopicResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return apiClient.invokeAPI("/api/v1/learning/plans/{planId}/milestones/{milestoneId}/topics", HttpMethod.POST, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Create learning topic
     * 
     * <p><b>200</b> - OK
     * @param planId Learning plan identifier
     * @param milestoneId Learning milestone identifier
     * @param learningTopicRequest The learningTopicRequest parameter
     * @return ApiResponseLearningTopicResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ApiResponseLearningTopicResponse create7(UUID planId, UUID milestoneId, LearningTopicRequest learningTopicRequest) throws RestClientResponseException {
        ParameterizedTypeReference<ApiResponseLearningTopicResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return create7RequestCreation(planId, milestoneId, learningTopicRequest).body(localVarReturnType);
    }

    /**
     * Create learning topic
     * 
     * <p><b>200</b> - OK
     * @param planId Learning plan identifier
     * @param milestoneId Learning milestone identifier
     * @param learningTopicRequest The learningTopicRequest parameter
     * @return ResponseEntity&lt;ApiResponseLearningTopicResponse&gt;
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<ApiResponseLearningTopicResponse> create7WithHttpInfo(UUID planId, UUID milestoneId, LearningTopicRequest learningTopicRequest) throws RestClientResponseException {
        ParameterizedTypeReference<ApiResponseLearningTopicResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return create7RequestCreation(planId, milestoneId, learningTopicRequest).toEntity(localVarReturnType);
    }

    /**
     * Create learning topic
     * 
     * <p><b>200</b> - OK
     * @param planId Learning plan identifier
     * @param milestoneId Learning milestone identifier
     * @param learningTopicRequest The learningTopicRequest parameter
     * @return ResponseSpec
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec create7WithResponseSpec(UUID planId, UUID milestoneId, LearningTopicRequest learningTopicRequest) throws RestClientResponseException {
        return create7RequestCreation(planId, milestoneId, learningTopicRequest);
    }
    /**
     * Delete learning topic
     * 
     * <p><b>200</b> - OK
     * @param planId Learning plan identifier
     * @param milestoneId Learning milestone identifier
     * @param topicId Learning topic identifier
     * @return ApiResponseVoid
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec delete7RequestCreation(UUID planId, UUID milestoneId, UUID topicId) throws RestClientResponseException {
        Object postBody = null;
        // verify the required parameter 'planId' is set
        if (planId == null) {
            throw new RestClientResponseException("Missing the required parameter 'planId' when calling delete7", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // verify the required parameter 'milestoneId' is set
        if (milestoneId == null) {
            throw new RestClientResponseException("Missing the required parameter 'milestoneId' when calling delete7", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // verify the required parameter 'topicId' is set
        if (topicId == null) {
            throw new RestClientResponseException("Missing the required parameter 'topicId' when calling delete7", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // create path and map variables
        final Map<String, Object> pathParams = new HashMap<>();

        pathParams.put("planId", planId);
        pathParams.put("milestoneId", milestoneId);
        pathParams.put("topicId", topicId);

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
        return apiClient.invokeAPI("/api/v1/learning/plans/{planId}/milestones/{milestoneId}/topics/{topicId}", HttpMethod.DELETE, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Delete learning topic
     * 
     * <p><b>200</b> - OK
     * @param planId Learning plan identifier
     * @param milestoneId Learning milestone identifier
     * @param topicId Learning topic identifier
     * @return ApiResponseVoid
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ApiResponseVoid delete7(UUID planId, UUID milestoneId, UUID topicId) throws RestClientResponseException {
        ParameterizedTypeReference<ApiResponseVoid> localVarReturnType = new ParameterizedTypeReference<>() {};
        return delete7RequestCreation(planId, milestoneId, topicId).body(localVarReturnType);
    }

    /**
     * Delete learning topic
     * 
     * <p><b>200</b> - OK
     * @param planId Learning plan identifier
     * @param milestoneId Learning milestone identifier
     * @param topicId Learning topic identifier
     * @return ResponseEntity&lt;ApiResponseVoid&gt;
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<ApiResponseVoid> delete7WithHttpInfo(UUID planId, UUID milestoneId, UUID topicId) throws RestClientResponseException {
        ParameterizedTypeReference<ApiResponseVoid> localVarReturnType = new ParameterizedTypeReference<>() {};
        return delete7RequestCreation(planId, milestoneId, topicId).toEntity(localVarReturnType);
    }

    /**
     * Delete learning topic
     * 
     * <p><b>200</b> - OK
     * @param planId Learning plan identifier
     * @param milestoneId Learning milestone identifier
     * @param topicId Learning topic identifier
     * @return ResponseSpec
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec delete7WithResponseSpec(UUID planId, UUID milestoneId, UUID topicId) throws RestClientResponseException {
        return delete7RequestCreation(planId, milestoneId, topicId);
    }
    /**
     * List learning topics
     * 
     * <p><b>200</b> - OK
     * @param planId Learning plan identifier
     * @param milestoneId Learning milestone identifier
     * @return ApiResponseListLearningTopicResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec list7RequestCreation(UUID planId, UUID milestoneId) throws RestClientResponseException {
        Object postBody = null;
        // verify the required parameter 'planId' is set
        if (planId == null) {
            throw new RestClientResponseException("Missing the required parameter 'planId' when calling list7", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // verify the required parameter 'milestoneId' is set
        if (milestoneId == null) {
            throw new RestClientResponseException("Missing the required parameter 'milestoneId' when calling list7", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
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

        ParameterizedTypeReference<ApiResponseListLearningTopicResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return apiClient.invokeAPI("/api/v1/learning/plans/{planId}/milestones/{milestoneId}/topics", HttpMethod.GET, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * List learning topics
     * 
     * <p><b>200</b> - OK
     * @param planId Learning plan identifier
     * @param milestoneId Learning milestone identifier
     * @return ApiResponseListLearningTopicResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ApiResponseListLearningTopicResponse list7(UUID planId, UUID milestoneId) throws RestClientResponseException {
        ParameterizedTypeReference<ApiResponseListLearningTopicResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return list7RequestCreation(planId, milestoneId).body(localVarReturnType);
    }

    /**
     * List learning topics
     * 
     * <p><b>200</b> - OK
     * @param planId Learning plan identifier
     * @param milestoneId Learning milestone identifier
     * @return ResponseEntity&lt;ApiResponseListLearningTopicResponse&gt;
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<ApiResponseListLearningTopicResponse> list7WithHttpInfo(UUID planId, UUID milestoneId) throws RestClientResponseException {
        ParameterizedTypeReference<ApiResponseListLearningTopicResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return list7RequestCreation(planId, milestoneId).toEntity(localVarReturnType);
    }

    /**
     * List learning topics
     * 
     * <p><b>200</b> - OK
     * @param planId Learning plan identifier
     * @param milestoneId Learning milestone identifier
     * @return ResponseSpec
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec list7WithResponseSpec(UUID planId, UUID milestoneId) throws RestClientResponseException {
        return list7RequestCreation(planId, milestoneId);
    }
    /**
     * Update learning topic
     * 
     * <p><b>200</b> - OK
     * @param planId Learning plan identifier
     * @param milestoneId Learning milestone identifier
     * @param topicId Learning topic identifier
     * @param learningTopicRequest The learningTopicRequest parameter
     * @return ApiResponseLearningTopicResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec update7RequestCreation(UUID planId, UUID milestoneId, UUID topicId, LearningTopicRequest learningTopicRequest) throws RestClientResponseException {
        Object postBody = learningTopicRequest;
        // verify the required parameter 'planId' is set
        if (planId == null) {
            throw new RestClientResponseException("Missing the required parameter 'planId' when calling update7", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // verify the required parameter 'milestoneId' is set
        if (milestoneId == null) {
            throw new RestClientResponseException("Missing the required parameter 'milestoneId' when calling update7", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // verify the required parameter 'topicId' is set
        if (topicId == null) {
            throw new RestClientResponseException("Missing the required parameter 'topicId' when calling update7", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // verify the required parameter 'learningTopicRequest' is set
        if (learningTopicRequest == null) {
            throw new RestClientResponseException("Missing the required parameter 'learningTopicRequest' when calling update7", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // create path and map variables
        final Map<String, Object> pathParams = new HashMap<>();

        pathParams.put("planId", planId);
        pathParams.put("milestoneId", milestoneId);
        pathParams.put("topicId", topicId);

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

        ParameterizedTypeReference<ApiResponseLearningTopicResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return apiClient.invokeAPI("/api/v1/learning/plans/{planId}/milestones/{milestoneId}/topics/{topicId}", HttpMethod.PUT, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Update learning topic
     * 
     * <p><b>200</b> - OK
     * @param planId Learning plan identifier
     * @param milestoneId Learning milestone identifier
     * @param topicId Learning topic identifier
     * @param learningTopicRequest The learningTopicRequest parameter
     * @return ApiResponseLearningTopicResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ApiResponseLearningTopicResponse update7(UUID planId, UUID milestoneId, UUID topicId, LearningTopicRequest learningTopicRequest) throws RestClientResponseException {
        ParameterizedTypeReference<ApiResponseLearningTopicResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return update7RequestCreation(planId, milestoneId, topicId, learningTopicRequest).body(localVarReturnType);
    }

    /**
     * Update learning topic
     * 
     * <p><b>200</b> - OK
     * @param planId Learning plan identifier
     * @param milestoneId Learning milestone identifier
     * @param topicId Learning topic identifier
     * @param learningTopicRequest The learningTopicRequest parameter
     * @return ResponseEntity&lt;ApiResponseLearningTopicResponse&gt;
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<ApiResponseLearningTopicResponse> update7WithHttpInfo(UUID planId, UUID milestoneId, UUID topicId, LearningTopicRequest learningTopicRequest) throws RestClientResponseException {
        ParameterizedTypeReference<ApiResponseLearningTopicResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return update7RequestCreation(planId, milestoneId, topicId, learningTopicRequest).toEntity(localVarReturnType);
    }

    /**
     * Update learning topic
     * 
     * <p><b>200</b> - OK
     * @param planId Learning plan identifier
     * @param milestoneId Learning milestone identifier
     * @param topicId Learning topic identifier
     * @param learningTopicRequest The learningTopicRequest parameter
     * @return ResponseSpec
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec update7WithResponseSpec(UUID planId, UUID milestoneId, UUID topicId, LearningTopicRequest learningTopicRequest) throws RestClientResponseException {
        return update7RequestCreation(planId, milestoneId, topicId, learningTopicRequest);
    }
    /**
     * Update learning topic status
     * 
     * <p><b>200</b> - OK
     * @param planId Learning plan identifier
     * @param milestoneId Learning milestone identifier
     * @param topicId Learning topic identifier
     * @param topicStatusUpdateRequest The topicStatusUpdateRequest parameter
     * @return ApiResponseLearningTopicResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec updateStatusRequestCreation(UUID planId, UUID milestoneId, UUID topicId, TopicStatusUpdateRequest topicStatusUpdateRequest) throws RestClientResponseException {
        Object postBody = topicStatusUpdateRequest;
        // verify the required parameter 'planId' is set
        if (planId == null) {
            throw new RestClientResponseException("Missing the required parameter 'planId' when calling updateStatus", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // verify the required parameter 'milestoneId' is set
        if (milestoneId == null) {
            throw new RestClientResponseException("Missing the required parameter 'milestoneId' when calling updateStatus", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // verify the required parameter 'topicId' is set
        if (topicId == null) {
            throw new RestClientResponseException("Missing the required parameter 'topicId' when calling updateStatus", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // verify the required parameter 'topicStatusUpdateRequest' is set
        if (topicStatusUpdateRequest == null) {
            throw new RestClientResponseException("Missing the required parameter 'topicStatusUpdateRequest' when calling updateStatus", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // create path and map variables
        final Map<String, Object> pathParams = new HashMap<>();

        pathParams.put("planId", planId);
        pathParams.put("milestoneId", milestoneId);
        pathParams.put("topicId", topicId);

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

        ParameterizedTypeReference<ApiResponseLearningTopicResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return apiClient.invokeAPI("/api/v1/learning/plans/{planId}/milestones/{milestoneId}/topics/{topicId}/status", HttpMethod.PATCH, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Update learning topic status
     * 
     * <p><b>200</b> - OK
     * @param planId Learning plan identifier
     * @param milestoneId Learning milestone identifier
     * @param topicId Learning topic identifier
     * @param topicStatusUpdateRequest The topicStatusUpdateRequest parameter
     * @return ApiResponseLearningTopicResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ApiResponseLearningTopicResponse updateStatus(UUID planId, UUID milestoneId, UUID topicId, TopicStatusUpdateRequest topicStatusUpdateRequest) throws RestClientResponseException {
        ParameterizedTypeReference<ApiResponseLearningTopicResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return updateStatusRequestCreation(planId, milestoneId, topicId, topicStatusUpdateRequest).body(localVarReturnType);
    }

    /**
     * Update learning topic status
     * 
     * <p><b>200</b> - OK
     * @param planId Learning plan identifier
     * @param milestoneId Learning milestone identifier
     * @param topicId Learning topic identifier
     * @param topicStatusUpdateRequest The topicStatusUpdateRequest parameter
     * @return ResponseEntity&lt;ApiResponseLearningTopicResponse&gt;
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<ApiResponseLearningTopicResponse> updateStatusWithHttpInfo(UUID planId, UUID milestoneId, UUID topicId, TopicStatusUpdateRequest topicStatusUpdateRequest) throws RestClientResponseException {
        ParameterizedTypeReference<ApiResponseLearningTopicResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return updateStatusRequestCreation(planId, milestoneId, topicId, topicStatusUpdateRequest).toEntity(localVarReturnType);
    }

    /**
     * Update learning topic status
     * 
     * <p><b>200</b> - OK
     * @param planId Learning plan identifier
     * @param milestoneId Learning milestone identifier
     * @param topicId Learning topic identifier
     * @param topicStatusUpdateRequest The topicStatusUpdateRequest parameter
     * @return ResponseSpec
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec updateStatusWithResponseSpec(UUID planId, UUID milestoneId, UUID topicId, TopicStatusUpdateRequest topicStatusUpdateRequest) throws RestClientResponseException {
        return updateStatusRequestCreation(planId, milestoneId, topicId, topicStatusUpdateRequest);
    }
}

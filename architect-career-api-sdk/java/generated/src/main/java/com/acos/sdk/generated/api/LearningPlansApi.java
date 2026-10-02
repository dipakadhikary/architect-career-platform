package com.acos.sdk.generated.api;

import com.acos.sdk.generated.ApiClient;

import com.acos.sdk.generated.model.ApiResponseLearningPlanPageResponse;
import com.acos.sdk.generated.model.ApiResponseLearningPlanResponse;
import com.acos.sdk.generated.model.ApiResponseVoid;
import com.acos.sdk.generated.model.LearningErrorApiResponse;
import com.acos.sdk.generated.model.LearningPlanApiResponse;
import com.acos.sdk.generated.model.LearningPlanRequest;
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
public class LearningPlansApi {
    private ApiClient apiClient;

    public LearningPlansApi() {
        this(new ApiClient());
    }

    @Autowired
    public LearningPlansApi(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public ApiClient getApiClient() {
        return apiClient;
    }

    public void setApiClient(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    /**
     * Create learning plan
     * 
     * <p><b>201</b> - Plan created
     * <p><b>400</b> - Validation failed
     * <p><b>401</b> - Authentication required
     * @param learningPlanRequest The learningPlanRequest parameter
     * @return LearningPlanApiResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec create5RequestCreation(LearningPlanRequest learningPlanRequest) throws RestClientResponseException {
        Object postBody = learningPlanRequest;
        // verify the required parameter 'learningPlanRequest' is set
        if (learningPlanRequest == null) {
            throw new RestClientResponseException("Missing the required parameter 'learningPlanRequest' when calling create5", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
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

        ParameterizedTypeReference<LearningPlanApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return apiClient.invokeAPI("/api/v1/learning/plans", HttpMethod.POST, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Create learning plan
     * 
     * <p><b>201</b> - Plan created
     * <p><b>400</b> - Validation failed
     * <p><b>401</b> - Authentication required
     * @param learningPlanRequest The learningPlanRequest parameter
     * @return LearningPlanApiResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public LearningPlanApiResponse create5(LearningPlanRequest learningPlanRequest) throws RestClientResponseException {
        ParameterizedTypeReference<LearningPlanApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return create5RequestCreation(learningPlanRequest).body(localVarReturnType);
    }

    /**
     * Create learning plan
     * 
     * <p><b>201</b> - Plan created
     * <p><b>400</b> - Validation failed
     * <p><b>401</b> - Authentication required
     * @param learningPlanRequest The learningPlanRequest parameter
     * @return ResponseEntity&lt;LearningPlanApiResponse&gt;
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<LearningPlanApiResponse> create5WithHttpInfo(LearningPlanRequest learningPlanRequest) throws RestClientResponseException {
        ParameterizedTypeReference<LearningPlanApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return create5RequestCreation(learningPlanRequest).toEntity(localVarReturnType);
    }

    /**
     * Create learning plan
     * 
     * <p><b>201</b> - Plan created
     * <p><b>400</b> - Validation failed
     * <p><b>401</b> - Authentication required
     * @param learningPlanRequest The learningPlanRequest parameter
     * @return ResponseSpec
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec create5WithResponseSpec(LearningPlanRequest learningPlanRequest) throws RestClientResponseException {
        return create5RequestCreation(learningPlanRequest);
    }
    /**
     * Delete learning plan
     * 
     * <p><b>200</b> - OK
     * @param planId Learning plan identifier
     * @return ApiResponseVoid
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec delete5RequestCreation(UUID planId) throws RestClientResponseException {
        Object postBody = null;
        // verify the required parameter 'planId' is set
        if (planId == null) {
            throw new RestClientResponseException("Missing the required parameter 'planId' when calling delete5", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
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

        ParameterizedTypeReference<ApiResponseVoid> localVarReturnType = new ParameterizedTypeReference<>() {};
        return apiClient.invokeAPI("/api/v1/learning/plans/{planId}", HttpMethod.DELETE, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Delete learning plan
     * 
     * <p><b>200</b> - OK
     * @param planId Learning plan identifier
     * @return ApiResponseVoid
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ApiResponseVoid delete5(UUID planId) throws RestClientResponseException {
        ParameterizedTypeReference<ApiResponseVoid> localVarReturnType = new ParameterizedTypeReference<>() {};
        return delete5RequestCreation(planId).body(localVarReturnType);
    }

    /**
     * Delete learning plan
     * 
     * <p><b>200</b> - OK
     * @param planId Learning plan identifier
     * @return ResponseEntity&lt;ApiResponseVoid&gt;
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<ApiResponseVoid> delete5WithHttpInfo(UUID planId) throws RestClientResponseException {
        ParameterizedTypeReference<ApiResponseVoid> localVarReturnType = new ParameterizedTypeReference<>() {};
        return delete5RequestCreation(planId).toEntity(localVarReturnType);
    }

    /**
     * Delete learning plan
     * 
     * <p><b>200</b> - OK
     * @param planId Learning plan identifier
     * @return ResponseSpec
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec delete5WithResponseSpec(UUID planId) throws RestClientResponseException {
        return delete5RequestCreation(planId);
    }
    /**
     * Get learning plan
     * 
     * <p><b>200</b> - OK
     * @param planId Learning plan identifier
     * @return ApiResponseLearningPlanResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec get5RequestCreation(UUID planId) throws RestClientResponseException {
        Object postBody = null;
        // verify the required parameter 'planId' is set
        if (planId == null) {
            throw new RestClientResponseException("Missing the required parameter 'planId' when calling get5", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
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

        ParameterizedTypeReference<ApiResponseLearningPlanResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return apiClient.invokeAPI("/api/v1/learning/plans/{planId}", HttpMethod.GET, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Get learning plan
     * 
     * <p><b>200</b> - OK
     * @param planId Learning plan identifier
     * @return ApiResponseLearningPlanResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ApiResponseLearningPlanResponse get5(UUID planId) throws RestClientResponseException {
        ParameterizedTypeReference<ApiResponseLearningPlanResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return get5RequestCreation(planId).body(localVarReturnType);
    }

    /**
     * Get learning plan
     * 
     * <p><b>200</b> - OK
     * @param planId Learning plan identifier
     * @return ResponseEntity&lt;ApiResponseLearningPlanResponse&gt;
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<ApiResponseLearningPlanResponse> get5WithHttpInfo(UUID planId) throws RestClientResponseException {
        ParameterizedTypeReference<ApiResponseLearningPlanResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return get5RequestCreation(planId).toEntity(localVarReturnType);
    }

    /**
     * Get learning plan
     * 
     * <p><b>200</b> - OK
     * @param planId Learning plan identifier
     * @return ResponseSpec
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec get5WithResponseSpec(UUID planId) throws RestClientResponseException {
        return get5RequestCreation(planId);
    }
    /**
     * List learning plans
     * 
     * <p><b>200</b> - OK
     * @param page Zero-based page index (0..N)
     * @param size The size of the page to be returned
     * @param sort Sorting criteria in the format: property,(asc|desc). Default sort order is ascending. Multiple sort criteria are supported.
     * @return ApiResponseLearningPlanPageResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec list5RequestCreation(Integer page, Integer size, List<String> sort) throws RestClientResponseException {
        Object postBody = null;
        // create path and map variables
        final Map<String, Object> pathParams = new HashMap<>();

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, String> cookieParams = new LinkedMultiValueMap<>();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<>();

        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "page", page));
        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "size", size));
        queryParams.putAll(apiClient.parameterToMultiValueMap(ApiClient.CollectionFormat.valueOf("multi".toUpperCase(Locale.ROOT)), "sort", sort));
        
        final String[] localVarAccepts = { 
            "application/json"
        };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        String[] localVarAuthNames = new String[] { "bearer-jwt" };

        ParameterizedTypeReference<ApiResponseLearningPlanPageResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return apiClient.invokeAPI("/api/v1/learning/plans", HttpMethod.GET, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * List learning plans
     * 
     * <p><b>200</b> - OK
     * @param page Zero-based page index (0..N)
     * @param size The size of the page to be returned
     * @param sort Sorting criteria in the format: property,(asc|desc). Default sort order is ascending. Multiple sort criteria are supported.
     * @return ApiResponseLearningPlanPageResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ApiResponseLearningPlanPageResponse list5(Integer page, Integer size, List<String> sort) throws RestClientResponseException {
        ParameterizedTypeReference<ApiResponseLearningPlanPageResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return list5RequestCreation(page, size, sort).body(localVarReturnType);
    }

    /**
     * List learning plans
     * 
     * <p><b>200</b> - OK
     * @param page Zero-based page index (0..N)
     * @param size The size of the page to be returned
     * @param sort Sorting criteria in the format: property,(asc|desc). Default sort order is ascending. Multiple sort criteria are supported.
     * @return ResponseEntity&lt;ApiResponseLearningPlanPageResponse&gt;
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<ApiResponseLearningPlanPageResponse> list5WithHttpInfo(Integer page, Integer size, List<String> sort) throws RestClientResponseException {
        ParameterizedTypeReference<ApiResponseLearningPlanPageResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return list5RequestCreation(page, size, sort).toEntity(localVarReturnType);
    }

    /**
     * List learning plans
     * 
     * <p><b>200</b> - OK
     * @param page Zero-based page index (0..N)
     * @param size The size of the page to be returned
     * @param sort Sorting criteria in the format: property,(asc|desc). Default sort order is ascending. Multiple sort criteria are supported.
     * @return ResponseSpec
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec list5WithResponseSpec(Integer page, Integer size, List<String> sort) throws RestClientResponseException {
        return list5RequestCreation(page, size, sort);
    }
    /**
     * Update learning plan
     * 
     * <p><b>200</b> - OK
     * @param planId Learning plan identifier
     * @param learningPlanRequest The learningPlanRequest parameter
     * @return ApiResponseLearningPlanResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec update5RequestCreation(UUID planId, LearningPlanRequest learningPlanRequest) throws RestClientResponseException {
        Object postBody = learningPlanRequest;
        // verify the required parameter 'planId' is set
        if (planId == null) {
            throw new RestClientResponseException("Missing the required parameter 'planId' when calling update5", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // verify the required parameter 'learningPlanRequest' is set
        if (learningPlanRequest == null) {
            throw new RestClientResponseException("Missing the required parameter 'learningPlanRequest' when calling update5", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
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

        ParameterizedTypeReference<ApiResponseLearningPlanResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return apiClient.invokeAPI("/api/v1/learning/plans/{planId}", HttpMethod.PUT, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Update learning plan
     * 
     * <p><b>200</b> - OK
     * @param planId Learning plan identifier
     * @param learningPlanRequest The learningPlanRequest parameter
     * @return ApiResponseLearningPlanResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ApiResponseLearningPlanResponse update5(UUID planId, LearningPlanRequest learningPlanRequest) throws RestClientResponseException {
        ParameterizedTypeReference<ApiResponseLearningPlanResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return update5RequestCreation(planId, learningPlanRequest).body(localVarReturnType);
    }

    /**
     * Update learning plan
     * 
     * <p><b>200</b> - OK
     * @param planId Learning plan identifier
     * @param learningPlanRequest The learningPlanRequest parameter
     * @return ResponseEntity&lt;ApiResponseLearningPlanResponse&gt;
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<ApiResponseLearningPlanResponse> update5WithHttpInfo(UUID planId, LearningPlanRequest learningPlanRequest) throws RestClientResponseException {
        ParameterizedTypeReference<ApiResponseLearningPlanResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return update5RequestCreation(planId, learningPlanRequest).toEntity(localVarReturnType);
    }

    /**
     * Update learning plan
     * 
     * <p><b>200</b> - OK
     * @param planId Learning plan identifier
     * @param learningPlanRequest The learningPlanRequest parameter
     * @return ResponseSpec
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec update5WithResponseSpec(UUID planId, LearningPlanRequest learningPlanRequest) throws RestClientResponseException {
        return update5RequestCreation(planId, learningPlanRequest);
    }
}

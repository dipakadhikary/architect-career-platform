package com.acos.sdk.generated.api;

import com.acos.sdk.generated.ApiClient;

import com.acos.sdk.generated.model.ApiResponseListSkillResponse;
import com.acos.sdk.generated.model.ApiResponseSkillResponse;
import com.acos.sdk.generated.model.ApiResponseVoid;
import com.acos.sdk.generated.model.SkillRequest;
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
public class PortfolioSkillsApi {
    private ApiClient apiClient;

    public PortfolioSkillsApi() {
        this(new ApiClient());
    }

    @Autowired
    public PortfolioSkillsApi(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public ApiClient getApiClient() {
        return apiClient;
    }

    public void setApiClient(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    /**
     * Create skill
     * 
     * <p><b>200</b> - OK
     * @param skillRequest The skillRequest parameter
     * @return ApiResponseSkillResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec create1RequestCreation(SkillRequest skillRequest) throws RestClientResponseException {
        Object postBody = skillRequest;
        // verify the required parameter 'skillRequest' is set
        if (skillRequest == null) {
            throw new RestClientResponseException("Missing the required parameter 'skillRequest' when calling create1", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
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

        ParameterizedTypeReference<ApiResponseSkillResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return apiClient.invokeAPI("/api/v1/portfolio/skills", HttpMethod.POST, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Create skill
     * 
     * <p><b>200</b> - OK
     * @param skillRequest The skillRequest parameter
     * @return ApiResponseSkillResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ApiResponseSkillResponse create1(SkillRequest skillRequest) throws RestClientResponseException {
        ParameterizedTypeReference<ApiResponseSkillResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return create1RequestCreation(skillRequest).body(localVarReturnType);
    }

    /**
     * Create skill
     * 
     * <p><b>200</b> - OK
     * @param skillRequest The skillRequest parameter
     * @return ResponseEntity&lt;ApiResponseSkillResponse&gt;
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<ApiResponseSkillResponse> create1WithHttpInfo(SkillRequest skillRequest) throws RestClientResponseException {
        ParameterizedTypeReference<ApiResponseSkillResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return create1RequestCreation(skillRequest).toEntity(localVarReturnType);
    }

    /**
     * Create skill
     * 
     * <p><b>200</b> - OK
     * @param skillRequest The skillRequest parameter
     * @return ResponseSpec
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec create1WithResponseSpec(SkillRequest skillRequest) throws RestClientResponseException {
        return create1RequestCreation(skillRequest);
    }
    /**
     * Delete skill
     * 
     * <p><b>200</b> - OK
     * @param skillId Skill identifier
     * @return ApiResponseVoid
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec delete1RequestCreation(UUID skillId) throws RestClientResponseException {
        Object postBody = null;
        // verify the required parameter 'skillId' is set
        if (skillId == null) {
            throw new RestClientResponseException("Missing the required parameter 'skillId' when calling delete1", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // create path and map variables
        final Map<String, Object> pathParams = new HashMap<>();

        pathParams.put("skillId", skillId);

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
        return apiClient.invokeAPI("/api/v1/portfolio/skills/{skillId}", HttpMethod.DELETE, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Delete skill
     * 
     * <p><b>200</b> - OK
     * @param skillId Skill identifier
     * @return ApiResponseVoid
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ApiResponseVoid delete1(UUID skillId) throws RestClientResponseException {
        ParameterizedTypeReference<ApiResponseVoid> localVarReturnType = new ParameterizedTypeReference<>() {};
        return delete1RequestCreation(skillId).body(localVarReturnType);
    }

    /**
     * Delete skill
     * 
     * <p><b>200</b> - OK
     * @param skillId Skill identifier
     * @return ResponseEntity&lt;ApiResponseVoid&gt;
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<ApiResponseVoid> delete1WithHttpInfo(UUID skillId) throws RestClientResponseException {
        ParameterizedTypeReference<ApiResponseVoid> localVarReturnType = new ParameterizedTypeReference<>() {};
        return delete1RequestCreation(skillId).toEntity(localVarReturnType);
    }

    /**
     * Delete skill
     * 
     * <p><b>200</b> - OK
     * @param skillId Skill identifier
     * @return ResponseSpec
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec delete1WithResponseSpec(UUID skillId) throws RestClientResponseException {
        return delete1RequestCreation(skillId);
    }
    /**
     * Get skill
     * 
     * <p><b>200</b> - OK
     * @param skillId Skill identifier
     * @return ApiResponseSkillResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec get1RequestCreation(UUID skillId) throws RestClientResponseException {
        Object postBody = null;
        // verify the required parameter 'skillId' is set
        if (skillId == null) {
            throw new RestClientResponseException("Missing the required parameter 'skillId' when calling get1", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // create path and map variables
        final Map<String, Object> pathParams = new HashMap<>();

        pathParams.put("skillId", skillId);

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

        ParameterizedTypeReference<ApiResponseSkillResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return apiClient.invokeAPI("/api/v1/portfolio/skills/{skillId}", HttpMethod.GET, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Get skill
     * 
     * <p><b>200</b> - OK
     * @param skillId Skill identifier
     * @return ApiResponseSkillResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ApiResponseSkillResponse get1(UUID skillId) throws RestClientResponseException {
        ParameterizedTypeReference<ApiResponseSkillResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return get1RequestCreation(skillId).body(localVarReturnType);
    }

    /**
     * Get skill
     * 
     * <p><b>200</b> - OK
     * @param skillId Skill identifier
     * @return ResponseEntity&lt;ApiResponseSkillResponse&gt;
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<ApiResponseSkillResponse> get1WithHttpInfo(UUID skillId) throws RestClientResponseException {
        ParameterizedTypeReference<ApiResponseSkillResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return get1RequestCreation(skillId).toEntity(localVarReturnType);
    }

    /**
     * Get skill
     * 
     * <p><b>200</b> - OK
     * @param skillId Skill identifier
     * @return ResponseSpec
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec get1WithResponseSpec(UUID skillId) throws RestClientResponseException {
        return get1RequestCreation(skillId);
    }
    /**
     * List skills
     * 
     * <p><b>200</b> - OK
     * @return ApiResponseListSkillResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec list1RequestCreation() throws RestClientResponseException {
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

        ParameterizedTypeReference<ApiResponseListSkillResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return apiClient.invokeAPI("/api/v1/portfolio/skills", HttpMethod.GET, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * List skills
     * 
     * <p><b>200</b> - OK
     * @return ApiResponseListSkillResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ApiResponseListSkillResponse list1() throws RestClientResponseException {
        ParameterizedTypeReference<ApiResponseListSkillResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return list1RequestCreation().body(localVarReturnType);
    }

    /**
     * List skills
     * 
     * <p><b>200</b> - OK
     * @return ResponseEntity&lt;ApiResponseListSkillResponse&gt;
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<ApiResponseListSkillResponse> list1WithHttpInfo() throws RestClientResponseException {
        ParameterizedTypeReference<ApiResponseListSkillResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return list1RequestCreation().toEntity(localVarReturnType);
    }

    /**
     * List skills
     * 
     * <p><b>200</b> - OK
     * @return ResponseSpec
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec list1WithResponseSpec() throws RestClientResponseException {
        return list1RequestCreation();
    }
    /**
     * Update skill
     * 
     * <p><b>200</b> - OK
     * @param skillId Skill identifier
     * @param skillRequest The skillRequest parameter
     * @return ApiResponseSkillResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec update1RequestCreation(UUID skillId, SkillRequest skillRequest) throws RestClientResponseException {
        Object postBody = skillRequest;
        // verify the required parameter 'skillId' is set
        if (skillId == null) {
            throw new RestClientResponseException("Missing the required parameter 'skillId' when calling update1", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // verify the required parameter 'skillRequest' is set
        if (skillRequest == null) {
            throw new RestClientResponseException("Missing the required parameter 'skillRequest' when calling update1", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // create path and map variables
        final Map<String, Object> pathParams = new HashMap<>();

        pathParams.put("skillId", skillId);

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

        ParameterizedTypeReference<ApiResponseSkillResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return apiClient.invokeAPI("/api/v1/portfolio/skills/{skillId}", HttpMethod.PUT, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Update skill
     * 
     * <p><b>200</b> - OK
     * @param skillId Skill identifier
     * @param skillRequest The skillRequest parameter
     * @return ApiResponseSkillResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ApiResponseSkillResponse update1(UUID skillId, SkillRequest skillRequest) throws RestClientResponseException {
        ParameterizedTypeReference<ApiResponseSkillResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return update1RequestCreation(skillId, skillRequest).body(localVarReturnType);
    }

    /**
     * Update skill
     * 
     * <p><b>200</b> - OK
     * @param skillId Skill identifier
     * @param skillRequest The skillRequest parameter
     * @return ResponseEntity&lt;ApiResponseSkillResponse&gt;
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<ApiResponseSkillResponse> update1WithHttpInfo(UUID skillId, SkillRequest skillRequest) throws RestClientResponseException {
        ParameterizedTypeReference<ApiResponseSkillResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return update1RequestCreation(skillId, skillRequest).toEntity(localVarReturnType);
    }

    /**
     * Update skill
     * 
     * <p><b>200</b> - OK
     * @param skillId Skill identifier
     * @param skillRequest The skillRequest parameter
     * @return ResponseSpec
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec update1WithResponseSpec(UUID skillId, SkillRequest skillRequest) throws RestClientResponseException {
        return update1RequestCreation(skillId, skillRequest);
    }
}

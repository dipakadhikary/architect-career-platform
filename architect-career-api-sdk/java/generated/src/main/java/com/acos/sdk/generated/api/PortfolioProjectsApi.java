package com.acos.sdk.generated.api;

import com.acos.sdk.generated.ApiClient;

import com.acos.sdk.generated.model.ApiResponsePortfolioProjectPageResponse;
import com.acos.sdk.generated.model.ApiResponsePortfolioProjectResponse;
import com.acos.sdk.generated.model.ApiResponseVoid;
import com.acos.sdk.generated.model.PortfolioErrorApiResponse;
import com.acos.sdk.generated.model.PortfolioProjectApiResponse;
import com.acos.sdk.generated.model.PortfolioProjectRequest;
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
public class PortfolioProjectsApi {
    private ApiClient apiClient;

    public PortfolioProjectsApi() {
        this(new ApiClient());
    }

    @Autowired
    public PortfolioProjectsApi(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public ApiClient getApiClient() {
        return apiClient;
    }

    public void setApiClient(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    /**
     * Create portfolio project
     * 
     * <p><b>201</b> - Project created
     * <p><b>400</b> - Validation failed
     * <p><b>401</b> - Authentication required
     * @param portfolioProjectRequest The portfolioProjectRequest parameter
     * @return PortfolioProjectApiResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec create2RequestCreation(PortfolioProjectRequest portfolioProjectRequest) throws RestClientResponseException {
        Object postBody = portfolioProjectRequest;
        // verify the required parameter 'portfolioProjectRequest' is set
        if (portfolioProjectRequest == null) {
            throw new RestClientResponseException("Missing the required parameter 'portfolioProjectRequest' when calling create2", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
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

        ParameterizedTypeReference<PortfolioProjectApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return apiClient.invokeAPI("/api/v1/portfolio/projects", HttpMethod.POST, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Create portfolio project
     * 
     * <p><b>201</b> - Project created
     * <p><b>400</b> - Validation failed
     * <p><b>401</b> - Authentication required
     * @param portfolioProjectRequest The portfolioProjectRequest parameter
     * @return PortfolioProjectApiResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public PortfolioProjectApiResponse create2(PortfolioProjectRequest portfolioProjectRequest) throws RestClientResponseException {
        ParameterizedTypeReference<PortfolioProjectApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return create2RequestCreation(portfolioProjectRequest).body(localVarReturnType);
    }

    /**
     * Create portfolio project
     * 
     * <p><b>201</b> - Project created
     * <p><b>400</b> - Validation failed
     * <p><b>401</b> - Authentication required
     * @param portfolioProjectRequest The portfolioProjectRequest parameter
     * @return ResponseEntity&lt;PortfolioProjectApiResponse&gt;
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<PortfolioProjectApiResponse> create2WithHttpInfo(PortfolioProjectRequest portfolioProjectRequest) throws RestClientResponseException {
        ParameterizedTypeReference<PortfolioProjectApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return create2RequestCreation(portfolioProjectRequest).toEntity(localVarReturnType);
    }

    /**
     * Create portfolio project
     * 
     * <p><b>201</b> - Project created
     * <p><b>400</b> - Validation failed
     * <p><b>401</b> - Authentication required
     * @param portfolioProjectRequest The portfolioProjectRequest parameter
     * @return ResponseSpec
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec create2WithResponseSpec(PortfolioProjectRequest portfolioProjectRequest) throws RestClientResponseException {
        return create2RequestCreation(portfolioProjectRequest);
    }
    /**
     * Delete portfolio project
     * 
     * <p><b>200</b> - OK
     * @param projectId Portfolio project identifier
     * @return ApiResponseVoid
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec delete2RequestCreation(UUID projectId) throws RestClientResponseException {
        Object postBody = null;
        // verify the required parameter 'projectId' is set
        if (projectId == null) {
            throw new RestClientResponseException("Missing the required parameter 'projectId' when calling delete2", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // create path and map variables
        final Map<String, Object> pathParams = new HashMap<>();

        pathParams.put("projectId", projectId);

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
        return apiClient.invokeAPI("/api/v1/portfolio/projects/{projectId}", HttpMethod.DELETE, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Delete portfolio project
     * 
     * <p><b>200</b> - OK
     * @param projectId Portfolio project identifier
     * @return ApiResponseVoid
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ApiResponseVoid delete2(UUID projectId) throws RestClientResponseException {
        ParameterizedTypeReference<ApiResponseVoid> localVarReturnType = new ParameterizedTypeReference<>() {};
        return delete2RequestCreation(projectId).body(localVarReturnType);
    }

    /**
     * Delete portfolio project
     * 
     * <p><b>200</b> - OK
     * @param projectId Portfolio project identifier
     * @return ResponseEntity&lt;ApiResponseVoid&gt;
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<ApiResponseVoid> delete2WithHttpInfo(UUID projectId) throws RestClientResponseException {
        ParameterizedTypeReference<ApiResponseVoid> localVarReturnType = new ParameterizedTypeReference<>() {};
        return delete2RequestCreation(projectId).toEntity(localVarReturnType);
    }

    /**
     * Delete portfolio project
     * 
     * <p><b>200</b> - OK
     * @param projectId Portfolio project identifier
     * @return ResponseSpec
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec delete2WithResponseSpec(UUID projectId) throws RestClientResponseException {
        return delete2RequestCreation(projectId);
    }
    /**
     * Get portfolio project
     * 
     * <p><b>200</b> - OK
     * @param projectId Portfolio project identifier
     * @return ApiResponsePortfolioProjectResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec get2RequestCreation(UUID projectId) throws RestClientResponseException {
        Object postBody = null;
        // verify the required parameter 'projectId' is set
        if (projectId == null) {
            throw new RestClientResponseException("Missing the required parameter 'projectId' when calling get2", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // create path and map variables
        final Map<String, Object> pathParams = new HashMap<>();

        pathParams.put("projectId", projectId);

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

        ParameterizedTypeReference<ApiResponsePortfolioProjectResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return apiClient.invokeAPI("/api/v1/portfolio/projects/{projectId}", HttpMethod.GET, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Get portfolio project
     * 
     * <p><b>200</b> - OK
     * @param projectId Portfolio project identifier
     * @return ApiResponsePortfolioProjectResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ApiResponsePortfolioProjectResponse get2(UUID projectId) throws RestClientResponseException {
        ParameterizedTypeReference<ApiResponsePortfolioProjectResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return get2RequestCreation(projectId).body(localVarReturnType);
    }

    /**
     * Get portfolio project
     * 
     * <p><b>200</b> - OK
     * @param projectId Portfolio project identifier
     * @return ResponseEntity&lt;ApiResponsePortfolioProjectResponse&gt;
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<ApiResponsePortfolioProjectResponse> get2WithHttpInfo(UUID projectId) throws RestClientResponseException {
        ParameterizedTypeReference<ApiResponsePortfolioProjectResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return get2RequestCreation(projectId).toEntity(localVarReturnType);
    }

    /**
     * Get portfolio project
     * 
     * <p><b>200</b> - OK
     * @param projectId Portfolio project identifier
     * @return ResponseSpec
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec get2WithResponseSpec(UUID projectId) throws RestClientResponseException {
        return get2RequestCreation(projectId);
    }
    /**
     * List portfolio projects
     * 
     * <p><b>200</b> - OK
     * @param page Zero-based page index (0..N)
     * @param size The size of the page to be returned
     * @param sort Sorting criteria in the format: property,(asc|desc). Default sort order is ascending. Multiple sort criteria are supported.
     * @return ApiResponsePortfolioProjectPageResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec list2RequestCreation(Integer page, Integer size, List<String> sort) throws RestClientResponseException {
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

        ParameterizedTypeReference<ApiResponsePortfolioProjectPageResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return apiClient.invokeAPI("/api/v1/portfolio/projects", HttpMethod.GET, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * List portfolio projects
     * 
     * <p><b>200</b> - OK
     * @param page Zero-based page index (0..N)
     * @param size The size of the page to be returned
     * @param sort Sorting criteria in the format: property,(asc|desc). Default sort order is ascending. Multiple sort criteria are supported.
     * @return ApiResponsePortfolioProjectPageResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ApiResponsePortfolioProjectPageResponse list2(Integer page, Integer size, List<String> sort) throws RestClientResponseException {
        ParameterizedTypeReference<ApiResponsePortfolioProjectPageResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return list2RequestCreation(page, size, sort).body(localVarReturnType);
    }

    /**
     * List portfolio projects
     * 
     * <p><b>200</b> - OK
     * @param page Zero-based page index (0..N)
     * @param size The size of the page to be returned
     * @param sort Sorting criteria in the format: property,(asc|desc). Default sort order is ascending. Multiple sort criteria are supported.
     * @return ResponseEntity&lt;ApiResponsePortfolioProjectPageResponse&gt;
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<ApiResponsePortfolioProjectPageResponse> list2WithHttpInfo(Integer page, Integer size, List<String> sort) throws RestClientResponseException {
        ParameterizedTypeReference<ApiResponsePortfolioProjectPageResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return list2RequestCreation(page, size, sort).toEntity(localVarReturnType);
    }

    /**
     * List portfolio projects
     * 
     * <p><b>200</b> - OK
     * @param page Zero-based page index (0..N)
     * @param size The size of the page to be returned
     * @param sort Sorting criteria in the format: property,(asc|desc). Default sort order is ascending. Multiple sort criteria are supported.
     * @return ResponseSpec
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec list2WithResponseSpec(Integer page, Integer size, List<String> sort) throws RestClientResponseException {
        return list2RequestCreation(page, size, sort);
    }
    /**
     * Search portfolio projects
     * 
     * <p><b>200</b> - OK
     * @param q Search text matched against title or summary
     * @param page Zero-based page index (0..N)
     * @param size The size of the page to be returned
     * @param sort Sorting criteria in the format: property,(asc|desc). Default sort order is ascending. Multiple sort criteria are supported.
     * @return ApiResponsePortfolioProjectPageResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec searchRequestCreation(String q, Integer page, Integer size, List<String> sort) throws RestClientResponseException {
        Object postBody = null;
        // verify the required parameter 'q' is set
        if (q == null) {
            throw new RestClientResponseException("Missing the required parameter 'q' when calling search", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // create path and map variables
        final Map<String, Object> pathParams = new HashMap<>();

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, String> cookieParams = new LinkedMultiValueMap<>();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<>();

        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "q", q));
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

        ParameterizedTypeReference<ApiResponsePortfolioProjectPageResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return apiClient.invokeAPI("/api/v1/portfolio/projects/search", HttpMethod.GET, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Search portfolio projects
     * 
     * <p><b>200</b> - OK
     * @param q Search text matched against title or summary
     * @param page Zero-based page index (0..N)
     * @param size The size of the page to be returned
     * @param sort Sorting criteria in the format: property,(asc|desc). Default sort order is ascending. Multiple sort criteria are supported.
     * @return ApiResponsePortfolioProjectPageResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ApiResponsePortfolioProjectPageResponse search(String q, Integer page, Integer size, List<String> sort) throws RestClientResponseException {
        ParameterizedTypeReference<ApiResponsePortfolioProjectPageResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return searchRequestCreation(q, page, size, sort).body(localVarReturnType);
    }

    /**
     * Search portfolio projects
     * 
     * <p><b>200</b> - OK
     * @param q Search text matched against title or summary
     * @param page Zero-based page index (0..N)
     * @param size The size of the page to be returned
     * @param sort Sorting criteria in the format: property,(asc|desc). Default sort order is ascending. Multiple sort criteria are supported.
     * @return ResponseEntity&lt;ApiResponsePortfolioProjectPageResponse&gt;
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<ApiResponsePortfolioProjectPageResponse> searchWithHttpInfo(String q, Integer page, Integer size, List<String> sort) throws RestClientResponseException {
        ParameterizedTypeReference<ApiResponsePortfolioProjectPageResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return searchRequestCreation(q, page, size, sort).toEntity(localVarReturnType);
    }

    /**
     * Search portfolio projects
     * 
     * <p><b>200</b> - OK
     * @param q Search text matched against title or summary
     * @param page Zero-based page index (0..N)
     * @param size The size of the page to be returned
     * @param sort Sorting criteria in the format: property,(asc|desc). Default sort order is ascending. Multiple sort criteria are supported.
     * @return ResponseSpec
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec searchWithResponseSpec(String q, Integer page, Integer size, List<String> sort) throws RestClientResponseException {
        return searchRequestCreation(q, page, size, sort);
    }
    /**
     * Update portfolio project
     * 
     * <p><b>200</b> - OK
     * @param projectId Portfolio project identifier
     * @param portfolioProjectRequest The portfolioProjectRequest parameter
     * @return ApiResponsePortfolioProjectResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec update2RequestCreation(UUID projectId, PortfolioProjectRequest portfolioProjectRequest) throws RestClientResponseException {
        Object postBody = portfolioProjectRequest;
        // verify the required parameter 'projectId' is set
        if (projectId == null) {
            throw new RestClientResponseException("Missing the required parameter 'projectId' when calling update2", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // verify the required parameter 'portfolioProjectRequest' is set
        if (portfolioProjectRequest == null) {
            throw new RestClientResponseException("Missing the required parameter 'portfolioProjectRequest' when calling update2", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // create path and map variables
        final Map<String, Object> pathParams = new HashMap<>();

        pathParams.put("projectId", projectId);

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

        ParameterizedTypeReference<ApiResponsePortfolioProjectResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return apiClient.invokeAPI("/api/v1/portfolio/projects/{projectId}", HttpMethod.PUT, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Update portfolio project
     * 
     * <p><b>200</b> - OK
     * @param projectId Portfolio project identifier
     * @param portfolioProjectRequest The portfolioProjectRequest parameter
     * @return ApiResponsePortfolioProjectResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ApiResponsePortfolioProjectResponse update2(UUID projectId, PortfolioProjectRequest portfolioProjectRequest) throws RestClientResponseException {
        ParameterizedTypeReference<ApiResponsePortfolioProjectResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return update2RequestCreation(projectId, portfolioProjectRequest).body(localVarReturnType);
    }

    /**
     * Update portfolio project
     * 
     * <p><b>200</b> - OK
     * @param projectId Portfolio project identifier
     * @param portfolioProjectRequest The portfolioProjectRequest parameter
     * @return ResponseEntity&lt;ApiResponsePortfolioProjectResponse&gt;
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<ApiResponsePortfolioProjectResponse> update2WithHttpInfo(UUID projectId, PortfolioProjectRequest portfolioProjectRequest) throws RestClientResponseException {
        ParameterizedTypeReference<ApiResponsePortfolioProjectResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return update2RequestCreation(projectId, portfolioProjectRequest).toEntity(localVarReturnType);
    }

    /**
     * Update portfolio project
     * 
     * <p><b>200</b> - OK
     * @param projectId Portfolio project identifier
     * @param portfolioProjectRequest The portfolioProjectRequest parameter
     * @return ResponseSpec
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec update2WithResponseSpec(UUID projectId, PortfolioProjectRequest portfolioProjectRequest) throws RestClientResponseException {
        return update2RequestCreation(projectId, portfolioProjectRequest);
    }
}

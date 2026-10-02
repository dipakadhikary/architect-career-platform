package com.acos.sdk.generated.api;

import com.acos.sdk.generated.ApiClient;

import java.math.BigDecimal;
import com.acos.sdk.generated.model.CareerApplicationHistoryApiResponse;
import com.acos.sdk.generated.model.CareerApplicationTimelineApiResponse;
import com.acos.sdk.generated.model.CareerDeleteApiResponse;
import com.acos.sdk.generated.model.CareerErrorApiResponse;
import com.acos.sdk.generated.model.CareerJobApplicationApiResponse;
import com.acos.sdk.generated.model.CareerJobApplicationPageApiResponse;
import com.acos.sdk.generated.model.JobApplicationRequest;
import java.time.LocalDate;
import com.acos.sdk.generated.model.StatusTransitionRequest;
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
public class CareerApplicationsApi {
    private ApiClient apiClient;

    public CareerApplicationsApi() {
        this(new ApiClient());
    }

    @Autowired
    public CareerApplicationsApi(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public ApiClient getApiClient() {
        return apiClient;
    }

    public void setApiClient(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    /**
     * Archive job application
     * Soft-archives a job application owned by the authenticated user. Archived applications are excluded from default listings and search.
     * <p><b>200</b> - Job application archived
     * <p><b>401</b> - JWT authentication required for application APIs
     * <p><b>404</b> - Job application not found for the authenticated user
     * @param applicationId Job application identifier
     * @return CareerDeleteApiResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec archiveRequestCreation(UUID applicationId) throws RestClientResponseException {
        Object postBody = null;
        // verify the required parameter 'applicationId' is set
        if (applicationId == null) {
            throw new RestClientResponseException("Missing the required parameter 'applicationId' when calling archive", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // create path and map variables
        final Map<String, Object> pathParams = new HashMap<>();

        pathParams.put("applicationId", applicationId);

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

        ParameterizedTypeReference<CareerDeleteApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return apiClient.invokeAPI("/api/v1/career/applications/{applicationId}", HttpMethod.DELETE, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Archive job application
     * Soft-archives a job application owned by the authenticated user. Archived applications are excluded from default listings and search.
     * <p><b>200</b> - Job application archived
     * <p><b>401</b> - JWT authentication required for application APIs
     * <p><b>404</b> - Job application not found for the authenticated user
     * @param applicationId Job application identifier
     * @return CareerDeleteApiResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public CareerDeleteApiResponse archive(UUID applicationId) throws RestClientResponseException {
        ParameterizedTypeReference<CareerDeleteApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return archiveRequestCreation(applicationId).body(localVarReturnType);
    }

    /**
     * Archive job application
     * Soft-archives a job application owned by the authenticated user. Archived applications are excluded from default listings and search.
     * <p><b>200</b> - Job application archived
     * <p><b>401</b> - JWT authentication required for application APIs
     * <p><b>404</b> - Job application not found for the authenticated user
     * @param applicationId Job application identifier
     * @return ResponseEntity&lt;CareerDeleteApiResponse&gt;
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<CareerDeleteApiResponse> archiveWithHttpInfo(UUID applicationId) throws RestClientResponseException {
        ParameterizedTypeReference<CareerDeleteApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return archiveRequestCreation(applicationId).toEntity(localVarReturnType);
    }

    /**
     * Archive job application
     * Soft-archives a job application owned by the authenticated user. Archived applications are excluded from default listings and search.
     * <p><b>200</b> - Job application archived
     * <p><b>401</b> - JWT authentication required for application APIs
     * <p><b>404</b> - Job application not found for the authenticated user
     * @param applicationId Job application identifier
     * @return ResponseSpec
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec archiveWithResponseSpec(UUID applicationId) throws RestClientResponseException {
        return archiveRequestCreation(applicationId);
    }
    /**
     * Create job application
     * Creates a job application owned by the authenticated user. New applications always start in DRAFT. Status changes must use the dedicated status endpoint.
     * <p><b>201</b> - Job application created
     * <p><b>400</b> - Job application request validation failed
     * <p><b>401</b> - JWT authentication required for application APIs
     * <p><b>404</b> - Referenced company or recruiter not found
     * @param jobApplicationRequest The jobApplicationRequest parameter
     * @return CareerJobApplicationApiResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec create11RequestCreation(JobApplicationRequest jobApplicationRequest) throws RestClientResponseException {
        Object postBody = jobApplicationRequest;
        // verify the required parameter 'jobApplicationRequest' is set
        if (jobApplicationRequest == null) {
            throw new RestClientResponseException("Missing the required parameter 'jobApplicationRequest' when calling create11", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
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

        ParameterizedTypeReference<CareerJobApplicationApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return apiClient.invokeAPI("/api/v1/career/applications", HttpMethod.POST, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Create job application
     * Creates a job application owned by the authenticated user. New applications always start in DRAFT. Status changes must use the dedicated status endpoint.
     * <p><b>201</b> - Job application created
     * <p><b>400</b> - Job application request validation failed
     * <p><b>401</b> - JWT authentication required for application APIs
     * <p><b>404</b> - Referenced company or recruiter not found
     * @param jobApplicationRequest The jobApplicationRequest parameter
     * @return CareerJobApplicationApiResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public CareerJobApplicationApiResponse create11(JobApplicationRequest jobApplicationRequest) throws RestClientResponseException {
        ParameterizedTypeReference<CareerJobApplicationApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return create11RequestCreation(jobApplicationRequest).body(localVarReturnType);
    }

    /**
     * Create job application
     * Creates a job application owned by the authenticated user. New applications always start in DRAFT. Status changes must use the dedicated status endpoint.
     * <p><b>201</b> - Job application created
     * <p><b>400</b> - Job application request validation failed
     * <p><b>401</b> - JWT authentication required for application APIs
     * <p><b>404</b> - Referenced company or recruiter not found
     * @param jobApplicationRequest The jobApplicationRequest parameter
     * @return ResponseEntity&lt;CareerJobApplicationApiResponse&gt;
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<CareerJobApplicationApiResponse> create11WithHttpInfo(JobApplicationRequest jobApplicationRequest) throws RestClientResponseException {
        ParameterizedTypeReference<CareerJobApplicationApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return create11RequestCreation(jobApplicationRequest).toEntity(localVarReturnType);
    }

    /**
     * Create job application
     * Creates a job application owned by the authenticated user. New applications always start in DRAFT. Status changes must use the dedicated status endpoint.
     * <p><b>201</b> - Job application created
     * <p><b>400</b> - Job application request validation failed
     * <p><b>401</b> - JWT authentication required for application APIs
     * <p><b>404</b> - Referenced company or recruiter not found
     * @param jobApplicationRequest The jobApplicationRequest parameter
     * @return ResponseSpec
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec create11WithResponseSpec(JobApplicationRequest jobApplicationRequest) throws RestClientResponseException {
        return create11RequestCreation(jobApplicationRequest);
    }
    /**
     * Get job application
     * Returns a job application owned by the authenticated user.
     * <p><b>200</b> - Job application found
     * <p><b>401</b> - JWT authentication required for application APIs
     * <p><b>404</b> - Job application not found for the authenticated user
     * @param applicationId Job application identifier
     * @return CareerJobApplicationApiResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec get10RequestCreation(UUID applicationId) throws RestClientResponseException {
        Object postBody = null;
        // verify the required parameter 'applicationId' is set
        if (applicationId == null) {
            throw new RestClientResponseException("Missing the required parameter 'applicationId' when calling get10", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // create path and map variables
        final Map<String, Object> pathParams = new HashMap<>();

        pathParams.put("applicationId", applicationId);

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

        ParameterizedTypeReference<CareerJobApplicationApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return apiClient.invokeAPI("/api/v1/career/applications/{applicationId}", HttpMethod.GET, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Get job application
     * Returns a job application owned by the authenticated user.
     * <p><b>200</b> - Job application found
     * <p><b>401</b> - JWT authentication required for application APIs
     * <p><b>404</b> - Job application not found for the authenticated user
     * @param applicationId Job application identifier
     * @return CareerJobApplicationApiResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public CareerJobApplicationApiResponse get10(UUID applicationId) throws RestClientResponseException {
        ParameterizedTypeReference<CareerJobApplicationApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return get10RequestCreation(applicationId).body(localVarReturnType);
    }

    /**
     * Get job application
     * Returns a job application owned by the authenticated user.
     * <p><b>200</b> - Job application found
     * <p><b>401</b> - JWT authentication required for application APIs
     * <p><b>404</b> - Job application not found for the authenticated user
     * @param applicationId Job application identifier
     * @return ResponseEntity&lt;CareerJobApplicationApiResponse&gt;
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<CareerJobApplicationApiResponse> get10WithHttpInfo(UUID applicationId) throws RestClientResponseException {
        ParameterizedTypeReference<CareerJobApplicationApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return get10RequestCreation(applicationId).toEntity(localVarReturnType);
    }

    /**
     * Get job application
     * Returns a job application owned by the authenticated user.
     * <p><b>200</b> - Job application found
     * <p><b>401</b> - JWT authentication required for application APIs
     * <p><b>404</b> - Job application not found for the authenticated user
     * @param applicationId Job application identifier
     * @return ResponseSpec
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec get10WithResponseSpec(UUID applicationId) throws RestClientResponseException {
        return get10RequestCreation(applicationId);
    }
    /**
     * Get job application status history
     * Returns the complete ordered status history for a job application owned by the authenticated user.
     * <p><b>200</b> - Status history returned
     * <p><b>401</b> - JWT authentication required for application APIs
     * <p><b>404</b> - Job application not found for the authenticated user
     * @param applicationId Job application identifier
     * @return CareerApplicationHistoryApiResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec getHistoryRequestCreation(UUID applicationId) throws RestClientResponseException {
        Object postBody = null;
        // verify the required parameter 'applicationId' is set
        if (applicationId == null) {
            throw new RestClientResponseException("Missing the required parameter 'applicationId' when calling getHistory", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // create path and map variables
        final Map<String, Object> pathParams = new HashMap<>();

        pathParams.put("applicationId", applicationId);

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

        ParameterizedTypeReference<CareerApplicationHistoryApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return apiClient.invokeAPI("/api/v1/career/applications/{applicationId}/history", HttpMethod.GET, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Get job application status history
     * Returns the complete ordered status history for a job application owned by the authenticated user.
     * <p><b>200</b> - Status history returned
     * <p><b>401</b> - JWT authentication required for application APIs
     * <p><b>404</b> - Job application not found for the authenticated user
     * @param applicationId Job application identifier
     * @return CareerApplicationHistoryApiResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public CareerApplicationHistoryApiResponse getHistory(UUID applicationId) throws RestClientResponseException {
        ParameterizedTypeReference<CareerApplicationHistoryApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return getHistoryRequestCreation(applicationId).body(localVarReturnType);
    }

    /**
     * Get job application status history
     * Returns the complete ordered status history for a job application owned by the authenticated user.
     * <p><b>200</b> - Status history returned
     * <p><b>401</b> - JWT authentication required for application APIs
     * <p><b>404</b> - Job application not found for the authenticated user
     * @param applicationId Job application identifier
     * @return ResponseEntity&lt;CareerApplicationHistoryApiResponse&gt;
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<CareerApplicationHistoryApiResponse> getHistoryWithHttpInfo(UUID applicationId) throws RestClientResponseException {
        ParameterizedTypeReference<CareerApplicationHistoryApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return getHistoryRequestCreation(applicationId).toEntity(localVarReturnType);
    }

    /**
     * Get job application status history
     * Returns the complete ordered status history for a job application owned by the authenticated user.
     * <p><b>200</b> - Status history returned
     * <p><b>401</b> - JWT authentication required for application APIs
     * <p><b>404</b> - Job application not found for the authenticated user
     * @param applicationId Job application identifier
     * @return ResponseSpec
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec getHistoryWithResponseSpec(UUID applicationId) throws RestClientResponseException {
        return getHistoryRequestCreation(applicationId);
    }
    /**
     * Get job application timeline
     * Returns the ordered application timeline milestones (Applied → Screening → Technical Interview → Manager Interview → HR Interview → Offer → Accepted), including alternate terminal outcomes when applicable.
     * <p><b>200</b> - Timeline returned
     * <p><b>401</b> - JWT authentication required for application APIs
     * <p><b>404</b> - Job application not found for the authenticated user
     * @param applicationId Job application identifier
     * @return CareerApplicationTimelineApiResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec getTimelineRequestCreation(UUID applicationId) throws RestClientResponseException {
        Object postBody = null;
        // verify the required parameter 'applicationId' is set
        if (applicationId == null) {
            throw new RestClientResponseException("Missing the required parameter 'applicationId' when calling getTimeline", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // create path and map variables
        final Map<String, Object> pathParams = new HashMap<>();

        pathParams.put("applicationId", applicationId);

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

        ParameterizedTypeReference<CareerApplicationTimelineApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return apiClient.invokeAPI("/api/v1/career/applications/{applicationId}/timeline", HttpMethod.GET, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Get job application timeline
     * Returns the ordered application timeline milestones (Applied → Screening → Technical Interview → Manager Interview → HR Interview → Offer → Accepted), including alternate terminal outcomes when applicable.
     * <p><b>200</b> - Timeline returned
     * <p><b>401</b> - JWT authentication required for application APIs
     * <p><b>404</b> - Job application not found for the authenticated user
     * @param applicationId Job application identifier
     * @return CareerApplicationTimelineApiResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public CareerApplicationTimelineApiResponse getTimeline(UUID applicationId) throws RestClientResponseException {
        ParameterizedTypeReference<CareerApplicationTimelineApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return getTimelineRequestCreation(applicationId).body(localVarReturnType);
    }

    /**
     * Get job application timeline
     * Returns the ordered application timeline milestones (Applied → Screening → Technical Interview → Manager Interview → HR Interview → Offer → Accepted), including alternate terminal outcomes when applicable.
     * <p><b>200</b> - Timeline returned
     * <p><b>401</b> - JWT authentication required for application APIs
     * <p><b>404</b> - Job application not found for the authenticated user
     * @param applicationId Job application identifier
     * @return ResponseEntity&lt;CareerApplicationTimelineApiResponse&gt;
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<CareerApplicationTimelineApiResponse> getTimelineWithHttpInfo(UUID applicationId) throws RestClientResponseException {
        ParameterizedTypeReference<CareerApplicationTimelineApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return getTimelineRequestCreation(applicationId).toEntity(localVarReturnType);
    }

    /**
     * Get job application timeline
     * Returns the ordered application timeline milestones (Applied → Screening → Technical Interview → Manager Interview → HR Interview → Offer → Accepted), including alternate terminal outcomes when applicable.
     * <p><b>200</b> - Timeline returned
     * <p><b>401</b> - JWT authentication required for application APIs
     * <p><b>404</b> - Job application not found for the authenticated user
     * @param applicationId Job application identifier
     * @return ResponseSpec
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec getTimelineWithResponseSpec(UUID applicationId) throws RestClientResponseException {
        return getTimelineRequestCreation(applicationId);
    }
    /**
     * List job applications
     * Lists job applications owned by the authenticated user with pagination and sorting. Archived applications are excluded unless archived&#x3D;true.
     * <p><b>200</b> - Job applications listed
     * <p><b>400</b> - Invalid job application pageable request
     * <p><b>401</b> - JWT authentication required for application APIs
     * @param archived Include archived applications instead of active ones
     * @param page Zero-based page index (0..N)
     * @param size The size of the page to be returned
     * @param sort Sorting criteria in the format: property,(asc|desc). Default sort order is ascending. Multiple sort criteria are supported.
     * @return CareerJobApplicationPageApiResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec list11RequestCreation(Boolean archived, Integer page, Integer size, List<String> sort) throws RestClientResponseException {
        Object postBody = null;
        // create path and map variables
        final Map<String, Object> pathParams = new HashMap<>();

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, String> cookieParams = new LinkedMultiValueMap<>();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<>();

        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "archived", archived));
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

        ParameterizedTypeReference<CareerJobApplicationPageApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return apiClient.invokeAPI("/api/v1/career/applications", HttpMethod.GET, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * List job applications
     * Lists job applications owned by the authenticated user with pagination and sorting. Archived applications are excluded unless archived&#x3D;true.
     * <p><b>200</b> - Job applications listed
     * <p><b>400</b> - Invalid job application pageable request
     * <p><b>401</b> - JWT authentication required for application APIs
     * @param archived Include archived applications instead of active ones
     * @param page Zero-based page index (0..N)
     * @param size The size of the page to be returned
     * @param sort Sorting criteria in the format: property,(asc|desc). Default sort order is ascending. Multiple sort criteria are supported.
     * @return CareerJobApplicationPageApiResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public CareerJobApplicationPageApiResponse list11(Boolean archived, Integer page, Integer size, List<String> sort) throws RestClientResponseException {
        ParameterizedTypeReference<CareerJobApplicationPageApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return list11RequestCreation(archived, page, size, sort).body(localVarReturnType);
    }

    /**
     * List job applications
     * Lists job applications owned by the authenticated user with pagination and sorting. Archived applications are excluded unless archived&#x3D;true.
     * <p><b>200</b> - Job applications listed
     * <p><b>400</b> - Invalid job application pageable request
     * <p><b>401</b> - JWT authentication required for application APIs
     * @param archived Include archived applications instead of active ones
     * @param page Zero-based page index (0..N)
     * @param size The size of the page to be returned
     * @param sort Sorting criteria in the format: property,(asc|desc). Default sort order is ascending. Multiple sort criteria are supported.
     * @return ResponseEntity&lt;CareerJobApplicationPageApiResponse&gt;
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<CareerJobApplicationPageApiResponse> list11WithHttpInfo(Boolean archived, Integer page, Integer size, List<String> sort) throws RestClientResponseException {
        ParameterizedTypeReference<CareerJobApplicationPageApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return list11RequestCreation(archived, page, size, sort).toEntity(localVarReturnType);
    }

    /**
     * List job applications
     * Lists job applications owned by the authenticated user with pagination and sorting. Archived applications are excluded unless archived&#x3D;true.
     * <p><b>200</b> - Job applications listed
     * <p><b>400</b> - Invalid job application pageable request
     * <p><b>401</b> - JWT authentication required for application APIs
     * @param archived Include archived applications instead of active ones
     * @param page Zero-based page index (0..N)
     * @param size The size of the page to be returned
     * @param sort Sorting criteria in the format: property,(asc|desc). Default sort order is ascending. Multiple sort criteria are supported.
     * @return ResponseSpec
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec list11WithResponseSpec(Boolean archived, Integer page, Integer size, List<String> sort) throws RestClientResponseException {
        return list11RequestCreation(archived, page, size, sort);
    }
    /**
     * List archived job applications
     * Lists soft-archived job applications owned by the authenticated user.
     * <p><b>200</b> - Archived job applications listed
     * <p><b>401</b> - JWT authentication required for application APIs
     * @param page Zero-based page index (0..N)
     * @param size The size of the page to be returned
     * @param sort Sorting criteria in the format: property,(asc|desc). Default sort order is ascending. Multiple sort criteria are supported.
     * @return CareerJobApplicationPageApiResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec listArchivedRequestCreation(Integer page, Integer size, List<String> sort) throws RestClientResponseException {
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

        ParameterizedTypeReference<CareerJobApplicationPageApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return apiClient.invokeAPI("/api/v1/career/applications/archived", HttpMethod.GET, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * List archived job applications
     * Lists soft-archived job applications owned by the authenticated user.
     * <p><b>200</b> - Archived job applications listed
     * <p><b>401</b> - JWT authentication required for application APIs
     * @param page Zero-based page index (0..N)
     * @param size The size of the page to be returned
     * @param sort Sorting criteria in the format: property,(asc|desc). Default sort order is ascending. Multiple sort criteria are supported.
     * @return CareerJobApplicationPageApiResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public CareerJobApplicationPageApiResponse listArchived(Integer page, Integer size, List<String> sort) throws RestClientResponseException {
        ParameterizedTypeReference<CareerJobApplicationPageApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return listArchivedRequestCreation(page, size, sort).body(localVarReturnType);
    }

    /**
     * List archived job applications
     * Lists soft-archived job applications owned by the authenticated user.
     * <p><b>200</b> - Archived job applications listed
     * <p><b>401</b> - JWT authentication required for application APIs
     * @param page Zero-based page index (0..N)
     * @param size The size of the page to be returned
     * @param sort Sorting criteria in the format: property,(asc|desc). Default sort order is ascending. Multiple sort criteria are supported.
     * @return ResponseEntity&lt;CareerJobApplicationPageApiResponse&gt;
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<CareerJobApplicationPageApiResponse> listArchivedWithHttpInfo(Integer page, Integer size, List<String> sort) throws RestClientResponseException {
        ParameterizedTypeReference<CareerJobApplicationPageApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return listArchivedRequestCreation(page, size, sort).toEntity(localVarReturnType);
    }

    /**
     * List archived job applications
     * Lists soft-archived job applications owned by the authenticated user.
     * <p><b>200</b> - Archived job applications listed
     * <p><b>401</b> - JWT authentication required for application APIs
     * @param page Zero-based page index (0..N)
     * @param size The size of the page to be returned
     * @param sort Sorting criteria in the format: property,(asc|desc). Default sort order is ascending. Multiple sort criteria are supported.
     * @return ResponseSpec
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec listArchivedWithResponseSpec(Integer page, Integer size, List<String> sort) throws RestClientResponseException {
        return listArchivedRequestCreation(page, size, sort);
    }
    /**
     * Search job applications
     * Searches non-archived job applications owned by the authenticated user using optional filters for company, recruiter, status, interview round, applied date range, salary range, and keyword. Supports Spring Pageable and Sort.
     * <p><b>200</b> - Matching job applications returned
     * <p><b>400</b> - Invalid job application search pageable or filter request
     * <p><b>401</b> - JWT authentication required for application APIs
     * @param companyId Optional company identifier filter
     * @param recruiterId Optional recruiter identifier filter
     * @param status Optional application status filter
     * @param interviewRound Optional interview round filter
     * @param appliedFrom Optional applied-on lower bound (inclusive)
     * @param appliedTo Optional applied-on upper bound (inclusive)
     * @param salaryMin Optional salary expectation lower bound
     * @param salaryMax Optional salary expectation upper bound
     * @param keyword Optional keyword matched against title, description, and notes
     * @param page Zero-based page index (0..N)
     * @param size The size of the page to be returned
     * @param sort Sorting criteria in the format: property,(asc|desc). Default sort order is ascending. Multiple sort criteria are supported.
     * @return CareerJobApplicationPageApiResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec search2RequestCreation(UUID companyId, UUID recruiterId, String status, String interviewRound, LocalDate appliedFrom, LocalDate appliedTo, BigDecimal salaryMin, BigDecimal salaryMax, String keyword, Integer page, Integer size, List<String> sort) throws RestClientResponseException {
        Object postBody = null;
        // create path and map variables
        final Map<String, Object> pathParams = new HashMap<>();

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, String> cookieParams = new LinkedMultiValueMap<>();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<>();

        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "companyId", companyId));
        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "recruiterId", recruiterId));
        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "status", status));
        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "interviewRound", interviewRound));
        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "appliedFrom", appliedFrom));
        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "appliedTo", appliedTo));
        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "salaryMin", salaryMin));
        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "salaryMax", salaryMax));
        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "keyword", keyword));
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

        ParameterizedTypeReference<CareerJobApplicationPageApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return apiClient.invokeAPI("/api/v1/career/applications/search", HttpMethod.GET, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Search job applications
     * Searches non-archived job applications owned by the authenticated user using optional filters for company, recruiter, status, interview round, applied date range, salary range, and keyword. Supports Spring Pageable and Sort.
     * <p><b>200</b> - Matching job applications returned
     * <p><b>400</b> - Invalid job application search pageable or filter request
     * <p><b>401</b> - JWT authentication required for application APIs
     * @param companyId Optional company identifier filter
     * @param recruiterId Optional recruiter identifier filter
     * @param status Optional application status filter
     * @param interviewRound Optional interview round filter
     * @param appliedFrom Optional applied-on lower bound (inclusive)
     * @param appliedTo Optional applied-on upper bound (inclusive)
     * @param salaryMin Optional salary expectation lower bound
     * @param salaryMax Optional salary expectation upper bound
     * @param keyword Optional keyword matched against title, description, and notes
     * @param page Zero-based page index (0..N)
     * @param size The size of the page to be returned
     * @param sort Sorting criteria in the format: property,(asc|desc). Default sort order is ascending. Multiple sort criteria are supported.
     * @return CareerJobApplicationPageApiResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public CareerJobApplicationPageApiResponse search2(UUID companyId, UUID recruiterId, String status, String interviewRound, LocalDate appliedFrom, LocalDate appliedTo, BigDecimal salaryMin, BigDecimal salaryMax, String keyword, Integer page, Integer size, List<String> sort) throws RestClientResponseException {
        ParameterizedTypeReference<CareerJobApplicationPageApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return search2RequestCreation(companyId, recruiterId, status, interviewRound, appliedFrom, appliedTo, salaryMin, salaryMax, keyword, page, size, sort).body(localVarReturnType);
    }

    /**
     * Search job applications
     * Searches non-archived job applications owned by the authenticated user using optional filters for company, recruiter, status, interview round, applied date range, salary range, and keyword. Supports Spring Pageable and Sort.
     * <p><b>200</b> - Matching job applications returned
     * <p><b>400</b> - Invalid job application search pageable or filter request
     * <p><b>401</b> - JWT authentication required for application APIs
     * @param companyId Optional company identifier filter
     * @param recruiterId Optional recruiter identifier filter
     * @param status Optional application status filter
     * @param interviewRound Optional interview round filter
     * @param appliedFrom Optional applied-on lower bound (inclusive)
     * @param appliedTo Optional applied-on upper bound (inclusive)
     * @param salaryMin Optional salary expectation lower bound
     * @param salaryMax Optional salary expectation upper bound
     * @param keyword Optional keyword matched against title, description, and notes
     * @param page Zero-based page index (0..N)
     * @param size The size of the page to be returned
     * @param sort Sorting criteria in the format: property,(asc|desc). Default sort order is ascending. Multiple sort criteria are supported.
     * @return ResponseEntity&lt;CareerJobApplicationPageApiResponse&gt;
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<CareerJobApplicationPageApiResponse> search2WithHttpInfo(UUID companyId, UUID recruiterId, String status, String interviewRound, LocalDate appliedFrom, LocalDate appliedTo, BigDecimal salaryMin, BigDecimal salaryMax, String keyword, Integer page, Integer size, List<String> sort) throws RestClientResponseException {
        ParameterizedTypeReference<CareerJobApplicationPageApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return search2RequestCreation(companyId, recruiterId, status, interviewRound, appliedFrom, appliedTo, salaryMin, salaryMax, keyword, page, size, sort).toEntity(localVarReturnType);
    }

    /**
     * Search job applications
     * Searches non-archived job applications owned by the authenticated user using optional filters for company, recruiter, status, interview round, applied date range, salary range, and keyword. Supports Spring Pageable and Sort.
     * <p><b>200</b> - Matching job applications returned
     * <p><b>400</b> - Invalid job application search pageable or filter request
     * <p><b>401</b> - JWT authentication required for application APIs
     * @param companyId Optional company identifier filter
     * @param recruiterId Optional recruiter identifier filter
     * @param status Optional application status filter
     * @param interviewRound Optional interview round filter
     * @param appliedFrom Optional applied-on lower bound (inclusive)
     * @param appliedTo Optional applied-on upper bound (inclusive)
     * @param salaryMin Optional salary expectation lower bound
     * @param salaryMax Optional salary expectation upper bound
     * @param keyword Optional keyword matched against title, description, and notes
     * @param page Zero-based page index (0..N)
     * @param size The size of the page to be returned
     * @param sort Sorting criteria in the format: property,(asc|desc). Default sort order is ascending. Multiple sort criteria are supported.
     * @return ResponseSpec
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec search2WithResponseSpec(UUID companyId, UUID recruiterId, String status, String interviewRound, LocalDate appliedFrom, LocalDate appliedTo, BigDecimal salaryMin, BigDecimal salaryMax, String keyword, Integer page, Integer size, List<String> sort) throws RestClientResponseException {
        return search2RequestCreation(companyId, recruiterId, status, interviewRound, appliedFrom, appliedTo, salaryMin, salaryMax, keyword, page, size, sort);
    }
    /**
     * Transition job application status
     * Applies an allowed status transition for a non-archived job application. Invalid transitions are rejected by the application state machine. A status history entry is recorded for every successful change.
     * <p><b>200</b> - Status transitioned
     * <p><b>400</b> - Status transition payload validation failed
     * <p><b>401</b> - JWT authentication required for status transitions
     * <p><b>404</b> - Application for status transition was not found for this user
     * <p><b>422</b> - Illegal application status transition or archived application
     * @param applicationId Job application identifier
     * @param statusTransitionRequest The statusTransitionRequest parameter
     * @return CareerJobApplicationApiResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec transitionStatusRequestCreation(UUID applicationId, StatusTransitionRequest statusTransitionRequest) throws RestClientResponseException {
        Object postBody = statusTransitionRequest;
        // verify the required parameter 'applicationId' is set
        if (applicationId == null) {
            throw new RestClientResponseException("Missing the required parameter 'applicationId' when calling transitionStatus", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // verify the required parameter 'statusTransitionRequest' is set
        if (statusTransitionRequest == null) {
            throw new RestClientResponseException("Missing the required parameter 'statusTransitionRequest' when calling transitionStatus", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // create path and map variables
        final Map<String, Object> pathParams = new HashMap<>();

        pathParams.put("applicationId", applicationId);

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

        ParameterizedTypeReference<CareerJobApplicationApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return apiClient.invokeAPI("/api/v1/career/applications/{applicationId}/status", HttpMethod.PATCH, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Transition job application status
     * Applies an allowed status transition for a non-archived job application. Invalid transitions are rejected by the application state machine. A status history entry is recorded for every successful change.
     * <p><b>200</b> - Status transitioned
     * <p><b>400</b> - Status transition payload validation failed
     * <p><b>401</b> - JWT authentication required for status transitions
     * <p><b>404</b> - Application for status transition was not found for this user
     * <p><b>422</b> - Illegal application status transition or archived application
     * @param applicationId Job application identifier
     * @param statusTransitionRequest The statusTransitionRequest parameter
     * @return CareerJobApplicationApiResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public CareerJobApplicationApiResponse transitionStatus(UUID applicationId, StatusTransitionRequest statusTransitionRequest) throws RestClientResponseException {
        ParameterizedTypeReference<CareerJobApplicationApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return transitionStatusRequestCreation(applicationId, statusTransitionRequest).body(localVarReturnType);
    }

    /**
     * Transition job application status
     * Applies an allowed status transition for a non-archived job application. Invalid transitions are rejected by the application state machine. A status history entry is recorded for every successful change.
     * <p><b>200</b> - Status transitioned
     * <p><b>400</b> - Status transition payload validation failed
     * <p><b>401</b> - JWT authentication required for status transitions
     * <p><b>404</b> - Application for status transition was not found for this user
     * <p><b>422</b> - Illegal application status transition or archived application
     * @param applicationId Job application identifier
     * @param statusTransitionRequest The statusTransitionRequest parameter
     * @return ResponseEntity&lt;CareerJobApplicationApiResponse&gt;
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<CareerJobApplicationApiResponse> transitionStatusWithHttpInfo(UUID applicationId, StatusTransitionRequest statusTransitionRequest) throws RestClientResponseException {
        ParameterizedTypeReference<CareerJobApplicationApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return transitionStatusRequestCreation(applicationId, statusTransitionRequest).toEntity(localVarReturnType);
    }

    /**
     * Transition job application status
     * Applies an allowed status transition for a non-archived job application. Invalid transitions are rejected by the application state machine. A status history entry is recorded for every successful change.
     * <p><b>200</b> - Status transitioned
     * <p><b>400</b> - Status transition payload validation failed
     * <p><b>401</b> - JWT authentication required for status transitions
     * <p><b>404</b> - Application for status transition was not found for this user
     * <p><b>422</b> - Illegal application status transition or archived application
     * @param applicationId Job application identifier
     * @param statusTransitionRequest The statusTransitionRequest parameter
     * @return ResponseSpec
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec transitionStatusWithResponseSpec(UUID applicationId, StatusTransitionRequest statusTransitionRequest) throws RestClientResponseException {
        return transitionStatusRequestCreation(applicationId, statusTransitionRequest);
    }
    /**
     * Update job application
     * Updates mutable fields of a non-archived job application owned by the authenticated user. Status is not changed by this endpoint.
     * <p><b>200</b> - Job application updated
     * <p><b>400</b> - Job application request validation failed
     * <p><b>401</b> - JWT authentication required for application APIs
     * <p><b>404</b> - Job application not found for the authenticated user
     * <p><b>422</b> - Archived job applications cannot be modified
     * @param applicationId Job application identifier
     * @param jobApplicationRequest The jobApplicationRequest parameter
     * @return CareerJobApplicationApiResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec update11RequestCreation(UUID applicationId, JobApplicationRequest jobApplicationRequest) throws RestClientResponseException {
        Object postBody = jobApplicationRequest;
        // verify the required parameter 'applicationId' is set
        if (applicationId == null) {
            throw new RestClientResponseException("Missing the required parameter 'applicationId' when calling update11", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // verify the required parameter 'jobApplicationRequest' is set
        if (jobApplicationRequest == null) {
            throw new RestClientResponseException("Missing the required parameter 'jobApplicationRequest' when calling update11", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // create path and map variables
        final Map<String, Object> pathParams = new HashMap<>();

        pathParams.put("applicationId", applicationId);

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

        ParameterizedTypeReference<CareerJobApplicationApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return apiClient.invokeAPI("/api/v1/career/applications/{applicationId}", HttpMethod.PUT, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Update job application
     * Updates mutable fields of a non-archived job application owned by the authenticated user. Status is not changed by this endpoint.
     * <p><b>200</b> - Job application updated
     * <p><b>400</b> - Job application request validation failed
     * <p><b>401</b> - JWT authentication required for application APIs
     * <p><b>404</b> - Job application not found for the authenticated user
     * <p><b>422</b> - Archived job applications cannot be modified
     * @param applicationId Job application identifier
     * @param jobApplicationRequest The jobApplicationRequest parameter
     * @return CareerJobApplicationApiResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public CareerJobApplicationApiResponse update11(UUID applicationId, JobApplicationRequest jobApplicationRequest) throws RestClientResponseException {
        ParameterizedTypeReference<CareerJobApplicationApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return update11RequestCreation(applicationId, jobApplicationRequest).body(localVarReturnType);
    }

    /**
     * Update job application
     * Updates mutable fields of a non-archived job application owned by the authenticated user. Status is not changed by this endpoint.
     * <p><b>200</b> - Job application updated
     * <p><b>400</b> - Job application request validation failed
     * <p><b>401</b> - JWT authentication required for application APIs
     * <p><b>404</b> - Job application not found for the authenticated user
     * <p><b>422</b> - Archived job applications cannot be modified
     * @param applicationId Job application identifier
     * @param jobApplicationRequest The jobApplicationRequest parameter
     * @return ResponseEntity&lt;CareerJobApplicationApiResponse&gt;
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<CareerJobApplicationApiResponse> update11WithHttpInfo(UUID applicationId, JobApplicationRequest jobApplicationRequest) throws RestClientResponseException {
        ParameterizedTypeReference<CareerJobApplicationApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return update11RequestCreation(applicationId, jobApplicationRequest).toEntity(localVarReturnType);
    }

    /**
     * Update job application
     * Updates mutable fields of a non-archived job application owned by the authenticated user. Status is not changed by this endpoint.
     * <p><b>200</b> - Job application updated
     * <p><b>400</b> - Job application request validation failed
     * <p><b>401</b> - JWT authentication required for application APIs
     * <p><b>404</b> - Job application not found for the authenticated user
     * <p><b>422</b> - Archived job applications cannot be modified
     * @param applicationId Job application identifier
     * @param jobApplicationRequest The jobApplicationRequest parameter
     * @return ResponseSpec
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec update11WithResponseSpec(UUID applicationId, JobApplicationRequest jobApplicationRequest) throws RestClientResponseException {
        return update11RequestCreation(applicationId, jobApplicationRequest);
    }
}

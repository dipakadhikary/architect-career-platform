package com.acos.sdk.generated.api;

import com.acos.sdk.generated.ApiClient;

import com.acos.sdk.generated.model.CareerDeleteApiResponse;
import com.acos.sdk.generated.model.CareerErrorApiResponse;
import com.acos.sdk.generated.model.CareerInterviewApiResponse;
import com.acos.sdk.generated.model.CareerInterviewListApiResponse;
import com.acos.sdk.generated.model.InterviewRequest;
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
public class CareerInterviewsApi {
    private ApiClient apiClient;

    public CareerInterviewsApi() {
        this(new ApiClient());
    }

    @Autowired
    public CareerInterviewsApi(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public ApiClient getApiClient() {
        return apiClient;
    }

    public void setApiClient(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    /**
     * Create interview
     * Schedules an interview under a job application owned by the authenticated user. The interview date must not be in the past when scheduling.
     * <p><b>201</b> - Interview created
     * <p><b>400</b> - Interview request validation failed
     * <p><b>401</b> - JWT authentication required for interview APIs
     * <p><b>404</b> - Owning job application not found for the authenticated user
     * <p><b>422</b> - Archived applications cannot manage interviews
     * @param applicationId Job application identifier
     * @param interviewRequest The interviewRequest parameter
     * @return CareerInterviewApiResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec create13RequestCreation(UUID applicationId, InterviewRequest interviewRequest) throws RestClientResponseException {
        Object postBody = interviewRequest;
        // verify the required parameter 'applicationId' is set
        if (applicationId == null) {
            throw new RestClientResponseException("Missing the required parameter 'applicationId' when calling create13", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // verify the required parameter 'interviewRequest' is set
        if (interviewRequest == null) {
            throw new RestClientResponseException("Missing the required parameter 'interviewRequest' when calling create13", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
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

        ParameterizedTypeReference<CareerInterviewApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return apiClient.invokeAPI("/api/v1/career/applications/{applicationId}/interviews", HttpMethod.POST, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Create interview
     * Schedules an interview under a job application owned by the authenticated user. The interview date must not be in the past when scheduling.
     * <p><b>201</b> - Interview created
     * <p><b>400</b> - Interview request validation failed
     * <p><b>401</b> - JWT authentication required for interview APIs
     * <p><b>404</b> - Owning job application not found for the authenticated user
     * <p><b>422</b> - Archived applications cannot manage interviews
     * @param applicationId Job application identifier
     * @param interviewRequest The interviewRequest parameter
     * @return CareerInterviewApiResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public CareerInterviewApiResponse create13(UUID applicationId, InterviewRequest interviewRequest) throws RestClientResponseException {
        ParameterizedTypeReference<CareerInterviewApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return create13RequestCreation(applicationId, interviewRequest).body(localVarReturnType);
    }

    /**
     * Create interview
     * Schedules an interview under a job application owned by the authenticated user. The interview date must not be in the past when scheduling.
     * <p><b>201</b> - Interview created
     * <p><b>400</b> - Interview request validation failed
     * <p><b>401</b> - JWT authentication required for interview APIs
     * <p><b>404</b> - Owning job application not found for the authenticated user
     * <p><b>422</b> - Archived applications cannot manage interviews
     * @param applicationId Job application identifier
     * @param interviewRequest The interviewRequest parameter
     * @return ResponseEntity&lt;CareerInterviewApiResponse&gt;
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<CareerInterviewApiResponse> create13WithHttpInfo(UUID applicationId, InterviewRequest interviewRequest) throws RestClientResponseException {
        ParameterizedTypeReference<CareerInterviewApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return create13RequestCreation(applicationId, interviewRequest).toEntity(localVarReturnType);
    }

    /**
     * Create interview
     * Schedules an interview under a job application owned by the authenticated user. The interview date must not be in the past when scheduling.
     * <p><b>201</b> - Interview created
     * <p><b>400</b> - Interview request validation failed
     * <p><b>401</b> - JWT authentication required for interview APIs
     * <p><b>404</b> - Owning job application not found for the authenticated user
     * <p><b>422</b> - Archived applications cannot manage interviews
     * @param applicationId Job application identifier
     * @param interviewRequest The interviewRequest parameter
     * @return ResponseSpec
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec create13WithResponseSpec(UUID applicationId, InterviewRequest interviewRequest) throws RestClientResponseException {
        return create13RequestCreation(applicationId, interviewRequest);
    }
    /**
     * Delete interview
     * Deletes an interview under a job application owned by the authenticated user.
     * <p><b>200</b> - Interview deleted
     * <p><b>401</b> - JWT authentication required for interview APIs
     * <p><b>404</b> - Interview not found under the authenticated user application
     * @param applicationId Job application identifier
     * @param interviewId Interview identifier
     * @return CareerDeleteApiResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec delete12RequestCreation(UUID applicationId, UUID interviewId) throws RestClientResponseException {
        Object postBody = null;
        // verify the required parameter 'applicationId' is set
        if (applicationId == null) {
            throw new RestClientResponseException("Missing the required parameter 'applicationId' when calling delete12", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // verify the required parameter 'interviewId' is set
        if (interviewId == null) {
            throw new RestClientResponseException("Missing the required parameter 'interviewId' when calling delete12", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // create path and map variables
        final Map<String, Object> pathParams = new HashMap<>();

        pathParams.put("applicationId", applicationId);
        pathParams.put("interviewId", interviewId);

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
        return apiClient.invokeAPI("/api/v1/career/applications/{applicationId}/interviews/{interviewId}", HttpMethod.DELETE, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Delete interview
     * Deletes an interview under a job application owned by the authenticated user.
     * <p><b>200</b> - Interview deleted
     * <p><b>401</b> - JWT authentication required for interview APIs
     * <p><b>404</b> - Interview not found under the authenticated user application
     * @param applicationId Job application identifier
     * @param interviewId Interview identifier
     * @return CareerDeleteApiResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public CareerDeleteApiResponse delete12(UUID applicationId, UUID interviewId) throws RestClientResponseException {
        ParameterizedTypeReference<CareerDeleteApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return delete12RequestCreation(applicationId, interviewId).body(localVarReturnType);
    }

    /**
     * Delete interview
     * Deletes an interview under a job application owned by the authenticated user.
     * <p><b>200</b> - Interview deleted
     * <p><b>401</b> - JWT authentication required for interview APIs
     * <p><b>404</b> - Interview not found under the authenticated user application
     * @param applicationId Job application identifier
     * @param interviewId Interview identifier
     * @return ResponseEntity&lt;CareerDeleteApiResponse&gt;
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<CareerDeleteApiResponse> delete12WithHttpInfo(UUID applicationId, UUID interviewId) throws RestClientResponseException {
        ParameterizedTypeReference<CareerDeleteApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return delete12RequestCreation(applicationId, interviewId).toEntity(localVarReturnType);
    }

    /**
     * Delete interview
     * Deletes an interview under a job application owned by the authenticated user.
     * <p><b>200</b> - Interview deleted
     * <p><b>401</b> - JWT authentication required for interview APIs
     * <p><b>404</b> - Interview not found under the authenticated user application
     * @param applicationId Job application identifier
     * @param interviewId Interview identifier
     * @return ResponseSpec
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec delete12WithResponseSpec(UUID applicationId, UUID interviewId) throws RestClientResponseException {
        return delete12RequestCreation(applicationId, interviewId);
    }
    /**
     * Get interview
     * Returns an interview under a job application owned by the authenticated user.
     * <p><b>200</b> - Interview found
     * <p><b>401</b> - JWT authentication required for interview APIs
     * <p><b>404</b> - Interview not found under the authenticated user application
     * @param applicationId Job application identifier
     * @param interviewId Interview identifier
     * @return CareerInterviewApiResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec get12RequestCreation(UUID applicationId, UUID interviewId) throws RestClientResponseException {
        Object postBody = null;
        // verify the required parameter 'applicationId' is set
        if (applicationId == null) {
            throw new RestClientResponseException("Missing the required parameter 'applicationId' when calling get12", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // verify the required parameter 'interviewId' is set
        if (interviewId == null) {
            throw new RestClientResponseException("Missing the required parameter 'interviewId' when calling get12", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // create path and map variables
        final Map<String, Object> pathParams = new HashMap<>();

        pathParams.put("applicationId", applicationId);
        pathParams.put("interviewId", interviewId);

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

        ParameterizedTypeReference<CareerInterviewApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return apiClient.invokeAPI("/api/v1/career/applications/{applicationId}/interviews/{interviewId}", HttpMethod.GET, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Get interview
     * Returns an interview under a job application owned by the authenticated user.
     * <p><b>200</b> - Interview found
     * <p><b>401</b> - JWT authentication required for interview APIs
     * <p><b>404</b> - Interview not found under the authenticated user application
     * @param applicationId Job application identifier
     * @param interviewId Interview identifier
     * @return CareerInterviewApiResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public CareerInterviewApiResponse get12(UUID applicationId, UUID interviewId) throws RestClientResponseException {
        ParameterizedTypeReference<CareerInterviewApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return get12RequestCreation(applicationId, interviewId).body(localVarReturnType);
    }

    /**
     * Get interview
     * Returns an interview under a job application owned by the authenticated user.
     * <p><b>200</b> - Interview found
     * <p><b>401</b> - JWT authentication required for interview APIs
     * <p><b>404</b> - Interview not found under the authenticated user application
     * @param applicationId Job application identifier
     * @param interviewId Interview identifier
     * @return ResponseEntity&lt;CareerInterviewApiResponse&gt;
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<CareerInterviewApiResponse> get12WithHttpInfo(UUID applicationId, UUID interviewId) throws RestClientResponseException {
        ParameterizedTypeReference<CareerInterviewApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return get12RequestCreation(applicationId, interviewId).toEntity(localVarReturnType);
    }

    /**
     * Get interview
     * Returns an interview under a job application owned by the authenticated user.
     * <p><b>200</b> - Interview found
     * <p><b>401</b> - JWT authentication required for interview APIs
     * <p><b>404</b> - Interview not found under the authenticated user application
     * @param applicationId Job application identifier
     * @param interviewId Interview identifier
     * @return ResponseSpec
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec get12WithResponseSpec(UUID applicationId, UUID interviewId) throws RestClientResponseException {
        return get12RequestCreation(applicationId, interviewId);
    }
    /**
     * List interviews
     * Lists interviews for a job application owned by the authenticated user, ordered by interview date.
     * <p><b>200</b> - Interviews listed
     * <p><b>401</b> - JWT authentication required for interview APIs
     * <p><b>404</b> - Owning job application not found for the authenticated user
     * @param applicationId Job application identifier
     * @return CareerInterviewListApiResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec list13RequestCreation(UUID applicationId) throws RestClientResponseException {
        Object postBody = null;
        // verify the required parameter 'applicationId' is set
        if (applicationId == null) {
            throw new RestClientResponseException("Missing the required parameter 'applicationId' when calling list13", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
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

        ParameterizedTypeReference<CareerInterviewListApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return apiClient.invokeAPI("/api/v1/career/applications/{applicationId}/interviews", HttpMethod.GET, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * List interviews
     * Lists interviews for a job application owned by the authenticated user, ordered by interview date.
     * <p><b>200</b> - Interviews listed
     * <p><b>401</b> - JWT authentication required for interview APIs
     * <p><b>404</b> - Owning job application not found for the authenticated user
     * @param applicationId Job application identifier
     * @return CareerInterviewListApiResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public CareerInterviewListApiResponse list13(UUID applicationId) throws RestClientResponseException {
        ParameterizedTypeReference<CareerInterviewListApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return list13RequestCreation(applicationId).body(localVarReturnType);
    }

    /**
     * List interviews
     * Lists interviews for a job application owned by the authenticated user, ordered by interview date.
     * <p><b>200</b> - Interviews listed
     * <p><b>401</b> - JWT authentication required for interview APIs
     * <p><b>404</b> - Owning job application not found for the authenticated user
     * @param applicationId Job application identifier
     * @return ResponseEntity&lt;CareerInterviewListApiResponse&gt;
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<CareerInterviewListApiResponse> list13WithHttpInfo(UUID applicationId) throws RestClientResponseException {
        ParameterizedTypeReference<CareerInterviewListApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return list13RequestCreation(applicationId).toEntity(localVarReturnType);
    }

    /**
     * List interviews
     * Lists interviews for a job application owned by the authenticated user, ordered by interview date.
     * <p><b>200</b> - Interviews listed
     * <p><b>401</b> - JWT authentication required for interview APIs
     * <p><b>404</b> - Owning job application not found for the authenticated user
     * @param applicationId Job application identifier
     * @return ResponseSpec
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec list13WithResponseSpec(UUID applicationId) throws RestClientResponseException {
        return list13RequestCreation(applicationId);
    }
    /**
     * Update interview
     * Updates an interview under a job application owned by the authenticated user.
     * <p><b>200</b> - Interview updated
     * <p><b>400</b> - Interview request validation failed
     * <p><b>401</b> - JWT authentication required for interview APIs
     * <p><b>404</b> - Interview not found under the authenticated user application
     * <p><b>422</b> - Archived applications cannot manage interviews
     * @param applicationId Job application identifier
     * @param interviewId Interview identifier
     * @param interviewRequest The interviewRequest parameter
     * @return CareerInterviewApiResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec update13RequestCreation(UUID applicationId, UUID interviewId, InterviewRequest interviewRequest) throws RestClientResponseException {
        Object postBody = interviewRequest;
        // verify the required parameter 'applicationId' is set
        if (applicationId == null) {
            throw new RestClientResponseException("Missing the required parameter 'applicationId' when calling update13", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // verify the required parameter 'interviewId' is set
        if (interviewId == null) {
            throw new RestClientResponseException("Missing the required parameter 'interviewId' when calling update13", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // verify the required parameter 'interviewRequest' is set
        if (interviewRequest == null) {
            throw new RestClientResponseException("Missing the required parameter 'interviewRequest' when calling update13", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // create path and map variables
        final Map<String, Object> pathParams = new HashMap<>();

        pathParams.put("applicationId", applicationId);
        pathParams.put("interviewId", interviewId);

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

        ParameterizedTypeReference<CareerInterviewApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return apiClient.invokeAPI("/api/v1/career/applications/{applicationId}/interviews/{interviewId}", HttpMethod.PUT, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Update interview
     * Updates an interview under a job application owned by the authenticated user.
     * <p><b>200</b> - Interview updated
     * <p><b>400</b> - Interview request validation failed
     * <p><b>401</b> - JWT authentication required for interview APIs
     * <p><b>404</b> - Interview not found under the authenticated user application
     * <p><b>422</b> - Archived applications cannot manage interviews
     * @param applicationId Job application identifier
     * @param interviewId Interview identifier
     * @param interviewRequest The interviewRequest parameter
     * @return CareerInterviewApiResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public CareerInterviewApiResponse update13(UUID applicationId, UUID interviewId, InterviewRequest interviewRequest) throws RestClientResponseException {
        ParameterizedTypeReference<CareerInterviewApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return update13RequestCreation(applicationId, interviewId, interviewRequest).body(localVarReturnType);
    }

    /**
     * Update interview
     * Updates an interview under a job application owned by the authenticated user.
     * <p><b>200</b> - Interview updated
     * <p><b>400</b> - Interview request validation failed
     * <p><b>401</b> - JWT authentication required for interview APIs
     * <p><b>404</b> - Interview not found under the authenticated user application
     * <p><b>422</b> - Archived applications cannot manage interviews
     * @param applicationId Job application identifier
     * @param interviewId Interview identifier
     * @param interviewRequest The interviewRequest parameter
     * @return ResponseEntity&lt;CareerInterviewApiResponse&gt;
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<CareerInterviewApiResponse> update13WithHttpInfo(UUID applicationId, UUID interviewId, InterviewRequest interviewRequest) throws RestClientResponseException {
        ParameterizedTypeReference<CareerInterviewApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return update13RequestCreation(applicationId, interviewId, interviewRequest).toEntity(localVarReturnType);
    }

    /**
     * Update interview
     * Updates an interview under a job application owned by the authenticated user.
     * <p><b>200</b> - Interview updated
     * <p><b>400</b> - Interview request validation failed
     * <p><b>401</b> - JWT authentication required for interview APIs
     * <p><b>404</b> - Interview not found under the authenticated user application
     * <p><b>422</b> - Archived applications cannot manage interviews
     * @param applicationId Job application identifier
     * @param interviewId Interview identifier
     * @param interviewRequest The interviewRequest parameter
     * @return ResponseSpec
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec update13WithResponseSpec(UUID applicationId, UUID interviewId, InterviewRequest interviewRequest) throws RestClientResponseException {
        return update13RequestCreation(applicationId, interviewId, interviewRequest);
    }
}

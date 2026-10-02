package com.acos.sdk.generated.api;

import com.acos.sdk.generated.ApiClient;

import com.acos.sdk.generated.model.CareerDeleteApiResponse;
import com.acos.sdk.generated.model.CareerErrorApiResponse;
import com.acos.sdk.generated.model.CareerRecruiterApiResponse;
import com.acos.sdk.generated.model.CareerRecruiterListApiResponse;
import com.acos.sdk.generated.model.RecruiterRequest;
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
public class CareerRecruitersApi {
    private ApiClient apiClient;

    public CareerRecruitersApi() {
        this(new ApiClient());
    }

    @Autowired
    public CareerRecruitersApi(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public ApiClient getApiClient() {
        return apiClient;
    }

    public void setApiClient(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    /**
     * Create recruiter
     * Creates a recruiter contact owned by the authenticated user. Email addresses must be unique per owner when provided.
     * <p><b>201</b> - Recruiter created
     * <p><b>400</b> - Recruiter request validation failed
     * <p><b>401</b> - JWT authentication required for recruiter APIs
     * <p><b>404</b> - Referenced company not found
     * <p><b>422</b> - Duplicate recruiter email
     * @param recruiterRequest The recruiterRequest parameter
     * @return CareerRecruiterApiResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec create9RequestCreation(RecruiterRequest recruiterRequest) throws RestClientResponseException {
        Object postBody = recruiterRequest;
        // verify the required parameter 'recruiterRequest' is set
        if (recruiterRequest == null) {
            throw new RestClientResponseException("Missing the required parameter 'recruiterRequest' when calling create9", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
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

        ParameterizedTypeReference<CareerRecruiterApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return apiClient.invokeAPI("/api/v1/career/recruiters", HttpMethod.POST, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Create recruiter
     * Creates a recruiter contact owned by the authenticated user. Email addresses must be unique per owner when provided.
     * <p><b>201</b> - Recruiter created
     * <p><b>400</b> - Recruiter request validation failed
     * <p><b>401</b> - JWT authentication required for recruiter APIs
     * <p><b>404</b> - Referenced company not found
     * <p><b>422</b> - Duplicate recruiter email
     * @param recruiterRequest The recruiterRequest parameter
     * @return CareerRecruiterApiResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public CareerRecruiterApiResponse create9(RecruiterRequest recruiterRequest) throws RestClientResponseException {
        ParameterizedTypeReference<CareerRecruiterApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return create9RequestCreation(recruiterRequest).body(localVarReturnType);
    }

    /**
     * Create recruiter
     * Creates a recruiter contact owned by the authenticated user. Email addresses must be unique per owner when provided.
     * <p><b>201</b> - Recruiter created
     * <p><b>400</b> - Recruiter request validation failed
     * <p><b>401</b> - JWT authentication required for recruiter APIs
     * <p><b>404</b> - Referenced company not found
     * <p><b>422</b> - Duplicate recruiter email
     * @param recruiterRequest The recruiterRequest parameter
     * @return ResponseEntity&lt;CareerRecruiterApiResponse&gt;
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<CareerRecruiterApiResponse> create9WithHttpInfo(RecruiterRequest recruiterRequest) throws RestClientResponseException {
        ParameterizedTypeReference<CareerRecruiterApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return create9RequestCreation(recruiterRequest).toEntity(localVarReturnType);
    }

    /**
     * Create recruiter
     * Creates a recruiter contact owned by the authenticated user. Email addresses must be unique per owner when provided.
     * <p><b>201</b> - Recruiter created
     * <p><b>400</b> - Recruiter request validation failed
     * <p><b>401</b> - JWT authentication required for recruiter APIs
     * <p><b>404</b> - Referenced company not found
     * <p><b>422</b> - Duplicate recruiter email
     * @param recruiterRequest The recruiterRequest parameter
     * @return ResponseSpec
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec create9WithResponseSpec(RecruiterRequest recruiterRequest) throws RestClientResponseException {
        return create9RequestCreation(recruiterRequest);
    }
    /**
     * Delete recruiter
     * Deletes a recruiter owned by the authenticated user.
     * <p><b>200</b> - Recruiter deleted
     * <p><b>401</b> - JWT authentication required for recruiter APIs
     * <p><b>404</b> - Recruiter not found for the authenticated user
     * @param recruiterId Recruiter identifier
     * @return CareerDeleteApiResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec delete9RequestCreation(UUID recruiterId) throws RestClientResponseException {
        Object postBody = null;
        // verify the required parameter 'recruiterId' is set
        if (recruiterId == null) {
            throw new RestClientResponseException("Missing the required parameter 'recruiterId' when calling delete9", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // create path and map variables
        final Map<String, Object> pathParams = new HashMap<>();

        pathParams.put("recruiterId", recruiterId);

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
        return apiClient.invokeAPI("/api/v1/career/recruiters/{recruiterId}", HttpMethod.DELETE, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Delete recruiter
     * Deletes a recruiter owned by the authenticated user.
     * <p><b>200</b> - Recruiter deleted
     * <p><b>401</b> - JWT authentication required for recruiter APIs
     * <p><b>404</b> - Recruiter not found for the authenticated user
     * @param recruiterId Recruiter identifier
     * @return CareerDeleteApiResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public CareerDeleteApiResponse delete9(UUID recruiterId) throws RestClientResponseException {
        ParameterizedTypeReference<CareerDeleteApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return delete9RequestCreation(recruiterId).body(localVarReturnType);
    }

    /**
     * Delete recruiter
     * Deletes a recruiter owned by the authenticated user.
     * <p><b>200</b> - Recruiter deleted
     * <p><b>401</b> - JWT authentication required for recruiter APIs
     * <p><b>404</b> - Recruiter not found for the authenticated user
     * @param recruiterId Recruiter identifier
     * @return ResponseEntity&lt;CareerDeleteApiResponse&gt;
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<CareerDeleteApiResponse> delete9WithHttpInfo(UUID recruiterId) throws RestClientResponseException {
        ParameterizedTypeReference<CareerDeleteApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return delete9RequestCreation(recruiterId).toEntity(localVarReturnType);
    }

    /**
     * Delete recruiter
     * Deletes a recruiter owned by the authenticated user.
     * <p><b>200</b> - Recruiter deleted
     * <p><b>401</b> - JWT authentication required for recruiter APIs
     * <p><b>404</b> - Recruiter not found for the authenticated user
     * @param recruiterId Recruiter identifier
     * @return ResponseSpec
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec delete9WithResponseSpec(UUID recruiterId) throws RestClientResponseException {
        return delete9RequestCreation(recruiterId);
    }
    /**
     * Get recruiter
     * Returns a recruiter owned by the authenticated user.
     * <p><b>200</b> - Recruiter found
     * <p><b>401</b> - JWT authentication required for recruiter APIs
     * <p><b>404</b> - Recruiter not found for the authenticated user
     * @param recruiterId Recruiter identifier
     * @return CareerRecruiterApiResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec get8RequestCreation(UUID recruiterId) throws RestClientResponseException {
        Object postBody = null;
        // verify the required parameter 'recruiterId' is set
        if (recruiterId == null) {
            throw new RestClientResponseException("Missing the required parameter 'recruiterId' when calling get8", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // create path and map variables
        final Map<String, Object> pathParams = new HashMap<>();

        pathParams.put("recruiterId", recruiterId);

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

        ParameterizedTypeReference<CareerRecruiterApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return apiClient.invokeAPI("/api/v1/career/recruiters/{recruiterId}", HttpMethod.GET, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Get recruiter
     * Returns a recruiter owned by the authenticated user.
     * <p><b>200</b> - Recruiter found
     * <p><b>401</b> - JWT authentication required for recruiter APIs
     * <p><b>404</b> - Recruiter not found for the authenticated user
     * @param recruiterId Recruiter identifier
     * @return CareerRecruiterApiResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public CareerRecruiterApiResponse get8(UUID recruiterId) throws RestClientResponseException {
        ParameterizedTypeReference<CareerRecruiterApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return get8RequestCreation(recruiterId).body(localVarReturnType);
    }

    /**
     * Get recruiter
     * Returns a recruiter owned by the authenticated user.
     * <p><b>200</b> - Recruiter found
     * <p><b>401</b> - JWT authentication required for recruiter APIs
     * <p><b>404</b> - Recruiter not found for the authenticated user
     * @param recruiterId Recruiter identifier
     * @return ResponseEntity&lt;CareerRecruiterApiResponse&gt;
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<CareerRecruiterApiResponse> get8WithHttpInfo(UUID recruiterId) throws RestClientResponseException {
        ParameterizedTypeReference<CareerRecruiterApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return get8RequestCreation(recruiterId).toEntity(localVarReturnType);
    }

    /**
     * Get recruiter
     * Returns a recruiter owned by the authenticated user.
     * <p><b>200</b> - Recruiter found
     * <p><b>401</b> - JWT authentication required for recruiter APIs
     * <p><b>404</b> - Recruiter not found for the authenticated user
     * @param recruiterId Recruiter identifier
     * @return ResponseSpec
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec get8WithResponseSpec(UUID recruiterId) throws RestClientResponseException {
        return get8RequestCreation(recruiterId);
    }
    /**
     * List recruiters
     * Lists all recruiters owned by the authenticated user.
     * <p><b>200</b> - Recruiters listed
     * <p><b>401</b> - JWT authentication required for recruiter APIs
     * @return CareerRecruiterListApiResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec list9RequestCreation() throws RestClientResponseException {
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

        ParameterizedTypeReference<CareerRecruiterListApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return apiClient.invokeAPI("/api/v1/career/recruiters", HttpMethod.GET, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * List recruiters
     * Lists all recruiters owned by the authenticated user.
     * <p><b>200</b> - Recruiters listed
     * <p><b>401</b> - JWT authentication required for recruiter APIs
     * @return CareerRecruiterListApiResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public CareerRecruiterListApiResponse list9() throws RestClientResponseException {
        ParameterizedTypeReference<CareerRecruiterListApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return list9RequestCreation().body(localVarReturnType);
    }

    /**
     * List recruiters
     * Lists all recruiters owned by the authenticated user.
     * <p><b>200</b> - Recruiters listed
     * <p><b>401</b> - JWT authentication required for recruiter APIs
     * @return ResponseEntity&lt;CareerRecruiterListApiResponse&gt;
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<CareerRecruiterListApiResponse> list9WithHttpInfo() throws RestClientResponseException {
        ParameterizedTypeReference<CareerRecruiterListApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return list9RequestCreation().toEntity(localVarReturnType);
    }

    /**
     * List recruiters
     * Lists all recruiters owned by the authenticated user.
     * <p><b>200</b> - Recruiters listed
     * <p><b>401</b> - JWT authentication required for recruiter APIs
     * @return ResponseSpec
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec list9WithResponseSpec() throws RestClientResponseException {
        return list9RequestCreation();
    }
    /**
     * Update recruiter
     * Updates a recruiter owned by the authenticated user.
     * <p><b>200</b> - Recruiter updated
     * <p><b>400</b> - Recruiter request validation failed
     * <p><b>401</b> - JWT authentication required for recruiter APIs
     * <p><b>404</b> - Recruiter not found for the authenticated user
     * <p><b>422</b> - Duplicate recruiter email
     * @param recruiterId Recruiter identifier
     * @param recruiterRequest The recruiterRequest parameter
     * @return CareerRecruiterApiResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec update9RequestCreation(UUID recruiterId, RecruiterRequest recruiterRequest) throws RestClientResponseException {
        Object postBody = recruiterRequest;
        // verify the required parameter 'recruiterId' is set
        if (recruiterId == null) {
            throw new RestClientResponseException("Missing the required parameter 'recruiterId' when calling update9", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // verify the required parameter 'recruiterRequest' is set
        if (recruiterRequest == null) {
            throw new RestClientResponseException("Missing the required parameter 'recruiterRequest' when calling update9", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // create path and map variables
        final Map<String, Object> pathParams = new HashMap<>();

        pathParams.put("recruiterId", recruiterId);

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

        ParameterizedTypeReference<CareerRecruiterApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return apiClient.invokeAPI("/api/v1/career/recruiters/{recruiterId}", HttpMethod.PUT, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Update recruiter
     * Updates a recruiter owned by the authenticated user.
     * <p><b>200</b> - Recruiter updated
     * <p><b>400</b> - Recruiter request validation failed
     * <p><b>401</b> - JWT authentication required for recruiter APIs
     * <p><b>404</b> - Recruiter not found for the authenticated user
     * <p><b>422</b> - Duplicate recruiter email
     * @param recruiterId Recruiter identifier
     * @param recruiterRequest The recruiterRequest parameter
     * @return CareerRecruiterApiResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public CareerRecruiterApiResponse update9(UUID recruiterId, RecruiterRequest recruiterRequest) throws RestClientResponseException {
        ParameterizedTypeReference<CareerRecruiterApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return update9RequestCreation(recruiterId, recruiterRequest).body(localVarReturnType);
    }

    /**
     * Update recruiter
     * Updates a recruiter owned by the authenticated user.
     * <p><b>200</b> - Recruiter updated
     * <p><b>400</b> - Recruiter request validation failed
     * <p><b>401</b> - JWT authentication required for recruiter APIs
     * <p><b>404</b> - Recruiter not found for the authenticated user
     * <p><b>422</b> - Duplicate recruiter email
     * @param recruiterId Recruiter identifier
     * @param recruiterRequest The recruiterRequest parameter
     * @return ResponseEntity&lt;CareerRecruiterApiResponse&gt;
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<CareerRecruiterApiResponse> update9WithHttpInfo(UUID recruiterId, RecruiterRequest recruiterRequest) throws RestClientResponseException {
        ParameterizedTypeReference<CareerRecruiterApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return update9RequestCreation(recruiterId, recruiterRequest).toEntity(localVarReturnType);
    }

    /**
     * Update recruiter
     * Updates a recruiter owned by the authenticated user.
     * <p><b>200</b> - Recruiter updated
     * <p><b>400</b> - Recruiter request validation failed
     * <p><b>401</b> - JWT authentication required for recruiter APIs
     * <p><b>404</b> - Recruiter not found for the authenticated user
     * <p><b>422</b> - Duplicate recruiter email
     * @param recruiterId Recruiter identifier
     * @param recruiterRequest The recruiterRequest parameter
     * @return ResponseSpec
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec update9WithResponseSpec(UUID recruiterId, RecruiterRequest recruiterRequest) throws RestClientResponseException {
        return update9RequestCreation(recruiterId, recruiterRequest);
    }
}

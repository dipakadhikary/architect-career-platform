package com.acos.sdk.generated.api;

import com.acos.sdk.generated.ApiClient;

import com.acos.sdk.generated.model.CareerDeleteApiResponse;
import com.acos.sdk.generated.model.CareerErrorApiResponse;
import com.acos.sdk.generated.model.CareerOfferApiResponse;
import com.acos.sdk.generated.model.CareerOfferListApiResponse;
import com.acos.sdk.generated.model.OfferRequest;
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
public class CareerOffersApi {
    private ApiClient apiClient;

    public CareerOffersApi() {
        this(new ApiClient());
    }

    @Autowired
    public CareerOffersApi(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public ApiClient getApiClient() {
        return apiClient;
    }

    public void setApiClient(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    /**
     * Create offer
     * Creates an offer under a job application owned by the authenticated user. Only one active (non-archived) offer is allowed per application.
     * <p><b>201</b> - Offer created
     * <p><b>400</b> - Offer request validation failed
     * <p><b>401</b> - JWT authentication required for offer APIs
     * <p><b>404</b> - Owning job application was not found for the authenticated user
     * <p><b>422</b> - Pending offer already exists or application is archived
     * @param applicationId Job application identifier
     * @param offerRequest The offerRequest parameter
     * @return CareerOfferApiResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec create12RequestCreation(UUID applicationId, OfferRequest offerRequest) throws RestClientResponseException {
        Object postBody = offerRequest;
        // verify the required parameter 'applicationId' is set
        if (applicationId == null) {
            throw new RestClientResponseException("Missing the required parameter 'applicationId' when calling create12", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // verify the required parameter 'offerRequest' is set
        if (offerRequest == null) {
            throw new RestClientResponseException("Missing the required parameter 'offerRequest' when calling create12", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
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

        ParameterizedTypeReference<CareerOfferApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return apiClient.invokeAPI("/api/v1/career/applications/{applicationId}/offers", HttpMethod.POST, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Create offer
     * Creates an offer under a job application owned by the authenticated user. Only one active (non-archived) offer is allowed per application.
     * <p><b>201</b> - Offer created
     * <p><b>400</b> - Offer request validation failed
     * <p><b>401</b> - JWT authentication required for offer APIs
     * <p><b>404</b> - Owning job application was not found for the authenticated user
     * <p><b>422</b> - Pending offer already exists or application is archived
     * @param applicationId Job application identifier
     * @param offerRequest The offerRequest parameter
     * @return CareerOfferApiResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public CareerOfferApiResponse create12(UUID applicationId, OfferRequest offerRequest) throws RestClientResponseException {
        ParameterizedTypeReference<CareerOfferApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return create12RequestCreation(applicationId, offerRequest).body(localVarReturnType);
    }

    /**
     * Create offer
     * Creates an offer under a job application owned by the authenticated user. Only one active (non-archived) offer is allowed per application.
     * <p><b>201</b> - Offer created
     * <p><b>400</b> - Offer request validation failed
     * <p><b>401</b> - JWT authentication required for offer APIs
     * <p><b>404</b> - Owning job application was not found for the authenticated user
     * <p><b>422</b> - Pending offer already exists or application is archived
     * @param applicationId Job application identifier
     * @param offerRequest The offerRequest parameter
     * @return ResponseEntity&lt;CareerOfferApiResponse&gt;
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<CareerOfferApiResponse> create12WithHttpInfo(UUID applicationId, OfferRequest offerRequest) throws RestClientResponseException {
        ParameterizedTypeReference<CareerOfferApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return create12RequestCreation(applicationId, offerRequest).toEntity(localVarReturnType);
    }

    /**
     * Create offer
     * Creates an offer under a job application owned by the authenticated user. Only one active (non-archived) offer is allowed per application.
     * <p><b>201</b> - Offer created
     * <p><b>400</b> - Offer request validation failed
     * <p><b>401</b> - JWT authentication required for offer APIs
     * <p><b>404</b> - Owning job application was not found for the authenticated user
     * <p><b>422</b> - Pending offer already exists or application is archived
     * @param applicationId Job application identifier
     * @param offerRequest The offerRequest parameter
     * @return ResponseSpec
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec create12WithResponseSpec(UUID applicationId, OfferRequest offerRequest) throws RestClientResponseException {
        return create12RequestCreation(applicationId, offerRequest);
    }
    /**
     * Delete offer
     * Deletes an offer under a job application owned by the authenticated user.
     * <p><b>200</b> - Offer deleted
     * <p><b>401</b> - JWT authentication required for offer APIs
     * <p><b>404</b> - Offer not found under the authenticated user application
     * @param applicationId Job application identifier
     * @param offerId Offer identifier
     * @return CareerDeleteApiResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec delete11RequestCreation(UUID applicationId, UUID offerId) throws RestClientResponseException {
        Object postBody = null;
        // verify the required parameter 'applicationId' is set
        if (applicationId == null) {
            throw new RestClientResponseException("Missing the required parameter 'applicationId' when calling delete11", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // verify the required parameter 'offerId' is set
        if (offerId == null) {
            throw new RestClientResponseException("Missing the required parameter 'offerId' when calling delete11", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // create path and map variables
        final Map<String, Object> pathParams = new HashMap<>();

        pathParams.put("applicationId", applicationId);
        pathParams.put("offerId", offerId);

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
        return apiClient.invokeAPI("/api/v1/career/applications/{applicationId}/offers/{offerId}", HttpMethod.DELETE, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Delete offer
     * Deletes an offer under a job application owned by the authenticated user.
     * <p><b>200</b> - Offer deleted
     * <p><b>401</b> - JWT authentication required for offer APIs
     * <p><b>404</b> - Offer not found under the authenticated user application
     * @param applicationId Job application identifier
     * @param offerId Offer identifier
     * @return CareerDeleteApiResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public CareerDeleteApiResponse delete11(UUID applicationId, UUID offerId) throws RestClientResponseException {
        ParameterizedTypeReference<CareerDeleteApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return delete11RequestCreation(applicationId, offerId).body(localVarReturnType);
    }

    /**
     * Delete offer
     * Deletes an offer under a job application owned by the authenticated user.
     * <p><b>200</b> - Offer deleted
     * <p><b>401</b> - JWT authentication required for offer APIs
     * <p><b>404</b> - Offer not found under the authenticated user application
     * @param applicationId Job application identifier
     * @param offerId Offer identifier
     * @return ResponseEntity&lt;CareerDeleteApiResponse&gt;
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<CareerDeleteApiResponse> delete11WithHttpInfo(UUID applicationId, UUID offerId) throws RestClientResponseException {
        ParameterizedTypeReference<CareerDeleteApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return delete11RequestCreation(applicationId, offerId).toEntity(localVarReturnType);
    }

    /**
     * Delete offer
     * Deletes an offer under a job application owned by the authenticated user.
     * <p><b>200</b> - Offer deleted
     * <p><b>401</b> - JWT authentication required for offer APIs
     * <p><b>404</b> - Offer not found under the authenticated user application
     * @param applicationId Job application identifier
     * @param offerId Offer identifier
     * @return ResponseSpec
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec delete11WithResponseSpec(UUID applicationId, UUID offerId) throws RestClientResponseException {
        return delete11RequestCreation(applicationId, offerId);
    }
    /**
     * Get offer
     * Returns an offer under a job application owned by the authenticated user.
     * <p><b>200</b> - Offer found
     * <p><b>401</b> - JWT authentication required for offer APIs
     * <p><b>404</b> - Offer not found under the authenticated user application
     * @param applicationId Job application identifier
     * @param offerId Offer identifier
     * @return CareerOfferApiResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec get11RequestCreation(UUID applicationId, UUID offerId) throws RestClientResponseException {
        Object postBody = null;
        // verify the required parameter 'applicationId' is set
        if (applicationId == null) {
            throw new RestClientResponseException("Missing the required parameter 'applicationId' when calling get11", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // verify the required parameter 'offerId' is set
        if (offerId == null) {
            throw new RestClientResponseException("Missing the required parameter 'offerId' when calling get11", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // create path and map variables
        final Map<String, Object> pathParams = new HashMap<>();

        pathParams.put("applicationId", applicationId);
        pathParams.put("offerId", offerId);

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

        ParameterizedTypeReference<CareerOfferApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return apiClient.invokeAPI("/api/v1/career/applications/{applicationId}/offers/{offerId}", HttpMethod.GET, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Get offer
     * Returns an offer under a job application owned by the authenticated user.
     * <p><b>200</b> - Offer found
     * <p><b>401</b> - JWT authentication required for offer APIs
     * <p><b>404</b> - Offer not found under the authenticated user application
     * @param applicationId Job application identifier
     * @param offerId Offer identifier
     * @return CareerOfferApiResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public CareerOfferApiResponse get11(UUID applicationId, UUID offerId) throws RestClientResponseException {
        ParameterizedTypeReference<CareerOfferApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return get11RequestCreation(applicationId, offerId).body(localVarReturnType);
    }

    /**
     * Get offer
     * Returns an offer under a job application owned by the authenticated user.
     * <p><b>200</b> - Offer found
     * <p><b>401</b> - JWT authentication required for offer APIs
     * <p><b>404</b> - Offer not found under the authenticated user application
     * @param applicationId Job application identifier
     * @param offerId Offer identifier
     * @return ResponseEntity&lt;CareerOfferApiResponse&gt;
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<CareerOfferApiResponse> get11WithHttpInfo(UUID applicationId, UUID offerId) throws RestClientResponseException {
        ParameterizedTypeReference<CareerOfferApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return get11RequestCreation(applicationId, offerId).toEntity(localVarReturnType);
    }

    /**
     * Get offer
     * Returns an offer under a job application owned by the authenticated user.
     * <p><b>200</b> - Offer found
     * <p><b>401</b> - JWT authentication required for offer APIs
     * <p><b>404</b> - Offer not found under the authenticated user application
     * @param applicationId Job application identifier
     * @param offerId Offer identifier
     * @return ResponseSpec
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec get11WithResponseSpec(UUID applicationId, UUID offerId) throws RestClientResponseException {
        return get11RequestCreation(applicationId, offerId);
    }
    /**
     * List offers
     * Lists offers for a job application owned by the authenticated user.
     * <p><b>200</b> - Offers listed
     * <p><b>401</b> - JWT authentication required for offer APIs
     * <p><b>404</b> - Owning job application was not found for the authenticated user
     * @param applicationId Job application identifier
     * @return CareerOfferListApiResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec list12RequestCreation(UUID applicationId) throws RestClientResponseException {
        Object postBody = null;
        // verify the required parameter 'applicationId' is set
        if (applicationId == null) {
            throw new RestClientResponseException("Missing the required parameter 'applicationId' when calling list12", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
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

        ParameterizedTypeReference<CareerOfferListApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return apiClient.invokeAPI("/api/v1/career/applications/{applicationId}/offers", HttpMethod.GET, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * List offers
     * Lists offers for a job application owned by the authenticated user.
     * <p><b>200</b> - Offers listed
     * <p><b>401</b> - JWT authentication required for offer APIs
     * <p><b>404</b> - Owning job application was not found for the authenticated user
     * @param applicationId Job application identifier
     * @return CareerOfferListApiResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public CareerOfferListApiResponse list12(UUID applicationId) throws RestClientResponseException {
        ParameterizedTypeReference<CareerOfferListApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return list12RequestCreation(applicationId).body(localVarReturnType);
    }

    /**
     * List offers
     * Lists offers for a job application owned by the authenticated user.
     * <p><b>200</b> - Offers listed
     * <p><b>401</b> - JWT authentication required for offer APIs
     * <p><b>404</b> - Owning job application was not found for the authenticated user
     * @param applicationId Job application identifier
     * @return ResponseEntity&lt;CareerOfferListApiResponse&gt;
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<CareerOfferListApiResponse> list12WithHttpInfo(UUID applicationId) throws RestClientResponseException {
        ParameterizedTypeReference<CareerOfferListApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return list12RequestCreation(applicationId).toEntity(localVarReturnType);
    }

    /**
     * List offers
     * Lists offers for a job application owned by the authenticated user.
     * <p><b>200</b> - Offers listed
     * <p><b>401</b> - JWT authentication required for offer APIs
     * <p><b>404</b> - Owning job application was not found for the authenticated user
     * @param applicationId Job application identifier
     * @return ResponseSpec
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec list12WithResponseSpec(UUID applicationId) throws RestClientResponseException {
        return list12RequestCreation(applicationId);
    }
    /**
     * Update offer
     * Updates an offer under a job application owned by the authenticated user. Accepting or declining an offer may synchronize the application status when allowed.
     * <p><b>200</b> - Offer updated
     * <p><b>400</b> - Offer request validation failed
     * <p><b>401</b> - JWT authentication required for offer APIs
     * <p><b>404</b> - Offer not found under the authenticated user application
     * <p><b>422</b> - Archived applications cannot manage offers
     * @param applicationId Job application identifier
     * @param offerId Offer identifier
     * @param offerRequest The offerRequest parameter
     * @return CareerOfferApiResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec update12RequestCreation(UUID applicationId, UUID offerId, OfferRequest offerRequest) throws RestClientResponseException {
        Object postBody = offerRequest;
        // verify the required parameter 'applicationId' is set
        if (applicationId == null) {
            throw new RestClientResponseException("Missing the required parameter 'applicationId' when calling update12", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // verify the required parameter 'offerId' is set
        if (offerId == null) {
            throw new RestClientResponseException("Missing the required parameter 'offerId' when calling update12", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // verify the required parameter 'offerRequest' is set
        if (offerRequest == null) {
            throw new RestClientResponseException("Missing the required parameter 'offerRequest' when calling update12", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // create path and map variables
        final Map<String, Object> pathParams = new HashMap<>();

        pathParams.put("applicationId", applicationId);
        pathParams.put("offerId", offerId);

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

        ParameterizedTypeReference<CareerOfferApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return apiClient.invokeAPI("/api/v1/career/applications/{applicationId}/offers/{offerId}", HttpMethod.PUT, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Update offer
     * Updates an offer under a job application owned by the authenticated user. Accepting or declining an offer may synchronize the application status when allowed.
     * <p><b>200</b> - Offer updated
     * <p><b>400</b> - Offer request validation failed
     * <p><b>401</b> - JWT authentication required for offer APIs
     * <p><b>404</b> - Offer not found under the authenticated user application
     * <p><b>422</b> - Archived applications cannot manage offers
     * @param applicationId Job application identifier
     * @param offerId Offer identifier
     * @param offerRequest The offerRequest parameter
     * @return CareerOfferApiResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public CareerOfferApiResponse update12(UUID applicationId, UUID offerId, OfferRequest offerRequest) throws RestClientResponseException {
        ParameterizedTypeReference<CareerOfferApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return update12RequestCreation(applicationId, offerId, offerRequest).body(localVarReturnType);
    }

    /**
     * Update offer
     * Updates an offer under a job application owned by the authenticated user. Accepting or declining an offer may synchronize the application status when allowed.
     * <p><b>200</b> - Offer updated
     * <p><b>400</b> - Offer request validation failed
     * <p><b>401</b> - JWT authentication required for offer APIs
     * <p><b>404</b> - Offer not found under the authenticated user application
     * <p><b>422</b> - Archived applications cannot manage offers
     * @param applicationId Job application identifier
     * @param offerId Offer identifier
     * @param offerRequest The offerRequest parameter
     * @return ResponseEntity&lt;CareerOfferApiResponse&gt;
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<CareerOfferApiResponse> update12WithHttpInfo(UUID applicationId, UUID offerId, OfferRequest offerRequest) throws RestClientResponseException {
        ParameterizedTypeReference<CareerOfferApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return update12RequestCreation(applicationId, offerId, offerRequest).toEntity(localVarReturnType);
    }

    /**
     * Update offer
     * Updates an offer under a job application owned by the authenticated user. Accepting or declining an offer may synchronize the application status when allowed.
     * <p><b>200</b> - Offer updated
     * <p><b>400</b> - Offer request validation failed
     * <p><b>401</b> - JWT authentication required for offer APIs
     * <p><b>404</b> - Offer not found under the authenticated user application
     * <p><b>422</b> - Archived applications cannot manage offers
     * @param applicationId Job application identifier
     * @param offerId Offer identifier
     * @param offerRequest The offerRequest parameter
     * @return ResponseSpec
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec update12WithResponseSpec(UUID applicationId, UUID offerId, OfferRequest offerRequest) throws RestClientResponseException {
        return update12RequestCreation(applicationId, offerId, offerRequest);
    }
}

package com.acos.sdk.generated.api;

import com.acos.sdk.generated.ApiClient;

import com.acos.sdk.generated.model.ApiResponseCertificationResponse;
import com.acos.sdk.generated.model.ApiResponseListCertificationResponse;
import com.acos.sdk.generated.model.ApiResponseVoid;
import com.acos.sdk.generated.model.CertificationRequest;
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
public class PortfolioCertificationsApi {
    private ApiClient apiClient;

    public PortfolioCertificationsApi() {
        this(new ApiClient());
    }

    @Autowired
    public PortfolioCertificationsApi(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public ApiClient getApiClient() {
        return apiClient;
    }

    public void setApiClient(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    /**
     * Create certification
     * 
     * <p><b>200</b> - OK
     * @param certificationRequest The certificationRequest parameter
     * @return ApiResponseCertificationResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec create3RequestCreation(CertificationRequest certificationRequest) throws RestClientResponseException {
        Object postBody = certificationRequest;
        // verify the required parameter 'certificationRequest' is set
        if (certificationRequest == null) {
            throw new RestClientResponseException("Missing the required parameter 'certificationRequest' when calling create3", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
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

        ParameterizedTypeReference<ApiResponseCertificationResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return apiClient.invokeAPI("/api/v1/portfolio/certifications", HttpMethod.POST, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Create certification
     * 
     * <p><b>200</b> - OK
     * @param certificationRequest The certificationRequest parameter
     * @return ApiResponseCertificationResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ApiResponseCertificationResponse create3(CertificationRequest certificationRequest) throws RestClientResponseException {
        ParameterizedTypeReference<ApiResponseCertificationResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return create3RequestCreation(certificationRequest).body(localVarReturnType);
    }

    /**
     * Create certification
     * 
     * <p><b>200</b> - OK
     * @param certificationRequest The certificationRequest parameter
     * @return ResponseEntity&lt;ApiResponseCertificationResponse&gt;
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<ApiResponseCertificationResponse> create3WithHttpInfo(CertificationRequest certificationRequest) throws RestClientResponseException {
        ParameterizedTypeReference<ApiResponseCertificationResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return create3RequestCreation(certificationRequest).toEntity(localVarReturnType);
    }

    /**
     * Create certification
     * 
     * <p><b>200</b> - OK
     * @param certificationRequest The certificationRequest parameter
     * @return ResponseSpec
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec create3WithResponseSpec(CertificationRequest certificationRequest) throws RestClientResponseException {
        return create3RequestCreation(certificationRequest);
    }
    /**
     * Delete certification
     * 
     * <p><b>200</b> - OK
     * @param certificationId Certification identifier
     * @return ApiResponseVoid
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec delete3RequestCreation(UUID certificationId) throws RestClientResponseException {
        Object postBody = null;
        // verify the required parameter 'certificationId' is set
        if (certificationId == null) {
            throw new RestClientResponseException("Missing the required parameter 'certificationId' when calling delete3", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // create path and map variables
        final Map<String, Object> pathParams = new HashMap<>();

        pathParams.put("certificationId", certificationId);

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
        return apiClient.invokeAPI("/api/v1/portfolio/certifications/{certificationId}", HttpMethod.DELETE, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Delete certification
     * 
     * <p><b>200</b> - OK
     * @param certificationId Certification identifier
     * @return ApiResponseVoid
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ApiResponseVoid delete3(UUID certificationId) throws RestClientResponseException {
        ParameterizedTypeReference<ApiResponseVoid> localVarReturnType = new ParameterizedTypeReference<>() {};
        return delete3RequestCreation(certificationId).body(localVarReturnType);
    }

    /**
     * Delete certification
     * 
     * <p><b>200</b> - OK
     * @param certificationId Certification identifier
     * @return ResponseEntity&lt;ApiResponseVoid&gt;
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<ApiResponseVoid> delete3WithHttpInfo(UUID certificationId) throws RestClientResponseException {
        ParameterizedTypeReference<ApiResponseVoid> localVarReturnType = new ParameterizedTypeReference<>() {};
        return delete3RequestCreation(certificationId).toEntity(localVarReturnType);
    }

    /**
     * Delete certification
     * 
     * <p><b>200</b> - OK
     * @param certificationId Certification identifier
     * @return ResponseSpec
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec delete3WithResponseSpec(UUID certificationId) throws RestClientResponseException {
        return delete3RequestCreation(certificationId);
    }
    /**
     * Get certification
     * 
     * <p><b>200</b> - OK
     * @param certificationId Certification identifier
     * @return ApiResponseCertificationResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec get3RequestCreation(UUID certificationId) throws RestClientResponseException {
        Object postBody = null;
        // verify the required parameter 'certificationId' is set
        if (certificationId == null) {
            throw new RestClientResponseException("Missing the required parameter 'certificationId' when calling get3", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // create path and map variables
        final Map<String, Object> pathParams = new HashMap<>();

        pathParams.put("certificationId", certificationId);

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

        ParameterizedTypeReference<ApiResponseCertificationResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return apiClient.invokeAPI("/api/v1/portfolio/certifications/{certificationId}", HttpMethod.GET, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Get certification
     * 
     * <p><b>200</b> - OK
     * @param certificationId Certification identifier
     * @return ApiResponseCertificationResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ApiResponseCertificationResponse get3(UUID certificationId) throws RestClientResponseException {
        ParameterizedTypeReference<ApiResponseCertificationResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return get3RequestCreation(certificationId).body(localVarReturnType);
    }

    /**
     * Get certification
     * 
     * <p><b>200</b> - OK
     * @param certificationId Certification identifier
     * @return ResponseEntity&lt;ApiResponseCertificationResponse&gt;
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<ApiResponseCertificationResponse> get3WithHttpInfo(UUID certificationId) throws RestClientResponseException {
        ParameterizedTypeReference<ApiResponseCertificationResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return get3RequestCreation(certificationId).toEntity(localVarReturnType);
    }

    /**
     * Get certification
     * 
     * <p><b>200</b> - OK
     * @param certificationId Certification identifier
     * @return ResponseSpec
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec get3WithResponseSpec(UUID certificationId) throws RestClientResponseException {
        return get3RequestCreation(certificationId);
    }
    /**
     * List certifications
     * 
     * <p><b>200</b> - OK
     * @return ApiResponseListCertificationResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec list3RequestCreation() throws RestClientResponseException {
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

        ParameterizedTypeReference<ApiResponseListCertificationResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return apiClient.invokeAPI("/api/v1/portfolio/certifications", HttpMethod.GET, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * List certifications
     * 
     * <p><b>200</b> - OK
     * @return ApiResponseListCertificationResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ApiResponseListCertificationResponse list3() throws RestClientResponseException {
        ParameterizedTypeReference<ApiResponseListCertificationResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return list3RequestCreation().body(localVarReturnType);
    }

    /**
     * List certifications
     * 
     * <p><b>200</b> - OK
     * @return ResponseEntity&lt;ApiResponseListCertificationResponse&gt;
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<ApiResponseListCertificationResponse> list3WithHttpInfo() throws RestClientResponseException {
        ParameterizedTypeReference<ApiResponseListCertificationResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return list3RequestCreation().toEntity(localVarReturnType);
    }

    /**
     * List certifications
     * 
     * <p><b>200</b> - OK
     * @return ResponseSpec
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec list3WithResponseSpec() throws RestClientResponseException {
        return list3RequestCreation();
    }
    /**
     * Update certification
     * 
     * <p><b>200</b> - OK
     * @param certificationId Certification identifier
     * @param certificationRequest The certificationRequest parameter
     * @return ApiResponseCertificationResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec update3RequestCreation(UUID certificationId, CertificationRequest certificationRequest) throws RestClientResponseException {
        Object postBody = certificationRequest;
        // verify the required parameter 'certificationId' is set
        if (certificationId == null) {
            throw new RestClientResponseException("Missing the required parameter 'certificationId' when calling update3", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // verify the required parameter 'certificationRequest' is set
        if (certificationRequest == null) {
            throw new RestClientResponseException("Missing the required parameter 'certificationRequest' when calling update3", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // create path and map variables
        final Map<String, Object> pathParams = new HashMap<>();

        pathParams.put("certificationId", certificationId);

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

        ParameterizedTypeReference<ApiResponseCertificationResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return apiClient.invokeAPI("/api/v1/portfolio/certifications/{certificationId}", HttpMethod.PUT, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Update certification
     * 
     * <p><b>200</b> - OK
     * @param certificationId Certification identifier
     * @param certificationRequest The certificationRequest parameter
     * @return ApiResponseCertificationResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ApiResponseCertificationResponse update3(UUID certificationId, CertificationRequest certificationRequest) throws RestClientResponseException {
        ParameterizedTypeReference<ApiResponseCertificationResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return update3RequestCreation(certificationId, certificationRequest).body(localVarReturnType);
    }

    /**
     * Update certification
     * 
     * <p><b>200</b> - OK
     * @param certificationId Certification identifier
     * @param certificationRequest The certificationRequest parameter
     * @return ResponseEntity&lt;ApiResponseCertificationResponse&gt;
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<ApiResponseCertificationResponse> update3WithHttpInfo(UUID certificationId, CertificationRequest certificationRequest) throws RestClientResponseException {
        ParameterizedTypeReference<ApiResponseCertificationResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return update3RequestCreation(certificationId, certificationRequest).toEntity(localVarReturnType);
    }

    /**
     * Update certification
     * 
     * <p><b>200</b> - OK
     * @param certificationId Certification identifier
     * @param certificationRequest The certificationRequest parameter
     * @return ResponseSpec
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec update3WithResponseSpec(UUID certificationId, CertificationRequest certificationRequest) throws RestClientResponseException {
        return update3RequestCreation(certificationId, certificationRequest);
    }
}

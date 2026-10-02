package com.acos.sdk.generated.api;

import com.acos.sdk.generated.ApiClient;

import com.acos.sdk.generated.model.CareerCompanyApiResponse;
import com.acos.sdk.generated.model.CareerCompanyListApiResponse;
import com.acos.sdk.generated.model.CareerDeleteApiResponse;
import com.acos.sdk.generated.model.CareerErrorApiResponse;
import com.acos.sdk.generated.model.CompanyRequest;
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
public class CareerCompaniesApi {
    private ApiClient apiClient;

    public CareerCompaniesApi() {
        this(new ApiClient());
    }

    @Autowired
    public CareerCompaniesApi(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public ApiClient getApiClient() {
        return apiClient;
    }

    public void setApiClient(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    /**
     * Create company
     * Creates a tracked company owned by the authenticated user. Duplicate names for the same owner are rejected.
     * <p><b>201</b> - Company created
     * <p><b>400</b> - Company request validation failed
     * <p><b>401</b> - JWT authentication required for company APIs
     * <p><b>422</b> - Business rule violation (for example duplicate company name)
     * @param companyRequest The companyRequest parameter
     * @return CareerCompanyApiResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec create10RequestCreation(CompanyRequest companyRequest) throws RestClientResponseException {
        Object postBody = companyRequest;
        // verify the required parameter 'companyRequest' is set
        if (companyRequest == null) {
            throw new RestClientResponseException("Missing the required parameter 'companyRequest' when calling create10", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
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

        ParameterizedTypeReference<CareerCompanyApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return apiClient.invokeAPI("/api/v1/career/companies", HttpMethod.POST, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Create company
     * Creates a tracked company owned by the authenticated user. Duplicate names for the same owner are rejected.
     * <p><b>201</b> - Company created
     * <p><b>400</b> - Company request validation failed
     * <p><b>401</b> - JWT authentication required for company APIs
     * <p><b>422</b> - Business rule violation (for example duplicate company name)
     * @param companyRequest The companyRequest parameter
     * @return CareerCompanyApiResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public CareerCompanyApiResponse create10(CompanyRequest companyRequest) throws RestClientResponseException {
        ParameterizedTypeReference<CareerCompanyApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return create10RequestCreation(companyRequest).body(localVarReturnType);
    }

    /**
     * Create company
     * Creates a tracked company owned by the authenticated user. Duplicate names for the same owner are rejected.
     * <p><b>201</b> - Company created
     * <p><b>400</b> - Company request validation failed
     * <p><b>401</b> - JWT authentication required for company APIs
     * <p><b>422</b> - Business rule violation (for example duplicate company name)
     * @param companyRequest The companyRequest parameter
     * @return ResponseEntity&lt;CareerCompanyApiResponse&gt;
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<CareerCompanyApiResponse> create10WithHttpInfo(CompanyRequest companyRequest) throws RestClientResponseException {
        ParameterizedTypeReference<CareerCompanyApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return create10RequestCreation(companyRequest).toEntity(localVarReturnType);
    }

    /**
     * Create company
     * Creates a tracked company owned by the authenticated user. Duplicate names for the same owner are rejected.
     * <p><b>201</b> - Company created
     * <p><b>400</b> - Company request validation failed
     * <p><b>401</b> - JWT authentication required for company APIs
     * <p><b>422</b> - Business rule violation (for example duplicate company name)
     * @param companyRequest The companyRequest parameter
     * @return ResponseSpec
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec create10WithResponseSpec(CompanyRequest companyRequest) throws RestClientResponseException {
        return create10RequestCreation(companyRequest);
    }
    /**
     * Delete company
     * Deletes a company owned by the authenticated user. Companies referenced by job applications cannot be deleted.
     * <p><b>200</b> - Company deleted
     * <p><b>401</b> - JWT authentication required for company APIs
     * <p><b>404</b> - Company not found for the authenticated user
     * <p><b>422</b> - Company is still referenced by job applications
     * @param companyId Company identifier
     * @return CareerDeleteApiResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec delete10RequestCreation(UUID companyId) throws RestClientResponseException {
        Object postBody = null;
        // verify the required parameter 'companyId' is set
        if (companyId == null) {
            throw new RestClientResponseException("Missing the required parameter 'companyId' when calling delete10", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // create path and map variables
        final Map<String, Object> pathParams = new HashMap<>();

        pathParams.put("companyId", companyId);

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
        return apiClient.invokeAPI("/api/v1/career/companies/{companyId}", HttpMethod.DELETE, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Delete company
     * Deletes a company owned by the authenticated user. Companies referenced by job applications cannot be deleted.
     * <p><b>200</b> - Company deleted
     * <p><b>401</b> - JWT authentication required for company APIs
     * <p><b>404</b> - Company not found for the authenticated user
     * <p><b>422</b> - Company is still referenced by job applications
     * @param companyId Company identifier
     * @return CareerDeleteApiResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public CareerDeleteApiResponse delete10(UUID companyId) throws RestClientResponseException {
        ParameterizedTypeReference<CareerDeleteApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return delete10RequestCreation(companyId).body(localVarReturnType);
    }

    /**
     * Delete company
     * Deletes a company owned by the authenticated user. Companies referenced by job applications cannot be deleted.
     * <p><b>200</b> - Company deleted
     * <p><b>401</b> - JWT authentication required for company APIs
     * <p><b>404</b> - Company not found for the authenticated user
     * <p><b>422</b> - Company is still referenced by job applications
     * @param companyId Company identifier
     * @return ResponseEntity&lt;CareerDeleteApiResponse&gt;
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<CareerDeleteApiResponse> delete10WithHttpInfo(UUID companyId) throws RestClientResponseException {
        ParameterizedTypeReference<CareerDeleteApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return delete10RequestCreation(companyId).toEntity(localVarReturnType);
    }

    /**
     * Delete company
     * Deletes a company owned by the authenticated user. Companies referenced by job applications cannot be deleted.
     * <p><b>200</b> - Company deleted
     * <p><b>401</b> - JWT authentication required for company APIs
     * <p><b>404</b> - Company not found for the authenticated user
     * <p><b>422</b> - Company is still referenced by job applications
     * @param companyId Company identifier
     * @return ResponseSpec
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec delete10WithResponseSpec(UUID companyId) throws RestClientResponseException {
        return delete10RequestCreation(companyId);
    }
    /**
     * Get company
     * Returns a company owned by the authenticated user.
     * <p><b>200</b> - Company found
     * <p><b>401</b> - JWT authentication required for company APIs
     * <p><b>404</b> - Company not found for the authenticated user
     * @param companyId Company identifier
     * @return CareerCompanyApiResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec get9RequestCreation(UUID companyId) throws RestClientResponseException {
        Object postBody = null;
        // verify the required parameter 'companyId' is set
        if (companyId == null) {
            throw new RestClientResponseException("Missing the required parameter 'companyId' when calling get9", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // create path and map variables
        final Map<String, Object> pathParams = new HashMap<>();

        pathParams.put("companyId", companyId);

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

        ParameterizedTypeReference<CareerCompanyApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return apiClient.invokeAPI("/api/v1/career/companies/{companyId}", HttpMethod.GET, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Get company
     * Returns a company owned by the authenticated user.
     * <p><b>200</b> - Company found
     * <p><b>401</b> - JWT authentication required for company APIs
     * <p><b>404</b> - Company not found for the authenticated user
     * @param companyId Company identifier
     * @return CareerCompanyApiResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public CareerCompanyApiResponse get9(UUID companyId) throws RestClientResponseException {
        ParameterizedTypeReference<CareerCompanyApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return get9RequestCreation(companyId).body(localVarReturnType);
    }

    /**
     * Get company
     * Returns a company owned by the authenticated user.
     * <p><b>200</b> - Company found
     * <p><b>401</b> - JWT authentication required for company APIs
     * <p><b>404</b> - Company not found for the authenticated user
     * @param companyId Company identifier
     * @return ResponseEntity&lt;CareerCompanyApiResponse&gt;
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<CareerCompanyApiResponse> get9WithHttpInfo(UUID companyId) throws RestClientResponseException {
        ParameterizedTypeReference<CareerCompanyApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return get9RequestCreation(companyId).toEntity(localVarReturnType);
    }

    /**
     * Get company
     * Returns a company owned by the authenticated user.
     * <p><b>200</b> - Company found
     * <p><b>401</b> - JWT authentication required for company APIs
     * <p><b>404</b> - Company not found for the authenticated user
     * @param companyId Company identifier
     * @return ResponseSpec
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec get9WithResponseSpec(UUID companyId) throws RestClientResponseException {
        return get9RequestCreation(companyId);
    }
    /**
     * List companies
     * Lists all companies owned by the authenticated user.
     * <p><b>200</b> - Companies listed
     * <p><b>401</b> - JWT authentication required for company APIs
     * @return CareerCompanyListApiResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec list10RequestCreation() throws RestClientResponseException {
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

        ParameterizedTypeReference<CareerCompanyListApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return apiClient.invokeAPI("/api/v1/career/companies", HttpMethod.GET, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * List companies
     * Lists all companies owned by the authenticated user.
     * <p><b>200</b> - Companies listed
     * <p><b>401</b> - JWT authentication required for company APIs
     * @return CareerCompanyListApiResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public CareerCompanyListApiResponse list10() throws RestClientResponseException {
        ParameterizedTypeReference<CareerCompanyListApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return list10RequestCreation().body(localVarReturnType);
    }

    /**
     * List companies
     * Lists all companies owned by the authenticated user.
     * <p><b>200</b> - Companies listed
     * <p><b>401</b> - JWT authentication required for company APIs
     * @return ResponseEntity&lt;CareerCompanyListApiResponse&gt;
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<CareerCompanyListApiResponse> list10WithHttpInfo() throws RestClientResponseException {
        ParameterizedTypeReference<CareerCompanyListApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return list10RequestCreation().toEntity(localVarReturnType);
    }

    /**
     * List companies
     * Lists all companies owned by the authenticated user.
     * <p><b>200</b> - Companies listed
     * <p><b>401</b> - JWT authentication required for company APIs
     * @return ResponseSpec
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec list10WithResponseSpec() throws RestClientResponseException {
        return list10RequestCreation();
    }
    /**
     * Update company
     * Updates a company owned by the authenticated user.
     * <p><b>200</b> - Company updated
     * <p><b>400</b> - Company request validation failed
     * <p><b>401</b> - JWT authentication required for company APIs
     * <p><b>404</b> - Company not found for the authenticated user
     * <p><b>422</b> - Business rule violation
     * @param companyId Company identifier
     * @param companyRequest The companyRequest parameter
     * @return CareerCompanyApiResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec update10RequestCreation(UUID companyId, CompanyRequest companyRequest) throws RestClientResponseException {
        Object postBody = companyRequest;
        // verify the required parameter 'companyId' is set
        if (companyId == null) {
            throw new RestClientResponseException("Missing the required parameter 'companyId' when calling update10", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // verify the required parameter 'companyRequest' is set
        if (companyRequest == null) {
            throw new RestClientResponseException("Missing the required parameter 'companyRequest' when calling update10", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // create path and map variables
        final Map<String, Object> pathParams = new HashMap<>();

        pathParams.put("companyId", companyId);

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

        ParameterizedTypeReference<CareerCompanyApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return apiClient.invokeAPI("/api/v1/career/companies/{companyId}", HttpMethod.PUT, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Update company
     * Updates a company owned by the authenticated user.
     * <p><b>200</b> - Company updated
     * <p><b>400</b> - Company request validation failed
     * <p><b>401</b> - JWT authentication required for company APIs
     * <p><b>404</b> - Company not found for the authenticated user
     * <p><b>422</b> - Business rule violation
     * @param companyId Company identifier
     * @param companyRequest The companyRequest parameter
     * @return CareerCompanyApiResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public CareerCompanyApiResponse update10(UUID companyId, CompanyRequest companyRequest) throws RestClientResponseException {
        ParameterizedTypeReference<CareerCompanyApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return update10RequestCreation(companyId, companyRequest).body(localVarReturnType);
    }

    /**
     * Update company
     * Updates a company owned by the authenticated user.
     * <p><b>200</b> - Company updated
     * <p><b>400</b> - Company request validation failed
     * <p><b>401</b> - JWT authentication required for company APIs
     * <p><b>404</b> - Company not found for the authenticated user
     * <p><b>422</b> - Business rule violation
     * @param companyId Company identifier
     * @param companyRequest The companyRequest parameter
     * @return ResponseEntity&lt;CareerCompanyApiResponse&gt;
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<CareerCompanyApiResponse> update10WithHttpInfo(UUID companyId, CompanyRequest companyRequest) throws RestClientResponseException {
        ParameterizedTypeReference<CareerCompanyApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return update10RequestCreation(companyId, companyRequest).toEntity(localVarReturnType);
    }

    /**
     * Update company
     * Updates a company owned by the authenticated user.
     * <p><b>200</b> - Company updated
     * <p><b>400</b> - Company request validation failed
     * <p><b>401</b> - JWT authentication required for company APIs
     * <p><b>404</b> - Company not found for the authenticated user
     * <p><b>422</b> - Business rule violation
     * @param companyId Company identifier
     * @param companyRequest The companyRequest parameter
     * @return ResponseSpec
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec update10WithResponseSpec(UUID companyId, CompanyRequest companyRequest) throws RestClientResponseException {
        return update10RequestCreation(companyId, companyRequest);
    }
}

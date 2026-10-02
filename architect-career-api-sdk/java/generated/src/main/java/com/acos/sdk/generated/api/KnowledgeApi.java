package com.acos.sdk.generated.api;

import com.acos.sdk.generated.ApiClient;

import com.acos.sdk.generated.model.KnowledgeDeleteApiResponse;
import com.acos.sdk.generated.model.KnowledgeErrorApiResponse;
import com.acos.sdk.generated.model.KnowledgeNoteApiResponse;
import com.acos.sdk.generated.model.KnowledgeNotePageApiResponse;
import com.acos.sdk.generated.model.KnowledgeNoteRequest;
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
public class KnowledgeApi {
    private ApiClient apiClient;

    public KnowledgeApi() {
        this(new ApiClient());
    }

    @Autowired
    public KnowledgeApi(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public ApiClient getApiClient() {
        return apiClient;
    }

    public void setApiClient(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    /**
     * Create knowledge note
     * Creates a markdown knowledge note owned by the authenticated user. Optional category and tags are created when missing.
     * <p><b>201</b> - Note created
     * <p><b>400</b> - Validation failed
     * <p><b>401</b> - Authentication required
     * @param knowledgeNoteRequest The knowledgeNoteRequest parameter
     * @return KnowledgeNoteApiResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec create8RequestCreation(KnowledgeNoteRequest knowledgeNoteRequest) throws RestClientResponseException {
        Object postBody = knowledgeNoteRequest;
        // verify the required parameter 'knowledgeNoteRequest' is set
        if (knowledgeNoteRequest == null) {
            throw new RestClientResponseException("Missing the required parameter 'knowledgeNoteRequest' when calling create8", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
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

        ParameterizedTypeReference<KnowledgeNoteApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return apiClient.invokeAPI("/api/v1/knowledge/notes", HttpMethod.POST, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Create knowledge note
     * Creates a markdown knowledge note owned by the authenticated user. Optional category and tags are created when missing.
     * <p><b>201</b> - Note created
     * <p><b>400</b> - Validation failed
     * <p><b>401</b> - Authentication required
     * @param knowledgeNoteRequest The knowledgeNoteRequest parameter
     * @return KnowledgeNoteApiResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public KnowledgeNoteApiResponse create8(KnowledgeNoteRequest knowledgeNoteRequest) throws RestClientResponseException {
        ParameterizedTypeReference<KnowledgeNoteApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return create8RequestCreation(knowledgeNoteRequest).body(localVarReturnType);
    }

    /**
     * Create knowledge note
     * Creates a markdown knowledge note owned by the authenticated user. Optional category and tags are created when missing.
     * <p><b>201</b> - Note created
     * <p><b>400</b> - Validation failed
     * <p><b>401</b> - Authentication required
     * @param knowledgeNoteRequest The knowledgeNoteRequest parameter
     * @return ResponseEntity&lt;KnowledgeNoteApiResponse&gt;
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<KnowledgeNoteApiResponse> create8WithHttpInfo(KnowledgeNoteRequest knowledgeNoteRequest) throws RestClientResponseException {
        ParameterizedTypeReference<KnowledgeNoteApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return create8RequestCreation(knowledgeNoteRequest).toEntity(localVarReturnType);
    }

    /**
     * Create knowledge note
     * Creates a markdown knowledge note owned by the authenticated user. Optional category and tags are created when missing.
     * <p><b>201</b> - Note created
     * <p><b>400</b> - Validation failed
     * <p><b>401</b> - Authentication required
     * @param knowledgeNoteRequest The knowledgeNoteRequest parameter
     * @return ResponseSpec
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec create8WithResponseSpec(KnowledgeNoteRequest knowledgeNoteRequest) throws RestClientResponseException {
        return create8RequestCreation(knowledgeNoteRequest);
    }
    /**
     * Delete knowledge note
     * Deletes a markdown knowledge note owned by the authenticated user.
     * <p><b>200</b> - Note deleted
     * <p><b>401</b> - Authentication required
     * <p><b>404</b> - Note not found
     * @param noteId Knowledge note identifier
     * @return KnowledgeDeleteApiResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec delete8RequestCreation(UUID noteId) throws RestClientResponseException {
        Object postBody = null;
        // verify the required parameter 'noteId' is set
        if (noteId == null) {
            throw new RestClientResponseException("Missing the required parameter 'noteId' when calling delete8", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // create path and map variables
        final Map<String, Object> pathParams = new HashMap<>();

        pathParams.put("noteId", noteId);

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

        ParameterizedTypeReference<KnowledgeDeleteApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return apiClient.invokeAPI("/api/v1/knowledge/notes/{noteId}", HttpMethod.DELETE, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Delete knowledge note
     * Deletes a markdown knowledge note owned by the authenticated user.
     * <p><b>200</b> - Note deleted
     * <p><b>401</b> - Authentication required
     * <p><b>404</b> - Note not found
     * @param noteId Knowledge note identifier
     * @return KnowledgeDeleteApiResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public KnowledgeDeleteApiResponse delete8(UUID noteId) throws RestClientResponseException {
        ParameterizedTypeReference<KnowledgeDeleteApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return delete8RequestCreation(noteId).body(localVarReturnType);
    }

    /**
     * Delete knowledge note
     * Deletes a markdown knowledge note owned by the authenticated user.
     * <p><b>200</b> - Note deleted
     * <p><b>401</b> - Authentication required
     * <p><b>404</b> - Note not found
     * @param noteId Knowledge note identifier
     * @return ResponseEntity&lt;KnowledgeDeleteApiResponse&gt;
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<KnowledgeDeleteApiResponse> delete8WithHttpInfo(UUID noteId) throws RestClientResponseException {
        ParameterizedTypeReference<KnowledgeDeleteApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return delete8RequestCreation(noteId).toEntity(localVarReturnType);
    }

    /**
     * Delete knowledge note
     * Deletes a markdown knowledge note owned by the authenticated user.
     * <p><b>200</b> - Note deleted
     * <p><b>401</b> - Authentication required
     * <p><b>404</b> - Note not found
     * @param noteId Knowledge note identifier
     * @return ResponseSpec
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec delete8WithResponseSpec(UUID noteId) throws RestClientResponseException {
        return delete8RequestCreation(noteId);
    }
    /**
     * Get knowledge note
     * Returns a markdown knowledge note owned by the authenticated user.
     * <p><b>200</b> - Note returned
     * <p><b>401</b> - Authentication required
     * <p><b>404</b> - Note not found
     * @param noteId Knowledge note identifier
     * @return KnowledgeNoteApiResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec get7RequestCreation(UUID noteId) throws RestClientResponseException {
        Object postBody = null;
        // verify the required parameter 'noteId' is set
        if (noteId == null) {
            throw new RestClientResponseException("Missing the required parameter 'noteId' when calling get7", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // create path and map variables
        final Map<String, Object> pathParams = new HashMap<>();

        pathParams.put("noteId", noteId);

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

        ParameterizedTypeReference<KnowledgeNoteApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return apiClient.invokeAPI("/api/v1/knowledge/notes/{noteId}", HttpMethod.GET, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Get knowledge note
     * Returns a markdown knowledge note owned by the authenticated user.
     * <p><b>200</b> - Note returned
     * <p><b>401</b> - Authentication required
     * <p><b>404</b> - Note not found
     * @param noteId Knowledge note identifier
     * @return KnowledgeNoteApiResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public KnowledgeNoteApiResponse get7(UUID noteId) throws RestClientResponseException {
        ParameterizedTypeReference<KnowledgeNoteApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return get7RequestCreation(noteId).body(localVarReturnType);
    }

    /**
     * Get knowledge note
     * Returns a markdown knowledge note owned by the authenticated user.
     * <p><b>200</b> - Note returned
     * <p><b>401</b> - Authentication required
     * <p><b>404</b> - Note not found
     * @param noteId Knowledge note identifier
     * @return ResponseEntity&lt;KnowledgeNoteApiResponse&gt;
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<KnowledgeNoteApiResponse> get7WithHttpInfo(UUID noteId) throws RestClientResponseException {
        ParameterizedTypeReference<KnowledgeNoteApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return get7RequestCreation(noteId).toEntity(localVarReturnType);
    }

    /**
     * Get knowledge note
     * Returns a markdown knowledge note owned by the authenticated user.
     * <p><b>200</b> - Note returned
     * <p><b>401</b> - Authentication required
     * <p><b>404</b> - Note not found
     * @param noteId Knowledge note identifier
     * @return ResponseSpec
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec get7WithResponseSpec(UUID noteId) throws RestClientResponseException {
        return get7RequestCreation(noteId);
    }
    /**
     * List knowledge notes
     * Returns a paginated list of markdown knowledge notes owned by the authenticated user.
     * <p><b>200</b> - Notes returned
     * <p><b>400</b> - Validation failed
     * <p><b>401</b> - Authentication required
     * @param page Zero-based page index (0..N)
     * @param size The size of the page to be returned
     * @param sort Sorting criteria in the format: property,(asc|desc). Default sort order is ascending. Multiple sort criteria are supported.
     * @return KnowledgeNotePageApiResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec list8RequestCreation(Integer page, Integer size, List<String> sort) throws RestClientResponseException {
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

        ParameterizedTypeReference<KnowledgeNotePageApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return apiClient.invokeAPI("/api/v1/knowledge/notes", HttpMethod.GET, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * List knowledge notes
     * Returns a paginated list of markdown knowledge notes owned by the authenticated user.
     * <p><b>200</b> - Notes returned
     * <p><b>400</b> - Validation failed
     * <p><b>401</b> - Authentication required
     * @param page Zero-based page index (0..N)
     * @param size The size of the page to be returned
     * @param sort Sorting criteria in the format: property,(asc|desc). Default sort order is ascending. Multiple sort criteria are supported.
     * @return KnowledgeNotePageApiResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public KnowledgeNotePageApiResponse list8(Integer page, Integer size, List<String> sort) throws RestClientResponseException {
        ParameterizedTypeReference<KnowledgeNotePageApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return list8RequestCreation(page, size, sort).body(localVarReturnType);
    }

    /**
     * List knowledge notes
     * Returns a paginated list of markdown knowledge notes owned by the authenticated user.
     * <p><b>200</b> - Notes returned
     * <p><b>400</b> - Validation failed
     * <p><b>401</b> - Authentication required
     * @param page Zero-based page index (0..N)
     * @param size The size of the page to be returned
     * @param sort Sorting criteria in the format: property,(asc|desc). Default sort order is ascending. Multiple sort criteria are supported.
     * @return ResponseEntity&lt;KnowledgeNotePageApiResponse&gt;
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<KnowledgeNotePageApiResponse> list8WithHttpInfo(Integer page, Integer size, List<String> sort) throws RestClientResponseException {
        ParameterizedTypeReference<KnowledgeNotePageApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return list8RequestCreation(page, size, sort).toEntity(localVarReturnType);
    }

    /**
     * List knowledge notes
     * Returns a paginated list of markdown knowledge notes owned by the authenticated user.
     * <p><b>200</b> - Notes returned
     * <p><b>400</b> - Validation failed
     * <p><b>401</b> - Authentication required
     * @param page Zero-based page index (0..N)
     * @param size The size of the page to be returned
     * @param sort Sorting criteria in the format: property,(asc|desc). Default sort order is ascending. Multiple sort criteria are supported.
     * @return ResponseSpec
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec list8WithResponseSpec(Integer page, Integer size, List<String> sort) throws RestClientResponseException {
        return list8RequestCreation(page, size, sort);
    }
    /**
     * Search knowledge notes
     * Searches markdown knowledge notes owned by the authenticated user by title or summary.
     * <p><b>200</b> - Matching notes returned
     * <p><b>400</b> - Validation failed
     * <p><b>401</b> - Authentication required
     * @param q Search text matched against title or summary
     * @param page Zero-based page index (0..N)
     * @param size The size of the page to be returned
     * @param sort Sorting criteria in the format: property,(asc|desc). Default sort order is ascending. Multiple sort criteria are supported.
     * @return KnowledgeNotePageApiResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec search1RequestCreation(String q, Integer page, Integer size, List<String> sort) throws RestClientResponseException {
        Object postBody = null;
        // verify the required parameter 'q' is set
        if (q == null) {
            throw new RestClientResponseException("Missing the required parameter 'q' when calling search1", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
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

        ParameterizedTypeReference<KnowledgeNotePageApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return apiClient.invokeAPI("/api/v1/knowledge/notes/search", HttpMethod.GET, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Search knowledge notes
     * Searches markdown knowledge notes owned by the authenticated user by title or summary.
     * <p><b>200</b> - Matching notes returned
     * <p><b>400</b> - Validation failed
     * <p><b>401</b> - Authentication required
     * @param q Search text matched against title or summary
     * @param page Zero-based page index (0..N)
     * @param size The size of the page to be returned
     * @param sort Sorting criteria in the format: property,(asc|desc). Default sort order is ascending. Multiple sort criteria are supported.
     * @return KnowledgeNotePageApiResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public KnowledgeNotePageApiResponse search1(String q, Integer page, Integer size, List<String> sort) throws RestClientResponseException {
        ParameterizedTypeReference<KnowledgeNotePageApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return search1RequestCreation(q, page, size, sort).body(localVarReturnType);
    }

    /**
     * Search knowledge notes
     * Searches markdown knowledge notes owned by the authenticated user by title or summary.
     * <p><b>200</b> - Matching notes returned
     * <p><b>400</b> - Validation failed
     * <p><b>401</b> - Authentication required
     * @param q Search text matched against title or summary
     * @param page Zero-based page index (0..N)
     * @param size The size of the page to be returned
     * @param sort Sorting criteria in the format: property,(asc|desc). Default sort order is ascending. Multiple sort criteria are supported.
     * @return ResponseEntity&lt;KnowledgeNotePageApiResponse&gt;
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<KnowledgeNotePageApiResponse> search1WithHttpInfo(String q, Integer page, Integer size, List<String> sort) throws RestClientResponseException {
        ParameterizedTypeReference<KnowledgeNotePageApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return search1RequestCreation(q, page, size, sort).toEntity(localVarReturnType);
    }

    /**
     * Search knowledge notes
     * Searches markdown knowledge notes owned by the authenticated user by title or summary.
     * <p><b>200</b> - Matching notes returned
     * <p><b>400</b> - Validation failed
     * <p><b>401</b> - Authentication required
     * @param q Search text matched against title or summary
     * @param page Zero-based page index (0..N)
     * @param size The size of the page to be returned
     * @param sort Sorting criteria in the format: property,(asc|desc). Default sort order is ascending. Multiple sort criteria are supported.
     * @return ResponseSpec
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec search1WithResponseSpec(String q, Integer page, Integer size, List<String> sort) throws RestClientResponseException {
        return search1RequestCreation(q, page, size, sort);
    }
    /**
     * Update knowledge note
     * Updates a markdown knowledge note owned by the authenticated user.
     * <p><b>200</b> - Note updated
     * <p><b>400</b> - Validation failed
     * <p><b>401</b> - Authentication required
     * <p><b>404</b> - Note not found
     * @param noteId Knowledge note identifier
     * @param knowledgeNoteRequest The knowledgeNoteRequest parameter
     * @return KnowledgeNoteApiResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec update8RequestCreation(UUID noteId, KnowledgeNoteRequest knowledgeNoteRequest) throws RestClientResponseException {
        Object postBody = knowledgeNoteRequest;
        // verify the required parameter 'noteId' is set
        if (noteId == null) {
            throw new RestClientResponseException("Missing the required parameter 'noteId' when calling update8", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // verify the required parameter 'knowledgeNoteRequest' is set
        if (knowledgeNoteRequest == null) {
            throw new RestClientResponseException("Missing the required parameter 'knowledgeNoteRequest' when calling update8", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // create path and map variables
        final Map<String, Object> pathParams = new HashMap<>();

        pathParams.put("noteId", noteId);

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

        ParameterizedTypeReference<KnowledgeNoteApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return apiClient.invokeAPI("/api/v1/knowledge/notes/{noteId}", HttpMethod.PUT, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Update knowledge note
     * Updates a markdown knowledge note owned by the authenticated user.
     * <p><b>200</b> - Note updated
     * <p><b>400</b> - Validation failed
     * <p><b>401</b> - Authentication required
     * <p><b>404</b> - Note not found
     * @param noteId Knowledge note identifier
     * @param knowledgeNoteRequest The knowledgeNoteRequest parameter
     * @return KnowledgeNoteApiResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public KnowledgeNoteApiResponse update8(UUID noteId, KnowledgeNoteRequest knowledgeNoteRequest) throws RestClientResponseException {
        ParameterizedTypeReference<KnowledgeNoteApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return update8RequestCreation(noteId, knowledgeNoteRequest).body(localVarReturnType);
    }

    /**
     * Update knowledge note
     * Updates a markdown knowledge note owned by the authenticated user.
     * <p><b>200</b> - Note updated
     * <p><b>400</b> - Validation failed
     * <p><b>401</b> - Authentication required
     * <p><b>404</b> - Note not found
     * @param noteId Knowledge note identifier
     * @param knowledgeNoteRequest The knowledgeNoteRequest parameter
     * @return ResponseEntity&lt;KnowledgeNoteApiResponse&gt;
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<KnowledgeNoteApiResponse> update8WithHttpInfo(UUID noteId, KnowledgeNoteRequest knowledgeNoteRequest) throws RestClientResponseException {
        ParameterizedTypeReference<KnowledgeNoteApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return update8RequestCreation(noteId, knowledgeNoteRequest).toEntity(localVarReturnType);
    }

    /**
     * Update knowledge note
     * Updates a markdown knowledge note owned by the authenticated user.
     * <p><b>200</b> - Note updated
     * <p><b>400</b> - Validation failed
     * <p><b>401</b> - Authentication required
     * <p><b>404</b> - Note not found
     * @param noteId Knowledge note identifier
     * @param knowledgeNoteRequest The knowledgeNoteRequest parameter
     * @return ResponseSpec
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec update8WithResponseSpec(UUID noteId, KnowledgeNoteRequest knowledgeNoteRequest) throws RestClientResponseException {
        return update8RequestCreation(noteId, knowledgeNoteRequest);
    }
}

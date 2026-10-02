package com.acos.sdk.generated.api;

import com.acos.sdk.generated.ApiClient;

import com.acos.sdk.generated.model.AuthenticationApiResponse;
import com.acos.sdk.generated.model.ErrorApiResponse;
import com.acos.sdk.generated.model.LoginRequest;
import com.acos.sdk.generated.model.LogoutRequest;
import com.acos.sdk.generated.model.MeApiResponse;
import com.acos.sdk.generated.model.RefreshRequest;
import com.acos.sdk.generated.model.RegisterApiResponse;
import com.acos.sdk.generated.model.RegisterRequest;
import com.acos.sdk.generated.model.TokenApiResponse;
import com.acos.sdk.generated.model.VoidApiResponse;

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
public class AuthenticationApi {
    private ApiClient apiClient;

    public AuthenticationApi() {
        this(new ApiClient());
    }

    @Autowired
    public AuthenticationApi(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public ApiClient getApiClient() {
        return apiClient;
    }

    public void setApiClient(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    /**
     * Current user
     * Returns the authenticated user profile.
     * <p><b>200</b> - Current user profile
     * <p><b>401</b> - Authentication required
     * @return MeApiResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec getCurrentUserRequestCreation() throws RestClientResponseException {
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

        ParameterizedTypeReference<MeApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return apiClient.invokeAPI("/api/v1/auth/me", HttpMethod.GET, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Current user
     * Returns the authenticated user profile.
     * <p><b>200</b> - Current user profile
     * <p><b>401</b> - Authentication required
     * @return MeApiResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public MeApiResponse getCurrentUser() throws RestClientResponseException {
        ParameterizedTypeReference<MeApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return getCurrentUserRequestCreation().body(localVarReturnType);
    }

    /**
     * Current user
     * Returns the authenticated user profile.
     * <p><b>200</b> - Current user profile
     * <p><b>401</b> - Authentication required
     * @return ResponseEntity&lt;MeApiResponse&gt;
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<MeApiResponse> getCurrentUserWithHttpInfo() throws RestClientResponseException {
        ParameterizedTypeReference<MeApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return getCurrentUserRequestCreation().toEntity(localVarReturnType);
    }

    /**
     * Current user
     * Returns the authenticated user profile.
     * <p><b>200</b> - Current user profile
     * <p><b>401</b> - Authentication required
     * @return ResponseSpec
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec getCurrentUserWithResponseSpec() throws RestClientResponseException {
        return getCurrentUserRequestCreation();
    }
    /**
     * Login
     * Authenticates a user with email and password and issues a JWT access token plus opaque refresh token.
     * <p><b>200</b> - Authentication succeeded
     * <p><b>400</b> - Request validation failed
     * <p><b>401</b> - Invalid email or password
     * <p><b>403</b> - Account is disabled
     * @param loginRequest The loginRequest parameter
     * @return AuthenticationApiResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec loginRequestCreation(LoginRequest loginRequest) throws RestClientResponseException {
        Object postBody = loginRequest;
        // verify the required parameter 'loginRequest' is set
        if (loginRequest == null) {
            throw new RestClientResponseException("Missing the required parameter 'loginRequest' when calling login", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
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

        String[] localVarAuthNames = new String[] {  };

        ParameterizedTypeReference<AuthenticationApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return apiClient.invokeAPI("/api/v1/auth/login", HttpMethod.POST, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Login
     * Authenticates a user with email and password and issues a JWT access token plus opaque refresh token.
     * <p><b>200</b> - Authentication succeeded
     * <p><b>400</b> - Request validation failed
     * <p><b>401</b> - Invalid email or password
     * <p><b>403</b> - Account is disabled
     * @param loginRequest The loginRequest parameter
     * @return AuthenticationApiResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public AuthenticationApiResponse login(LoginRequest loginRequest) throws RestClientResponseException {
        ParameterizedTypeReference<AuthenticationApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return loginRequestCreation(loginRequest).body(localVarReturnType);
    }

    /**
     * Login
     * Authenticates a user with email and password and issues a JWT access token plus opaque refresh token.
     * <p><b>200</b> - Authentication succeeded
     * <p><b>400</b> - Request validation failed
     * <p><b>401</b> - Invalid email or password
     * <p><b>403</b> - Account is disabled
     * @param loginRequest The loginRequest parameter
     * @return ResponseEntity&lt;AuthenticationApiResponse&gt;
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<AuthenticationApiResponse> loginWithHttpInfo(LoginRequest loginRequest) throws RestClientResponseException {
        ParameterizedTypeReference<AuthenticationApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return loginRequestCreation(loginRequest).toEntity(localVarReturnType);
    }

    /**
     * Login
     * Authenticates a user with email and password and issues a JWT access token plus opaque refresh token.
     * <p><b>200</b> - Authentication succeeded
     * <p><b>400</b> - Request validation failed
     * <p><b>401</b> - Invalid email or password
     * <p><b>403</b> - Account is disabled
     * @param loginRequest The loginRequest parameter
     * @return ResponseSpec
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec loginWithResponseSpec(LoginRequest loginRequest) throws RestClientResponseException {
        return loginRequestCreation(loginRequest);
    }
    /**
     * Logout
     * Revokes the presented refresh token. Requires a valid access token.
     * <p><b>200</b> - Refresh token revoked
     * <p><b>401</b> - Authentication required
     * @param logoutRequest The logoutRequest parameter
     * @return VoidApiResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec logoutRequestCreation(LogoutRequest logoutRequest) throws RestClientResponseException {
        Object postBody = logoutRequest;
        // verify the required parameter 'logoutRequest' is set
        if (logoutRequest == null) {
            throw new RestClientResponseException("Missing the required parameter 'logoutRequest' when calling logout", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
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

        ParameterizedTypeReference<VoidApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return apiClient.invokeAPI("/api/v1/auth/logout", HttpMethod.POST, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Logout
     * Revokes the presented refresh token. Requires a valid access token.
     * <p><b>200</b> - Refresh token revoked
     * <p><b>401</b> - Authentication required
     * @param logoutRequest The logoutRequest parameter
     * @return VoidApiResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public VoidApiResponse logout(LogoutRequest logoutRequest) throws RestClientResponseException {
        ParameterizedTypeReference<VoidApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return logoutRequestCreation(logoutRequest).body(localVarReturnType);
    }

    /**
     * Logout
     * Revokes the presented refresh token. Requires a valid access token.
     * <p><b>200</b> - Refresh token revoked
     * <p><b>401</b> - Authentication required
     * @param logoutRequest The logoutRequest parameter
     * @return ResponseEntity&lt;VoidApiResponse&gt;
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<VoidApiResponse> logoutWithHttpInfo(LogoutRequest logoutRequest) throws RestClientResponseException {
        ParameterizedTypeReference<VoidApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return logoutRequestCreation(logoutRequest).toEntity(localVarReturnType);
    }

    /**
     * Logout
     * Revokes the presented refresh token. Requires a valid access token.
     * <p><b>200</b> - Refresh token revoked
     * <p><b>401</b> - Authentication required
     * @param logoutRequest The logoutRequest parameter
     * @return ResponseSpec
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec logoutWithResponseSpec(LogoutRequest logoutRequest) throws RestClientResponseException {
        return logoutRequestCreation(logoutRequest);
    }
    /**
     * Refresh tokens
     * Rotates a valid refresh token into a new access/refresh token pair.
     * <p><b>200</b> - Tokens refreshed
     * <p><b>401</b> - Invalid or expired refresh token
     * @param refreshRequest The refreshRequest parameter
     * @return TokenApiResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec refreshRequestCreation(RefreshRequest refreshRequest) throws RestClientResponseException {
        Object postBody = refreshRequest;
        // verify the required parameter 'refreshRequest' is set
        if (refreshRequest == null) {
            throw new RestClientResponseException("Missing the required parameter 'refreshRequest' when calling refresh", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
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

        String[] localVarAuthNames = new String[] {  };

        ParameterizedTypeReference<TokenApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return apiClient.invokeAPI("/api/v1/auth/refresh", HttpMethod.POST, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Refresh tokens
     * Rotates a valid refresh token into a new access/refresh token pair.
     * <p><b>200</b> - Tokens refreshed
     * <p><b>401</b> - Invalid or expired refresh token
     * @param refreshRequest The refreshRequest parameter
     * @return TokenApiResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public TokenApiResponse refresh(RefreshRequest refreshRequest) throws RestClientResponseException {
        ParameterizedTypeReference<TokenApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return refreshRequestCreation(refreshRequest).body(localVarReturnType);
    }

    /**
     * Refresh tokens
     * Rotates a valid refresh token into a new access/refresh token pair.
     * <p><b>200</b> - Tokens refreshed
     * <p><b>401</b> - Invalid or expired refresh token
     * @param refreshRequest The refreshRequest parameter
     * @return ResponseEntity&lt;TokenApiResponse&gt;
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<TokenApiResponse> refreshWithHttpInfo(RefreshRequest refreshRequest) throws RestClientResponseException {
        ParameterizedTypeReference<TokenApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return refreshRequestCreation(refreshRequest).toEntity(localVarReturnType);
    }

    /**
     * Refresh tokens
     * Rotates a valid refresh token into a new access/refresh token pair.
     * <p><b>200</b> - Tokens refreshed
     * <p><b>401</b> - Invalid or expired refresh token
     * @param refreshRequest The refreshRequest parameter
     * @return ResponseSpec
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec refreshWithResponseSpec(RefreshRequest refreshRequest) throws RestClientResponseException {
        return refreshRequestCreation(refreshRequest);
    }
    /**
     * Register a new user
     * Creates a new user account with the default USER role. Passwords are validated against platform policy and stored using BCrypt.
     * <p><b>201</b> - User registered successfully
     * <p><b>400</b> - Validation failed or password policy violated
     * <p><b>409</b> - Email address is already registered
     * @param registerRequest The registerRequest parameter
     * @return RegisterApiResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec registerRequestCreation(RegisterRequest registerRequest) throws RestClientResponseException {
        Object postBody = registerRequest;
        // verify the required parameter 'registerRequest' is set
        if (registerRequest == null) {
            throw new RestClientResponseException("Missing the required parameter 'registerRequest' when calling register", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
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

        String[] localVarAuthNames = new String[] {  };

        ParameterizedTypeReference<RegisterApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return apiClient.invokeAPI("/api/v1/auth/register", HttpMethod.POST, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Register a new user
     * Creates a new user account with the default USER role. Passwords are validated against platform policy and stored using BCrypt.
     * <p><b>201</b> - User registered successfully
     * <p><b>400</b> - Validation failed or password policy violated
     * <p><b>409</b> - Email address is already registered
     * @param registerRequest The registerRequest parameter
     * @return RegisterApiResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public RegisterApiResponse register(RegisterRequest registerRequest) throws RestClientResponseException {
        ParameterizedTypeReference<RegisterApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return registerRequestCreation(registerRequest).body(localVarReturnType);
    }

    /**
     * Register a new user
     * Creates a new user account with the default USER role. Passwords are validated against platform policy and stored using BCrypt.
     * <p><b>201</b> - User registered successfully
     * <p><b>400</b> - Validation failed or password policy violated
     * <p><b>409</b> - Email address is already registered
     * @param registerRequest The registerRequest parameter
     * @return ResponseEntity&lt;RegisterApiResponse&gt;
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<RegisterApiResponse> registerWithHttpInfo(RegisterRequest registerRequest) throws RestClientResponseException {
        ParameterizedTypeReference<RegisterApiResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return registerRequestCreation(registerRequest).toEntity(localVarReturnType);
    }

    /**
     * Register a new user
     * Creates a new user account with the default USER role. Passwords are validated against platform policy and stored using BCrypt.
     * <p><b>201</b> - User registered successfully
     * <p><b>400</b> - Validation failed or password policy violated
     * <p><b>409</b> - Email address is already registered
     * @param registerRequest The registerRequest parameter
     * @return ResponseSpec
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec registerWithResponseSpec(RegisterRequest registerRequest) throws RestClientResponseException {
        return registerRequestCreation(registerRequest);
    }
}

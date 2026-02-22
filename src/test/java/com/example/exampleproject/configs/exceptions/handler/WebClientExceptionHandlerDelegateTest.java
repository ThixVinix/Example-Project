package com.example.exampleproject.configs.exceptions.handler;

import com.example.exampleproject.configs.exceptions.BaseError;
import com.example.exampleproject.configs.exceptions.ErrorSingleResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Method test for {@link WebClientExceptionHandlerDelegate}
 */
@Tag(value = "WebClientExceptionHandlerDelegate_Tests")
@DisplayName("WebClientExceptionHandlerDelegate Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
class WebClientExceptionHandlerDelegateTest {

    private static final String HANDLE_WEB_CLIENT_EXCEPTION = "handleWebClientException";

    @Mock
    private GlobalExceptionHandlerOperations operations;

    @Mock
    private WebRequest request;

    @InjectMocks
    private WebClientExceptionHandlerDelegate delegate;

    /**
     * Method test for
     * {@link WebClientExceptionHandlerDelegate#handleWebClientException(WebClientResponseException, WebRequest)}
     */
    @Order(1)
    @Tag(value = HANDLE_WEB_CLIENT_EXCEPTION)
    @DisplayName(HANDLE_WEB_CLIENT_EXCEPTION + " Given Bad Request status, then should delegate to handleBadRequestException")
    @Test
    void handleWebClientException_WhenBadRequest_ThenShouldDelegateToHandleBadRequestException() {
        // Arrange
        WebClientResponseException ex = WebClientResponseException.create(
                400, "Bad Request", null, null, StandardCharsets.UTF_8);
        
        when(request.getDescription(false)).thenReturn("/test/path");
        
        ResponseEntity<BaseError> expectedResponse = ResponseEntity.badRequest().build();
        doReturn(expectedResponse).when(operations).handleBadRequestException(eq(ex), eq(request));

        // Act
        ResponseEntity<? extends BaseError> result = delegate.handleWebClientException(ex, request);

        // Assert
        assertNotNull(result, "The result should not be null");
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode(), "The status should be 400");
        verify(operations).handleBadRequestException(eq(ex), eq(request));
    }

    /**
     * Method test for
     * {@link WebClientExceptionHandlerDelegate#handleWebClientException(WebClientResponseException, WebRequest)}
     */
    @Order(2)
    @Tag(value = HANDLE_WEB_CLIENT_EXCEPTION)
    @DisplayName(HANDLE_WEB_CLIENT_EXCEPTION + " Given Unauthorized status, then should delegate to handleSingleErrorResponse")
    @Test
    void handleWebClientException_WhenUnauthorized_ThenShouldDelegateToHandleSingleErrorResponse() {
        // Arrange
        WebClientResponseException ex = WebClientResponseException.create(
                401, "Unauthorized", null, null, StandardCharsets.UTF_8);
        
        when(request.getDescription(false)).thenReturn("/test/path");
        
        ResponseEntity<ErrorSingleResponse> expectedResponse = ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        when(operations.handleSingleErrorResponse(eq(ex), eq(request), eq(HttpStatus.UNAUTHORIZED), anyString(), any()))
                .thenReturn(expectedResponse);

        // Act
        ResponseEntity<? extends BaseError> result = delegate.handleWebClientException(ex, request);

        // Assert
        assertNotNull(result, "The result should not be null");
        assertEquals(HttpStatus.UNAUTHORIZED, result.getStatusCode(), "The status should be 401");
        verify(operations).handleSingleErrorResponse(eq(ex), eq(request), eq(HttpStatus.UNAUTHORIZED), anyString(), any());
    }

    /**
     * Method test for
     * {@link WebClientExceptionHandlerDelegate#handleWebClientException(WebClientResponseException, WebRequest)}
     */
    @Order(3)
    @Tag(value = HANDLE_WEB_CLIENT_EXCEPTION)
    @DisplayName(HANDLE_WEB_CLIENT_EXCEPTION + " Given Unknown status, then should delegate to handleGlobalException")
    @Test
    void handleWebClientException_WhenUnknownStatus_ThenShouldDelegateToHandleGlobalException() {
        // Arrange
        WebClientResponseException ex = WebClientResponseException.create(
                999, "Unknown Status", null, null, StandardCharsets.UTF_8);
        
        when(request.getDescription(false)).thenReturn("/test/path");
        
        ResponseEntity<ErrorSingleResponse> expectedResponse = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        when(operations.handleGlobalException(eq(ex), eq(request))).thenReturn(expectedResponse);

        // Act
        ResponseEntity<? extends BaseError> result = delegate.handleWebClientException(ex, request);

        // Assert
        assertNotNull(result, "The result should not be null");
        verify(operations).handleGlobalException(eq(ex), eq(request));
    }
}

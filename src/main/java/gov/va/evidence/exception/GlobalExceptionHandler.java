package gov.va.evidence.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidation(ValidationException ex, HttpServletRequest request) {
        Violation v = new Violation("payload", "DomainValidation", ex.getMessage());
        return buildWithViolations(HttpStatus.BAD_REQUEST, "Validation failed", request, java.util.List.of(v));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArg(MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<Violation> violations = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> new Violation(fe.getField(), fe.getCode(), fe.getDefaultMessage()))
                .collect(Collectors.toList());
        return buildWithViolations(HttpStatus.BAD_REQUEST, "Validation failed", request, violations);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraint(ConstraintViolationException ex, HttpServletRequest request) {
        List<Violation> violations = ex.getConstraintViolations().stream()
                .map(cv -> new Violation(cv.getPropertyPath().toString(),
                        cv.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(),
                        cv.getMessage()))
                .collect(Collectors.toList());
        return buildWithViolations(HttpStatus.BAD_REQUEST, "Validation failed", request, violations);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleNotReadable(HttpMessageNotReadableException ex, HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST, "Malformed JSON request", request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleOther(Exception ex, HttpServletRequest request) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), request);
    }

    private ResponseEntity<ErrorResponse> build(HttpStatus status, String message, HttpServletRequest request) {
        ErrorResponse body = new ErrorResponse(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequestURI(),
                MDC.get("traceId")
        );
        return ResponseEntity.status(status).body(body);
    }

    private ResponseEntity<ErrorResponse> buildWithViolations(HttpStatus status, String message, HttpServletRequest request, List<Violation> violations) {
        ErrorResponse body = new ErrorResponse(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequestURI(),
                MDC.get("traceId")
        );
        body.setViolations(violations);
        return ResponseEntity.status(status).body(body);
    }
}

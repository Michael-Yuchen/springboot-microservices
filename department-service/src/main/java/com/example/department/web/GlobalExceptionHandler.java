package com.example.department.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.MDC;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private ProblemDetail base(HttpStatus status, String title, String detail, HttpServletRequest req) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(status, detail);
        pd.setTitle(title);
        pd.setType(URI.create("about:blank"));
        pd.setProperty("timestamp", Instant.now().toString());
        pd.setProperty("instance", req.getRequestURI());
        pd.setProperty("traceId", MDC.get("traceId"));
        return pd;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        ProblemDetail pd = base(HttpStatus.BAD_REQUEST, "Validation failed",
                "Request parameters did not pass validation", req);
        List<Map<String, String>> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> Map.of("field", fe.getField(),
                        "message", Optional.ofNullable(fe.getDefaultMessage()).orElse("")))
                .toList();
        pd.setProperty("errors", errors);
        return pd;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail handleConstraint(ConstraintViolationException ex, HttpServletRequest req) {
        return base(HttpStatus.BAD_REQUEST, "Constraint violation", ex.getMessage(), req);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ProblemDetail handleNotFound(NoSuchElementException ex, HttpServletRequest req) {
        return base(HttpStatus.NOT_FOUND, "Resource not found", ex.getMessage(), req);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail handleConflict(DataIntegrityViolationException ex, HttpServletRequest req) {
        return base(HttpStatus.CONFLICT, "Conflict", "Duplicate or illegal state", req);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ProblemDetail handleRSE(ResponseStatusException ex, HttpServletRequest req) {
        return base(HttpStatus.valueOf(ex.getStatusCode().value()),
                Optional.ofNullable(ex.getReason()).orElse("Error"),
                Optional.ofNullable(ex.getMessage()).orElse(""), req);
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleUnknown(Exception ex, HttpServletRequest req) {
        return base(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error", "Unexpected error", req);
    }
}
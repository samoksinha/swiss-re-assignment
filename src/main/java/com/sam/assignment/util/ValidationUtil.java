package com.sam.assignment.util;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

import java.util.Objects;
import java.util.Set;

public class ValidationUtil {

    private final Validator validator;

    public ValidationUtil(
        final Validator validator) {
        if (Objects.isNull(validator))
            throw new IllegalArgumentException("Validator cannot be null");

        this.validator = validator;
    }

    /**
     * Validates the given object using the configured validator.
     *
     * @param object the object to validate
     * @param <T>    the type of the object
     * @throws IllegalArgumentException if validation fails, containing details of the violations
     */
    public <T> void validate(
        final T object) {

        Set<ConstraintViolation<T>> violations = validator.validate(object);
        if (!violations.isEmpty())
            throw new IllegalArgumentException(
                "Validation failed for object: " +
                    violations.stream()
                        .map(ConstraintViolation::getMessage)
                        .reduce((msg1, msg2) -> msg1 + ", " + msg2)
                        .orElse("Unknown validation error")
            );
    }

}

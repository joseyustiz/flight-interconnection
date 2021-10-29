package com.joseyustiz.flightinterconnection.core.domain;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

public abstract class SelfValidating<T> {
    private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    /*** Evaluates all Bean Validations on the attributes of this* instance.*/
    public void validateSelf() {
        Set<ConstraintViolation<T>> violations = validator.validate((T) this);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }
}

package ru.t1.debut.muse.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CreatePostRequestValidator.class)
public @interface ValidCreatePostRequest {
    String message() default "invalid data";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

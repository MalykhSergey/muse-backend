package ru.t1.debut.muse.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.t1.debut.muse.dto.CreatePostRequest;
import ru.t1.debut.muse.entity.PostType;

public class CreatePostRequestValidator implements ConstraintValidator<ValidCreatePostRequest, CreatePostRequest> {
    @Override
    public boolean isValid(CreatePostRequest createPostRequest, ConstraintValidatorContext context) {
        if (createPostRequest == null) return true;
        boolean valid = true;
        context.disableDefaultConstraintViolation();
        String title = createPostRequest.getTitle();
        PostType postType = createPostRequest.getPostType();
        if (postType == PostType.QUESTION) {
            if (createPostRequest.getParentId() != null) {
                context.buildConstraintViolationWithTemplate("{valid.createPostRequest.excessParent}")
                        .addPropertyNode("excessParent")
                        .addConstraintViolation();
                valid = false;
            }
            if (title == null) {
                context.buildConstraintViolationWithTemplate("{jakarta.validation.constraints.NotNull.message}")
                        .addPropertyNode("title")
                        .addConstraintViolation();
                valid = false;
            }
        } else if (postType == PostType.ANSWER) {
            if (title != null) {
                context.buildConstraintViolationWithTemplate("{valid.createPostRequest.excessTitle}")
                        .addPropertyNode("excessTitle")
                        .addConstraintViolation();
                valid = false;
            }
        }
        return valid;
    }
}

package com.voidhub.api.form.update;

import jakarta.validation.constraints.*;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@Builder
public class UpdateEventForm {

    @Size(min = 3, max = 50, message = "Title must be between 3 and 50 characters")
    private String title;

    @Size(min = 1, max = 500, message = "Short description may not exceed 500 characters")
    private String shortDescription;

    @Size(min = 1, max = 10000, message = "Full description may not exceed 10000 characters")
    private String fullDescription;

    @FutureOrPresent(message = "Application deadline must be in the future")
    private Date applicationDeadline;

    @FutureOrPresent(message = "Starting date must be in the future")
    private Date startingDate;

}

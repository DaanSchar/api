package com.voidhub.api.event;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Builder
public class NewEventForm {

    @Size(min = 3, max = 50, message = "Title must be between 3 and 50 characters")
    @NotBlank(message = "Title is mandatory")
    private String title;

    @Size(min = 1, max = 500, message = "Short description may not exceed 500 characters")
    @NotBlank(message = "A short description is mandatory")
    private String shortDescription;

    @Size(min = 1, max = 10000, message = "Full description may not exceed 10000 characters")
    @NotBlank(message = "A full description is mandatory")
    private String fullDescription;

    @FutureOrPresent(message = "Application deadline must be in the future")
    @NotNull(message = "Application deadline is mandatory")
    private Date applicationDeadline;

    @FutureOrPresent(message = "Starting date must be in the future")
    @NotNull(message = "Starting date is mandatory")
    private Date startingDate;

}

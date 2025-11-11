package org.kenne.app_asymetry_sec.todo.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TodoRequest {

    @NotBlank(message = "VALIDATION.TODO.TITLE.NOT_BLANK")
    private String title;

    @NotBlank(message = "VALIDATION.TODO.DESCRIPTION.NOT_BLANK")
    private String description;

    @FutureOrPresent(message = "VALIDATION.TODO.START_DATE.FUTURE_OR_PRESENT")
    private LocalDate startDate;

    @FutureOrPresent(message = "VALIDATION.TODO.END_DATE.FUTURE_OR_PRESENT")
    private LocalDate endDate;

    private LocalTime startTime;

    private LocalTime endTime;

    private String categoryId;
}

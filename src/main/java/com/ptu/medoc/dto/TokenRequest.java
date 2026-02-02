package com.ptu.medoc.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Data
@Setter
@Getter
@Schema(description = "Token allocation request payload")
public class TokenRequest {

    @Schema(example = "John Doe", required = true)
    @NotEmpty
    private String nameOfPatient;
    @Schema(example = "9876543210", required = true)
    @NotNull
    @Pattern(
            regexp =
            "^[0-9]{10}$",
            message = "Invalid phone number format."
    )
    private String phoneNumber;
    @Schema(example = "Dr. Sarma", required = true)
    @NotEmpty(message = "Doctor name is required.")
    private String doctorName;
    @Schema(example = "PAID_PRIORITY", allowableValues = {
            "ONLINE", "WALK_IN", "PAID_PRIORITY", "EMERGENCY", "FOLLOWUP"
    })
    @NotEmpty(message = "source is required.")
    private  String source;
}

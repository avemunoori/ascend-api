package com.ascend.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class VerifyResetCodeRequest {
    
    @NotBlank(message = "Reset code is required")
    @Pattern(regexp = "^\\d{6}$", message = "Reset code must be exactly 6 digits")
    private String code;
} 
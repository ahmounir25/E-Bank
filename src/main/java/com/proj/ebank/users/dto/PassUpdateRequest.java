package com.proj.ebank.users.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PassUpdateRequest {

    @NotBlank(message = "old password is required")
    private String oldPassword;

    @NotBlank(message = "new password is required")
    private String newPassword;


}

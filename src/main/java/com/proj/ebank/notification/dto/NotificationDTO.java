package com.proj.ebank.notification.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.proj.ebank.enums.NotificationType;
import com.proj.ebank.users.dto.UserDTO;
import com.proj.ebank.users.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(content = JsonInclude.Include.NON_NULL)
public class NotificationDTO {

    private Long id;

    private String subject;

    @NotBlank(message = "Recipient is Required")
    private String recipient;

    private String body;

    private NotificationType type;

    private LocalDateTime createdAt;

    private String templateName;

    private Map<String,Object> templateVariables;


}

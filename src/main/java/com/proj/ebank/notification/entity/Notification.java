package com.proj.ebank.notification.entity;

import com.proj.ebank.enums.NotificationType;
import com.proj.ebank.users.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "notifications")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "subject")
    private String subject;
    @Column(name = "recipient")
    private String recipient;
    @Column(name = "body")
    private String body;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "notification_type")
    private NotificationType type;
    @Column(name = "created_at")
    private LocalDateTime createdAt=LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

}

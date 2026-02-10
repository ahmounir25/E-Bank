package com.proj.ebank.notification.service;

import com.proj.ebank.enums.NotificationType;
import com.proj.ebank.notification.dto.NotificationDTO;
import com.proj.ebank.notification.entity.Notification;
import com.proj.ebank.notification.repo.NotificationRepo;
import com.proj.ebank.users.entity.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepo notificationRepo;
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Override
    @Async
    public void sendEmail(NotificationDTO notificationDTO, User user) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(
                    mimeMessage,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name());
            mimeMessageHelper.setTo(notificationDTO.getRecipient());
            mimeMessageHelper.setSubject(notificationDTO.getSubject());

            if (notificationDTO.getTemplateName() != null) {

                Context context = new Context();
                context.setVariables(notificationDTO.getTemplateVariables());
                String htmlContent = templateEngine.process(notificationDTO.getTemplateName(), context);
                mimeMessageHelper.setText(htmlContent, true);

            } else {

                mimeMessageHelper.setText(notificationDTO.getBody(), true);
            }

            // Sending the mail
            mailSender.send(mimeMessage);

            // Save To DB
            Notification notification = Notification.builder()
                    .user(user)
                    .recipient(notificationDTO.getRecipient())
                    .subject(notificationDTO.getSubject())
                    .body(notificationDTO.getBody())
                    .type(NotificationType.EMAIL)
                    .createdAt(LocalDateTime.now())
                    .build();
            notificationRepo.save(notification);

        } catch (MessagingException e) {

            log.error(e.getMessage());

        }

    }
}

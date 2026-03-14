package com.proj.ebank;

import com.proj.ebank.enums.NotificationType;
import com.proj.ebank.notification.dto.NotificationDTO;
import com.proj.ebank.notification.service.NotificationService;
import com.proj.ebank.users.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
@RequiredArgsConstructor
public class EbankApplication {
//    private final NotificationService notificationService;

    public static void main(String[] args) {
        SpringApplication.run(EbankApplication.class, args);
    }

}

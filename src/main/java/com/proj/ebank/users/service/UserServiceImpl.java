package com.proj.ebank.users.service;

import com.proj.ebank.aws.S3Service;
import com.proj.ebank.exceptions.BadRequestException;
import com.proj.ebank.exceptions.NotFoundException;
import com.proj.ebank.notification.dto.NotificationDTO;
import com.proj.ebank.notification.service.NotificationService;
import com.proj.ebank.response.Response;
import com.proj.ebank.users.dto.PassUpdateRequest;
import com.proj.ebank.users.dto.UserDTO;
import com.proj.ebank.users.entity.User;
import com.proj.ebank.users.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepo userRepo;
    private final NotificationService notificationService;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final S3Service s3Service;


    // for backend
//    private final String uploadDir = "uploads/profilePic";


    // for frontend
    private final String uploadDir = "E:\\dev-spring-boot\\ebank-react\\ebank-react\\public\\profile-picture\\";

    @Override
    public User getCurrentLoggedInUser() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null) {
            throw new NotFoundException("User is not authenticated");
        }

        User myUser = userRepo.findByEmail(auth.getName()).orElseThrow(
                () -> new NotFoundException("User Not Found")
        );

        return myUser;
    }

    @Override
    public Response<UserDTO> getMyProfile() {
        User myUser = getCurrentLoggedInUser();
        UserDTO userDTO = modelMapper.map(myUser, UserDTO.class);
        return Response
                .<UserDTO>builder()
                .StatusCode(HttpStatus.OK.value())
                .message("User Has Been retrieved")
                .data(userDTO)
                .build();
    }

    @Override
    public Response<Page<UserDTO>> getAllUsers(int page, int quantity) {

        Page<User> allUsers = userRepo.findAll(PageRequest.of(page, quantity));
        Page<UserDTO> usersDTO = allUsers.map(user -> modelMapper.map(user, UserDTO.class));

        return Response
                .<Page<UserDTO>>builder()
                .StatusCode(HttpStatus.OK.value())
                .message(" retrieved all users")
                .data(usersDTO)
                .build();
    }

    @Override
    public Response<?> updatePassword(PassUpdateRequest passUpdateRequest) {

        User myUser = getCurrentLoggedInUser();
        String oldPass = passUpdateRequest.getOldPassword();
        String newPass = passUpdateRequest.getNewPassword();

        if (oldPass == null || newPass == null) {
            throw new BadRequestException("Old / New Password are required");
        }

        if (!passwordEncoder.matches(oldPass, myUser.getPassword())) {
            throw new BadRequestException("Old Password is Not Correct");
        }

        myUser.setPassword(passwordEncoder.encode(newPass));
        myUser.setUpdatedAt(LocalDateTime.now());
        userRepo.save(myUser);

        Map<String, Object> vars = new HashMap<>();
        vars.put("name", myUser.getFirstName());

        NotificationDTO updatePassMail = NotificationDTO.builder()
                .recipient(myUser.getEmail())
                .subject("Password Has Been Updated")
                .templateName("updatePass")
                .templateVariables(vars)
                .build();

        notificationService.sendEmail(updatePassMail, myUser);

        return Response
                .builder()
                .StatusCode(HttpStatus.OK.value())
                .message("Password has been changed")
                .build();
    }

    @Override
    public Response<?> uploadProfilePic(MultipartFile file) {

        User myUser = getCurrentLoggedInUser();

        try {
            Path dirPath = Paths.get(uploadDir);

            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }
            if (myUser.getProfilePicUrl() != null && !myUser.getProfilePicUrl().isEmpty()) {

                Path oldPicPath = Paths.get(myUser.getProfilePicUrl());
                if (Files.exists(oldPicPath)) {
                    Files.delete(oldPicPath);
                }
            }

            String orgFileName = file.getOriginalFilename();
            String fileExtension = "";

            if (orgFileName != null && orgFileName.contains(".")) {
                fileExtension = orgFileName.substring(orgFileName.lastIndexOf("."));
            }

            String newFileName = UUID.randomUUID() + fileExtension;

            Path filePath = dirPath.resolve(newFileName);
            Files.copy(file.getInputStream(), filePath);

            // for backend
//            String fileUrl=uploadDir+newFileName;

            // for frontend
            String fileUrl = "profile-picture/" + newFileName;

            myUser.setProfilePicUrl(fileUrl);
            myUser.setUpdatedAt(LocalDateTime.now());

            userRepo.save(myUser);

            return Response.builder()
                    .StatusCode(HttpStatus.OK.value())
                    .message("Profile Picture Uploaded Successfully")
                    .data(fileUrl)
                    .build();

        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    @Override
    public Response<?> uploadProfilePicToS3(MultipartFile file) {

        User myUser = getCurrentLoggedInUser();
        try {

            if (!myUser.getProfilePicUrl().isEmpty() && myUser.getProfilePicUrl() != null) {
                s3Service.deleteFile(myUser.getProfilePicUrl());
            }
            String s3Url = s3Service.uploadFile(file,"profile-pictures");
            myUser.setProfilePicUrl(s3Url);
            userRepo.save(myUser);

            return Response.builder()
                    .StatusCode(HttpStatus.OK.value())
                    .message("Profile Picture Uploaded Successfully")
                    .data(s3Url)
                    .build();

        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Response<?> deleteProfilePicFromS3(String url) {

             User myUser = getCurrentLoggedInUser();
            if (!myUser.getProfilePicUrl().isEmpty() && myUser.getProfilePicUrl() != null) {
                s3Service.deleteFile(myUser.getProfilePicUrl());
                myUser.setProfilePicUrl("");
                userRepo.save(myUser);
            }
            return Response.builder()
                    .StatusCode(HttpStatus.OK.value())
                    .message("Profile Picture Uploaded Successfully")
                    .build();


    }
}

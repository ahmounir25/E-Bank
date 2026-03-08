package com.proj.ebank.users.service;

import com.proj.ebank.response.Response;
import com.proj.ebank.users.dto.PassUpdateRequest;
import com.proj.ebank.users.dto.UserDTO;
import com.proj.ebank.users.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {
    User getCurrentLoggedInUser();

    Response<UserDTO> getMyProfile();

    Response<Page<UserDTO>> getAllUsers(int page, int quantity);

    Response<?> updatePassword(PassUpdateRequest passUpdateRequest);

    Response<?> uploadProfilePic(MultipartFile file);

    Response<?> uploadProfilePicToS3(MultipartFile file);

    Response<?> deleteProfilePicFromS3(String url);

}

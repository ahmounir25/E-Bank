package com.proj.ebank.users.controller;

import com.proj.ebank.response.Response;
import com.proj.ebank.users.dto.PassUpdateRequest;
import com.proj.ebank.users.dto.UserDTO;
import com.proj.ebank.users.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response<Page<UserDTO>>> getAllUsers(
            @RequestParam(defaultValue = "0") int page
            , @RequestParam(defaultValue = "20") int size
    ) {

        return ResponseEntity.ok(userService.getAllUsers(page, size));
    }

    @GetMapping("/profile")
    public ResponseEntity<Response<UserDTO>> getUserProfile(){
        return ResponseEntity.ok(userService.getMyProfile());
    }

    @PutMapping("/update-pass")
    public ResponseEntity<Response<?>> updatePass(@RequestBody @Valid PassUpdateRequest passUpdateRequest){
        return ResponseEntity.ok(userService.updatePassword(passUpdateRequest));
    }

    @PutMapping("/profile-picture")
    public ResponseEntity<Response<?>>uploadProfilePic(@RequestParam("file")MultipartFile file){

        return ResponseEntity.ok(userService.uploadProfilePicToS3(file));
    }

    @DeleteMapping("/profile-picture")
    public ResponseEntity<Response<?>>deleteProfilePic(@RequestParam("url")String url){

        return ResponseEntity.ok(userService.deleteProfilePicFromS3(url));
    }

}

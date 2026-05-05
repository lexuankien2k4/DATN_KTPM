package com.Nhom7.DACN_KTPM.controller;


import com.Nhom7.DACN_KTPM.dto.request.ApiRequest;
import com.Nhom7.DACN_KTPM.dto.request.UserCreationRequest;
import com.Nhom7.DACN_KTPM.dto.request.UserUpdateRequest;
import com.Nhom7.DACN_KTPM.dto.response.UserResponse;
import com.Nhom7.DACN_KTPM.service.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserController {
    UserService userService;

    @PostMapping
    ApiRequest<UserResponse> createUser(@RequestBody @Valid UserCreationRequest request) {
        return ApiRequest.<UserResponse>builder()
                .result(userService.createUser(request))
                .build();
    }

    @GetMapping
    ApiRequest<List<UserResponse>> getUsers() {
        return ApiRequest.<List<UserResponse>>builder()
                .result(userService.getUsers())
                .build();
    }

    @GetMapping("/{userId}")
    ApiRequest<UserResponse> getUser(@PathVariable("userId") String userId) {
        return ApiRequest.<UserResponse>builder()
                .result(userService.getUser(userId))
                .build();
    }

    @GetMapping("/my-info")
    ApiRequest<UserResponse> getMyInfo() {
        return ApiRequest.<UserResponse>builder()
                .result(userService.getMyInfo())
                .build();
    }

    @DeleteMapping("/{userId}")
    ApiRequest<String> deleteUser(@PathVariable String userId) {
        userService.deleteUser(userId);
        return ApiRequest.<String>builder().result("User has been deleted").build();
    }

    @PutMapping("/{userId}")
    ApiRequest<UserResponse> updateUser(@PathVariable String userId, @RequestBody UserUpdateRequest request) {
        return ApiRequest.<UserResponse>builder()
                .result(userService.updateUser(userId, request))
                .build();
    }
}

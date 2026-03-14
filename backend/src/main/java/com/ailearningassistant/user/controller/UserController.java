package com.ailearningassistant.user.controller;

import com.ailearningassistant.common.api.Result;
import com.ailearningassistant.user.service.UserService;
import com.ailearningassistant.user.vo.UserProfileVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/current")
    public Result<UserProfileVO> getCurrentUser() {
        return Result.success(userService.getCurrentUserProfile());
    }
}

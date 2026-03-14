package com.ailearningassistant.user.service;

import com.ailearningassistant.auth.dto.RegisterRequest;
import com.ailearningassistant.user.entity.SysUser;
import com.ailearningassistant.user.vo.UserProfileVO;
import com.baomidou.mybatisplus.extension.service.IService;

public interface UserService extends IService<SysUser> {

    SysUser getByUsername(String username);

    SysUser register(RegisterRequest request);

    void updateLastLoginTime(Long userId);

    UserProfileVO getCurrentUserProfile();
}

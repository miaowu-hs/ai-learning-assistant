package com.ailearningassistant.user.service.impl;

import com.ailearningassistant.auth.dto.RegisterRequest;
import com.ailearningassistant.common.enums.StatusCode;
import com.ailearningassistant.common.exception.BusinessException;
import com.ailearningassistant.common.util.SecurityUtils;
import com.ailearningassistant.user.entity.SysUser;
import com.ailearningassistant.user.mapper.SysUserMapper;
import com.ailearningassistant.user.service.UserService;
import com.ailearningassistant.user.vo.UserProfileVO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements UserService {

    private final PasswordEncoder passwordEncoder;

    @Override
    public SysUser getByUsername(String username) {
        if (!StringUtils.hasText(username)) {
            return null;
        }
        return lambdaQuery()
                .eq(SysUser::getUsername, username.trim())
                .one();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysUser register(RegisterRequest request) {
        String username = request.getUsername().trim();
        if (getByUsername(username) != null) {
            throw new BusinessException(StatusCode.USERNAME_ALREADY_EXISTS);
        }

        LocalDateTime now = LocalDateTime.now();
        SysUser user = SysUser.builder()
                .username(username)
                .password(passwordEncoder.encode(request.getPassword()))
                .nickname(StringUtils.hasText(request.getNickname()) ? request.getNickname().trim() : username)
                .email(StringUtils.hasText(request.getEmail()) ? request.getEmail().trim() : null)
                .phone(StringUtils.hasText(request.getPhone()) ? request.getPhone().trim() : null)
                .status(1)
                .deleted(0)
                .createTime(now)
                .updateTime(now)
                .build();

        boolean saved = save(user);
        if (!saved) {
            throw new BusinessException(StatusCode.SYSTEM_ERROR);
        }
        return user;
    }

    @Override
    public void updateLastLoginTime(Long userId) {
        LocalDateTime now = LocalDateTime.now();
        boolean updated = lambdaUpdate()
                .eq(SysUser::getId, userId)
                .set(SysUser::getLastLoginTime, now)
                .set(SysUser::getUpdateTime, now)
                .update();
        if (!updated) {
            throw new BusinessException(StatusCode.USER_NOT_FOUND);
        }
    }

    @Override
    public UserProfileVO getCurrentUserProfile() {
        Long userId = SecurityUtils.getUserId();
        SysUser user = getById(userId);
        if (user == null) {
            throw new BusinessException(StatusCode.USER_NOT_FOUND);
        }
        return UserProfileVO.fromEntity(user);
    }
}

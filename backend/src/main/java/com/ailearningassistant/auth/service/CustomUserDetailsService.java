package com.ailearningassistant.auth.service;

import com.ailearningassistant.auth.model.LoginUser;
import com.ailearningassistant.user.entity.SysUser;
import com.ailearningassistant.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return loadLoginUserByUsername(username);
    }

    public LoginUser loadLoginUserByUsername(String username) {
        SysUser user = userService.getByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return new LoginUser(user);
    }
}

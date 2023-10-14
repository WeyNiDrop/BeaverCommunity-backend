package com.macro.mall.tiny.modules.ums.service.impl;

import com.macro.mall.tiny.domain.AdminUserDetails;
import com.macro.mall.tiny.modules.ums.model.UmsAdmin;
import com.macro.mall.tiny.modules.ums.service.GlobalUserService;
import com.macro.mall.tiny.modules.ums.service.UmsAdminCacheService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class GlobalUserServiceImpl implements GlobalUserService {
    private final UmsAdminCacheService adminCacheService;

    @Override
    public UmsAdmin currentUser() {
        AdminUserDetails userInfo = null;
        // 获取用户认证信息对象。
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // 认证信息可能为空，因此需要进行判断。
        if (authentication != null) {
            Object principal = authentication.getPrincipal();
            System.out.println(principal.toString());
            if (principal instanceof AdminUserDetails) {
                userInfo = (AdminUserDetails) principal;
                return adminCacheService.getAdmin(userInfo.getUsername());
            }
        }
        return null;
    }

    @Override
    public String currentUserAccount() {
        UmsAdmin user = currentUser();
        return user.getUsername();
    }


}

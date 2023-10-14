package com.macro.mall.tiny.modules.ums.service;

import com.macro.mall.tiny.modules.ums.model.UmsAdmin;

public interface GlobalUserService {
    UmsAdmin currentUser();

    String currentUserAccount();
}

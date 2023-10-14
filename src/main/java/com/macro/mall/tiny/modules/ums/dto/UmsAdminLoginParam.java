package com.macro.mall.tiny.modules.ums.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

/**
 * 用户登录参数
 * Created by macro on 2018/4/26.
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class UmsAdminLoginParam {
    @NotEmpty
    @Email
    @ApiModelProperty(value = "用户名",required = true)
    private String username;

    @NotEmpty
    @Size(min = 6, max = 20)
    @ApiModelProperty(value = "密码",required = true)
    private String password;

    @NotEmpty
    @ApiModelProperty(value = "验证码",required = true)
    private String captchaCode;

    @NotEmpty
    @ApiModelProperty(value = "验证码所属的key",required = true)
    private String captchaKey;

    @ApiModelProperty(value = "邀请码")
    private String inviteCode;
}

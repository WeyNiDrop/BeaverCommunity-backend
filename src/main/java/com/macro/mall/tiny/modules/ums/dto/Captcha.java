package com.macro.mall.tiny.modules.ums.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class Captcha {
    @ApiModelProperty(value = "验证码key")
    private String key;

    @ApiModelProperty(value = "验证码图片base64")
    private String image64;
}

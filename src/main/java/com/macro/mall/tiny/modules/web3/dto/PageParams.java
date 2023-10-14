package com.macro.mall.tiny.modules.web3.dto;

import lombok.Data;

@Data
public class PageParams {
    private Integer pageSize = 10;
    private Integer pageNum = 1;
}

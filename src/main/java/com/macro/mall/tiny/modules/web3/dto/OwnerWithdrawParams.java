package com.macro.mall.tiny.modules.web3.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class OwnerWithdrawParams extends PageParams{
    @NotNull
    private Long round;

    @Size(max = 42)
    @NotEmpty
    private String owner;
}

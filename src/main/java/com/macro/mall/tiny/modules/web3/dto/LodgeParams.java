package com.macro.mall.tiny.modules.web3.dto;

import com.macro.mall.tiny.modules.web3.model.Web3Lodge;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class LodgeParams extends PageParams{
    @NotNull
    private Long round;

    @Size(max = 42)
    private String creator;

    @Size(max = 42)
    private String owner;

    @ApiModelProperty("options: id, competition_votes")
    private String orderBy = Web3Lodge.ORDER_BY_ID;

    @ApiModelProperty("options: 0 desc; 1 asc; 2 random")
    private Integer orderType = 0;
}

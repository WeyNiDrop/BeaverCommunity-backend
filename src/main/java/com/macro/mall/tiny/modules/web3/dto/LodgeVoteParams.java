package com.macro.mall.tiny.modules.web3.dto;

import com.macro.mall.tiny.modules.web3.model.Web3Lodge;
import com.macro.mall.tiny.modules.web3.model.Web3LodgeVotes;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class LodgeVoteParams extends PageParams{
    @NotNull
    private Long lodgeId;

    @Size(max = 42)
    private String sponsor;

    private boolean filterComments = false;

    @ApiModelProperty("options: id, comment_index")
    private String orderBy = Web3LodgeVotes.ORDER_BY_ID;

    @ApiModelProperty("options: 0 desc; 1 asc; 2 random")
    private Integer orderType = 0;
}

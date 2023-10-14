package com.macro.mall.tiny.modules.web3.model;

import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 *
 * </p>
 *
 * @author evan
 * @since 2023-10-14
 */
@Getter
@Setter
@TableName("web3_lodge")
@ApiModel(value = "Web3Lodge", description = "")
public class Web3Lodge implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String ORDER_BY_COMPETITION_VOTES  = "competition_votes";
    public static final String ORDER_BY_ID  = "id";

    private Long id;

    private String creator;

    private String content;

    private Boolean requireSubscribe;

    private Integer round;

    private String owner;

    private Long commentCount;

    private Long competitionVotes;

    private Long extraVotes;

    private Long createTime;

}

package com.macro.mall.tiny.modules.web3.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
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
 * @since 2023-10-15
 */
@Getter
@Setter
@TableName("web3_fans_votes")
@ApiModel(value = "Web3FansVotes对象", description = "")
public class Web3FansVotes implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long lodgeId;

    private String sponsor;

    private Integer competitionVotes;

    private Integer extraVotes;

    private Boolean isWithdraw;


}

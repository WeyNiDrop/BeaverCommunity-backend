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
 * @since 2023-10-14
 */
@Getter
@Setter
@TableName("web3_lodge_votes")
@ApiModel(value = "Web3LodgeVotes对象", description = "")
public class Web3LodgeVotes implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String ORDER_BY_COMMENT_INDEX  = "comment_index";
    public static final String ORDER_BY_ID  = "id";

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long lodgeId;

    private String ammount;

    private String sponsor;

    private Boolean inCompetition;

    private Long createTime;

    private Long commentIndex;

    private String comment;

    private Long replyComment;


}

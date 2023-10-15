package com.macro.mall.tiny.modules.web3.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.macro.mall.tiny.modules.web3.dto.SponsorWithdrawParams;
import com.macro.mall.tiny.modules.web3.model.Web3Lodge;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author evan
 * @since 2023-10-14
 */
public interface Web3LodgeMapper extends BaseMapper<Web3Lodge> {

    Page<Web3Lodge> sponsorWithdrawAble(IPage<?> page, @Param("params") SponsorWithdrawParams params);
}

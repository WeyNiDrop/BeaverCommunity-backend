package com.macro.mall.tiny.modules.web3.service;

import com.macro.mall.tiny.common.api.CommonPage;
import com.macro.mall.tiny.common.api.CommonResult;
import com.macro.mall.tiny.modules.web3.dto.LodgeParams;
import com.macro.mall.tiny.modules.web3.dto.LodgeVoteParams;
import com.macro.mall.tiny.modules.web3.model.Web3Lodge;
import com.baomidou.mybatisplus.extension.service.IService;
import com.macro.mall.tiny.modules.web3.model.Web3LodgeVotes;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author evan
 * @since 2023-10-14
 */
public interface Web3LodgeService extends IService<Web3Lodge> {
    CommonPage<Web3Lodge> lodges(LodgeParams params);

    CommonPage<Web3LodgeVotes> votes(LodgeVoteParams params);
}

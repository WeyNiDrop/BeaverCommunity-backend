package com.macro.mall.tiny.modules.web3.service;

import com.macro.mall.tiny.modules.web3.model.Web3FansVotes;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author evan
 * @since 2023-10-15
 */
public interface Web3FansVotesService extends IService<Web3FansVotes> {
    Web3FansVotes getByLodgeAndFans(Long lodge, String sponsor);
}

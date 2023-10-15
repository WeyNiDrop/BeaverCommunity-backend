package com.macro.mall.tiny.modules.web3.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.macro.mall.tiny.modules.web3.model.Web3LodgeVotes;
import com.macro.mall.tiny.modules.web3.mapper.Web3LodgeVotesMapper;
import com.macro.mall.tiny.modules.web3.service.Web3LodgeVotesService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author evan
 * @since 2023-10-14
 */
@Service
public class Web3LodgeVotesServiceImpl extends ServiceImpl<Web3LodgeVotesMapper, Web3LodgeVotes> implements Web3LodgeVotesService {

    @Override
    public Web3LodgeVotes getByTX(String tx) {
        LambdaQueryWrapper<Web3LodgeVotes> wrapper =  new LambdaQueryWrapper<>();
        wrapper.eq(Web3LodgeVotes:: getTx, tx);
        return getOne(wrapper);
    }
}

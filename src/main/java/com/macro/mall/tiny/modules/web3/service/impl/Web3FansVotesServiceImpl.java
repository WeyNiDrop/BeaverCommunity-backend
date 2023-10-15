package com.macro.mall.tiny.modules.web3.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.macro.mall.tiny.modules.web3.model.Web3FansVotes;
import com.macro.mall.tiny.modules.web3.mapper.Web3FansVotesMapper;
import com.macro.mall.tiny.modules.web3.service.Web3FansVotesService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author evan
 * @since 2023-10-15
 */
@Service
public class Web3FansVotesServiceImpl extends ServiceImpl<Web3FansVotesMapper, Web3FansVotes> implements Web3FansVotesService {

    @Override
    public Web3FansVotes getByLodgeAndFans(Long lodge, String sponsor) {
        LambdaQueryWrapper<Web3FansVotes> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Web3FansVotes::getLodgeId, lodge);
        wrapper.eq(Web3FansVotes::getSponsor, sponsor);
        return getOne(wrapper);
    }
}

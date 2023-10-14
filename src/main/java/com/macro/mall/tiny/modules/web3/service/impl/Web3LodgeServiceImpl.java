package com.macro.mall.tiny.modules.web3.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.macro.mall.tiny.common.api.CommonPage;
import com.macro.mall.tiny.modules.web3.dto.LodgeParams;
import com.macro.mall.tiny.modules.web3.dto.LodgeVoteParams;
import com.macro.mall.tiny.modules.web3.model.Web3Lodge;
import com.macro.mall.tiny.modules.web3.mapper.Web3LodgeMapper;
import com.macro.mall.tiny.modules.web3.model.Web3LodgeVotes;
import com.macro.mall.tiny.modules.web3.service.Web3LodgeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.macro.mall.tiny.modules.web3.service.Web3LodgeVotesService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author evan
 * @since 2023-10-14
 */
@Service
public class Web3LodgeServiceImpl extends ServiceImpl<Web3LodgeMapper, Web3Lodge> implements Web3LodgeService {

    @Resource
    private Web3LodgeVotesService votesService;

    @Override
    public CommonPage<Web3Lodge> lodges(LodgeParams params) {
        Page<Web3Lodge> page = new Page<>(params.getPageNum(), params.getPageSize());

        QueryWrapper<Web3Lodge> wrapper = new QueryWrapper<>();
        wrapper.eq("round", params.getRound());
        if (StringUtils.isNotBlank(params.getCreator())){
            wrapper.eq("creator", params.getCreator());
        }
        if (StringUtils.isNotBlank(params.getOwner())){
            wrapper.eq("owner", params.getOwner());
        }
        boolean isDesc = params.getOrderType() == 0;
        String orderField = params.getOrderBy().equals(Web3Lodge.ORDER_BY_COMPETITION_VOTES)? Web3Lodge.ORDER_BY_COMPETITION_VOTES: Web3Lodge.ORDER_BY_ID;
        if (isDesc){
            wrapper.orderByDesc(orderField);
        }else {
            wrapper.orderByAsc(orderField);
        }

        Page<Web3Lodge> result = page(page, wrapper);
        return CommonPage.restPage(result);
    }

    @Override
    public CommonPage<Web3LodgeVotes> votes(LodgeVoteParams params) {
        Page<Web3LodgeVotes> page = new Page<>(params.getPageNum(), params.getPageSize());

        QueryWrapper<Web3LodgeVotes> wrapper = new QueryWrapper<>();
        wrapper.eq("lodge_id", params.getLodgeId());
        if (StringUtils.isNotBlank(params.getSponsor())){
            wrapper.eq("sponsor", params.getSponsor());
        }
        if (params.isFilterComments()){
            wrapper.ne("comment_index", 0);
        }
        boolean isDesc = params.getOrderType() == 0;
        String orderField = params.getOrderBy().equals(Web3LodgeVotes.ORDER_BY_COMMENT_INDEX)? Web3LodgeVotes.ORDER_BY_COMMENT_INDEX: Web3LodgeVotes.ORDER_BY_ID;
        if (isDesc){
            wrapper.orderByDesc(orderField);
        }else {
            wrapper.orderByAsc(orderField);
        }

        Page<Web3LodgeVotes> result = votesService.page(page, wrapper);
        return CommonPage.restPage(result);
    }
}

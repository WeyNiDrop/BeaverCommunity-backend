package com.macro.mall.tiny.modules.web3.controller;


import com.macro.mall.tiny.common.api.CommonPage;
import com.macro.mall.tiny.common.api.CommonResult;
import com.macro.mall.tiny.modules.web3.dto.LodgeParams;
import com.macro.mall.tiny.modules.web3.dto.LodgeVoteParams;
import com.macro.mall.tiny.modules.web3.dto.OwnerWithdrawParams;
import com.macro.mall.tiny.modules.web3.dto.SponsorWithdrawParams;
import com.macro.mall.tiny.modules.web3.model.Web3Lodge;
import com.macro.mall.tiny.modules.web3.model.Web3LodgeVotes;
import com.macro.mall.tiny.modules.web3.service.Web3LodgeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author evan
 * @since 2023-10-14
 */
@RestController
@RequestMapping("/web3/community")
@Api(tags = "Web3CommunityController")
@Tag(name = "Web3CommunityController", description = "Beaver community apis")
@Slf4j
public class Web3CommunityController {

    @Resource
    private Web3LodgeService lodgeService;


    @ApiOperation(value = "Get lodgeList")
    @PostMapping(value = "/lodges")
    @ResponseBody
    public CommonResult<CommonPage<Web3Lodge>> lodges(@Validated @RequestBody LodgeParams params){
        return CommonResult.success(lodgeService.lodges(params));
    }


    @ApiOperation(value = "Get votes list")
    @PostMapping(value = "/votes")
    @ResponseBody
    public CommonResult<CommonPage<Web3LodgeVotes>> votes(@Validated @RequestBody LodgeVoteParams params){
        return CommonResult.success(lodgeService.votes(params));
    }

    @ApiOperation(value = "Get owner's withdrawAble list")
    @PostMapping(value = "/owner/withdrawAble")
    @ResponseBody
    public CommonResult<CommonPage<Web3Lodge>> ownerWithdrawAble(@Validated @RequestBody OwnerWithdrawParams params){
        return CommonResult.success(lodgeService.ownerWithdrawAble(params));
    }

    @ApiOperation(value = "Get sponsor's withdrawAble list")
    @PostMapping(value = "/sponsor/withdrawAble")
    @ResponseBody
    public CommonResult<CommonPage<Web3Lodge>> sponsorWithdrawAble(@Validated @RequestBody SponsorWithdrawParams params){

        return CommonResult.success(lodgeService.sponsorWithdrawAble(params));
    }
}


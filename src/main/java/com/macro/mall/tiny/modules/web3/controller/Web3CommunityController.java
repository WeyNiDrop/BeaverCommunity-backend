package com.macro.mall.tiny.modules.web3.controller;


import com.macro.mall.tiny.common.api.CommonPage;
import com.macro.mall.tiny.common.api.CommonResult;
import com.macro.mall.tiny.modules.web3.dto.LodgeParams;
import com.macro.mall.tiny.modules.web3.dto.LodgeVoteParams;
import com.macro.mall.tiny.modules.web3.model.Web3Lodge;
import com.macro.mall.tiny.modules.web3.model.Web3LodgeVotes;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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

    @ApiOperation(value = "Get lodgeList")
    @PostMapping(value = "/lodges")
    @ResponseBody
    public CommonResult<CommonPage<Web3Lodge>> lodges(@Validated @RequestBody LodgeParams params){
        return null;
    }


    @ApiOperation(value = "Get votes list")
    @PostMapping(value = "/votes")
    @ResponseBody
    public CommonResult<CommonPage<Web3LodgeVotes>> votes(@Validated @RequestBody LodgeVoteParams params){
        return null;
    }
}


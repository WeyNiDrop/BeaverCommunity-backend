package com.macro.mall.tiny.modules.web3.controller;


import com.macro.mall.tiny.common.api.CommonResult;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * <p>
 *  upload file
 * </p>
 *
 * @author evan
 * @since 2023-10-14
 */
@RestController
@RequestMapping("/web3")
public class Web3UploadController {

    @Value("${system.sysDomain}")
    private String sysDomain;

    @ApiOperation(value = "upload")
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult<String> buildInscriptionData(@RequestParam("file") MultipartFile file) {
        //save to disk
        int doIndex = file.getOriginalFilename().lastIndexOf(".");
        String fileExtension = file.getOriginalFilename().substring(doIndex);
        File local  = null;
        String name = null;
        do {
            name = System.currentTimeMillis() + fileExtension;
            local = new File(System.getProperty("user.dir") + "/uploads/" + name);
        }while (local.exists());

        try {
            file.transferTo(local);
        } catch (IOException e) {
            e.printStackTrace();
            return CommonResult.failed();
        }
        return CommonResult.success(sysDomain + "/uploads/" + name);
    }
}


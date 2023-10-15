package com.macro.mall.tiny;

import com.macro.mall.tiny.modules.web3.config.Web3Configure;
import com.macro.mall.tiny.tasks.EthTask;
import jnr.ffi.annotations.IgnoreError;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.web3j.crypto.Hash;
import org.web3j.utils.Numeric;

import javax.annotation.Resource;

@SpringBootTest
@Slf4j
@IgnoreError
public class BeaverCommunityTest {

    @Resource
    private EthTask ethTask;

    @Resource
    private Web3Configure web3Configure;

    @Test
    public void filterCreateLogs(){
        ethTask.handleBlock(web3Configure.getWeb3j(), 1609213, 1609215);
    }

}

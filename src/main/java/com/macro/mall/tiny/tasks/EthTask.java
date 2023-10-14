package com.macro.mall.tiny.tasks;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.macro.mall.tiny.common.service.RedisService;
import com.macro.mall.tiny.modules.web3.config.Web3Configure;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.web3j.abi.EventValues;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.*;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.response.*;
import org.web3j.tx.Contract;

import javax.annotation.Resource;
import java.io.IOException;
import java.math.BigInteger;
import java.util.*;


@Component
@Slf4j
@EnableScheduling
public class EthTask {

    @Resource
    private Web3Configure web3Configure;

    @Resource
    private RedisService redisService;

    private static final String CONFIG_CURRENT_BLOCK_HEIGHT = "current_block_height";
    public static final String ADDRESS_ZERO = "0x0000000000000000000000000000000000000000";
    public static final String TRANSACTION_SUCCESS = "0x1";
    public static final String PREFIX_INSCRIPTION = "0x646174613a";

    public static long currentBlock = 0L;

    public static int blockError = 0;

    @Scheduled(fixedRate = 12000)
    public void batchBlockListener() throws IOException {
        if (!web3Configure.getListenerSwitch()){
            return;
        }
        String str = (String) redisService.get(CONFIG_CURRENT_BLOCK_HEIGHT);
        currentBlock = web3Configure.getWeb3j().ethBlockNumber().send().getBlockNumber().longValue();
        long index = str == null? currentBlock - 1: Long.parseLong(str);

        long end = index + 100;
        if (end > currentBlock) end = currentBlock;
        if (index >= end){
            return;
        }

        if (handleBlock(web3Configure.getWeb3j(), index, end)) {
            redisService.set(CONFIG_CURRENT_BLOCK_HEIGHT, end + "");
        } else {
            log.error("区块解析失败：{}", index);
        }
    }

    public boolean handleBlock(Web3j web3j, long start, long end) {
        blockError = 0;
        log.info("ETH block：from {}, to {}", start, end);
        try {
            // TODO
            // 创建一个 EthFilter 实例
            org.web3j.protocol.core.methods.request.EthFilter filter = new org.web3j.protocol.core.methods.request.EthFilter(DefaultBlockParameter.valueOf(BigInteger.valueOf(start)),
                    DefaultBlockParameter.valueOf(BigInteger.valueOf(end)), Collections.singletonList(""));
            //TODO
            filter.addOptionalTopics("EVENT_ETHS_MINTED");

            EthLog ethLog = web3j.ethGetLogs(filter).send();
            if (ethLog.getError() != null){
                return false;
            }
            List<EthLog.LogResult> logList = ethLog.getLogs();
            for (EthLog.LogResult<Log> logResult : logList) {
                Log evntLog = logResult.get();
                Event event = new Event("EthsSouvenirMinted", Arrays.asList(
                        new TypeReference<Uint256>(true) {},
                        new TypeReference<Address>(true) {},
                        new TypeReference<Utf8String>(false) {}));
                EventValues eventValues = Contract.staticExtractEventParameters(event, evntLog);

                Long id = ((Uint256) eventValues.getIndexedValues().get(0)).getValue().longValue();
                String address = eventValues.getIndexedValues().get(1).getValue().toString();
                String contentHash = eventValues.getNonIndexedValues().get(0).getValue().toString();
//                LambdaUpdateWrapper<Web3EthsFinal> wrapper = new LambdaUpdateWrapper<>();
//                wrapper.set(Web3EthsFinal::getMinted, 1);
//                wrapper.eq(Web3EthsFinal::getContentHash, contentHash);
//                ethsFinalService.update(wrapper);
                log.info(" id:{}, toAddress:{}, contentHash:{}", id, address, contentHash);
            }
            return true;
        } catch (IOException e) {
            log.error("block filter fail:", e);
            e.printStackTrace();
            return false;
        }
    }


}

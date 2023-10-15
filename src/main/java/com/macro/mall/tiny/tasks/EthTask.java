package com.macro.mall.tiny.tasks;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.macro.mall.tiny.common.service.RedisService;
import com.macro.mall.tiny.modules.web3.config.Web3Configure;
import com.macro.mall.tiny.modules.web3.contracts.BeaverCommunityEvents;
import com.macro.mall.tiny.modules.web3.model.Web3FansVotes;
import com.macro.mall.tiny.modules.web3.model.Web3Lodge;
import com.macro.mall.tiny.modules.web3.model.Web3LodgeVotes;
import com.macro.mall.tiny.modules.web3.service.Web3FansVotesService;
import com.macro.mall.tiny.modules.web3.service.Web3LodgeService;
import com.macro.mall.tiny.modules.web3.service.Web3LodgeVotesService;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.web3j.abi.EventValues;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.*;
import org.web3j.abi.datatypes.generated.Uint128;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint64;
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

    @Resource
    private Web3LodgeVotesService votesService;

    @Resource
    private Web3LodgeService lodgeService;

    @Resource
    private Web3FansVotesService fansVotesService;

    private static final String CONFIG_CURRENT_BLOCK_HEIGHT = "current_block_height";

    public static long currentBlock = 0L;

    public static int blockError = 0;

    @Scheduled(fixedRate = 3000)
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

            // 创建一个 EthFilter 实例
            org.web3j.protocol.core.methods.request.EthFilter filter = new org.web3j.protocol.core.methods.request.EthFilter(DefaultBlockParameter.valueOf(BigInteger.valueOf(start)),
                    DefaultBlockParameter.valueOf(BigInteger.valueOf(end)), Collections.singletonList(web3Configure.getContracts().split(",")[0]));

            filter.addOptionalTopics(BeaverCommunityEvents.NewLodge, BeaverCommunityEvents.VoteInCompetition, BeaverCommunityEvents.VoteOutCompetition,
                    BeaverCommunityEvents.NewComment, BeaverCommunityEvents.WithdrawRewards, BeaverCommunityEvents.BatchWithdrawRewards,
                    BeaverCommunityEvents.WithdrawRoyalties, BeaverCommunityEvents.BatchWithdrawRoyalties );

            EthLog ethLog = web3j.ethGetLogs(filter).send();
            if (ethLog.getError() != null){
                return false;
            }
            List<EthLog.LogResult> logList = ethLog.getLogs();
            for (EthLog.LogResult<Log> logResult : logList) {
                Log eventLog = logResult.get();
                switch (eventLog.getTopics().get(0)){
                    case BeaverCommunityEvents.NewLodge:
                        newLodge(eventLog);
                        break;
                    case BeaverCommunityEvents.VoteInCompetition:
                        voteInCompetition(eventLog);
                        break;
                    case BeaverCommunityEvents.VoteOutCompetition:
                        voteOutCompetition(eventLog);
                        break;
                    case BeaverCommunityEvents.NewComment:
                        newComment(eventLog);
                        break;
                    case BeaverCommunityEvents.WithdrawRewards:
                        withdrawRewards(eventLog);
                        break;
                    case BeaverCommunityEvents.BatchWithdrawRewards:
                        batchWithdrawRewards(eventLog);
                        break;
                    case BeaverCommunityEvents.WithdrawRoyalties:
                        withdrawRoyalties(eventLog);
                        break;
                    case BeaverCommunityEvents.BatchWithdrawRoyalties:
                        batchWithdrawRoyalties(eventLog);
                        break;
                }

            }
            return true;
        } catch (IOException e) {
            log.error("block filter fail:", e);
            e.printStackTrace();
            return false;
        }
    }

    public void batchWithdrawRoyalties(Log eventLog){
        Event batchWithdrawRoyalties = new Event("BatchWithdrawRoyalties", Arrays.asList(
                new TypeReference<Uint256>(true) {},
                new TypeReference<Address>(true) {},
                new TypeReference<DynamicArray<Uint256>>(false) {}
        ));

        EventValues values = Contract.staticExtractEventParameters(batchWithdrawRoyalties, eventLog);
        List<Uint256> lodges = ((DynamicArray<Uint256>) values.getNonIndexedValues().get(0)).getValue();
        List<Long> lodgeList = new ArrayList<>();
        for (Uint256 lodge : lodges) {
            lodgeList.add(lodge.getValue().longValue());
        }

        LambdaUpdateWrapper<Web3Lodge> wrapper = new LambdaUpdateWrapper<>();
        wrapper.set(Web3Lodge::getIsWithdraw, 1).in(Web3Lodge::getId, lodgeList);
        lodgeService.update(wrapper);
    }

    public void withdrawRoyalties(Log eventLog){
        Event withdrawRoyalties = new Event("WithdrawRoyalties", Arrays.asList(
                new TypeReference<Uint256>(true) {},
                new TypeReference<Address>(true) {},
                new TypeReference<Uint256>(false) {}
        ));
        EventValues values = Contract.staticExtractEventParameters(withdrawRoyalties, eventLog);

        Long lodge = ((Uint256) values.getIndexedValues().get(0)).getValue().longValue();

        LambdaUpdateWrapper<Web3Lodge> wrapper = new LambdaUpdateWrapper<>();
        wrapper.set(Web3Lodge::getIsWithdraw, 1).eq(Web3Lodge::getId, lodge);
        lodgeService.update(wrapper);
    }


    public void batchWithdrawRewards(Log eventLog){
        Event batchWithdrawRewards = new Event("BatchWithdrawRewards", Arrays.asList(
                new TypeReference<Uint256>(true) {},
                new TypeReference<Address>(true) {},
                new TypeReference<DynamicArray<Uint256>>(false) {}
        ));
        EventValues values = Contract.staticExtractEventParameters(batchWithdrawRewards, eventLog);
        List<Uint256> lodges = ((DynamicArray<Uint256>) values.getNonIndexedValues().get(0)).getValue();
        List<Long> lodgeList = new ArrayList<>();
        for (Uint256 lodge : lodges) {
            lodgeList.add(lodge.getValue().longValue());
        }
        String to = values.getIndexedValues().get(0).getValue().toString();
        LambdaUpdateWrapper<Web3FansVotes> wrapper = new LambdaUpdateWrapper<>();
        wrapper.in(Web3FansVotes::getLodgeId, lodgeList).eq(Web3FansVotes::getSponsor, to)
                .set(Web3FansVotes::getIsWithdraw, 1);
        fansVotesService.update(wrapper);
    }

    public void withdrawRewards(Log eventLog){
        Event withdrawRewards = new Event("WithdrawRewards", Arrays.asList(
                new TypeReference<Uint256>(true) {},
                new TypeReference<Address>(true) {},
                new TypeReference<Uint256>(false) {}
        ));
        EventValues values = Contract.staticExtractEventParameters(withdrawRewards, eventLog);
        Long lodge = ((Uint256) values.getIndexedValues().get(0)).getValue().longValue();
        String to = values.getIndexedValues().get(1).getValue().toString();
        LambdaUpdateWrapper<Web3FansVotes> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Web3FansVotes::getLodgeId, lodge).eq(Web3FansVotes::getSponsor, to)
                .set(Web3FansVotes::getIsWithdraw, 1);
        fansVotesService.update(wrapper);
    }

    public void newComment(Log eventLog){
        Event newComment = new Event("NewComment", Arrays.asList(
                new TypeReference<Uint256>(true) {},
                new TypeReference<Address>(true) {},
                new TypeReference<Uint128>(true) {},
                new TypeReference<Uint128>(false) {},
                new TypeReference<Uint64>(false) {},
                new TypeReference<Utf8String>(false) {}
        ));
        Web3LodgeVotes lodgeVotes = votesService.getByTX(eventLog.getTransactionHash());
        if(lodgeVotes != null){
            EventValues values = Contract.staticExtractEventParameters(newComment, eventLog);
            Long commentIndex = ((Uint128) values.getIndexedValues().get(2)).getValue().longValue();
            Long replyComment = ((Uint128) values.getNonIndexedValues().get(0)).getValue().longValue();
            String comment = values.getNonIndexedValues().get(2).getValue().toString();
            lodgeVotes.setCommentIndex(commentIndex);
            lodgeVotes.setReplyComment(replyComment);
            lodgeVotes.setComment(comment);
            votesService.updateById(lodgeVotes);
        }
    }

    @Transactional
    public void newLodge(Log eventLog) throws IOException {
        Event newLodge = new Event("NewLodge", Arrays.asList(
                new TypeReference<Uint256>(true) {},
                new TypeReference<Address>(true) {},
                new TypeReference<Uint64>(true) {},
                new TypeReference<Bool>(false) {},
                new TypeReference<Utf8String>(false) {}
        ));
        EventValues values1 = Contract.staticExtractEventParameters(newLodge, eventLog);
        Long id = ((Uint256) values1.getIndexedValues().get(0)).getValue().longValue();
        String creator = values1.getIndexedValues().get(1).getValue().toString();
        Integer round = ((Uint64) values1.getIndexedValues().get(2)).getValue().intValue();

        boolean requireSubscribe = (boolean) values1.getNonIndexedValues().get(0).getValue();
        String content = values1.getNonIndexedValues().get(1).getValue().toString();
        Web3Lodge web3Lodge = new Web3Lodge();
        web3Lodge.setId(id);
        web3Lodge.setCreator(creator);
        web3Lodge.setOwner(creator);
        web3Lodge.setRound(round);
        web3Lodge.setRequireSubscribe(requireSubscribe);
        web3Lodge.setContent(content);
        web3Lodge.setCreateTime(getBlockTime(eventLog.getBlockNumber()));
        lodgeService.saveOrUpdate(web3Lodge);
    }

    @Transactional
    public void voteInCompetition(Log eventLog) throws IOException {
        Event voteInCompetition = new Event("VoteInCompetition", Arrays.asList(
                new TypeReference<Uint256>(true) {},
                new TypeReference<Address>(true) {},
                new TypeReference<Uint64>(false) {}
        ));
        EventValues values2 = Contract.staticExtractEventParameters(voteInCompetition, eventLog);
        Long lodge = ((Uint256) values2.getIndexedValues().get(0)).getValue().longValue();
        String sponsor = values2.getIndexedValues().get(1).getValue().toString();
        Integer votes = ((Uint64) values2.getNonIndexedValues().get(0)).getValue().intValue();
        Web3LodgeVotes lodgeVotes = votesService.getByTX(eventLog.getTransactionHash());
        if (lodgeVotes == null){
            lodgeVotes  = new Web3LodgeVotes();
            lodgeVotes.setLodgeId(lodge);
            lodgeVotes.setSponsor(sponsor);
            lodgeVotes.setInCompetition(true);
            lodgeVotes.setAmount(votes);
            lodgeVotes.setCreateTime(getBlockTime(eventLog.getBlockNumber()));
            lodgeVotes.setTx(eventLog.getTransactionHash());
            votesService.save(lodgeVotes);
            refreshSponsorInfo(lodgeVotes);
        }
    }

    @Transactional
    public void voteOutCompetition(Log eventLog) throws IOException {
        Event voteOutCompetition = new Event("VoteOutCompetition", Arrays.asList(
                new TypeReference<Uint256>(true) {},
                new TypeReference<Address>(true) {},
                new TypeReference<Uint64>(false) {}
        ));
        EventValues values = Contract.staticExtractEventParameters(voteOutCompetition, eventLog);
        Long lodge = ((Uint256) values.getIndexedValues().get(0)).getValue().longValue();
        String sponsor = values.getIndexedValues().get(1).getValue().toString();
        Integer votes = ((Uint64) values.getNonIndexedValues().get(0)).getValue().intValue();
        Web3LodgeVotes lodgeVotes = votesService.getByTX(eventLog.getTransactionHash());
        if (lodgeVotes == null){
            lodgeVotes  = new Web3LodgeVotes();
            lodgeVotes.setLodgeId(lodge);
            lodgeVotes.setSponsor(sponsor);
            lodgeVotes.setInCompetition(false);
            lodgeVotes.setAmount(votes);
            lodgeVotes.setCreateTime(getBlockTime(eventLog.getBlockNumber()));
            lodgeVotes.setTx(eventLog.getTransactionHash());
            votesService.save(lodgeVotes);
            refreshSponsorInfo(lodgeVotes);
        }
    }

    //sum user votes
    public void refreshSponsorInfo(Web3LodgeVotes lodgeVotes){
        Web3FansVotes fansVotes =  fansVotesService.getByLodgeAndFans(lodgeVotes.getLodgeId(), lodgeVotes.getSponsor());
        if (fansVotes == null){
            fansVotes = new Web3FansVotes();
            fansVotes.setSponsor(lodgeVotes.getSponsor());
            fansVotes.setLodgeId(lodgeVotes.getLodgeId());
            if (lodgeVotes.getInCompetition()){
                fansVotes.setCompetitionVotes(lodgeVotes.getAmount());
            }else {
                fansVotes.setExtraVotes(lodgeVotes.getAmount());
            }
            fansVotesService.save(fansVotes);
        }else {
            LambdaUpdateWrapper<Web3FansVotes> wrapper = new LambdaUpdateWrapper<>();
            if (lodgeVotes.getInCompetition()){
                wrapper.set(Web3FansVotes::getCompetitionVotes, lodgeVotes.getAmount() + fansVotes.getCompetitionVotes());
            }else {
                wrapper.set(Web3FansVotes::getExtraVotes, lodgeVotes.getAmount() + fansVotes.getExtraVotes());
            }
            wrapper.eq(Web3FansVotes::getId, fansVotes.getId());
            fansVotesService.update(wrapper);
        }
    }

    private long getBlockTime(BigInteger blockHeight) throws IOException {
        // 获取区块
        EthBlock.Block block = web3Configure.getWeb3j().ethGetBlockByNumber(DefaultBlockParameter.valueOf(blockHeight), true)
                .send()
                .getBlock();

        // 获取区块的时间戳
        return block.getTimestamp().longValue();
    }
}

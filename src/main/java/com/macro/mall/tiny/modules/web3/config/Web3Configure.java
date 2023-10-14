package com.macro.mall.tiny.modules.web3.config;

import com.macro.mall.tiny.modules.web3.dto.EIP712Domain;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

import java.io.*;
import java.util.List;

@Component
@Slf4j
public class Web3Configure {

    @Value("${system.web3.rpcHost}")
    private String rpcHost;

    @Value("${system.web3.chainId}")
    private Long chainId;

    @Value("${system.web3.listenerSwitch}")
    private Boolean listenerSwitch;

    @Value("${system.web3.privateKey}")
    private String privateKey;

    @Value("${system.web3.domainVersion}")
    private String domainVersion;

    @Value("${system.web3.domain}")
    private String domain;

    private Credentials credentials;

    private Web3j web3j;

    private String marketDomainHash;


    public Credentials getCredentials() {
        if(credentials == null){
            File file = new File(System.getProperty("user.dir") + "/" + privateKey);

            try (FileInputStream inputStream = new FileInputStream(file);
                 BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

                // 读取一行文本
                String content = reader.readLine();
                // 关闭缓冲字符流
                credentials = Credentials.create(content);
            } catch (IOException e) {
                // Handle the exception
                e.printStackTrace();
            }
        }
        return credentials;
    }

    public Boolean getListenerSwitch() {
        return listenerSwitch;
    }

    public String getDomainHash(String contractAddress) {

        EIP712Domain eip712Domain = new EIP712Domain(domain,
                domainVersion,
                chainId,
                contractAddress);
        marketDomainHash = eip712Domain.domainSeparatorV4();
        return marketDomainHash;
    }


    public Long getChainId() {
        return chainId;
    }

    public synchronized Web3j getWeb3j() {
        web3j = Web3j.build(new HttpService(rpcHost));
        return web3j;
    }

    public String getLevelDbFilePath(){
        return "ethDb";
    }
}

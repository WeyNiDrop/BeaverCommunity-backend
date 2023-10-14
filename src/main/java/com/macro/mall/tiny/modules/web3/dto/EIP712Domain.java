package com.macro.mall.tiny.modules.web3.dto;

import lombok.Data;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Hash;

import java.math.BigInteger;
import java.util.Arrays;

@Data
public class EIP712Domain {

    private final byte[] TYPE_HASH = Hash.sha3("EIP712Domain(string name,string version,uint256 chainId,address verifyingContract)".getBytes());

    private Bytes32 name;
    private Bytes32 version;
    private Uint256 chainId;
    private Address verifyingContract;

    public EIP712Domain(String domain, String version, Long chainId, String contractAddress) {
        this.name = new Bytes32(Hash.sha3(domain.getBytes()));
        this.version = new Bytes32(Hash.sha3(version.getBytes()));
        this.chainId = new Uint256(BigInteger.valueOf(chainId));
        this.verifyingContract = new Address(contractAddress);
    }

    public String domainSeparatorV4(){
        return Hash.sha3(FunctionEncoder.encodeConstructor(Arrays.asList(new Bytes32(TYPE_HASH), name, version, chainId, verifyingContract)));
    }
}

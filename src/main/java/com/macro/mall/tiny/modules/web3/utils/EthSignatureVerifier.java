package com.macro.mall.tiny.modules.web3.utils;

import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Bytes2;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.Hash;
import org.web3j.crypto.Keys;
import org.web3j.crypto.Sign;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.security.SignatureException;
import java.util.Arrays;
import java.util.List;

public class EthSignatureVerifier {

    private static final byte[] eip712Bytes = {25, 1};

    /**
     * 验证签名
     *
     * @param signature     验签数据
     * @param content       原文数据
     * @param walletAddress 钱包地址
     * @return 结果
     */
    public static Boolean validate(String signature, String content, String walletAddress) throws SignatureException {
        if (content == null) {
            return false;
        }
        // 原文字节数组
        byte[] msgHash = content.getBytes();
        // 签名数据
        byte[] signatureBytes = Numeric.hexStringToByteArray(signature);
        byte v = signatureBytes[64];
        if (v < 27) {
            v += 27;
        }

        //通过摘要和签名后的数据，还原公钥
        Sign.SignatureData signatureData = new Sign.SignatureData(
                v,
                Arrays.copyOfRange(signatureBytes, 0, 32),
                Arrays.copyOfRange(signatureBytes, 32, 64));
        // 签名的前缀消息到密钥
        BigInteger publicKey = Sign.signedPrefixedMessageToKey(msgHash, signatureData);
        // 得到公钥(私钥对应的钱包地址)
        String parseAddress = "0x" + Keys.getAddress(publicKey);
        // 将钱包地址进行比对
        return parseAddress.equalsIgnoreCase(walletAddress);
    }

    /**
     * 签名
     *
     * @param content    原文信息
     * @param privateKey 私钥
     */
    public static String signPrefixedMessage(String content, String privateKey) {

        // 原文信息字节数组
        byte[] contentHashBytes = content.getBytes();
        // 根据私钥获取凭证对象
        Credentials credentials = Credentials.create(privateKey);
        //
        Sign.SignatureData signMessage = Sign.signPrefixedMessage(contentHashBytes, credentials.getEcKeyPair());

        byte[] r = signMessage.getR();
        byte[] s = signMessage.getS();
        byte[] v = signMessage.getV();

        byte[] signByte = Arrays.copyOf(r, v.length + r.length + s.length);
        System.arraycopy(s, 0, signByte, r.length, s.length);
        System.arraycopy(v, 0, signByte, r.length + s.length, v.length);

        return Numeric.toHexString(signByte);
    }


    public static String signEip712Data(String domainSeparator, List<Type> parameters, Credentials credentials){
        String hash = Hash.sha3(FunctionEncoder.encodeConstructor(parameters));

        String content = FunctionEncoder.encodeConstructorPacked(Arrays.asList(new Bytes2(eip712Bytes),
                new Bytes32(Numeric.hexStringToByteArray(domainSeparator)),
                new Bytes32(Numeric.hexStringToByteArray(hash))));
        String digest = Hash.sha3(content);
        // 使用用户的私钥对消息进行签名(一定要传false参数)
        Sign.SignatureData ethSignTypedData = Sign.signMessage(Numeric.hexStringToByteArray(digest), credentials.getEcKeyPair(), false);
        byte[] r = ethSignTypedData.getR();
        byte[] s = ethSignTypedData.getS();
        byte[] v = ethSignTypedData.getV();

        byte[] signByte = Arrays.copyOf(r, v.length + r.length + s.length);
        System.arraycopy(s, 0, signByte, r.length, s.length);
        System.arraycopy(v, 0, signByte, r.length + s.length, v.length);

        return Numeric.toHexString(signByte);
    }

}

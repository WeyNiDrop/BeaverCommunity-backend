package com.macro.mall.tiny.modules.web3.utils;
import org.bitcoinj.base.Coin;
import org.bitcoinj.core.*;
import org.bitcoinj.crypto.DumpedPrivateKey;
import org.bitcoinj.crypto.ECKey;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptBuilder;

import java.security.SignatureException;
import java.util.Base64;

public class BitcoinSignatureVerifier {

    public static void main(String[] args) throws SignatureException {
        // 比特币交易的公钥、签名和消息
        String compressedPublicKeyHex = "03e450a6e0d54eedf4a9decd8563191b8fa8320c5c46e3af9a17e6b7e97d89d6b4";
        String signatureBase64 = "GymNq1jGMGwmHU19++Zw9of7I3LoQ35i5phuhAhONSA5Mv8VulWKggKU56f1havhXzV62+XAtVcvYTB7UbH8bok=";
        String message = "test";

        // 创建比特币网络参数
//        NetworkParameters params = MainNetParams.get();
        NetworkParameters params = TestNet3Params.get();
        // 将WIF格式的公钥转换为ECKey对象
        // 将Hex格式的压缩公钥转换为ECKey对象
        ECKey compressedPublicKey = ECKey.fromPublicOnly(hexStringToByteArray(compressedPublicKeyHex));
//        ECKey publicKey = DumpedPrivateKey.fromBase58(params, publicKeyWif).getKey();

        // 验证签名
        compressedPublicKey.verifyMessage(message, signatureBase64);

    }

    public static boolean verifyMessage(String compressedPublicKeyHex, String signatureBase64, String message, boolean isMainNet){
        // 创建比特币网络参数
        NetworkParameters params = isMainNet? MainNetParams.get(): TestNet3Params.get();
        // 将WIF格式的公钥转换为ECKey对象
        // 将Hex格式的压缩公钥转换为ECKey对象
        ECKey compressedPublicKey = ECKey.fromPublicOnly(hexStringToByteArray(compressedPublicKeyHex));

        // 验证签名
        try {
            compressedPublicKey.verifyMessage(message, signatureBase64);
        } catch (SignatureException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    // 辅助方法：将十六进制字符串转换为字节数组
    private static byte[] hexStringToByteArray(String hex) {
        int len = hex.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i + 1), 16));
        }
        return data;
    }
}


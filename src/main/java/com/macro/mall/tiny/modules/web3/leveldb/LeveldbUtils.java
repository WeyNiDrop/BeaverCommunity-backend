package com.macro.mall.tiny.modules.web3.leveldb;

import com.macro.mall.tiny.modules.web3.config.Web3Configure;
import lombok.extern.slf4j.Slf4j;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBFactory;
import org.iq80.leveldb.Options;
import org.iq80.leveldb.WriteOptions;
import org.iq80.leveldb.impl.Iq80DBFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static org.iq80.leveldb.impl.Iq80DBFactory.asString;

@Component
@Slf4j
public class LeveldbUtils {
    @Resource
    private Web3Configure web3Configure;

    //编码集
    private static final Charset CHARSET = StandardCharsets.UTF_8;

    private DB db;
    private WriteOptions writeOptions;


    @PostConstruct
    public void init(){
        try {
            DBFactory factory = new Iq80DBFactory();
            // 默认如果没有则创建
            Options options = new Options();
            File file = new File(web3Configure.getLevelDbFilePath());
            db = factory.open(file, options);
            writeOptions = new WriteOptions();
            writeOptions.sync(true);
            log.info("leveldb 已加载");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @PreDestroy
    public void destroy() {
        // 此回调会在 Spring Boot 应用程序退出时调用
        if (db != null){
            try {
                db.close();
            } catch (IOException e) {
                log.error("leveldb 关闭失败：", e);
            }
        }
    }

    public void put(String key, Object value) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(); //byte[]输出流
        ObjectOutputStream oos = new ObjectOutputStream(baos); //包装流，对象输出到输出流对象中
        oos.writeObject(value);  //写对象到输出流
        db.put(key.getBytes(CHARSET), baos.toByteArray(), writeOptions);
        oos.close();
    }

    public void put(String key, String value){
        db.put(key.getBytes(CHARSET), value.getBytes(CHARSET));
    }

    public String getStringValue(String key){
        return asString(db.get(key.getBytes(CHARSET)));
    }

    public Object getObjectValue(String key) throws IOException, ClassNotFoundException {
        byte[] valueByte = db.get(key.getBytes(CHARSET));
        if (valueByte == null){
            return null;
        }//从数据库中读出byte[]
        ByteArrayInputStream bais = new ByteArrayInputStream(valueByte);//输入流对象读取结果
        ObjectInputStream ois = new ObjectInputStream(bais);//写对象流包装
        return ois.readObject();//获得对象
    }

    public void delete(String key){
        // 存在会删除，之后查询不出
        db.delete(key.getBytes(CHARSET));
    }

//    public void writeBatch(Map<String, byte[]> putList, Collection<String> removeKeys){
//
//    }
}

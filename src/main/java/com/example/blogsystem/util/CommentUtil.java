package com.example.blogsystem.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class CommentUtil {
    @Autowired
    private RedisUtil redisUtil;
    @Value("${limit-text-file.path}")
    private String limitTextFilePath;

    public String check(String input) {
        String key="limitText";
        StringBuilder sb;
        if(redisUtil.hasKey(key)){
            List<String> range = (List<String>) redisUtil.getListCache(key);
            for (String str:range
            ) {
                sb=new StringBuilder();
                sb.append(str.charAt(0));
                int len=str.length();
                for (int i = 1; i < len; i++) {
                    sb.append("*");
                }
                input=input.replaceAll(str,sb.toString());
            }
        }
        else {
            try {
                FileReader fileReader=new FileReader(new File(limitTextFilePath));
                BufferedReader bufferedReader=new BufferedReader(fileReader);
                String str;
                while ((str=bufferedReader.readLine())!=null){
                    redisUtil.setListCache(key,str);
                    sb=new StringBuilder();
                    sb.append(str.charAt(0));
                    int len=str.length();
                    for (int i = 1; i < len; i++) {
                        sb.append("*");
                    }
                    input=input.replaceAll(str,sb.toString());
                }
                redisUtil.expire(key,30, TimeUnit.MINUTES);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return input;
    }

}

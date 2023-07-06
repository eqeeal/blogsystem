package com.example.blogsystem.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
public class CommentUtil {
    @Autowired
    private RedisUtil redisUtil;
    @Value("${limit-text-file.path}")
    private String limitTextFilePath;
    @Value("${blog-richTxt.path}")
    private String richTxtFilePath;

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


    //存储富文本文件
    public String store(String origin) throws IOException {
        //文件名称
        String  name = UUID.randomUUID()+".txt";
        File dir = new File(richTxtFilePath);
        if(!dir.exists() && !dir.isDirectory()){
            dir.mkdir();
        }
        File file = new File(dir,name);
        FileWriter fw = new FileWriter(file);
        BufferedWriter bw = new BufferedWriter(fw);
        try {
            bw.write(origin);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            bw.close();
            fw.close();
        }
        return name;
    }
    //读取富文本文件字符串
    public String read(String path) throws IOException {
        File file = new File(richTxtFilePath,path);
        FileInputStream fileInputStream = null;
        String richStr="";

        try {
            fileInputStream = new FileInputStream(file);
            byte[] bytes = new byte[1024*5];
            int count=0;
            while ((count = fileInputStream.read(bytes))!=-1){
                 String str= new String(bytes,0,count);
                 richStr+=str;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            fileInputStream.close();
        }
        return richStr;
    }

}

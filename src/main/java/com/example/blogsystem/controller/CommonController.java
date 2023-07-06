package com.example.blogsystem.controller;

import com.example.blogsystem.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

@RestController
@Slf4j
@RequestMapping("/common")
public class CommonController {
    @Value("${blog-image.path}")
    private String basePath;

    @PostMapping("/uploadMany")
    public Result<String> uploadMany(MultipartFile[] files){
        for (MultipartFile file:files
             ) {
            //原始文件名
            String originalFilename = file.getOriginalFilename();
            String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
            //使用UUID生成文件名称
            String fileName = UUID.randomUUID().toString()+suffix;

            //创建一个目录
            File dir=new File(basePath);
            //判断是否存在
            if(!dir.exists()){
                //创建
                dir.mkdirs();
                log.info("目录不存在已经创建");
            }

            try {
                //file是一个临时文件需要转存到其他位置
                file.transferTo(new File(basePath+fileName));
            }
            catch (IOException e){
                e.printStackTrace();
            }

            return Result.ok(fileName);
        }
       return Result.fail("上传失败");
    }

    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file){

        //原始文件名
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        //使用UUID生成文件名称
        String fileName = UUID.randomUUID().toString()+suffix;

        //创建一个目录
        File dir=new File(basePath);
        //判断是否存在
        if(!dir.exists()){
            //创建
            dir.mkdirs();
            log.info("目录不存在已经创建");
        }

        try {
            //file是一个临时文件需要转存到其他位置
            file.transferTo(new File(basePath+fileName));
        }
        catch (IOException e){
            e.printStackTrace();
        }

        return Result.ok(fileName);
    }

    /**
     * 文件下载
     * @param name
     * @param response 使用输出流返回图片数据，并非使用返回值
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response){
        try {

            //输入流，通过输入流读取文件内容
            FileInputStream fileInputStream=new FileInputStream(new File(basePath+name));

            //输出流，将文件写会浏览器，在浏览器展示
            ServletOutputStream outputStream = response.getOutputStream();

            //设置输出类型
            response.setContentType("image/jpeg");

            //读取文件输入流直到读为空
            int len=0;
            byte[] bytes=new byte[1024];
            while ((len=fileInputStream.read(bytes))!=-1){
                outputStream.write(bytes,0,len);
                outputStream.flush();
            }
            //关闭
            outputStream.close();
            fileInputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

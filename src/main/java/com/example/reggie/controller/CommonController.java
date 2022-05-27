package com.example.reggie.controller;

import com.example.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/common")
public class CommonController {

    @Value("${reggie.path}")
    private String basePath; // 文件路径，在yml中配置

    /**
     * 文件上传
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file) {
        // file是一个临时文件，需要转存到指定位置，否则本次请求完成后临时文件会删除
        log.info(file.toString());

        // 原始文件名
        String originalFilename = file.getOriginalFilename(); // abc.jpg
        String suffix = originalFilename.substring(originalFilename.lastIndexOf(".")); // 文件后缀名

        // 使用UUID重新生成文件名，防止文件名称重复造成文件覆盖
        String fileName = UUID.randomUUID().toString() + suffix; // dsgshtsh.jpg

        // 处理文件目录不存在的情况
        File dir = new File(basePath); // 创建一个目录对象
        if(!dir.exists()) {
            dir.mkdirs();
        }

        try {
            // 将临时文件转存到指定位置
            file.transferTo(new File(basePath + fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return R.success(fileName); // 返回文件名称，后续前端添加菜品等要使用文件名
    }

    /**
     * 文件下载，只能通过response返回文件流，无法通过 R对象 返回
     * @param name
     * @param response
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response) {
        try {

            // 1、通过输入流读取文件内容
            FileInputStream inputStream = new FileInputStream(new File(basePath + name));

            // 2、通过输出流将文件写回浏览器，在浏览器展示图片
            ServletOutputStream outputStream = response.getOutputStream();
            response.setContentType("image/jpeg"); // 设置文件格式
            int len = 0;
            byte[] bytes = new byte[1024];
            while((len = inputStream.read(bytes)) != -1) { // 从输入流读取到bytes数组
                outputStream.write(bytes, 0, len); // 从bytes数组写入到输出流
                outputStream.flush(); // 执行写入，清空缓冲区
            }

            // 3、关闭输入输出流
            inputStream.close();
            outputStream.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}

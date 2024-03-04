package com.example.springboot;

import cn.hutool.http.HttpRequest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;

import javax.annotation.Resource;
import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
class ApplicationTests {

    @Resource
    private JavaMailSender javaMailSender;

    @Test
    void contextLoads() {
        System.out.println(javaMailSender);
    }

    /**
     * 发送一个简单邮件
     */
    @Test
    public void sendSimpleMail(){
        Map<String, Object> paramMap = new HashMap<>();

        // 收件人
        paramMap.put("formList", Collections.singletonList("2388436364@qq.com"));
        // 邮件标题
        paramMap.put("title", "使用springboot调用第三方邮件接口");
        // 正文图片
        paramMap.put("picture", new File("D:\\Backup\\Downloads\\时间.png"));
        // 附件名称
        paramMap.put("zipName", "统计图.zip");
        // 附件内容
        paramMap.put("attachment", new File("D:\\Backup\\Downloads\\static_16974374635522820_SparkApi_Python.zip"));

        // hutool工具类http请求
        String result2 = HttpRequest.post("http://localhost:9090/mail/sendComplexMail")
                .form(paramMap)   //表单内容
                .timeout(50000) // 超时，毫秒
                .execute().body();
        System.out.println(result2);

    }



}

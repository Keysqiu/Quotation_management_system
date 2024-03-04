package com.example.springboot.controller;

import com.example.springboot.common.Constants;
import com.example.springboot.common.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 描述: 邮件发送控制器
 * </p>
 * @author qiuqingsheng
 * @since 2023/12/12 12:37
 */
@RestController
@RequestMapping( "/mail" )
public class MailController {

    protected final static Logger logger = (Logger) LoggerFactory.getLogger(MailController.class);

    @Resource
    private JavaMailSender javaMailSender;

    /**
     * 邮件发件人，从配置文件获取
     */
    @Value( "${spring.mail.username}" )
    private String sender;

    /**
     * @param formList 接收人列表
     * @param title    邮件标题
     * @param picture   正文图片
     * @param zipName   附件名称
     * @param attachment 附件
     * @apiNote 发送复杂邮件，包括图片以及附件
     * @since 2023/12/12 12:37
     * @author qiuqingsheng
     */
    @PostMapping( "/sendComplexMail" )
    public Result sendComplexMail(@RequestParam( value = "formList" ) List<String> formList,
                                  @RequestParam( value = "title" ) String title,
                                  @RequestParam( value = "picture" ) MultipartFile picture,
                                  @RequestParam(value = "zipName") String zipName,
                                  @RequestParam(value =  "attachment") MultipartFile attachment ) {

        //创建一个复杂的邮寄对象
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        //创建一个MimeMessageHelper，来组装参数 true表示开启附件，utf-8表示编码
        MimeMessageHelper mimeMessageHelper = null;
        try {
            mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            //1、设置发送人
            mimeMessageHelper.setFrom(sender);
            //2、设置接收人
            String[] to = formList.toArray(new String[0]);
            logger.info("输出收件人： " + Arrays.toString(to));
            mimeMessageHelper.setTo(to);

            //3、设置主题
            mimeMessageHelper.setSubject(title);

            //拼接内容参数
            String sb = "<html><body> <img src='cid:picture'/> </body></html>";//如果要插入图片src='cid:picture',相当于占位符
            //4、设置内容，可以被html解析
            mimeMessageHelper.setText(sb, true);

            // 上传文件名，原始文件名
            String pictureName = picture.getOriginalFilename();
            logger.info("输出正文图片： " + pictureName);
            // 断言，为了避免空指针
            assert pictureName != null;
            // 将图片保存在本地
            File dest = new File(System.getProperty("user.dir") + "/temp" + pictureName.substring(pictureName.lastIndexOf(".")));
            picture.transferTo(dest);
            if ( !dest.getParentFile().exists() ) {
                if ( !dest.getParentFile().mkdirs() ) {
                    logger.error("生成图片目录失败！");
                }
            }
            //5、添加内容图片
            mimeMessageHelper.addInline("picture", dest);

            //6、添加附件
            logger.info("输出附件名称： " + zipName);
            mimeMessageHelper.addAttachment(zipName, attachment);

            // 7、发送邮件
            javaMailSender.send(mimeMessage);

            // 8、删除已保存的文件
            if ( dest.delete() ) {
                logger.info("删除正文图片成功");
            } else {
                logger.error("删除正文图片失败");
            }
        } catch ( MessagingException | IOException e ) {
            logger.error("--------发送失败---------");
            e.printStackTrace();
            return Result.error(Constants.CODE_500,"发送失败");
        }

        return Result.success("发送成功");
    }
}


package com.example.springboot.utils;
/*
* 代码生成器类
*
* */

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;

import java.util.Collections;

public class CodeGenerator {
    public static void main(String[] args) {
        generator();
    }
    private static void generator(){
        FastAutoGenerator.create("jdbc:mysql://localhost:3306/qiu?serverTimezone=GMT%2b8", "root", "123456")
                .globalConfig(builder -> {
                    builder.author("qiuqingsheng") // 设置作者
                            .enableSwagger() // 开启 swagger 模式
                            .fileOverride() // 覆盖已生成文件
                            .outputDir("D:\\Develop\\JavaWebProject\\Quotation_management_system\\Vue_SpringBoot\\SpringBoot\\src\\main\\java\\"); // 指定输出目录
                })
                .packageConfig(builder -> {
                    builder.parent("com.example.springboot") // 设置父包名
                            .moduleName(null) // 设置父包模块名
                            .pathInfo(Collections.singletonMap(OutputFile.mapperXml, "D:\\Develop\\JavaWebProject\\Quotation_management_system\\Vue_SpringBoot\\SpringBoot\\src\\main\\resources\\mapper\\")); // 设置mapperXml生成路径
                })
                .strategyConfig(builder -> {
                    builder.entityBuilder().enableLombok(); //引入Lombok插件，避免写太多的get、set方法
                    builder.mapperBuilder().enableMapperAnnotation().build(); //给mapper接口增加@@Mapper注解
                    builder.controllerBuilder().enableHyphenStyle()  // 开启驼峰转连字符
                            .enableRestStyle();  // 开启生成@RestController 控制器,必须要的，要不然生成的是@Controller(视图)，不是JSON字符串
                    builder.addInclude("sys_provider") // 设置需要生成的表名
                            .addTablePrefix("t_", "sys_"); // 设置过滤表前缀
                })
//                .templateEngine(new FreemarkerTemplateEngine()) // 使用Freemarker引擎模板，默认的是Velocity引擎模板
                .execute();
    }
}

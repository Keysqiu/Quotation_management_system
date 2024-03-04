package com.example.springboot.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.springboot.common.Result;
import com.example.springboot.entity.Files;
import com.example.springboot.mapper.FilesMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author qiuqingsheng
 * @since 2023-01-31
 */


@RestController
@RequestMapping("/file")
public class FilesController {

    //把配置文件中 files.upload.path的路径注入进来
    @Value("${files.upload.path}")
    private String fileUploadPath;

    //注入mapper
    @Resource
    private FilesMapper filesMapper;

    /*
     * 文件上传接口
     * @param file 前端传递过来的文件
     *@return
     */
    @PostMapping("/upload")
    public String upload(@RequestParam MultipartFile file) throws IOException {

        //传进来文件的名称
        String originalFilename = file.getOriginalFilename();
        //文件的后缀名
        String type = FileUtil.extName(originalFilename);
        //文件的大小
        long size = file.getSize();

        //定义一个文件的唯一标识码
        String uuid = IdUtil.fastSimpleUUID();
        // StrUtil.DOT其实就是一个 .号
        String fileUUTD = uuid + StrUtil.DOT + type;

        File uploadFile = new File(fileUploadPath + fileUUTD);

        //判断配置的父级文件目录是否存在
        if (!uploadFile.getParentFile().exists()) {
            //若不存在则创建一个新父级的文件目录
            uploadFile.getParentFile().mkdirs();
        }

        String url;
        //上传文件到磁盘
        file.transferTo(uploadFile);
        //获取文件的md5
        String md5 = SecureUtil.md5(uploadFile);
        //从数据库查询是否存在相同的记录
        Files dbFiles = getFileByMd5(md5);
        if (dbFiles != null) {
            url = dbFiles.getUrl();
            //由于文件已存在，所以删除刚才长传的重复文件
            uploadFile.delete();
        } else {
            //数据库不存在重复文件，则不删除刚才上传的文件
            url = "http://localhost:9090/file/" + fileUUTD;
        }

        //存储到数据库
        Files saveFile = new Files();
        saveFile.setName(originalFilename);
        saveFile.setType(type);
        //单位 B->KB
        saveFile.setSize(size / 1024);
        saveFile.setUrl(url);
        saveFile.setMd5(md5);
        filesMapper.insert(saveFile);
        return url;
    }


    @GetMapping("/{fileUUTD}")
    public void download(@PathVariable String fileUUTD, HttpServletResponse response) throws IOException {
        //根据文件的唯一标识码获取文件
        File uploadFile = new File(fileUploadPath + fileUUTD);
        //设置输出流格式
        ServletOutputStream os = response.getOutputStream();
        response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileUUTD, "UTF-8"));
        response.setContentType("application/octet-stream");

        //读取文件字节流
        os.write(FileUtil.readBytes(uploadFile));
        os.flush();
        os.close();
    }

    /*
     * 通过文件的md5查询文件
     *@return
     */
    private Files getFileByMd5(String md5) {
        //查询文件的md5是否存在
        QueryWrapper<Files> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("md5", md5);
        List<Files> filesList = filesMapper.selectList(queryWrapper);

        return filesList.size() == 0 ? null : filesList.get(0);
    }

    /*
    * 分页查询接口
    *@return
    */
    @GetMapping("/page")
    public Result findPage(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize,
                           @RequestParam(defaultValue = "") String name
                          ) {
        //分页查询
        QueryWrapper<Files> queryWrapper = new QueryWrapper<>();
        //查询未删除的记录
        queryWrapper.eq("is_delete",false);
        queryWrapper.orderByDesc("id");
        if (!"".equals(name)) {
            queryWrapper.like("name", name);
        }

        return Result.success(filesMapper.selectPage(new Page<>(pageNum, pageSize), queryWrapper));
    }

    /*
    * 单个删除接口
    *@return
    */
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        Files files = filesMapper.selectById(id);
        files.setIsDelete(true);
        filesMapper.updateById(files);
        return Result.success();
    }

    /*
     * 批量删除接口
     *@return
     */
    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        // select * from sys_file where id in (id,id,id...)
        QueryWrapper<Files> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id",ids);
        List<Files> filesList = filesMapper.selectList(queryWrapper);
        for (Files files : filesList) {
            files.setIsDelete(true);
            filesMapper.updateById(files);
        }
        return Result.success();
    }

    /*
    * 更新接口
    *@return
    */
    @PostMapping("/update")
    public Result save(@RequestBody Files files) {
        //新增或更新
        return Result.success(filesMapper.updateById(files));
    }
}


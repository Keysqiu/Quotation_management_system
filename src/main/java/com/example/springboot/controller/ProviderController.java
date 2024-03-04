package com.example.springboot.controller;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.springboot.common.Constants;
import com.example.springboot.common.Result;
import com.example.springboot.controller.dto.ProviderDTO;
import com.example.springboot.entity.Provider;
import com.example.springboot.service.IProviderService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author qiuqingsheng
 * @since 2023-11-25
 */
@RestController
@RequestMapping("/provider")
public class ProviderController {

    @Resource
    private IProviderService providerService;

    @PostMapping
    public Result save(@RequestBody Provider provider) {
        //新增或更新
        String uuid = provider.getUuid();
        if (uuid == null || uuid.equals("")) {
            String simpleUUID = IdUtil.simpleUUID();
            provider.setUuid(simpleUUID);
        }
        return Result.success(providerService.saveOrUpdate(provider));

    }

    @PostMapping("/save")
    public Result saveByName(@RequestBody Provider provider) {
        //新增或更新
        return Result.success(providerService.updateByName(provider));
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        //删除
        return Result.success(providerService.removeById(id));
    }

    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        return Result.success(providerService.removeByIds(ids));
    }

    @GetMapping
    public Result findAll() {
        //查询所有
        return Result.success(providerService.list());
    }

    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id) {
        //根据id查询
        return Result.success(providerService.getById(id));
    }

    @GetMapping("/page")
    public Result findPage(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize,
                           @RequestParam(defaultValue = "") String username,
                           @RequestParam(defaultValue = "") String phone,
                           @RequestParam(defaultValue = "") String address) {
        //分页查询
        QueryWrapper<Provider> queryWrapper = new QueryWrapper<>();
        if (!"".equals(username)) {
            queryWrapper.like("username", username);
        }
        if (!"".equals(phone)) {
            queryWrapper.like("phone", phone);
        }
        if (!"".equals(address)) {
            queryWrapper.like("address", address);
        }
        //根据id进行倒序排列，让最新插入的数据显示在最前面
        queryWrapper.orderByDesc("id");
        return Result.success(providerService.page(new Page<>(pageNum, pageSize), queryWrapper));
    }

    @GetMapping("/page/selectAll")
    public Result findPage_All(@RequestParam Integer pageNum,
                               @RequestParam Integer pageSize) {
        //分页查询
        QueryWrapper<Provider> queryWrapper = new QueryWrapper<>();
        //根据id进行倒序排列，让最新插入的数据显示在最前面
        queryWrapper.orderByDesc("id");
        return Result.success(providerService.page(new Page<>(pageNum, pageSize), queryWrapper));
    }

    //用户登录接口
    @PostMapping("/login")
    public Result login(@RequestBody ProviderDTO providerDTO) {
        String username = providerDTO.getUsername();
        String password = providerDTO.getPassword();
        if (StrUtil.isBlank(username) || StrUtil.isBlank(password)) {
            //判断用户名或密码是否为空
            return Result.error(Constants.CODE_400, "用户名或密码为空");
        }
        ProviderDTO dto = providerService.login(providerDTO);
        return Result.success(dto);
    }

    //用户注册接口
    @PostMapping("/register")
    public Result register(@RequestBody ProviderDTO providerDTO) {
        String providername = providerDTO.getUsername();
        String password = providerDTO.getPassword();
        if (StrUtil.isBlank(providername) || StrUtil.isBlank(password)) {
            //判断用户名或密码是否为空
            return Result.error(Constants.CODE_400, "用户名或密码为空");
        }
        return Result.success(providerService.register(providerDTO));
    }

    //查询用户信息接口
    @GetMapping("/username/{username}")
    public Result findOne(@PathVariable String username) {
        //根据username查询
        System.out.println("username"+username);
        QueryWrapper<Provider> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        return Result.success(providerService.getOne(queryWrapper));
    }

    /*
     * excel导入接口
     *@return
     */
    @PostMapping("/import")
    public Result imp(MultipartFile file) throws IOException {
        InputStream inputStream = file.getInputStream();
        ExcelReader reader = ExcelUtil.getReader(inputStream);
        //方式一：（推荐）通过JavaBean的方式读取Excel内的对象，但是要求表头必须是英文，跟JavaBean的属性要对应起来
        List<Provider> list = reader.readAll(Provider.class);
        providerService.saveBatch(list);
        //方式二：忽略表头的中文，直接读取表的内容，适用于表头为中文的情况
        //直接指明从第二行开始读
//        List<List<Object>>list=reader.read(1);
//        ArrayList<User> users = CollUtil.newArrayList();
//        for(List<Object>row:list){
//            User user = new User();
//            user.setUsername(row.get(0).toString());
//            user.setPassword(row.get(1).toString());
//            user.setNickname(row.get(2).toString());
//            user.setEmail(row.get(3).toString());
//            user.setPhone(row.get(4).toString());
//            user.setAddress(row.get(5).toString());
//            user.setAvatarUrl(row.get(6).toString());
//            users.add(user);
//        }
//        userService.saveBatch(users);
        return Result.success(true);
    }

    /*
     * 导出接口
     *@return
     */
    @GetMapping("/export")
    public void export(HttpServletResponse response) throws IOException {
        //从数据库查询出所有数据
        List<Provider> list = providerService.list();
        //（方式一）通过工具类创建writer 写出到磁盘路径
        //ExcelWriter writer= ExcelUtil.getWriter(filesUploadPath+"用户信息.xlsx");
        //（方式二）在内存操作，写出到浏览器
        ExcelWriter writer = ExcelUtil.getWriter(true);


        //（可选，一般不这样处理，因为如果表头是中文的话，导入时要把表头忽略掉，处理比较麻烦）让数据库的字段名变成中文
//        writer.addHeaderAlias("username","用户名");
//        writer.addHeaderAlias("password","密码");
//        writer.addHeaderAlias("nickname","昵称");
//        writer.addHeaderAlias("email","邮箱");
//        writer.addHeaderAlias("phone","电话");
//        writer.addHeaderAlias("address","地址");
//        writer.addHeaderAlias("createTime","创建时间");
//        writer.addHeaderAlias("avatarUrl","头像");

        //一次性写出list内的对象到excel，使用默认样式，强制输出标题
        writer.write(list, true);

        //设置浏览器响应的样式（固定写法）
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
        String fileName = URLEncoder.encode("用户信息", "UTF-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");

        ServletOutputStream out = response.getOutputStream();
        writer.flush(out, true);

        //关流
        out.close();
        writer.close();
    }
}


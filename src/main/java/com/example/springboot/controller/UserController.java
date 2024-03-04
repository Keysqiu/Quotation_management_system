package com.example.springboot.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.springboot.common.Constants;
import com.example.springboot.common.Result;
import com.example.springboot.controller.dto.UserDTO;
import com.example.springboot.entity.User;
import com.example.springboot.service.IUserService;
import org.jetbrains.annotations.NotNull;
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
 * @since 2023-01-23
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private IUserService userService;

    @PostMapping
    public Result save(@RequestBody User user) {
        //新增或更新
        String uuid = user.getUuid();
        if (uuid == null || uuid.equals("")) {
            String simpleUUID = IdUtil.simpleUUID();
            user.setUuid(simpleUUID);
        }
        return Result.success(userService.saveOrUpdate(user));
    }

    @PostMapping("/save")
    public Result saveByName(@RequestBody User user) {
        //新增或更新
        return Result.success(userService.updateByName(user));
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        //删除
        return Result.success(userService.removeById(id));
    }

    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        return Result.success(userService.removeByIds(ids));
    }

    @GetMapping
    public Result findAll() {
        //查询所有
        return Result.success(userService.list());
    }

    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id) {
        //根据id查询
        return Result.success(userService.getById(id));
    }

    @GetMapping("/page")
    public Result findPage(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize,
                           @RequestParam(defaultValue = "") String username,
                           @RequestParam(defaultValue = "") String email,
                           @RequestParam(defaultValue = "") String address) {
        //分页查询

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (!"".equals(username)) {
            queryWrapper.like("username", username);
        }
        if (!"".equals(email)) {
            queryWrapper.like("email", email);
        }
        if (!"".equals(address)) {
            queryWrapper.like("address", address);
        }
        //根据id进行倒序排列，让最新插入的数据显示在最前面
        queryWrapper.orderByDesc("id");
        return getResult(pageNum, pageSize, queryWrapper);
    }

    @GetMapping("/page/selectAll")
    public Result findPage_All(@RequestParam Integer pageNum,
                               @RequestParam Integer pageSize) {
        //分页查询
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        //根据id进行倒序排列，让最新插入的数据显示在最前面
        queryWrapper.orderByDesc("id");
        return getResult(pageNum, pageSize, queryWrapper);
    }

    @NotNull
    private Result getResult(@RequestParam Integer pageNum, @RequestParam Integer pageSize, QueryWrapper<User> queryWrapper) {
        String formatDateTime;
        Page<User> page = userService.page(new Page<>(pageNum, pageSize), queryWrapper);
        for (User record : page.getRecords()) {
            formatDateTime = DateUtil.formatDateTime(record.getCreateTime());
            record.setCreate_time(formatDateTime);
        }
        return Result.success(page);
    }

    /*
     * 导出接口
     *@return
     */
    @GetMapping("/export")
    public void export(HttpServletResponse response) throws IOException {
        //从数据库查询出所有数据
        List<User> list = userService.list();
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

    /*
     * excel导入接口
     *@return
     */
    @PostMapping("/import")
    public Result imp(MultipartFile file) throws IOException {
        InputStream inputStream = file.getInputStream();
        ExcelReader reader = ExcelUtil.getReader(inputStream);
        //方式一：（推荐）通过JavaBean的方式读取Excel内的对象，但是要求表头必须是英文，跟JavaBean的属性要对应起来
        List<User> list = reader.readAll(User.class);
        userService.saveBatch(list);
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

    //用户登录接口
    @PostMapping("/login")
    public Result login(@RequestBody UserDTO userDTO) {
        String username = userDTO.getUsername();
        String password = userDTO.getPassword();
        if (StrUtil.isBlank(username) || StrUtil.isBlank(password)) {
            //判断用户名或密码是否为空
            return Result.error(Constants.CODE_400, "用户名或密码为空");
        }
        UserDTO dto = userService.login(userDTO);
        return Result.success(dto);
    }

    //用户注册接口
    @PostMapping("/register")
    public Result register(@RequestBody UserDTO userDTO) {
        String username = userDTO.getUsername();
        String password = userDTO.getPassword();
        if (StrUtil.isBlank(username) || StrUtil.isBlank(password)) {
            //判断用户名或密码是否为空
            return Result.error(Constants.CODE_400, "用户名或密码为空");
        }
        return Result.success(userService.register(userDTO));
    }

    //查询用户信息接口
    @GetMapping("/username/{username}")
    public Result findOne(@PathVariable String username) {
        //根据username查询
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        return Result.success(userService.getOne(queryWrapper));
    }
}


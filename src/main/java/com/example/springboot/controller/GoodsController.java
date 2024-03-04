package com.example.springboot.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.springboot.common.Result;
import com.example.springboot.controller.dto.GoodsDTO;
import com.example.springboot.controller.dto.PageDTO;
import com.example.springboot.controller.dto.SelectGoodDTO;
import com.example.springboot.entity.Goods;
import com.example.springboot.entity.Provider;
import com.example.springboot.entity.User;
import com.example.springboot.service.IGoodsService;
import com.example.springboot.service.IProviderService;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author qiuqingsheng
 * @since 2023-11-20
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

    @Resource
    private IGoodsService goodsService;
    @Resource
    private IProviderService providerService;

    @PostMapping
    public Result save(@RequestBody Goods goods) {
        //新增或更新
        return Result.success(goodsService.saveOrUpdate(goods));
    }

    @PostMapping("/save")
    public Result save(@RequestBody GoodsDTO goods) {
        //新增或更新
        String goodId = goods.getGoodId();
        System.out.println("goodId" + goodId);
        if (goodId == null || goodId.equals("")) {
            //没有商品唯一id
            String username = goods.getUsername();
            int provider_id = providerService.getIdByName(username);
            goods.setProvideId(provider_id);
            //生成唯一商品id
            String simpleUUID = IdUtil.simpleUUID();
            System.out.println("simpleUUID:" + simpleUUID);
            //设置订单id
            goods.setGoodId(simpleUUID);
            Goods one = new Goods();
            BeanUtil.copyProperties(goods, one, true);
            System.out.println("one.id" + one.getId());
            goodsService.insertOne(one);
            return Result.success();
        } else {
            Goods one = new Goods();
            BeanUtil.copyProperties(goods, one, true);
            return Result.success(goodsService.updateOne(one));
        }
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        //删除
        return Result.success(goodsService.removeById(id));
    }

    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        return Result.success(goodsService.removeByIds(ids));
    }

    @GetMapping
    public Result findAll() {
        //查询所有
        return Result.success(goodsService.list());
    }

    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id) {
        //根据id查询
        return Result.success(goodsService.getById(id));
    }

    @GetMapping("/page")
    public Result findPage(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize, @RequestParam String username, @RequestParam String role) {
        QueryWrapper<Goods> queryWrapper = new QueryWrapper<>();
        String formatDateTime;
        //分页查询
        if (role.equals("ROLE_PROVIDER")) {
            int provide_id = providerService.getIdByName(username);
            queryWrapper.eq("provide_id", provide_id);
        }
        //根据id进行倒序排列，让最新插入的数据显示在最前面
        queryWrapper.orderByDesc("id");
        return getResult(pageNum, pageSize, queryWrapper);
    }

    @GetMapping("/page/selectAll")
    public Result findPage(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize) {
        //分页查询

        QueryWrapper<Goods> queryWrapper = new QueryWrapper<>();
        //根据id进行倒序排列，让最新插入的数据显示在最前面
        queryWrapper.orderByDesc("id");
        Page<Goods> page = goodsService.page(new Page<>(pageNum, pageSize), queryWrapper);
        List<PageDTO> pageDTOList = new ArrayList<>();
        String formatDateTime;
        for (Goods record : page.getRecords()) {
            formatDateTime = DateUtil.formatDateTime(record.getCreateTime());
            record.setCreate_time(formatDateTime);
            getResult(pageDTOList, record);
        }
        return Result.success(pageDTOList);
    }

    private void getResult(List<PageDTO> pageDTOList, Goods record) {
        String formatDateTime;
        Integer provideId;
        provideId = record.getProvideId();
        Provider provider = providerService.getById(provideId);
        PageDTO pageDTO = new PageDTO();
        pageDTO.setUsername(provider.getUsername());
        pageDTO.setAddress(provider.getAddress());
        pageDTO.setPhone(provider.getPhone());
        formatDateTime = DateUtil.formatDateTime(record.getCreateTime());
        record.setCreate_time(formatDateTime);
        pageDTO.setPageGoods(record);
        pageDTOList.add(pageDTO);
    }

    @GetMapping("/page/obscure")
    public Result findPage(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize,
                           @RequestParam(defaultValue = "") String goodName,
                           @RequestParam(defaultValue = "") String goodPrice,
                           @RequestParam(defaultValue = "") String goodKind) {
        //分页查询

        QueryWrapper<Goods> queryWrapper = new QueryWrapper<>();

        if (!"".equals(goodName)) {
            queryWrapper.like("good_name", goodName);
        }
        if (!"".equals(goodPrice)) {
            queryWrapper.like("good_price", goodPrice);
        }
        if (!"".equals(goodKind)) {
            queryWrapper.like("good_kind", goodKind);
        }
        //根据id进行倒序排列，让最新插入的数据显示在最前面
        queryWrapper.orderByDesc("id");
        Page<Goods> page = goodsService.page(new Page<>(pageNum, pageSize), queryWrapper);

        List<PageDTO> pageDTOList = new ArrayList<>();
        for (Goods record : page.getRecords()) {
            getResult(pageDTOList, record);
        }

        return Result.success(pageDTOList);
    }

    @GetMapping("/page/obscure/ById")
    public Result findPage(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize,
                           @RequestParam(defaultValue = "") String goodName,
                           @RequestParam(defaultValue = "") String goodPrice,
                           @RequestParam(defaultValue = "") String goodKind,
                           @RequestParam(defaultValue = "") String username) {
        //分页查询

        QueryWrapper<Goods> queryWrapper = new QueryWrapper<>();
        String formatDateTime;

        if (!"".equals(goodName)) {
            queryWrapper.like("good_name", goodName);
        }
        if (!"".equals(goodPrice)) {
            queryWrapper.like("good_price", goodPrice);
        }
        if (!"".equals(goodKind)) {
            queryWrapper.like("good_kind", goodKind);
        }
        //根据id进行倒序排列，让最新插入的数据显示在最前面
        queryWrapper.orderByDesc("id");
        int provide_id = providerService.getIdByName(username);
        queryWrapper.eq("provide_id", provide_id);

        return getResult(pageNum, pageSize, queryWrapper);
    }

    @GetMapping("/selectGood")
    public void selectGood(HttpServletResponse response,@RequestParam(defaultValue = "") String goodName,
                           @RequestParam(defaultValue = "") String goodPrice,
                           @RequestParam(defaultValue = "") String goodKind) throws IOException {
        //分页查询
        System.out.println(goodName);
        System.out.println(goodPrice);
        System.out.println(goodKind);

        QueryWrapper<Goods> queryWrapper = new QueryWrapper<>();

        if (!"".equals(goodName)) {
            queryWrapper.like("good_name", goodName);
        }
        if (!"".equals(goodPrice)&&!"undefined".equals(goodPrice)) {
            queryWrapper.like("good_price", goodPrice);
        }
        if (!"".equals(goodKind)&&!"undefined".equals(goodKind)) {
            queryWrapper.like("good_kind", goodKind);
        }
        //根据id进行倒序排列，让最新插入的数据显示在最前面
        queryWrapper.orderByDesc("id");
        List<Goods> list = goodsService.list(queryWrapper);
        ExcelWriter writer = ExcelUtil.getWriter(true);

        //一次性写出list内的对象到excel，使用默认样式，强制输出标题
        writer.write(list, true);
        //设置浏览器响应的样式（固定写法）
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
        String fileName = URLEncoder.encode("报价单", "UTF-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");
        ServletOutputStream out = response.getOutputStream();
        writer.flush(out, true);
        //关流
        out.close();
        writer.close();
    }

    @NotNull
    private Result getResult(@RequestParam Integer pageNum, @RequestParam Integer pageSize, QueryWrapper<Goods> queryWrapper) {
        String formatDateTime;
        Page<Goods> page = goodsService.page(new Page<>(pageNum, pageSize), queryWrapper);
        for (Goods record : page.getRecords()) {
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
    public void export(HttpServletResponse response, @RequestParam(defaultValue = "") String username) throws IOException {
        System.out.println("good" + username);
        int provide_id = providerService.getIdByName(username);
        QueryWrapper<Goods> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("provide_id", provide_id);
        //从数据库查询出所有数据
        List<Goods> list = goodsService.list(queryWrapper);
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


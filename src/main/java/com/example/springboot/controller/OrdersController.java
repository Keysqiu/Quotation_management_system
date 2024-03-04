package com.example.springboot.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.springboot.common.Result;
import com.example.springboot.entity.Goods;
import com.example.springboot.entity.Orders;
import com.example.springboot.service.IOrdersService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
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
@RequestMapping("/orders")
public class OrdersController {

    @Resource
    private IOrdersService ordersService;
    @Resource
    private GoodsController goodsController;

    @PostMapping
    public Result save(@RequestBody Orders orders) {
        //新增或更新
        return Result.success(ordersService.saveOrUpdate(orders));
    }

    @GetMapping("/save")
    public Result saveById(@RequestParam Integer id) {
        //从数据库中查到这个商品的信息
        Result one = goodsController.findOne(id);
        //将该商品的信息和随机生成的订单号写入orders数据库中
        Goods good = (Goods) one.getData();
        Orders order = new Orders();
        //设置商品ID
        order.setId(good.getGoodId());
        //生成唯一订单id
        String simpleUUID = IdUtil.simpleUUID();
        System.out.println("simpleUUID:"+simpleUUID);
        //设置订单id
        order.setOrderNo(simpleUUID);
        //设置商品名称
        order.setGoodsName(good.getGoodName());
        //设置购买的数量
        int Num = 1;
        order.setNum(Num);
        //设置订单总价
        order.setTotal(good.getGoodPrice() * Num);
        //获取当前时间字符串，格式：yyyy-MM-dd HH:mm:ss
        String now = DateUtil.now();
        //设置订单创建的时间
        order.setCreateTime(now);
        order.setStatus("待支付");
        return Result.success(ordersService.save(order));

    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        //删除
        return Result.success(ordersService.removeById(id));
    }

    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        return Result.success(ordersService.removeByIds(ids));
    }

    @GetMapping
    public Result findAll() {
        //查询所有
        return Result.success(ordersService.list());
    }

    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id) {
        //根据id查询
        return Result.success(ordersService.getById(id));
    }

    @GetMapping("/page")
    public Result findPage(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize) {
        //分页查询

        QueryWrapper<Orders> queryWrapper = new QueryWrapper<>();
        //根据id进行倒序排列，让最新插入的数据显示在最前面
        queryWrapper.orderByDesc("id");
        return Result.success(ordersService.page(new Page<>(pageNum, pageSize), queryWrapper));
    }
}


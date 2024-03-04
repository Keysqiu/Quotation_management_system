package com.example.springboot.service.impl;

import com.example.springboot.controller.dto.AliPayDTO;
import com.example.springboot.entity.Orders;
import com.example.springboot.mapper.OrdersMapper;
import com.example.springboot.mapper.RoleMapper;
import com.example.springboot.service.IOrdersService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author qiuqingsheng
 * @since 2023-11-20
 */
@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements IOrdersService {

    @Resource
    private OrdersMapper ordersMapper;

    @Override
    public Orders selectByOrderNo(String orderNo) {
        return ordersMapper.selectByOrderNo(orderNo);
    }

    @Override
    public void updateByOrderNo(AliPayDTO orderNo) {
         ordersMapper.updateByOrderNo(orderNo);
    }

}

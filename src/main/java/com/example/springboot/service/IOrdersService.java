package com.example.springboot.service;

import com.example.springboot.controller.dto.AliPayDTO;
import com.example.springboot.entity.Orders;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author qiuqingsheng
 * @since 2023-11-20
 */
public interface IOrdersService extends IService<Orders> {


    Orders selectByOrderNo(String orderNo);
    void updateByOrderNo(AliPayDTO orderNo);

}

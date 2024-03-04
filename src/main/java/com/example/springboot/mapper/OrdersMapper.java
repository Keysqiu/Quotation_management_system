package com.example.springboot.mapper;

import com.example.springboot.controller.dto.AliPayDTO;
import com.example.springboot.entity.Orders;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author qiuqingsheng
 * @since 2023-11-20
 */
@Mapper
public interface OrdersMapper extends BaseMapper<Orders> {
    @Select("select * from orders where order_no= #{orderNo}")
    Orders selectByOrderNo(String orderNo);

     void updateByOrderNo(AliPayDTO orderNo);
}

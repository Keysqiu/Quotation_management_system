package com.example.springboot.controller.dto;
/**
 * 阿里云修改数据实体类，在AliPayController控制类124行有所体现
 */

import lombok.Data;

@Data
public class AliPayDTO {

    //订单ID
    private String orderNo;
    //支付成功的单号
    private String payNo;
    //支付成功的时间
    private String payTime;
    //支付状态
    private String status;
}

package com.example.springboot.entity;

import java.io.Serializable;
import java.util.Date;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 
 * </p>
 *
 * @author qiuqingsheng
 * @since 2023-11-20
 */
@Getter
@Setter
  @ApiModel(value = "Orders对象", description = "")
public class Orders implements Serializable {

    private static final long serialVersionUID = 1L;

      @ApiModelProperty("商品ID")
      private String id;

      @ApiModelProperty("订单ID")
        private String orderNo;

      @ApiModelProperty("商品名称")
      private String goodsName;

      @ApiModelProperty("商品个数")
      private Integer num;

      @ApiModelProperty("订单总价")
      private Integer total;

      @ApiModelProperty("订单创建时间")
      private String createTime;

      @ApiModelProperty("支付成功的单号")
      private String payNo;

      @ApiModelProperty("支付成功的时间")
      private String payTime;

      @ApiModelProperty("支付状态")
      private String status;


}

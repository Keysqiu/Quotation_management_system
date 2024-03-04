package com.example.springboot.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
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
@ApiModel(value = "Goods对象", description = "")
public class Goods implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("id")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("商品名称")
    private String goodName;

    @ApiModelProperty("商品编号")
    private String goodId;

    @ApiModelProperty("商品价格")
    private Integer goodPrice;

    @ApiModelProperty("供应商ID")
    private Integer provideId;

    @ApiModelProperty("商品类型")
    private String goodKind;

    @ApiModelProperty("创建日期")
    private Date createTime;

    //只是用来保存创建日期的另一个形式，不是数据库中的字段
    private String create_time;
}

package com.example.springboot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

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
 * @since 2023-11-25
 */
@Getter
@Setter
@TableName("sys_provider")
@ApiModel(value = "Provider对象", description = "")
public class Provider implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("供应商ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("供应商名称")
    private String username;

    @ApiModelProperty("密码")
    private String password;

    @ApiModelProperty("供应商地址")
    private String address;

    @ApiModelProperty("供应商联系电话")
    private String phone;

    @ApiModelProperty("昵称")
    private String nickname;

    @ApiModelProperty("邮箱")
    private String email;

    @ApiModelProperty("供应商类型")
    private String kind;

    @ApiModelProperty("供应商信誉等级")
    private String credibilityrating;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("头像")
    private String avatarUrl;

    @ApiModelProperty("角色")
    private String role;

    @ApiModelProperty("该供应商的唯一标识符")
    private String uuid;
}

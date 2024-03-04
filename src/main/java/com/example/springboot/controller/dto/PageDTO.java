package com.example.springboot.controller.dto;

import com.example.springboot.entity.Goods;
import lombok.Data;

@Data
public class PageDTO {
    private String username;
    private String address;
    private String phone;
    Goods pageGoods;
}

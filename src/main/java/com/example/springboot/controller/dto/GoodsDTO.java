package com.example.springboot.controller.dto;

import com.example.springboot.entity.Menu;
import lombok.Data;

import java.util.List;

@Data
public class GoodsDTO {
    private String goodName;
    private String goodId;
    private String goodPrice;
    private int provideId;
    private String goodKind;
    private String username;

}

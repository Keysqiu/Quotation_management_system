package com.example.springboot.controller.dto;
import com.example.springboot.entity.Menu;
import lombok.Data;

import java.util.List;

@Data
public class ProviderDTO {
    private String username;
    private String password;
    private String avatarUrl;
    private String token;

    private String role;
    private List<Menu>menus;
}

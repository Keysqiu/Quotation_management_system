package com.example.springboot.service;

import com.example.springboot.entity.Menu;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author qiuqingsheng
 * @since 2023-02-01
 */
public interface IMenuService extends IService<Menu> {

    List<Menu> findMunus(String name);
}

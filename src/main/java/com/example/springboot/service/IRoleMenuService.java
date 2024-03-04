package com.example.springboot.service;

import com.example.springboot.entity.RoleMenu;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author qiuqingsheng
 * @since 2023-02-02
 */
public interface IRoleMenuService extends IService<RoleMenu> {

    void setRoleMenu(Integer roleId, List<Integer> menuIds);

    List<Integer> getRoleMenu(Integer roleId);
}

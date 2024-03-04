package com.example.springboot.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.springboot.common.Result;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;


import com.example.springboot.service.IRoleMenuService;
import com.example.springboot.entity.RoleMenu;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author qiuqingsheng
 * @since 2023-02-02
 */
@RestController
@RequestMapping("/role-menu")
public class RoleMenuController {

    @Resource
    private IRoleMenuService roleMenuService;

    @PostMapping
    public Boolean save(@RequestBody RoleMenu roleMenu) {
        //新增或更新
        return roleMenuService.saveOrUpdate(roleMenu);
    }

    @DeleteMapping("/{id}")
    public Boolean delete(@PathVariable Integer id) {
        //删除
        return roleMenuService.removeById(id);
    }

    @PostMapping("/del/batch")
    public boolean deleteBatch(@RequestBody List<Integer> ids) {
        return roleMenuService.removeByIds(ids);
    }

    @GetMapping
    public List<RoleMenu> findAll() {
        //查询所有
        return roleMenuService.list();
    }

    @GetMapping("/{id}")
    public RoleMenu findOne(@PathVariable Integer id) {
        //根据id查询
        return roleMenuService.getById(id);
    }

    @GetMapping("/page")
    public Page<RoleMenu> findPage(@RequestParam Integer pageNum,
                                   @RequestParam Integer pageSize) {
        //分页查询

        QueryWrapper<RoleMenu> queryWrapper = new QueryWrapper<>();
        //根据id进行倒序排列，让最新插入的数据显示在最前面
        queryWrapper.orderByDesc("id");
        return roleMenuService.page(new Page<>(pageNum, pageSize), queryWrapper);
    }

    /*
    * 绑定角色和菜单的关系
    *@return
    */
    @PostMapping("/roleMenu/{roleId}")
    public Result reloMenu(@PathVariable Integer roleId,@RequestBody List<Integer> menuIds){
        roleMenuService.setRoleMenu(roleId,menuIds);
        return Result.success();
    }


    @GetMapping("/roleMenu/{roleId}")
    public Result getReloMenu(@PathVariable Integer roleId){
        return Result.success(roleMenuService.getRoleMenu(roleId));
    }
}


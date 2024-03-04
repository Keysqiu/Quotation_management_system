package com.example.springboot.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.springboot.common.Constants;
import com.example.springboot.common.Result;
import com.example.springboot.entity.Dict;
import com.example.springboot.entity.Menu;
import com.example.springboot.service.IDictService;
import com.example.springboot.service.IMenuService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author qiuqingsheng
 * @since 2023-02-01
 */
@RestController
@RequestMapping("/menu")
public class MenuController {

    @Resource
    private IMenuService menuService;

    @Resource
    private IDictService dictService;

    @PostMapping
    public Result save(@RequestBody Menu menu) {
        //新增或更新
        return Result.success(menuService.saveOrUpdate(menu));
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        //删除
        return Result.success(menuService.removeById(id));
    }

    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        return Result.success(menuService.removeByIds(ids));
    }

    @GetMapping
    public Result findAll(@RequestParam(defaultValue = "") String name) {
        return Result.success(menuService.findMunus(name));
    }

    @GetMapping("/ids")
    public Result findAllds() {
        return Result.success(menuService.list().stream().map(Menu::getId));
    }

    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id) {
        //根据id查询
        return Result.success(menuService.getById(id));
    }

    @GetMapping("/page")
    public Page<Menu> findPage(@RequestParam Integer pageNum,
                               @RequestParam Integer pageSize,
                               @RequestParam(defaultValue = "") String name) {
        //分页查询

        QueryWrapper<Menu> queryWrapper = new QueryWrapper<>();
        if (!"".equals(name)) {
            queryWrapper.like("name", name);
        }
        //根据id进行倒序排列，让最新插入的数据显示在最前面
        queryWrapper.orderByDesc("id");
        return menuService.page(new Page<>(pageNum, pageSize), queryWrapper);
    }

    @GetMapping("/icons")
    public Result getIcons() {
        QueryWrapper<Dict> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("type", Constants.DICT_TYPE_ICON);
        return Result.success(dictService.list(queryWrapper));
    }
}


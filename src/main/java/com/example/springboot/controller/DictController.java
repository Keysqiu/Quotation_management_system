package com.example.springboot.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;


import com.example.springboot.service.IDictService;
import com.example.springboot.entity.Dict;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author qiuqingsheng
 * @since 2023-02-02
 */
@RestController
@RequestMapping("/dict")
public class DictController {
    
    @Resource
    private IDictService dictService;

    @PostMapping
    public Boolean save(@RequestBody Dict dict){
            //新增或更新
            return dictService.saveOrUpdate(dict);
        }

    @DeleteMapping("/{id}")
    public Boolean delete(@PathVariable Integer id) {
            //删除
            return dictService.removeById(id);
        }

    @PostMapping("/del/batch")
    public boolean deleteBatch(@RequestBody List<Integer> ids) {
            return dictService.removeByIds(ids);
        }

    @GetMapping
    public List<Dict> findAll() {
        //查询所有
            return dictService.list();
        }

    @GetMapping("/{id}")
    public Dict findOne(@PathVariable Integer id) {
        //根据id查询
            return dictService.getById(id);
        }

    @GetMapping("/page")
    public Page<Dict> findPage(@RequestParam Integer pageNum,
                                    @RequestParam Integer pageSize) {
        //分页查询

        QueryWrapper<Dict> queryWrapper = new QueryWrapper<>();
        //根据id进行倒序排列，让最新插入的数据显示在最前面
        queryWrapper.orderByDesc("id");
        return dictService.page(new Page<>(pageNum, pageSize),queryWrapper);
        }
}


package com.example.springboot.service.impl;

import com.example.springboot.entity.Goods;
import com.example.springboot.mapper.GoodsMapper;
import com.example.springboot.service.IGoodsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author qiuqingsheng
 * @since 2023-11-20
 */
@Service
public class GoodsServiceImpl extends ServiceImpl<GoodsMapper, Goods> implements IGoodsService {

    @Resource
    private GoodsMapper goodsMapper;
    public boolean insertOne(Goods goods){
        return goodsMapper.insertOne(goods);
    }

    @Override
    public boolean updateOne(Goods goods) {
        return goodsMapper.updateOne(goods);
    }

}

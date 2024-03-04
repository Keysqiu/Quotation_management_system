package com.example.springboot.service;

import com.example.springboot.entity.Goods;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author qiuqingsheng
 * @since 2023-11-20
 */
public interface IGoodsService extends IService<Goods> {
    boolean insertOne(Goods goods);

    boolean updateOne(Goods goods);


}

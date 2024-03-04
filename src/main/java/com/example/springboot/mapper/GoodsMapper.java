package com.example.springboot.mapper;

import com.example.springboot.entity.Goods;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author qiuqingsheng
 * @since 2023-11-20
 */
@Mapper
public interface GoodsMapper extends BaseMapper<Goods> {

     boolean insertOne(Goods goods);

     boolean updateOne(Goods goods);

}

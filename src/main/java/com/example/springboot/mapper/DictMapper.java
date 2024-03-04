package com.example.springboot.mapper;

import com.example.springboot.entity.Dict;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author qiuqingsheng
 * @since 2023-02-02
 */
@Mapper
public interface DictMapper extends BaseMapper<Dict> {

}

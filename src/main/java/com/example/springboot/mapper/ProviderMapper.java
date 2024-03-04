package com.example.springboot.mapper;

import com.example.springboot.entity.Provider;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author qiuqingsheng
 * @since 2023-11-25
 */
@Mapper
public interface ProviderMapper extends BaseMapper<Provider> {
    @Select("select id from sys_provider where username=#{username}")
    public int getIdByName(String username);

    boolean updateByName(Provider provider);
}

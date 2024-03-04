package com.example.springboot.mapper;

import com.example.springboot.entity.Provider;
import com.example.springboot.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author qiuqingsheng
 * @since 2023-01-23
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
    boolean updateByName(User user);
}

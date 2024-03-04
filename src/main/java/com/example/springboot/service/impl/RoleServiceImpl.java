package com.example.springboot.service.impl;

import com.example.springboot.entity.Role;
import com.example.springboot.mapper.RoleMapper;
import com.example.springboot.service.IRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author qiuqingsheng
 * @since 2023-02-01
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements IRoleService {

}

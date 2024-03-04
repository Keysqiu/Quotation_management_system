package com.example.springboot.service.impl;

import com.example.springboot.entity.Dict;
import com.example.springboot.mapper.DictMapper;
import com.example.springboot.service.IDictService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author qiuqingsheng
 * @since 2023-02-02
 */
@Service
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict> implements IDictService {

}

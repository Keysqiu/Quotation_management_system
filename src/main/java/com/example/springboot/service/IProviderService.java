package com.example.springboot.service;

import com.example.springboot.controller.dto.ProviderDTO;
import com.example.springboot.controller.dto.UserDTO;
import com.example.springboot.entity.Provider;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author qiuqingsheng
 * @since 2023-11-25
 */
public interface IProviderService extends IService<Provider> {

    ProviderDTO login(ProviderDTO userDTO);
    Provider register(ProviderDTO providerDTO);

    int getIdByName(String username);

    boolean updateByName(Provider provider);
}

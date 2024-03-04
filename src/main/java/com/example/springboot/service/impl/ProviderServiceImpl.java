package com.example.springboot.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.log.Log;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.springboot.common.Constants;
import com.example.springboot.common.PasswordUtils;
import com.example.springboot.controller.dto.ProviderDTO;
import com.example.springboot.controller.dto.UserDTO;
import com.example.springboot.entity.Menu;
import com.example.springboot.entity.Provider;
import com.example.springboot.entity.User;
import com.example.springboot.exception.ServiceException;
import com.example.springboot.mapper.ProviderMapper;
import com.example.springboot.mapper.RoleMapper;
import com.example.springboot.mapper.RoleMenuMapper;
import com.example.springboot.service.IMenuService;
import com.example.springboot.service.IProviderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.springboot.utils.TokenUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author qiuqingsheng
 * @since 2023-11-25
 */
@Service
public class ProviderServiceImpl extends ServiceImpl<ProviderMapper, Provider> implements IProviderService {

    private static final Log LOG = Log.get();
    @Resource
    private ProviderMapper providerMapper;
    @Resource
    private RoleMapper roleMapper;

    @Resource
    private RoleMenuMapper roleMenuMapper;

    @Resource
    private IMenuService menuService;

    @Override
    public ProviderDTO login(ProviderDTO providerDTO) {
        Provider one = getUserInfo(providerDTO);
        if (one != null && PasswordUtils.check(providerDTO.getPassword(),one.getPassword())) {
            //把查出来one的属性的值拷贝到userDTO属性的值里，并忽略大小写
            BeanUtil.copyProperties(one, providerDTO, true);
            //设置token
            String token = TokenUtils.genToken(one.getUuid(), one.getPassword());
            providerDTO.setToken(token);

            //获取登录用户的角色
            String role = one.getRole();
            List<Menu> roleMenus = getRoleMenus(role);
            //设置用户的菜单列表
            providerDTO.setMenus(roleMenus);
            return providerDTO;
        } else {
            throw new ServiceException(Constants.CODE_600, "用户名或密码错误");
        }

    }

    @Override
    public Provider register(ProviderDTO userDTO) {
        //在数据库查找有无此人信息
        Provider one = getUserInfo(userDTO);
        if (one == null) {
            //没有就加入此人
            one = new Provider();
            //获取前端传来的密码
            String password = userDTO.getPassword();
            System.out.println("原注册密码："+password);
            //使用MD5 + salt散列对密码进行加密
            //注册成功之后要将密码加盐，写进数据库中
            userDTO.setPassword(PasswordUtils.encrypt(userDTO.getPassword()));
            //设置初始用户的角色 -- 用户
            userDTO.setRole("ROLE_PROVIDER");
            BeanUtil.copyProperties(userDTO, one, true);
            String simpleUUID = IdUtil.simpleUUID();
            one.setUuid(simpleUUID);
            save(one); //把copy完之后的用户对象存储到数据库
            return one;
        } else {
            throw new ServiceException(Constants.CODE_600, "用户名已存在");
        }
    }

    @Override
    public int getIdByName(String username) {
        return providerMapper.getIdByName(username);
    }

    @Override
    public boolean updateByName(Provider provider) {
        return providerMapper.updateByName(provider);
    }

    private Provider getUserInfo(ProviderDTO providerDTO) {
        QueryWrapper<Provider> queryWrapper = new QueryWrapper<>();
        //获取前端传来的密码
        String password = providerDTO.getPassword();
        System.out.println("原输入的密码："+password);

        queryWrapper.eq("username", providerDTO.getUsername());

        Provider one;
        try {
            one = getOne(queryWrapper);
        } catch (Exception e) {
            //如果查出多个，那么就是系统错误了，因为一个系统中不允许出现两个用户名和密码相同的人
            LOG.error(e);
            throw new ServiceException(Constants.CODE_500, "系统错误");
        }
        return one;
    }

    /*
     * 获取当前用户所有的菜单项
     *@return
     */
    private List<Menu> getRoleMenus(String roleFlag) {
        Integer roleId = roleMapper.selectByFlag(roleFlag);
        //当前角色的所有菜单id集合
        List<Integer> menuIds = roleMenuMapper.selectByRoleId(roleId);

        //查出系统所有的菜单
        List<Menu> menus = menuService.findMunus("");
        // new一个最后筛选完成的list
        List<Menu> roleMenus = new ArrayList<>();
        //筛选当前用户角色的菜单
        for (Menu menu : menus) {
            if (menuIds.contains(menu.getId())) {
                roleMenus.add(menu);
            }
            List<Menu> children = menu.getChildren();
            //removeIf() 移除children里面不在menuIds集合中的元素
            children.removeIf(child -> !menuIds.contains(child.getId()));
        }

        return roleMenus;
    }
}

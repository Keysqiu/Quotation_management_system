package com.example.springboot.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.log.Log;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.springboot.common.Constants;
import com.example.springboot.common.PasswordUtils;
import com.example.springboot.controller.dto.UserDTO;
import com.example.springboot.entity.Menu;
import com.example.springboot.entity.Provider;
import com.example.springboot.entity.User;
import com.example.springboot.exception.ServiceException;
import com.example.springboot.mapper.RoleMapper;
import com.example.springboot.mapper.RoleMenuMapper;
import com.example.springboot.mapper.UserMapper;
import com.example.springboot.service.IMenuService;
import com.example.springboot.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.springboot.utils.TokenUtils;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author qiuqingsheng
 * @since 2023-01-23
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    private static final Log LOG = Log.get();

    @Resource
    private RoleMapper roleMapper;

    @Resource
    private RoleMenuMapper roleMenuMapper;

    @Resource
    private IMenuService menuService;

    @Resource
    private UserMapper userMapper;

    @Override
    public UserDTO login(UserDTO userDTO) {
        User one = getUserInfo(userDTO);
        if (one != null && PasswordUtils.check(userDTO.getPassword(),one.getPassword())) {
            //把查出来one的属性的值拷贝到userDTO属性的值里，并忽略大小写
            BeanUtil.copyProperties(one, userDTO, true);
            //设置token
            String token = TokenUtils.genToken(one.getUuid(), one.getPassword());
            userDTO.setToken(token);

            //获取登录用户的角色
            String role = one.getRole();
            List<Menu> roleMenus = getRoleMenus(role);
            //设置用户的菜单列表
            userDTO.setMenus(roleMenus);
            return userDTO;
        } else {
            throw new ServiceException(Constants.CODE_600, "用户名或密码错误");
        }

    }

    @Override
    public User register(UserDTO userDTO) {
        //在数据库查找有无此人信息
        User one = getUserInfo(userDTO);
        if (one == null) {
            //没有就加入此人
            one = new User();
            //获取前端传来的密码
            String password = userDTO.getPassword();
            System.out.println("原注册密码："+password);
            //使用MD5 + salt散列对密码进行加密
            //注册成功之后要将密码加盐，写进数据库中
            userDTO.setPassword(PasswordUtils.encrypt(userDTO.getPassword()));
            //设置初始用户的角色 -- 用户
            userDTO.setRole("ROLE_USER");
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
    public boolean updateByName(User user) {
        return userMapper.updateByName(user);
    }

    private User getUserInfo(UserDTO userDTO) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        //获取前端传来的密码
        String password = userDTO.getPassword();
        System.out.println("原输入的密码："+password);

        queryWrapper.eq("username", userDTO.getUsername());

        User one;
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

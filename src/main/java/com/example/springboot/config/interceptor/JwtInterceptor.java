package com.example.springboot.config.interceptor;

import cn.hutool.core.util.StrUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.springboot.common.Constants;
import com.example.springboot.entity.Provider;
import com.example.springboot.entity.User;
import com.example.springboot.exception.ServiceException;
import com.example.springboot.service.IProviderService;
import com.example.springboot.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author qiuqingsheng
 * @since 2023-11-27
 * @description: 具体逻辑为，用户在登录时，使用utils包下的TokenUtils获取到一个两小时的token，且这个token是拿用户的唯一标识符uuid与密码password来封装的，
 * 在用户每次对不放行的接口（具体可看InterceptorConfig类）进行请求时，都会进以下重写的preHandle方法，用于判定用户的token是否有效或登录已超时。
 */

public class JwtInterceptor implements HandlerInterceptor {
    @Autowired
    private IUserService userService;
    @Autowired
    private IProviderService providerService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String token = request.getHeader("token");
        System.out.println("token" + token);
        //如果不是映射到方法直接通过
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        //执行认证
        if (StrUtil.isBlank(token)) {
            throw new ServiceException(Constants.CODE_401, "无token，请重新登录");
        }
        //读取token中的userId
        String Uuid;
        try {
            Uuid = JWT.decode(token).getAudience().get(0);
            System.out.println("userId" + Uuid);
        } catch (JWTDecodeException j) {
//            throw new ServiceException(Constants.CODE_401, "token已过期，请重新登录");
            throw new ServiceException(Constants.CODE_401, "登录已超时，请重新登录");
        }
        QueryWrapper<User> queryWrapper_User = new QueryWrapper<>();
        queryWrapper_User.eq("uuid", Uuid);
        //根据token中的Uuid查询用户表有无此人
        User user = userService.getOne(queryWrapper_User);

        QueryWrapper<Provider> queryWrapper_Provider = new QueryWrapper<>();
        queryWrapper_Provider.eq("uuid", Uuid);
        //根据token中的Uuid查询供应商表有无此人
        Provider provider = providerService.getOne(queryWrapper_Provider);
        if (user == null && provider == null) {
            throw new ServiceException(Constants.CODE_401, "用户不存在，请重新登录");
        }
        JWTVerifier jwtVerifier;
        //用户密码加签验证token
        if (user != null && provider == null) {
            jwtVerifier = JWT.require(Algorithm.HMAC256(user.getPassword())).build();
        } else {
            jwtVerifier = JWT.require(Algorithm.HMAC256(provider.getPassword())).build();
        }
        try {
            jwtVerifier.verify(token);  //验证token
        } catch (JWTVerificationException e) {
//            throw new ServiceException(Constants.CODE_401, "token已过期，请重新登录");
            throw new ServiceException(Constants.CODE_401, "登录已超时，请重新登录");
        }

        return true;
    }
}

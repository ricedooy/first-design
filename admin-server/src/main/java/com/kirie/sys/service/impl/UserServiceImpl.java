package com.kirie.sys.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kirie.sys.entity.User;
import com.kirie.sys.mapper.UserMapper;
import com.kirie.sys.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kirie.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author kirie
 * @since 2023-06-06
 */
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Map<String, Object> login(User user) {
        // 根据用户名查询
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, user.getUsername());
        User userInfo = this.baseMapper.selectOne(wrapper);
        // 结果不为空，则生成token，并将用户信息存入redis
        if (userInfo != null && passwordEncoder.matches(user.getPassword(), userInfo.getPassword())) {
            // 暂时用UUID，终极方案是jwt
            String key = "user:" + UUID.randomUUID();
            log.info("key: {}", key);

            // jwt
            /*Map<String, Object> claims = new HashMap<>();
            claims.put("id", userInfo.getId());
            claims.put("username", userInfo.getUsername());
            String jwt = JwtUtils.generateJwt(claims);*/


            // 存入redis
            userInfo.setPassword(null);
            redisTemplate.opsForValue().set(key, userInfo, 30, TimeUnit.MINUTES);

            // 返回数据
            Map<String, Object> data = new HashMap<>();
            data.put("token", key);
            return data;
        }

        return null;
    }

    /*@Override
    public Map<String, Object> login(User user) {
        // 根据用户名和密码查询
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, user.getUsername());
        wrapper.eq(User::getPassword, user.getPassword());
        User userInfo = this.baseMapper.selectOne(wrapper);
        // 结果不为空，则生成token，并将用户信息存入redis
        if (userInfo != null) {
            // 暂时用UUID，终极方案是jwt
            String key = "user:" + UUID.randomUUID();

            // 存入redis
            userInfo.setPassword(null);
            redisTemplate.opsForValue().set(key, userInfo, 30, TimeUnit.MINUTES);

            // 返回数据
            Map<String, Object> data = new HashMap<>();
            data.put("token", key);
            return data;
        }

        return null;
    }*/

    @Override
    public Map<String, Object> getUserInfo(String token) {
        // 根据token获取用户信息
        Object obj = redisTemplate.opsForValue().get(token);
        if (obj != null) {
            User userInfo = JSON.parseObject(JSON.toJSONString(obj), User.class);
            Map<String, Object> data = new HashMap<>();
            data.put("name", userInfo);
            data.put("avatar", userInfo.getAvatar());

            // 角色
            List<String> roleList = this.baseMapper.getRoleNameByUserId(userInfo.getId());
            data.put("roles", roleList);

            return data;
        }
        return null;
    }

    @Override
    public void logout(String token) {
        redisTemplate.delete(token);
    }
}

package com.kirie.sys.mapper;

import com.kirie.sys.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author kirie
 * @since 2023-06-06
 */
public interface UserMapper extends BaseMapper<User> {

    List<String> getRoleNameByUserId(Integer userId);

}

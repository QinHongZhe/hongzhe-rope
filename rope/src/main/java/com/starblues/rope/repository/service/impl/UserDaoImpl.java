package com.starblues.rope.repository.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.starblues.rope.repository.entity.User;
import com.starblues.rope.repository.mapper.UserMapper;
import com.starblues.rope.repository.service.UserDao;
import org.springframework.stereotype.Service;

/**
 * UserDaoImpl
 *
 * @author zhangzhuo
 * @version 1.0
 */
@Service
public class UserDaoImpl extends ServiceImpl<UserMapper, User> implements UserDao {
}

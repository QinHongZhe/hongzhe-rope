package com.starblues.rope.repository.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.starblues.rope.repository.entity.ProcessInfo;
import com.starblues.rope.repository.mapper.ProcessInfoMapper;
import com.starblues.rope.repository.service.ProcessInfoDao;
import org.springframework.stereotype.Service;

/**
 * description
 *
 * @author zhangzhuo
 * @version 1.0
 */
@Service
public class ProcessInfoDaoImpl extends ServiceImpl<ProcessInfoMapper, ProcessInfo> implements ProcessInfoDao {
}

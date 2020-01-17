package com.starblues.rope.repository.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.starblues.rope.repository.entity.SyncInfo;
import com.starblues.rope.repository.mapper.SyncInfoMapper;
import com.starblues.rope.repository.service.SyncInfoDao;
import org.springframework.stereotype.Service;

/**
 * description
 *
 * @author zhangzhuo
 * @version 1.0
 */
@Service
public class SyncInfoDaoImpl extends ServiceImpl<SyncInfoMapper, SyncInfo> implements SyncInfoDao {
}

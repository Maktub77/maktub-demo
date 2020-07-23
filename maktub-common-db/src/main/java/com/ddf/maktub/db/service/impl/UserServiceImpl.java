package com.ddf.maktub.db.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.ddf.maktub.common.constant.CommonConstant;
import com.ddf.maktub.common.exceptions.RException;
import com.ddf.maktub.common.utils.DateUtil;
import com.ddf.maktub.common.utils.ShiroUtils;
import com.ddf.maktub.common.utils.SnowflakeIdWorker;
import com.ddf.maktub.db.entity.UserEntity;
import com.ddf.maktub.db.mapper.UserMapper;
import com.ddf.maktub.db.service.UserService;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.stereotype.Service;



@Service("userService")
public class UserServiceImpl extends ServiceImpl<UserMapper, UserEntity> implements UserService {


    @Override
    public UserEntity getUserByName(String userName) {

        UserEntity userEntity = new UserEntity();
        userEntity.setUserName(userName);

        return baseMapper.selectOne(userEntity);
    }


    /**
     * 创建用户
     *
     * @param entity
     * @return
     */
    @Override
    public boolean insert(UserEntity entity) {
        entity.setId(SnowflakeIdWorker.getId());
        entity.setSalt(RandomStringUtils.randomAlphanumeric(20));
        entity.setPassWord(ShiroUtils.sha256(entity.getPassWord(), entity.getSalt()));
        entity.setCreateTime(DateUtil.getTime());
        entity.setType(CommonConstant.UserType.Ordinary.getValue());
        return super.insert(entity);
    }

    @Override
    public boolean updateById(UserEntity entity) {
        entity.setUpdateTime(DateUtil.getTime());
        return super.updateById(entity);
    }

    @Override
    public void modifyPasswd(String userId, String oldPasswd, String passwd) {

        UserEntity userEntity = baseMapper.selectById(userId);

        if (!userEntity.getPassWord().equals(ShiroUtils.sha256(oldPasswd, userEntity.getSalt()))) {
            throw new RException("原密码不正确");
        }

        userEntity.setSalt(RandomStringUtils.randomAlphanumeric(20));
        userEntity.setPassWord(ShiroUtils.sha256(passwd, userEntity.getSalt()));
        userEntity.setType(CommonConstant.UserType.Ordinary.getValue());
        baseMapper.updateById(userEntity);
    }
}

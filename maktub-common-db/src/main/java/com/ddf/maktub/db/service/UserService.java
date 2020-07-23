package com.ddf.maktub.db.service;

import com.baomidou.mybatisplus.service.IService;
import com.ddf.maktub.db.entity.UserEntity;

public interface UserService extends IService<UserEntity> {


    /**
     * 根据用户名查找用户
     * 无论是否查询都正常返回
     *
     * @param userName
     * @return
     */
    UserEntity getUserByName(String userName);



    /**
     * 修改密码
     * @param userId 用户ID
     * @param oldPasswd 旧密码
     * @param passwd 新密码
     */
    void modifyPasswd(String userId, String oldPasswd, String passwd);
}


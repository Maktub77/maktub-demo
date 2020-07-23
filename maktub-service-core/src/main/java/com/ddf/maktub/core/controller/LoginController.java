package com.ddf.maktub.core.controller;


import com.ddf.maktub.common.models.R;
import com.ddf.maktub.common.valid.VG;
import com.ddf.maktub.db.entity.UserEntity;
import com.ddf.maktub.db.service.UserService;
import com.ddf.maktub.shiro.token.TokenService;
import com.ddf.maktub.shiro.utils.ShiroUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
public class LoginController extends AbstractController {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UserService userService;

    /**
     * 登陆
     *
     * @param userEntity
     * @return
     */
    @PostMapping("/login")
    public R login(@RequestBody @Validated(VG.Login.class) UserEntity userEntity) {


        UserEntity user = userService.getUserByName(userEntity.getUserName());

        //账号不存在、密码错误
        if (user == null || !user.getPassWord().equals(ShiroUtils.sha256(userEntity.getPassWord(), user.getSalt()))) {
            return R.error("账号或密码不正确");
        }

        return R.ok().put("token", tokenService.createToken(user));
    }


    /**
     * 登出
     *
     * @return
     */
    @PostMapping("/login/out")
    @ResponseBody
    public R out() {

        tokenService.cleanToken(getUser().getToken());

        return R.ok();
    }


}




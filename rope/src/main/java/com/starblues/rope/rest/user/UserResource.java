package com.starblues.rope.rest.user;

import com.starblues.rope.rest.common.BaseResource;
import com.starblues.rope.rest.common.Result;
import com.starblues.rope.service.user.UserService;
import com.starblues.rope.service.user.model.FrontUserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 用户信息
 *
 * @author zhangzhuo
 * @version 1.0
 */
@RestController
@RequestMapping("user")
@Slf4j
public class UserResource extends BaseResource {

    private final UserService userService;

    public UserResource(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("info")
    public Result<FrontUserInfo> info() {
        return responseBody(Result.ResponseEnum.GET_SUCCESS, userService.getFrontUserInfo());
    }


    @PutMapping("password")
    public Result<String> updatePassword(@RequestParam("oldPassword") String oldPassword,
                                         @RequestParam("newPassword") String newPassword) {
        try {
            boolean result = userService.updatePassword(oldPassword, newPassword);
            if(result){
                return responseBody(Result.ResponseEnum.OPERATE_SUCCESS, "修改密码成功");
            } else {
                return responseBody(Result.ResponseEnum.OPERATE_ERROR, "修改密码失败");
            }
        } catch (Exception e) {
            return responseBody(Result.ResponseEnum.OPERATE_ERROR, "修改密码失败. " + e.getMessage());
        }
    }


}

package com.starblues.rope.service.user.model;

import lombok.Data;

import java.util.Set;

/**
 * description
 *
 * @author zhangzhuo
 * @version 1.0
 */
@Data
public class FrontUserInfo {


    private UserInfo userInfo;
    private String roles;
    private Set<String> permission;


    @Data
    public static class UserInfo {

        private String username;
        private String name;
        private String avatar;
    }

}

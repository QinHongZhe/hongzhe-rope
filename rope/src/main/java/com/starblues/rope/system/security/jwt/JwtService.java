package com.starblues.rope.system.security.jwt;

import io.jsonwebtoken.Claims;

import java.util.Map;
import java.util.function.Consumer;

/**
 * jwt 服务
 *
 * @author zhangzhuo
 * @version 1.0
 */
public interface JwtService {

    /**
     * 加密jwt
     * @param id id
     * @param ttlMillis 过期时间
     * @param claims add 加密信息
     * @return String
     */
    String encrypt(String id, long ttlMillis, Consumer<Map<String, Object>> claims);

    /**
     * 加密jwt
     * @param ttlMillis 过期时间
     * @param claims add 加密信息
     * @return String
     */
    String encrypt(long ttlMillis, Consumer<Map<String, Object>> claims);


    /**
     * 校验jwt
     * @param jwt jwt加密字符串
     * @return 成功 true. 失败 false
     */
    boolean verify(String jwt);

    /**
     * 解密jwt
     * @param jwt jwt加密字符串
     * @return 成功 true. 失败 false
     * @throws Exception 解密异常
     */
    Claims decode(String jwt) throws Exception;


}

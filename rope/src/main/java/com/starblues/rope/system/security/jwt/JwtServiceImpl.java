package com.starblues.rope.system.security.jwt;

import com.google.common.collect.Maps;
import com.starblues.rope.config.configuration.JwtConfiguration;
import com.starblues.rope.utils.IDUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Date;
import java.util.Map;
import java.util.function.Consumer;

/**
 * jwt实现
 *
 * @author zhangzhuo
 * @version 1.0
 */
@Component
@Slf4j
public class JwtServiceImpl implements JwtService{

    private final JwtConfiguration jwtConfiguration;
    private final SecretKey secretKey;

    public JwtServiceImpl(JwtConfiguration jwtConfiguration) {
        this.jwtConfiguration = jwtConfiguration;
        this.secretKey = generalKey();
    }


    @Override
    public String encrypt(String id, long ttlMillis, Consumer<Map<String, Object>> claims) {
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        JwtBuilder builder = Jwts.builder()
                .setIssuedAt(now)
                .signWith(signatureAlgorithm(), this.secretKey);
        if(StringUtils.isEmpty(id)){
            builder.setId(IDUtils.uuid());
        } else {
            builder.setId(id);
        }
        Map<String, Object> claimsMap = Maps.newHashMap();
        claims.accept(claimsMap);
        if(!claimsMap.isEmpty()){
            builder.setClaims(claimsMap);
        }
        if (ttlMillis >= 0) {
            long expMillis = nowMillis + ttlMillis;
            Date exp = new Date(expMillis);
            builder.setExpiration(exp);
        }
        return builder.compact();
    }

    @Override
    public String encrypt(long ttlMillis, Consumer<Map<String, Object>> claims) {
        return encrypt(null, ttlMillis, claims);
    }

    @Override
    public boolean verify(String jwt) {
        try {
            decode(jwt);
            return true;
        } catch (Exception e){
            return false;
        }
    }

    @Override
    public Claims decode(String jwt) throws Exception{
        if(StringUtils.isEmpty(jwt)){
            throw new IllegalArgumentException("Jwt can't empty");
        }
        return Jwts.parser()
                .setSigningKey(this.secretKey)
                .parseClaimsJws(jwt)
                .getBody();
    }


    protected SignatureAlgorithm signatureAlgorithm(){
        return SignatureAlgorithm.HS256;
    }


    protected SecretKey generalKey(){
        byte[] encodedKey = Base64.decodeBase64(jwtConfiguration.getSecret());
        return new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
    }

}

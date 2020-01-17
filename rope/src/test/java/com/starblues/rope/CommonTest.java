package com.starblues.rope;

import com.google.common.collect.Lists;
import com.starblues.rope.config.configuration.JwtConfiguration;
import com.starblues.rope.system.security.jwt.JwtService;
import com.starblues.rope.system.security.jwt.JwtServiceImpl;
import com.starblues.rope.utils.IDUtils;
import io.jsonwebtoken.Claims;
import org.apache.logging.log4j.message.FormattedMessageFactory;
import org.apache.logging.log4j.message.Message;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 公用测试
 *
 * @author zhangzhuo
 * @version 1.0
 */
public class CommonTest {

    @Test
    public void date(){
        LocalDateTime localDateTime = LocalDateTime.now();
        localDateTime = localDateTime.plusSeconds(60L);
        Date date = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        System.out.println(date);
    }

    @Test
    public void log(){
        FormattedMessageFactory formattedMessageFactory = new FormattedMessageFactory();
        Message message = formattedMessageFactory.newMessage("dde {} dew {} -> {}", "343", 11, CommonTest.class);
        System.out.println(message.getFormattedMessage());
        System.out.println(message.getFormat());
    }


    @Test
    public void xx(){
        AtomicInteger atomicInteger = new AtomicInteger(0);
        atomicInteger.addAndGet(10);
        atomicInteger.addAndGet(2);
        int i = atomicInteger.addAndGet(12);
        System.out.println(i);
    }

    @Test
    public void aa() throws InterruptedException {
        ScheduledExecutorService executor = new ScheduledThreadPoolExecutor(1);


        ScheduledFuture<?> scheduledFuture = executor.schedule(() -> {
            System.out.println("defre");
        }, 1, TimeUnit.SECONDS);
        Thread.sleep(5000);
        scheduledFuture.cancel(true);
        System.out.println("取消成功");
        Thread.sleep(1000000);
    }


    @Test
    public void list()  {
        List<String> s = Lists.newArrayList();
        s.add("1");
        s.add("2");
        s.add("3");
        s.add("4");

        for (int i = s.size() - 1; i >= 0; i--) {
            System.out.println(s.get(i));
        }
    }

    @Test
    public void jwt() throws Exception {
        JwtConfiguration jwtConfiguration = new JwtConfiguration();
        jwtConfiguration.setSecret("A272ADAA36372C828E057F029D300BA2");
        JwtService jwtService = new JwtServiceImpl(jwtConfiguration);

        String encrypt = jwtService.encrypt(5000, claims -> {
            claims.put("name", "zhangzhuo");
        });

        System.out.println(encrypt);




    }


    @Test
    public void jwtDecode() throws Exception {
        JwtConfiguration jwtConfiguration = new JwtConfiguration();
        jwtConfiguration.setSecret("A272ADAA36372C828E057F029D300BA2");
        JwtService jwtService = new JwtServiceImpl(jwtConfiguration);
        String encrypt = "eyJhbGciOiJIUzI1NiJ9.eyJuYW1lIjoiemhhbmd6aHVvIiwiZXhwIjoxNTc3OTMwODM3fQ.eeSL3rKbwf8L0lDsku7GQI4BYH7vh9eXFRHHAT_jDTM";

        Claims decode = jwtService.decode(encrypt);
        String name = decode.get("name", String.class);
        System.out.println("name="+name);
    }


    @Test
    public void genAdminPassword() throws Exception {
        String uuid = IDUtils.uuid();
        Md5Hash md5Hash = new Md5Hash("123456", uuid, 2);
        System.out.println(uuid);
        System.out.println(md5Hash);
    }


}

package com.starblues.rope.system;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * description
 *
 * @author zhangzhuo
 * @version 1.0
 */
@Component
@Slf4j
public class StartListener implements ApplicationListener<ContextRefreshedEvent> {


    @Value("${server.port}")
    private String port;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        log.info("Start to finish. Management address: {}", "http://127.0.0.1:"+port+"/admin");
    }
}

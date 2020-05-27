package com.starblues.rope.utils;

import com.google.common.collect.Maps;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

/**
 * 占位符解析器测试
 * @author zhangzhuo
 * @version 1.0
 * @since 2020-05-27
 */
public class PlaceholderResolverTest {

    @Test
    public void format(){
        PlaceholderResolver placeholderResolver = new PlaceholderResolver();
        Map<String, Object> valueMap = Maps.newHashMap();
        valueMap.put("name", "zhangzhuo");
        valueMap.put("value", 123);
        String resolve = placeholderResolver.resolveByMap("name:${name}-value-${value}", valueMap);
        Assert.assertEquals("name:zhangzhuo-value-123", resolve);
    }

}

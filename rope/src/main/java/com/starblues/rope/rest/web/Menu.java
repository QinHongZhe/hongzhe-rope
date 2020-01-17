package com.starblues.rope.rest.web;

import com.google.common.collect.Lists;
import lombok.Data;

import java.util.List;

/**
 * description
 *
 * @author zhangzhuo
 * @version 1.0
 */
@Data
public class Menu {

    private Integer id;
    private String label;
    private String path;
    private String component;
    private String icon;
    private List<Menu> children = Lists.newArrayList();

}

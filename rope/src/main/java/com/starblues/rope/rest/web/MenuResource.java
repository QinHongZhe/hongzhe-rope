package com.starblues.rope.rest.web;

import com.gitee.starblues.integration.application.PluginApplication;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.starblues.rope.rest.common.BaseResource;
import com.starblues.rope.rest.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * description
 *
 * @author zhangzhuo
 * @version 1.0
 */
@RestController
@RequestMapping(BaseResource.API + "/user")
public class MenuResource extends BaseResource {

    private Map<Integer, String> menuMap = new HashMap<>();
    private final PluginApplication pluginApplication;

    public MenuResource(PluginApplication pluginApplication){
        menuMap.put(0, "/web/index-menu.json");
        this.pluginApplication = pluginApplication;
    }


    @Autowired
    private Gson gson;

    @GetMapping("/topMenu")
    public Result getTopMenu(){
        return responseBody(Result.ResponseEnum.GET_SUCCESS, getMenu("/web/top-menu.json"));
    }


    @GetMapping("/menu/{type}")
    public Result getMenu(@PathVariable("type") Integer type,
                          HttpServletRequest request){
        String path = menuMap.get(type);
        InputStream resourceAsStream = MenuResource.class.getResourceAsStream(path);
        if(resourceAsStream == null){
            return responseBody(Result.ResponseEnum.GET_SUCCESS, Collections.emptyList());
        }
        Reader reader = new InputStreamReader(resourceAsStream, StandardCharsets.UTF_8);
        Type t = new TypeToken<List<Menu>>() {}.getType();
        List<Menu> menus = gson.fromJson(reader, t);
        return responseBody(Result.ResponseEnum.GET_SUCCESS, menus);
    }


    private List getMenu(String path){
        if(path == null){
            return Collections.emptyList();
        }
        InputStream resourceAsStream = MenuResource.class.getResourceAsStream(path);
        if(resourceAsStream == null){
            return Collections.emptyList();
        }
        Reader reader = new InputStreamReader(resourceAsStream, StandardCharsets.UTF_8);
        return gson.fromJson(reader, List.class);
    }


}

package com.xiaoxin008.redisusage.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 主页控制器
 *
 * @author xiaoxin008(313595055 @ qq.com)
 * @since 1.0.0
 */
@Controller
public class IndexController {

    @RequestMapping("/index")
    public String index(){
        return "index";
    }
}

package com.chenyuxin.xinaiagent.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author YuXin.Dev
 * @Date: 2025/7/10 16:31
 */
@RestController
@RequestMapping("/test")
public class TestController {
    @GetMapping
    public String test(){
        return "ok";
    }
}

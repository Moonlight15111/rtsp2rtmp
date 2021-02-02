package com.qingtengtech.dssdemo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 〈功能简述〉<br>
 * 〈〉
 *
 * @author Moonlight
 * @date 2020/12/14 16:23
 */
@Controller
public class IndexController {
    @GetMapping(value = "/index")
    public String index() {
        return "index";
    }
}

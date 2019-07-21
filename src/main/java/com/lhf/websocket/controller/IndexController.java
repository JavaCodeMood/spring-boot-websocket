package com.lhf.websocket.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: IndexController
 * @Description:
 * @Author: liuhefei
 * @Date: 2019/7/21
 * @blog: https://www.imooc.com/u/1323320/articles
 **/
@Controller
public class IndexController {

    @RequestMapping(value = "/websocket", method = RequestMethod.GET)
    public String index(){

        return "websocket";
    }
}

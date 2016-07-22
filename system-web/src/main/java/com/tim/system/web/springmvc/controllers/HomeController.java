package com.tim.system.web.springmvc.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by eminxta on 2016/07/06.
 */
@Controller
@RequestMapping("/home")
public class HomeController {

    public String Index(){
        return "/";
    }
}

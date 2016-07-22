package com.tim.system.web.springmvc.controllers;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by eminxta on 2016/07/19.
 */
@Controller
@SpringBootApplication
public class HomeTest {

    @ResponseBody
    @RequestMapping(value = "/")
    String home(){
        return "Hello home";
    }

    public static void Main(String[] args) throws Exception{
        SpringApplication.run(HomeTest.class,args);
    }
}

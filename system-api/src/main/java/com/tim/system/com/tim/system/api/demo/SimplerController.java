package com.tim.system.com.tim.system.api.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by eminxta on 2016/07/11.
 */
@RestController
@RequestMapping("/test")
@EnableAutoConfiguration
public class SimplerController {

    @RequestMapping(value="/hello",method = RequestMethod.GET)
    @ResponseBody
    public String hello(){
        return "hello world";
    }

    public static void main(String[] args) {
        SpringApplication.run(SimplerController.class,args);
    }
}

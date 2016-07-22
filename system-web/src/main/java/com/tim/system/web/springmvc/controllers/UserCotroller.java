package com.tim.system.web.springmvc.controllers;

import com.codahale.metrics.*;
import org.apache.catalina.startup.UserConfig;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.RequestMapping;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

/**
 * Created by eminxta on 2016/07/01.
 */
@Controller
@RequestMapping("/user")
public class UserCotroller {

    private static final MetricRegistry metrics = new MetricRegistry();
    private static Queue<String> queue = new LinkedBlockingDeque<String>();
    private static ConsoleReporter reporter = ConsoleReporter.forRegistry(metrics).build();

    private static final Meter requests = metrics.meter(MetricRegistry.name(UserCotroller.class,"request"));

    @RequestMapping("/login")
    public String Login(Map<String,String> map){

        reporter.start(3, TimeUnit.SECONDS);
        requests.mark();

        Gauge<Integer> gauge = new Gauge<Integer>() {
            @Override
            public Integer getValue() {
                return queue.size();
            }
        };
        metrics.register(MetricRegistry.name(UserCotroller.class,"pending-job","size"),gauge);

        JmxReporter jmxReporter = JmxReporter.forRegistry(metrics).build();
        jmxReporter.start();


        String username = map.get("username");
        String password = map.get("password");
        return "/";

        //return "redirect:hello";
    }

    public void initBinder(ServletRequestDataBinder binder){
        binder.registerCustomEditor(Date.class,new CustomDateEditor(new SimpleDateFormat("yyyy-MM-dd"),true));

    }

    public static void main(String[] args) throws InterruptedException {
        reporter.start(1, TimeUnit.SECONDS);

        //实例化一个Gauge
        Gauge<Integer> gauge = new Gauge<Integer>() {
            @Override
            public Integer getValue() {
                return queue.size();
            }
        };

        //注册到容器中
        metrics.register(MetricRegistry.name(UserCotroller.class, "pending-job", "size"), gauge);

        //测试JMX
        JmxReporter jmxReporter = JmxReporter.forRegistry(metrics).build();
        jmxReporter.start();

        //模拟数据
        for (int i=0; i<20; i++){
            queue.add("a");
            Thread.sleep(5000);
        }
    }
}

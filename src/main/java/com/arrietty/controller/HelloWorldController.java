package com.arrietty.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: Yuechuan Zhang
 * @Date: 2021/6/24 15:19
 */

@RestController
@RequestMapping("/helloWorld")
public class HelloWorldController {

        @RequestMapping("/hello")
        public String hello(){
            return "Hello World";
        }


}

package com.jeeps.smartlandvault.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IndexController {
    @RequestMapping("/temp")
    public String home() {
        return "index";
    }
}

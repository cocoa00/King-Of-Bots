package com.kob.backend.controller.pk;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedList;
import java.util.List;

@RestController
@RequestMapping("/pk/")
public class BotInfoController {
    @RequestMapping("botinfo/")
    public List<String> botinfo(){
        List<String> temp = new LinkedList<>();
        temp.add("a");
        temp.add("va");
        return temp;

    }
}

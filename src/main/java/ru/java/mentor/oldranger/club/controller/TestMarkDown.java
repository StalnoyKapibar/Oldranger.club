package ru.java.mentor.oldranger.club.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TestMarkDown {

    @GetMapping("/markdown")
    String testMarkdown(){
        return "markdown";
    }
}
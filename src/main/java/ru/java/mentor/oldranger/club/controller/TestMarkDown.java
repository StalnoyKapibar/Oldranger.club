package ru.java.mentor.oldranger.club.controller;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Hidden
@Controller
public class TestMarkDown {

    @GetMapping("/markdown")
    String testMarkdown(){
        return "markdown";
    }
}
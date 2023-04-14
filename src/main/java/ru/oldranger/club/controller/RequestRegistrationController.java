package ru.oldranger.club.controller;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Deprecated
@Controller
@Getter
@Setter
@AllArgsConstructor
@RequestMapping("/request")
public class RequestRegistrationController {
    @GetMapping
    public String getRequestForm() {
        return "request";
    }
}

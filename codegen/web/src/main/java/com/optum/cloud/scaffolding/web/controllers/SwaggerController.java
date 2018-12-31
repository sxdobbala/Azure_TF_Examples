package com.optum.cloud.scaffolding.web.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import springfox.documentation.annotations.ApiIgnore;

@Controller
@ApiIgnore
public class SwaggerController {
    @RequestMapping("/")
    public String getSwagger() {
        return "redirect:/swagger-ui.html";
    }
}

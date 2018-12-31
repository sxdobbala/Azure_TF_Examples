package com.optum.cloud.scaffolding.web.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {

    @RequestMapping(method = RequestMethod.GET, path = "api/v1/healthCheck")
    public String healthCheck() {
        return "Health Check";
    }
}

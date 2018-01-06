package com.sagarandcompany.cloudnary;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RefreshScope
class HomeController {
    @Value("${name}")
    String name;

    @GetMapping("/name")
    
    public String name() {
        return name;
    }
}

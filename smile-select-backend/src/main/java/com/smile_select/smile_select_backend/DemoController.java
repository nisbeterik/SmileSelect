package com.smile_select.smile_select_backend;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class DemoController {
    
    @GetMapping("/helloworld")
    public String helloWorld(){
        return "Hello World!";
    }
}

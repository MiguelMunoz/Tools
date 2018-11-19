package com.neptunedreams.heroes.config;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Home redirection to swagger api documentation 
 */
@SuppressWarnings("HardcodedFileSeparator")
@Controller
public class HomeController {
    @RequestMapping("/")
    public String index() {
        System.out.println("swagger-ui.html");
        return "redirect:swagger-ui.html";
    }
}

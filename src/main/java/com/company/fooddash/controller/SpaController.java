package com.company.fooddash.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/** Lets a browser refresh on /menu, /story ... load the React app instead of a 404. */
@Controller
public class SpaController {
    @GetMapping({ "/explore", "/menu", "/story", "/login", "/register", "/orders" })
    public String spa() {
        return "forward:/index.html";
    }
}

package com.xtheggx.monedaDOS.controller;

import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class HomeController {

    @GetMapping({"/", "/index"})
    public String root() {
        return "redirect:/home";
    }

    @GetMapping("/home")
    public String home() {
        return "home/index"; // templates/home/index.html
    }

    @GetMapping("/panel")
    public String panel() {
        return "home/panel";
    }



    @GetMapping("/reportes")
    public String registrar() {
        return "home/reportes";
    }



}
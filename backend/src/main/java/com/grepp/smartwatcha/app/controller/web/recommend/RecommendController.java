package com.grepp.smartwatcha.app.controller.web.recommend;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/recommend")
public class RecommendController {

    @GetMapping
    public String recommendPage() {
        return "recommend/recommend";
    }
}
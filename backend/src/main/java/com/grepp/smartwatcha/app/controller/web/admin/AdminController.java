package com.grepp.smartwatcha.app.controller.web.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {

  @GetMapping("/login")
  public String login() {
    return "user/login";
  }

  @GetMapping("/admin")
  public String adminDashboard() {
    return "admin/dashboard";
  }
}

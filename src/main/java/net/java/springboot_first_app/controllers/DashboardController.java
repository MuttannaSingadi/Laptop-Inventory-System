package net.java.springboot_first_app.controllers;

import net.java.springboot_first_app.services.ProductRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final ProductRepository repo;

    public DashboardController(ProductRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("totalProducts", repo.count());
        model.addAttribute("recentProducts", repo.findAll());
        return "dashboard";
    }
}
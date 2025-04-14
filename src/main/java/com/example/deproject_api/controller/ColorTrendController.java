// src/main/java/com/example/deproject_api/controller/ColorTrendController.java
package com.example.deproject_api.controller;

import com.example.deproject_api.domain.ColorTrend;
import com.example.deproject_api.repository.ColorTrendRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/colors")
public class ColorTrendController {

    private final ColorTrendRepository repository;

    public ColorTrendController(ColorTrendRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<ColorTrend> getAllColors() {
        return repository.findAll();
    }
}
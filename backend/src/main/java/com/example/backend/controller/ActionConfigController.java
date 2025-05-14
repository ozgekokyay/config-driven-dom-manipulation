package com.example.backend.controller;

import com.example.backend.model.ActionConfig;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.yaml.snakeyaml.Yaml;

@RestController
@RequestMapping("/api/configuration")
@RequiredArgsConstructor
public class ActionConfigController {
    private final Map<String, ActionConfig> configStore = new ConcurrentHashMap<>();
    private final Yaml yaml = new Yaml();
    
    @GetMapping("/{id}")
    public ActionConfig getById(@PathVariable String id) {
        return findConfigOrThrow(id);       
    }

    @GetMapping("/all")
    public List<ActionConfig> getAll() {
        return new ArrayList<>(configStore.values());
    }

    @PostMapping(consumes = "application/json")
    public String create(@RequestBody ActionConfig config, HttpServletResponse response) {
        response.setHeader("Cache-Control", "no-store, must-revalidate");
        String id = UUID.randomUUID().toString();
        config.setId(id);
        configStore.put(id, config);
        return id;
    }
    
    // @GetMapping("/yaml/{id}")
    // public String getByIdYaml(@PathVariable String id) {
    //     ActionConfig config = findConfigOrThrow(id);
    //     return yaml.dumpAsMap(config);
    // }

    @PostMapping(consumes = "application/x-yaml")
    public String createFromYaml(@RequestBody String yamlContent) {
    try {
        ActionConfig config = yaml.loadAs(yamlContent, ActionConfig.class);
        config.setId(UUID.randomUUID().toString());
        configStore.put(config.getId(), config);
        return config.getId();
    } catch (Exception e) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid YAML: " + e.getMessage());
    }
    }
    
    private ActionConfig findConfigOrThrow(String id) {
        ActionConfig config = configStore.get(id);
        if (config == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Config not found");
        }
        return config;
    }
}
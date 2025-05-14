package com.example.backend.controller;

import com.example.backend.model.SpecificConfig;

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
@RequestMapping("/api/specific")
@RequiredArgsConstructor
public class SpecificConfigController {
    private final Map<String, SpecificConfig> configStore = new ConcurrentHashMap<>();
    private final Yaml yaml = new Yaml();

    @GetMapping
    public SpecificConfig getByContext(
            @RequestParam(required = false) String host,
            @RequestParam(required = false) String url,
            @RequestParam(required = false) String page) {
        
        return configStore.values().stream()
                .filter(config -> matchesContext(config, host, url, page))
                .findFirst()
                .orElse(null);
    }
    
    @GetMapping("/{id}")
    public SpecificConfig getById(@PathVariable String id) {
        return findConfigOrThrow(id);       
    }

    @GetMapping("/all")
    public List<SpecificConfig> getAll() {
        return new ArrayList<>(configStore.values());
    }

    @PostMapping(consumes = "application/json")
    public String create(@RequestBody SpecificConfig config, HttpServletResponse response) {
            // Add cache control headers
        response.setHeader("Cache-Control", "no-store, must-revalidate");
        String id = UUID.randomUUID().toString();
        config.setId(id);
        configStore.put(id, config);
        return id;
    }
    
    @GetMapping("/yaml/{id}")
    public String getByIdYaml(@PathVariable String id) {
        SpecificConfig config = findConfigOrThrow(id);
        return yaml.dumpAsMap(config);
    }

    @PostMapping(consumes = "application/x-yaml")
    public String createFromYaml(@RequestBody String yamlContent) {
    try {
        SpecificConfig config = yaml.loadAs(yamlContent, SpecificConfig.class);
        config.setId(UUID.randomUUID().toString());
        configStore.put(config.getId(), config);
        return config.getId();
    } catch (Exception e) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid YAML: " + e.getMessage());
    }
    }
    
    private boolean matchesContext(SpecificConfig config, String host, String url, String page) {
        boolean matches = false;
        
        if (host != null && config.getHosts() != null && config.getHosts().containsKey(host)) {
            matches = true;
        }
        
        if (url != null && config.getUrls() != null && config.getUrls().containsKey(url)) {
            matches = true;
        }
        
        if (page != null && config.getPages() != null && config.getPages().containsKey(page)) {
            matches = true;
        }
        
        return matches;
    }

    private SpecificConfig findConfigOrThrow(String id) {
        SpecificConfig config = configStore.get(id);
        if (config == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Config not found");
        }
        return config;
    }
}
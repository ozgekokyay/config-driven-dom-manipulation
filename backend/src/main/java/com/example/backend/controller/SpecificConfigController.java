package com.example.backend.controller;

import com.example.backend.model.SpecificConfig;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/specific")
@RequiredArgsConstructor
public class SpecificConfigController {
    private final Map<String, SpecificConfig> configStore = new ConcurrentHashMap<>();
    
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
        return configStore.get(id);
    }

    @GetMapping("/all")
    public List<SpecificConfig> getAll() {
        return new ArrayList<>(configStore.values());
    }

    @PostMapping
    public String create(@RequestBody SpecificConfig config) {
        String id = UUID.randomUUID().toString();
        config.setId(id);
        configStore.put(id, config);
        return id;
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
}
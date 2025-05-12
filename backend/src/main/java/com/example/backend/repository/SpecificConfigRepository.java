package com.example.backend.repository;

import com.example.backend.model.SpecificConfig;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class SpecificConfigRepository {
    private final Map<String, SpecificConfig> configStore = new ConcurrentHashMap<>();
    
    public SpecificConfig save(SpecificConfig config) {
        if (config.getId() == null) {
            config.setId(UUID.randomUUID().toString());
        }
        configStore.put(config.getId(), config);
        return config;
    }
    
    public Optional<SpecificConfig> findById(String id) {
        return Optional.ofNullable(configStore.get(id));
    }
    
    public List<SpecificConfig> findAll() {
        return new ArrayList<>(configStore.values());
    }
    
    public void deleteById(String id) {
        configStore.remove(id);
    }
    
    public boolean existsById(String id) {
        return configStore.containsKey(id);
    }
}
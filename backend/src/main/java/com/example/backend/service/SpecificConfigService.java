package com.example.backend.service;

import com.example.backend.model.SpecificConfig;
import com.example.backend.repository.SpecificConfigRepository;
import com.example.backend.exception.NotFoundException;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SpecificConfigService {
    private final SpecificConfigRepository repository;
    
    public SpecificConfig createConfig(SpecificConfig config) {
        return repository.save(config);
    }
    
    public SpecificConfig getConfigById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("SpecificConfig not found with id: " + id));
    }
    
    public List<SpecificConfig> getAllConfigs() {
        return repository.findAll();
    }
    
    public SpecificConfig updateConfig(String id, SpecificConfig config) {
        if (!repository.existsById(id)) {
            throw new NotFoundException("SpecificConfig not found with id: " + id);
        }
        config.setId(id);
        return repository.save(config);
    }
    
    public void deleteConfig(String id) {
        repository.deleteById(id);
    }
}
    
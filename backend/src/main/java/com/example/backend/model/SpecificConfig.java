package com.example.backend.model;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SpecificConfig {
    private String id;
    private Map<String, List<String>> pages;
    private Map<String, List<String>> urls;
    private Map<String, List<String>> hosts;
    
}



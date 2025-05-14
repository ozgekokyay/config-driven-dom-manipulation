package com.example.backend.model;
import lombok.Data;

@Data
public class ActionConfig {
    private String id;
    private String type;
    private String selector;
    private String newElement;
    private String position;
    private String target;
    private String element;
    private String oldValue;
    private String newValue;
    private ConfigPriority priority;
    private int loadOrder; 
}


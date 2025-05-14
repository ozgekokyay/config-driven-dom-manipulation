package com.example.backend.model;

public enum ConfigPriority {
    HOST(3),
    URL(2),
    PAGE(1),
    DEFAULT(0);

    private final int weight;
    ConfigPriority(int weight) { this.weight = weight; }
    public int getWeight() { return weight; }
}
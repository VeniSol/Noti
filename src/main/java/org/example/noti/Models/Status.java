package org.example.noti.Models;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Status {
    ACTIVE("ACTIVE"),
    ENDED("ENDED");
    private final String status;
}

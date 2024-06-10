package com.tetradunity.server.models.certificates;

public enum CertificateType {
    PARTICIPATION("за участь"),
    GOOD_RESULTS("за хороші результати"),
    EXCELLENT_RESULTS("за відмінні результати");

    private final String description;

    CertificateType(String description){
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
package com.tetradunity.server.models.certificates;

public enum CertificateType {
    PARTICIPATION("участь"),
    GOOD_RESULTS("гарні результати"),
    EXCELLENT_RESULTS("відмінні результати");

    private final String description;

    CertificateType(String description){
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
package com.tetradunity.server.projections;

import com.tetradunity.server.models.certificates.CertificateType;

public interface CertificateProjection {
    String getTitle();
    CertificateType getType();
    String getUid();
}

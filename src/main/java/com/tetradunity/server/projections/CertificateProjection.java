package com.tetradunity.server.projections;

import com.tetradunity.server.models.certificates.CertificateType;

public interface CertificateProjection {
    String getTitle();
    String getType();
    String getUid();
}

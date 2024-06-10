package com.tetradunity.server.models.certificates;

import com.tetradunity.server.projections.CertificateProjection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Certificate {
    private String title;
    private CertificateType type;
    private String uid;

    public Certificate(CertificateProjection projection){
        this(
                projection.getTitle(),
                projection.getType(),
                projection.getUid()
        );
    }
}
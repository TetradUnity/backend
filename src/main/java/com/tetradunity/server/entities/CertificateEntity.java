package com.tetradunity.server.entities;

import com.tetradunity.server.models.certificates.CertificateType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "certificates")
public class CertificateEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID uid;

    private long student_id;
    private long subject_id;
    private CertificateType type;

    public CertificateEntity(long student_id, long subject_id, double result){
        this.student_id = student_id;
        this.subject_id = subject_id;
        if(result < 65){
            this.type = CertificateType.PARTICIPATION;
        }
        else if(result < 90){
            this.type = CertificateType.GOOD_RESULTS;
        }
        else{
            this.type = CertificateType.EXCELLENT_RESULTS;
        }
    }
}

package com.tetradunity.server.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "password_recovery_requests")
public class PasswordRecoveryRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID uid;
    private String email;
    private long expiration = System.currentTimeMillis() + 300_000;

    public PasswordRecoveryRequest(String email){
        this.email = email;
    }

    @PrePersist
    public void prePersist(){
        if(uid == null){
            uid = UUID.randomUUID();
        }
    }

    public boolean isActual(){
        return System.currentTimeMillis() < expiration;
    }
}
package com.tetradunity.server.models.users;

import com.tetradunity.server.projections.CandidateProjection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Candidate {
    private long id;
    private String email, first_name, last_name;
    private double result;
    private int duration;

    public Candidate(CandidateProjection projection){
        this(
                projection.getId(), projection.getEmail(), projection.getFirst_name(),
                projection.getLast_name(), projection.getResult(), projection.getDuration()
        );
    }
}
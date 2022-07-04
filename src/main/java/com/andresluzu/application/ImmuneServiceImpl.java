package com.andresluzu.application;

import com.andresluzu.domain.Antibody;
import com.andresluzu.domain.Antigen;
import com.andresluzu.domain.InvalidAntigenException;
import com.andresluzu.domain.LymphaticSystem;

public class ImmuneServiceImpl implements ImmuneService {
    private ImmuneStore store;

    public ImmuneServiceImpl(ImmuneStore store) {
        this.store = store;
    }

    @Override
    public Antibody respond(Antigen antigen) throws InvalidAntigenException {
        // Search for antibody in immune store or produce it
        Antibody antibody = store.find(antigen).orElse(LymphaticSystem.produce(antigen));

        // Save antibody
        store.save(antibody);

        return antibody;
    }
}

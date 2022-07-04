package com.andresluzu.application;

import com.andresluzu.domain.Antigen;
import com.andresluzu.domain.Antibody;

import java.util.Optional;

public interface ImmuneStore {
    void save(Antibody antibody);

    Optional<Antibody> find(Antigen antigen);
}

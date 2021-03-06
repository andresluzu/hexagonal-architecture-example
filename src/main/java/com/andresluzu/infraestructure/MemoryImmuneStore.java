package com.andresluzu.infraestructure;

import com.andresluzu.application.ImmuneStore;
import com.andresluzu.domain.Antibody;
import com.andresluzu.domain.Antigen;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class MemoryImmuneStore implements ImmuneStore {
    private Map<Integer, Antigen> antigenMap = new HashMap<>();

    @Override
    public void save(Antibody antibody) {
        antigenMap.putIfAbsent(antibody.getAntigen().getValue(), antibody.getAntigen());
    }

    @Override
    public Optional<Antibody> find(Antigen antigen) {
        return antigenMap.entrySet().stream().filter(entry -> entry.getKey().equals(antigen.getValue())).map(Map.Entry::getValue).map(a -> new Antibody(a, 0)).findAny();
    }
}

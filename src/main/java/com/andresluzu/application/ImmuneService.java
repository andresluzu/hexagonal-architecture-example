package com.andresluzu.application;

import com.andresluzu.domain.Antibody;
import com.andresluzu.domain.Antigen;
import com.andresluzu.domain.InvalidAntigenException;

public interface ImmuneService {
    Antibody respond(Antigen antigen) throws InvalidAntigenException;
}

package com.fon.neda.da.service;

import com.fon.neda.da.entity.Parameter;
import com.fon.neda.da.repository.ParameterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
@ComponentScan
public class ParameterService {
    @Autowired
    ParameterRepository parameterRepository;

    public void save(Parameter parameter){
        parameterRepository.save(parameter);
    }
}

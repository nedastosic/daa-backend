package com.fon.neda.da.service;

import com.fon.neda.da.entity.ParameterCodelist;
import com.fon.neda.da.repository.ParameterCodelistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Service
@ComponentScan
public class ParameterCodelistService {
    @Autowired
    ParameterCodelistRepository parameterCodelistRepository;

    public ParameterCodelist findParameterCodelistsByName(String name){
        return parameterCodelistRepository.findParameterCodelistsByName(name);
    }

    public ParameterCodelist findParameterCodelistsById(long id){
        return parameterCodelistRepository.findParameterCodelistsById(id);
    }

    public List<ParameterCodelist> findParameterCodelistByAlgorithmId(long algorithmId){
        return parameterCodelistRepository.findParameterCodelistByAlgorithmId(algorithmId);
    }

}

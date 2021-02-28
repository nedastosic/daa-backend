package com.fon.neda.da.service;


import com.fon.neda.da.entity.Algorithm;
import com.fon.neda.da.entity.AlgorithmParameterCodelist;
import com.fon.neda.da.repository.AlgorithmParameterCodelistRepository;
import com.fon.neda.da.repository.AlgorithmRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Service
@ComponentScan
public class AlgorithmParameterCodelistService {

    @Autowired
    public AlgorithmParameterCodelistRepository algorithmParameterCodelistRepository;

    public List<AlgorithmParameterCodelist> findAlgorithmParameterCodelistByAlgorithmId(long algorithmId){
        return algorithmParameterCodelistRepository.findAlgorithmParameterCodelistByAlgorithmId(algorithmId);
    }

}

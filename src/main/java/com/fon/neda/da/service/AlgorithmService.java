package com.fon.neda.da.service;

import com.fon.neda.da.entity.Algorithm;
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
@EnableJpaRepositories(basePackages = {"com.fon.neda.da.repository"})
public class AlgorithmService {

    @Autowired
    public AlgorithmRepository algorithmRepository;


    public void saveAlgorithm(){
        //Algorithm a1 = new Algorithm(1,"test algorithm");
        //algorithmRepository.save(a1);
    }

    public List<Algorithm> findAllAlgorithms(){
        return (List<Algorithm>) algorithmRepository.findAll();
    }

    public Algorithm findAlgorithmByName(String name){
        return algorithmRepository.findAlgorithmByName(name);
    }

    public Algorithm findAlgorithmById(long id){
        return algorithmRepository.findAlgorithmById(id);
    }

}

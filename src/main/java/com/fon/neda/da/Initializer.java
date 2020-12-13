package com.fon.neda.da;

import com.fon.neda.da.entity.Algorithm;
import com.fon.neda.da.repository.AlgorithmRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class Initializer implements CommandLineRunner {

    @Autowired
    private final AlgorithmRepository algorithmRepository;

    public Initializer(AlgorithmRepository algorithmRepository) {
        this.algorithmRepository = algorithmRepository;
    }


    @Override
    public void run(String... args) throws Exception {
        //Stream.of("Algorithm1").forEach(name-> algorithmRepository.save(new Algorithm(1,name)));

        algorithmRepository.findAll().forEach(System.out::println);
    }
}

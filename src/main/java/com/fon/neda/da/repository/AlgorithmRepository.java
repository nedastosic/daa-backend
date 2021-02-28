package com.fon.neda.da.repository;


import com.fon.neda.da.entity.Algorithm;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface AlgorithmRepository extends CrudRepository<Algorithm, Long> {
    Algorithm findAlgorithmByName(String name);
    Algorithm findAlgorithmById(long id);
}

package com.fon.neda.da.repository;

import com.fon.neda.da.entity.AlgorithmParameterCodelist;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlgorithmParameterCodelistRepository extends CrudRepository<AlgorithmParameterCodelist, Long>{
    List<AlgorithmParameterCodelist> findAlgorithmParameterCodelistByAlgorithmId(long algorithmId);
}

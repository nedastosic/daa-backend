package com.fon.neda.da.repository;

import com.fon.neda.da.entity.Evaluation;
import com.fon.neda.da.entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EvaluationRepository extends CrudRepository<Evaluation, Long> {

     List<Evaluation> findEvaluationsByUser(User user);

     List<Evaluation> findAll();

     void deleteEvaluationById(Long id);


}

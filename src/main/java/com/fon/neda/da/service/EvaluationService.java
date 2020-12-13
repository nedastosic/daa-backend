package com.fon.neda.da.service;

import com.fon.neda.da.entity.Evaluation;
import com.fon.neda.da.entity.User;
import com.fon.neda.da.repository.EvaluationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Service
@ComponentScan
public class EvaluationService {

    @Autowired
    EvaluationRepository evaluationRepository;

    public Evaluation save(Evaluation evaluation){
        return evaluationRepository.save(evaluation);
    }


    public List<Evaluation> findEvaluationsByUser(User user){
        return evaluationRepository.findEvaluationsByUser(user);
    }

    public void deleteEvaluationById(Long id){
        evaluationRepository.deleteEvaluationById(id);
    }


}
